package cazcade.fountain.datastore.impl.email;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

/**
 * @author neilellis@cazcade.com
 */
public class EmailUtil {

    private static final org.jasypt.digest.StandardStringDigester digester = new org.jasypt.digest.StandardStringDigester();
    public static final String SALT = "MindPrecedesEverythingMindMattersMost";


    public static String getEmailHash(LSDEntity entity) {
        return digester.digest(encodeEmail(entity.getAttribute(LSDAttribute.EMAIL_ADDRESS)));
    }

    private static String encodeEmail(String email) {
        return email + SALT;
    }

    public static boolean confirmEmailHash(String emailAddress, String hash) {
        if (emailAddress == null || hash == null) {
            return false;
        }
        return digester.matches(encodeEmail(emailAddress), hash);
    }
}
