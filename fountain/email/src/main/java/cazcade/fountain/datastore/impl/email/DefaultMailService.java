/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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

    private VelocityEngine    velocity;
    private Session           mailSession;
    private Transport         mailTransport;
    private String            smtpHost;
    private SMTPAuthenticator smtpAuthenticator;
    private String            sender;
    private String            senderFullname;

    public void init() throws IOException, NoSuchProviderException {
        initialiseVelocity();
        initialiseMailSession(smtpHost, smtpAuthenticator);
    }

    private void initialiseVelocity() throws IOException {
        final ExtendedProperties extendedProperties = new ExtendedProperties();
        extendedProperties.load(DefaultMailService.class.getResourceAsStream("/email-velocity.properties"));
        velocity = new VelocityEngine();
        velocity.setExtendedProperties(extendedProperties);
        velocity.init();
    }

    private void initialiseMailSession(final String smtpHost, @Nullable final SMTPAuthenticator smtpAuthenticator) throws NoSuchProviderException {
        final Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.transport.protocol", "smtp");
        mailProperties.setProperty("mail.smtp.host", smtpHost);
        //noinspection VariableNotUsedInsideIf
        mailProperties.setProperty("mail.smtp.auth", smtpAuthenticator == null ? "false" : "true");
        mailSession = Session.getInstance(mailProperties, smtpAuthenticator);
        mailTransport = mailSession.getTransport();
    }

    @Override
    public void sendMailFromTemplate(final String templateIdentifier, final String subject, String[] to, @Nonnull final String[] cc, @Nonnull final String[] bcc, final Map<String, Object> templateParameters, final boolean test) {
        try {
            final Template template = velocity.getTemplate(templateIdentifier, "UTF-8");
            final StringWriter output = new StringWriter();
            final VelocityContext context = new VelocityContext(templateParameters);
            template.merge(context, output);
            final String messageBody = output.toString();

            if (test) {
                System.out.println("To: " + Arrays.toString(to));
                System.out.println("CC: " + Arrays.toString(cc));
                System.out.println("BCC: " + Arrays.toString(bcc));
                System.out.println("Subject: " + subject);
                System.out.println(messageBody);
                to = new String[]{"neil@boardcast.it"};
            }


            final MimeMessage message = new MimeMessage(mailSession);
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

    private void addRecipients(@Nonnull final MimeMessage message, @Nonnull final String[] recipients, final Message.RecipientType recipientType) throws MessagingException {
        for (final String recipient : recipients) {
            message.addRecipient(recipientType, new InternetAddress(recipient));
        }
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public void setSenderFullname(final String senderFullname) {
        this.senderFullname = senderFullname;
    }

    public void setSmtpAuthenticator(final SMTPAuthenticator smtpAuthenticator) {
        this.smtpAuthenticator = smtpAuthenticator;
    }

    public void setSmtpHost(final String smtpHost) {
        this.smtpHost = smtpHost;
    }
}
