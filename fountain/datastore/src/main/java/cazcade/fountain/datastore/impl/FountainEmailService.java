package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.impl.email.EmailUtil;
import cazcade.fountain.datastore.impl.email.MailService;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * This service bridges the gap between a pure email service and Fountain's
 * domain.
 *
 * @author neilellis@cazcade.com
 */
public class FountainEmailService {

    @Autowired
    MailService mailService;


    public void send(LSDEntity user, LSDEntity alias, String templateName, String subject, Object data, boolean test) throws UnsupportedEncodingException {

        Map<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("user", user.getCamelCaseMap());
        templateData.put("alias", alias.getCamelCaseMap());
        templateData.put("subject", subject);
        templateData.put("data", data);
        //We use the hash when confirming user actions
        templateData.put("hash", java.net.URLEncoder.encode(EmailUtil.getEmailHash(user), "utf8"));
        mailService.sendMailFromTemplate(templateName, subject, new String[]{user.getAttribute(LSDAttribute.EMAIL_ADDRESS)},
                new String[0], new String[0], templateData, test);
    }


    public void sendRegistrationEmail(LSDEntity user) throws UnsupportedEncodingException {
        //Please click on this link to complete your registration
        String link = "http://boardcast.it/_login-confirm-reg?user=" +
                java.net.URLEncoder.encode(user.getAttribute(LSDAttribute.NAME), "utf8") +
                "&hash=" + java.net.URLEncoder.encode(EmailUtil.getEmailHash(user), "utf8");
        Map<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("user", user.getCamelCaseMap());
        templateData.put("link", link);
        mailService.sendMailFromTemplate("welcome.html", "Welcome to Boardcast", new String[]{user.getAttribute(LSDAttribute.EMAIL_ADDRESS)}, new String[0],
                new String[0], templateData, false);


    }

    public void sendWelcomeEmail(LSDEntity user) {

    }


    public void sendChangePasswordRequest(LSDEntity user, String hash) throws UnsupportedEncodingException {
        String link = "http://boardcast.it/_password-change?username=" +
                java.net.URLEncoder.encode(user.getAttribute(LSDAttribute.NAME), "utf8") +
                "&hash=" + java.net.URLEncoder.encode(hash, "utf8");
        Map<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("user", user.getCamelCaseMap());
        templateData.put("link", link);
        mailService.sendMailFromTemplate("password.vm", "Password change request", new String[]{user.getAttribute(LSDAttribute.EMAIL_ADDRESS)}, new String[0],
                new String[0], templateData, false);
    }
}
