package cazcade.fountain.index.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * @author neilellis@cazcade.com
 */
public class EmailServiceImpl implements EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendBoardInvite(String emailAddress, String sender, String boardName, String hash) {
        String host = "smtp.gmail.com";
        int port = 587;
        String username = "info@hashbo.com";
        String password = "vipassana";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("info@hashbo.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailAddress));
            message.setSubject("You have been invited to HashBo board " + boardName);
            message.setText("To accept the invite click on this link: http://hashbo.com/_/acceptInvite.jsp?code=" + URLEncoder.encode(hash, "utf-8") + "&invitee=" + URLEncoder.encode(emailAddress, "utf-8"));
            Transport transport = session.getTransport("smtp");
            transport.connect(host, port, username, password);
            Transport.send(message);
            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
