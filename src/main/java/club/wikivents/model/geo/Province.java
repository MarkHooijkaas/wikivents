package club.wikivents.model.geo;

import java.io.File;
import java.util.HashMap;

import org.kisst.util.FileUtil;

public class Province {
	private static final HashMap<String, Province> index= new HashMap<>();

	public final String code;
	public final String name;

	private Province(String code, String name) {
		this.code = code;
		this.name = name;
		index.put(code, this);
		index.put(name, this);
	}

	public static Province from(Object obj, Province defaultValue) {
		if (obj instanceof Province)
			return (Province) obj;
		if (obj instanceof String)
			return fromString((String) obj);
		return defaultValue;
	}

	public static Province fromString(String text) { return index.get(text); }
	@Override public String toString() { return name;}

	public static void load(String filename) {
		File f=new File(filename);
		if (! f.exists())
			return;
		for (String line: FileUtil.loadString(f).split("\n")) {
			String[] parts = line.split(",");
			if (parts.length==2)
				new Province(parts[0], parts[1]);
			else
				System.out.println("Ignoring "+line);
		}
	}
}