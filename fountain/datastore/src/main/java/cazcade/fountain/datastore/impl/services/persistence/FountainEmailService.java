/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.fountain.datastore.impl.email.EmailUtil;
import cazcade.fountain.datastore.impl.email.MailService;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * This service bridges the gap between a pure email service and Fountain's
 * domain.
 *
 * @author neilellis@cazcade.com
 */
public class FountainEmailService {
    @Autowired MailService mailService;


    public void send(@Nonnull final LSDTransferEntity user, @Nonnull final LSDTransferEntity alias, final String templateName, final String subject, final Object data, final boolean test) throws UnsupportedEncodingException {
        final Map<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("user", user.getCamelCaseMap());
        templateData.put("alias", alias.getCamelCaseMap());
        templateData.put("subject", subject);
        templateData.put("data", data);
        //We use the hash when confirming user actions
        templateData.put("hash", URLEncoder.encode(EmailUtil.getEmailHash(user), "utf8"));
        mailService.sendMailFromTemplate(templateName, subject, new String[]{
                user.getAttribute(LSDAttribute.EMAIL_ADDRESS)}, new String[0], new String[0], templateData, test);
    }

    public void sendChangePasswordRequest(@Nonnull final LSDTransferEntity user, final String hash) throws UnsupportedEncodingException {
        final String link = "http://boardcast.it/_password-change?username=" +
                            URLEncoder.encode(user.getAttribute(LSDAttribute.NAME), "utf8") +
                            "&hash=" + URLEncoder.encode(hash, "utf8");
        final Map<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("user", user.getCamelCaseMap());
        templateData.put("link", link);
        mailService.sendMailFromTemplate("password.vm", "Password change request", new String[]{
                user.getAttribute(LSDAttribute.EMAIL_ADDRESS)}, new String[0], new String[0], templateData, false);
    }

    public void sendRegistrationEmail(@Nonnull final LSDTransferEntity user) throws UnsupportedEncodingException {
        //Please click on this link to complete your registration
        final String link = "http://boardcast.it/_login-confirm-reg?user=" +
                            URLEncoder.encode(user.getAttribute(LSDAttribute.NAME), "utf8") +
                            "&hash=" + URLEncoder.encode(EmailUtil.getEmailHash(user), "utf8");
        final Map<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("user", user.getCamelCaseMap());
        templateData.put("link", link);
        mailService.sendMailFromTemplate("welcome.vm", "Welcome to Boardcast", new String[]{
                user.getAttribute(LSDAttribute.EMAIL_ADDRESS)}, new String[0], new String[0], templateData, false);
    }

    public void sendWelcomeEmail(final LSDBaseEntity user) {

    }
}
