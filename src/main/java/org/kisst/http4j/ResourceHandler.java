package org.kisst.http4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ResourceHandler implements HttpHandler {
	private static final long ONE_SECOND_IN_MILLIS = TimeUnit.SECONDS.toMillis(1);
	private static final String ETAG_HEADER = "W/\"%s-%s\"";
	private static final String CONTENT_DISPOSITION_HEADER = "inline;filename=\"%1$s\"; filename*=UTF-8''%1$s";

	public static final long DEFAULT_EXPIRE_TIME_IN_MILLIS = TimeUnit.DAYS.toMillis(30);
	public static final int DEFAULT_STREAM_BUFFER_SIZE = 102400;

	private final ResourceFinder finder;
	public ResourceHandler(ResourceFinder finder) { this.finder=finder; } 
	public void handle(String subPath, HttpServletRequest request, HttpServletResponse response) {
		try {
			response.reset();
			Resource resource=finder.findResource(subPath);
			//System.out.println("found "+subPath+"==> "+resource.getContentLength()+" bytes");
			
			//if (resource == null) {
			//	response.sendError(HttpServletResponse.SC_NOT_FOUND);
			//	return;
			//}

			String fileName = URLEncoder.encode(resource.getFileName(), StandardCharsets.UTF_8.name());
			boolean notModified = setCacheHeaders(request, response, fileName, resource.getLastModified());

			if (notModified) {
				response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
			setContentHeaders(response, fileName, resource.getContentLength());

			if ("HEAD".equals(request.getMethod()))
				return;

			writeContent(response, resource);
		}
		catch (IOException e) { throw new RuntimeException(e);}

	}

	private boolean setCacheHeaders(HttpServletRequest request, HttpServletResponse response, String fileName, long lastModified) {
		String eTag = String.format(ETAG_HEADER, fileName, lastModified);
		response.setHeader("ETag", eTag);
		response.setDateHeader("Last-Modified", lastModified);
		response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME_IN_MILLIS);
		return notModified(request, eTag, lastModified);
	}

	private boolean notModified(HttpServletRequest request, String eTag, long lastModified) {
		String ifNoneMatch = request.getHeader("If-None-Match");

		if (ifNoneMatch != null) {
			String[] matches = ifNoneMatch.split("\\s*,\\s*");
			Arrays.sort(matches);
			return (Arrays.binarySearch(matches, eTag) > -1 || Arrays.binarySearch(matches, "*") > -1);
		}
		else {
			long ifModifiedSince = request.getDateHeader("If-Modified-Since");
			return (ifModifiedSince + ONE_SECOND_IN_MILLIS > lastModified); // That second is because the header is in seconds, not millis.
		}
	}

	private void setContentHeaders(HttpServletResponse response, String fileName, long contentLength) {
		try {
			response.setHeader("Content-Type", Files.probeContentType(Paths.get(fileName)));
		}
		catch (IOException e) { throw new RuntimeException(e);}
		response.setHeader("Content-Disposition", String.format(CONTENT_DISPOSITION_HEADER, fileName));

		if (contentLength != -1) {
			response.setHeader("Content-Length", String.valueOf(contentLength));
		}
	}

	private void writeContent(HttpServletResponse response, Resource resource) throws IOException {
		try (ReadableByteChannel inputChannel = Channels.newChannel(resource.getInputStream());
				WritableByteChannel outputChannel = Channels.newChannel(response.getOutputStream());)
		{
			ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_STREAM_BUFFER_SIZE);
			long size = 0;

			while (inputChannel.read(buffer) != -1) {
				buffer.flip();
				size += outputChannel.write(buffer);
				buffer.clear();
			}

			if (resource.getContentLength() == -1 && !response.isCommitted()) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
		}
	}


	public interface Resource {
		public String getFileName();
		public long getLastModified();
		public long getContentLength();
		public InputStream getInputStream() throws IOException;
	}
	public interface ResourceFinder {
		public Resource findResource(String name);
	}

	public static class FileResourceFinder implements ResourceFinder {
		private final File dir;
		public FileResourceFinder(File dir) {this.dir=dir;} 
		public Resource findResource(String name) { return new FileResource(name); }
		private class FileResource implements Resource {
			private final File file;
			public FileResource(String name) { this.file=new File(dir,name); 
				System.out.println(file.getAbsolutePath());
			}
			@Override public String getFileName() { return file.getName();}
			@Override public long getLastModified() { return file.lastModified();}
			@Override public long getContentLength() { return file.length();}
			@Override public InputStream getInputStream() {
				try {
					return new FileInputStream(file);
				}
				catch (FileNotFoundException e) { throw new RuntimeException(e);}
			}
		}
	}
	public static class ClasspathResourceFinder implements ResourceFinder {
		private final String prefix;
		public ClasspathResourceFinder(String prefix) {this.prefix=prefix;} 
		public Resource findResource(String name) { return new ClassPathResource(prefix+name); }
		private class ClassPathResource implements Resource {
			private final String name;
			//private final URL url;
			public ClassPathResource(String name) { this.name=name;} // this.url=this.getClass().getClassLoader().getResource(name);}
			@Override public String getFileName() { return name;}
			@Override public long getLastModified() { return System.currentTimeMillis();}
			@Override public long getContentLength() { return 0;}
			@Override public InputStream getInputStream() {
				return this.getClass().getClassLoader().getResourceAsStream(name);
			}
		}
	}

	
}