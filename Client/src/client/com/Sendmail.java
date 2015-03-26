package client.com;

import java.util.Properties;  
import javax.mail.*;  
import javax.mail.internet.*;  
  
public class Sendmail {  
 public static void mail(String message1) {  
     
	 String to="nandupk123@gmail.com";//change accordingly
	 //String cc="nandupk123@gmail.com";
	//Get the session object
	  Properties props = new Properties();
	  props.put("mail.smtp.host", "smtp.gmail.com");
	  props.put("mail.smtp.socketFactory.port", "465");
	  props.put("mail.smtp.socketFactory.class",
	        	"javax.net.ssl.SSLSocketFactory");
	  props.put("mail.smtp.auth", "true");
	  props.put("mail.smtp.port", "465");
	  props.put("mail.smtp.starttls.enable", "true");
	 
	  Session session = Session.getDefaultInstance(props,
	   new javax.mail.Authenticator() {
	   protected PasswordAuthentication getPasswordAuthentication() {
	   return new PasswordAuthentication("kaukkgan@gmail.com","gankkkau");//change accordingly
	   }
	  });
	 
	//compose message
	  try {
	   MimeMessage message = new MimeMessage(session);
	   message.setFrom(new InternetAddress("kaukkgun@gmail.com"));//change accordingly
	   message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
	   //message.addRecipient(Message.RecipientType.CC,new InternetAddress(cc));
	   message.setSubject("CPU WARNING ");
	   message.setContent(message1,"text/html");
	  // message.setText(message1);
	   
	   //send message
	   Transport.send(message);

	   System.out.println("message sent successfully");
	 
	  } catch (MessagingException e) {throw new RuntimeException(e);}
	 
	 }
}  