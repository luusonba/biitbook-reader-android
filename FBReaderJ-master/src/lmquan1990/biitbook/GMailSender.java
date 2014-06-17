package lmquan1990.biitbook;

import javax.activation.DataHandler;   
import javax.activation.DataSource;   
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Message;   
import javax.mail.PasswordAuthentication;   
import javax.mail.Session;   
import javax.mail.Transport;   
import javax.mail.internet.InternetAddress;   
import javax.mail.internet.MimeMessage;   

import java.io.ByteArrayInputStream;   
import java.io.IOException;   
import java.io.InputStream;   
import java.io.OutputStream;   
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.Security;   
import java.util.Properties;   

public class GMailSender extends javax.mail.Authenticator {   
    private String mailhost = "smtp.gmail.com";   
    private String user;   
    private String password;   
    private Session session;   

    static {   
        Security.addProvider(new lmquan1990.biitbook.JSSEProvider());   
    }  

    public GMailSender(String user, String password) {   
    	String[] byteValues = password.substring(1, password.length() - 1).split(",");
    	byte[] bytes = new byte[byteValues.length];

    	for (int i=0, len=bytes.length; i<len; i++) {
    	   bytes[i] = Byte.valueOf(byteValues[i].trim());     
    	}
    	String key = "dgjgdkjgdkgdjgkd";
    	try {
			password = decrypt(key, bytes);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	this.user = user;   
        this.password = password;   
        Properties props = new Properties();   
        props.setProperty("mail.transport.protocol", "smtp");   
        props.setProperty("mail.host", mailhost);   
        props.put("mail.smtp.auth", "true");   
        props.put("mail.smtp.port", "465");   
        props.put("mail.smtp.socketFactory.port", "465");   
        props.put("mail.smtp.socketFactory.class",   
                "javax.net.ssl.SSLSocketFactory");   
        props.put("mail.smtp.socketFactory.fallback", "false");   
        props.setProperty("mail.smtp.quitwait", "false");   

        session = Session.getDefaultInstance(props, this);   
    }   

    protected PasswordAuthentication getPasswordAuthentication() {   
        return new PasswordAuthentication(user, password);   
    }   
    
    public static String decrypt(String key, byte[] encrypted)
    	      throws GeneralSecurityException {
    		    		
    	    byte[] raw = key.getBytes(Charset.forName("US-ASCII"));
    	    if (raw.length > 200) {
    	      throw new IllegalArgumentException("Invalid key size.");
    	    }
    	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

    	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    	    cipher.init(Cipher.DECRYPT_MODE, skeySpec,
    	        new IvParameterSpec(new byte[16]));
    	    byte[] original = cipher.doFinal(encrypted);

    	    return new String(original, Charset.forName("US-ASCII"));
    	  }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {   
        try{
        MimeMessage message = new MimeMessage(session);   
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));   
        message.setSender(new InternetAddress(sender));   
        message.setSubject(subject);   
        message.setDataHandler(handler);   
        if (recipients.indexOf(',') > 0)   
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));   
        else  
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));   
        Transport.send(message);   
        }catch(Exception e){

        }
    }   

    public class ByteArrayDataSource implements DataSource {   
        private byte[] data;   
        private String type;   

        public ByteArrayDataSource(byte[] data, String type) {   
            super();   
            this.data = data;   
            this.type = type;   
        }   

        public ByteArrayDataSource(byte[] data) {   
            super();   
            this.data = data;   
        }   

        public void setType(String type) {   
            this.type = type;   
        }   

        public String getContentType() {   
            if (type == null)   
                return "application/octet-stream";   
            else  
                return type;   
        }   

        public InputStream getInputStream() throws IOException {   
            return new ByteArrayInputStream(data);   
        }   

        public String getName() {   
            return "ByteArrayDataSource";   
        }   

        public OutputStream getOutputStream() throws IOException {   
            throw new IOException("Not Supported");   
        }   
    }   
}  