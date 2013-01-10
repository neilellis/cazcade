/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.email;

import java.util.Map;

/**
 * Mail service for constructing and sending e-mails.
 */
public interface MailService {
    /**
     * Simple method for sending a mail based on a template.
     *
     * @param templateIdentifier The identifier of the template.
     * @param subject            the subject of the message.
     * @param to                 Who the message is to.
     * @param cc                 Who to CC on the message.
     * @param bcc                Who to BCC on the message.
     * @param templateParameters Any parameters to be passed into the template.
     * @param test
     */
    void sendMailFromTemplate(String templateIdentifier, String subject, String[] to, String[] cc, String[] bcc, Map<String, Object> templateParameters, boolean test);
}
