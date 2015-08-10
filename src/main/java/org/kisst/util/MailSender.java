package org.kisst.util;
import java.security.Security;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.kisst.props4j.Props;

import com.sun.mail.smtp.SMTPTransport;


public class MailSender {
	private final  Session session;
	private final String username;
	private final String password;

	public MailSender(Props p) {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		Authenticator auth=null;

		// Get a Properties object
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", p.getString("host")); //"smtp.gmail.com");
		//props.setProperty("mail.smtp.quitwait", "false");

		if (p.getBoolean("ssl")) {
			props.setProperty("mail.smtp.starttls.enable", "true");
			props.setProperty("mail.smtp.ssl.enable", "true");

			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			props.put("mail.smtp.socketFactory.port", p.getInteger("port",465));
			props.put("mail.smtp.port", p.getInteger("port",465));

		}
		else
			props.setProperty("mail.smtp.port", p.getString("port","25"));

		this.username=p.getString("username",null);
		this.password=p.getString("password",null);

		if (username!=null)
			props.setProperty("mail.smtp.auth", "true");
			auth=new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

		this.session = Session.getInstance(props, auth);
	}

	public static String format(String name, String email) {
		if (name==null || name.trim().length()==0)
			return email;
		return "\""+name+"\" <"+email+">";
	}

	public MimeMessage createMessage() { return new MimeMessage(session); }
	
	public void send(String from, String to, String title, String message) {
		final MimeMessage msg = createMessage();
		try {
			msg.setFrom(new InternetAddress(from));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
			msg.setSubject(title);
			msg.setText(message, "utf-8");
			send(msg);
		} 
		catch (MessagingException e) { throw new RuntimeException(e); }
	}
	public void send(String from, String replyTo, String to, String cc, String bcc, String title, String message) {
		final MimeMessage msg = new MimeMessage(session);

		try {
			msg.setFrom(new InternetAddress(from));
			msg.setReplyTo(InternetAddress.parse(replyTo, false));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

			if (cc!=null && cc.length() > 0) {
				msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
			}

			if (bcc!=null && bcc.length() > 0) {
				msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
			}

			msg.setSubject(title);
			msg.setText(message, "utf-8");
			//msg.setSentDate(new Date());

			send(msg);
		} 
		catch (MessagingException e) { throw new RuntimeException(e); }
	}

	public void send(MimeMessage msg ) {
		try {
			SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

			t.connect("smtp.gmail.com", username, password);
			t.sendMessage(msg, msg.getAllRecipients());      
			t.close();
		} 
		catch (MessagingException e) { throw new RuntimeException(e); }
	}
}