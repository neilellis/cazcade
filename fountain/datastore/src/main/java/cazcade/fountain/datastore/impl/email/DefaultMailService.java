package cazcade.fountain.datastore.impl.email;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of mail service.
 */
public class DefaultMailService implements MailService {

    /**
     * System property to set to override default template directory of &quot;./mail-templates&quot;.
     */
    @Nonnull
    public static final String TEMPLATE_DIR_PROP = "cazcade.template.dir";

    private static final Logger LOG = Logger.getLogger(DefaultMailService.class);

    private VelocityEngine velocity;
    private Session mailSession;
    private Transport mailTransport;
    private String smtpHost;
    private SMTPAuthenticator smtpAuthenticator;
    private String sender;
    private String senderFullname;

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public void setSmtpAuthenticator(SMTPAuthenticator smtpAuthenticator) {
        this.smtpAuthenticator = smtpAuthenticator;
    }

    public void init() throws IOException, NoSuchProviderException {
        initialiseVelocity();
        initialiseMailSession(smtpHost, smtpAuthenticator);
    }

    @Override
    public void sendMailFromTemplate(String templateIdentifier, String subject, String[] to, @Nonnull String[] cc, @Nonnull String[] bcc,
                                     Map<String, Object> templateParameters, boolean test) {
        try {
            Template template = velocity.getTemplate(templateIdentifier, "UTF-8");
            StringWriter output = new StringWriter();
            VelocityContext context = new VelocityContext(templateParameters);
            template.merge(context, output);
            String messageBody = output.toString();

            if (test) {
                System.out.println("To: " + Arrays.toString(to));
                System.out.println("CC: " + Arrays.toString(cc));
                System.out.println("BCC: " + Arrays.toString(bcc));
                System.out.println("Subject: " + subject);
                System.out.println(messageBody);
                to = new String[]{"neil@boardcast.it"};
            }


            MimeMessage message = new MimeMessage(mailSession);
            message.setContent(messageBody, "text/html");
            message.setFrom(new InternetAddress(sender, senderFullname));
            message.setSubject(subject);
            addRecipients(message, to, Message.RecipientType.TO);
            addRecipients(message, cc, Message.RecipientType.CC);
            addRecipients(message, bcc, Message.RecipientType.BCC);
            message.saveChanges();
            mailTransport.connect();
            try {
                mailTransport.sendMessage(message, message.getAllRecipients());
            } finally {
                mailTransport.close();
            }
        } catch (Exception e) {
            LOG.error("Failed to send message from template: " + templateIdentifier, e);
        }
    }

    private void addRecipients(@Nonnull MimeMessage message, @Nonnull String[] recipients, Message.RecipientType recipientType) throws MessagingException {
        for (String recipient : recipients) {
            message.addRecipient(recipientType, new InternetAddress(recipient));
        }
    }

    private void initialiseVelocity() throws IOException {
        ExtendedProperties extendedProperties = new ExtendedProperties();
        extendedProperties.load(DefaultMailService.class.getResourceAsStream("/email-velocity.properties"));
        velocity = new VelocityEngine();
        velocity.setExtendedProperties(extendedProperties);
        velocity.init();
    }

    private void initialiseMailSession(String smtpHost, @Nullable SMTPAuthenticator smtpAuthenticator) throws NoSuchProviderException {
        Properties mailProperties = new Properties();
        mailProperties.put("mail.transport.protocol", "smtp");
        mailProperties.put("mail.smtp.host", smtpHost);
        mailProperties.put("mail.smtp.auth", smtpAuthenticator == null ? "false" : "true");
        mailSession = Session.getInstance(mailProperties, smtpAuthenticator);
        mailTransport = mailSession.getTransport();
    }


    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSenderFullname(String senderFullname) {
        this.senderFullname = senderFullname;
    }
}
