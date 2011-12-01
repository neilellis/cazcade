package cazcade.fountain.datastore.impl.email;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class EmailUtil {

    @Nonnull
    private static final org.jasypt.digest.StandardStringDigester digester = new org.jasypt.digest.StandardStringDigester();
    @Nonnull
    public static final String SALT = "MindPrecedesEverythingMindMattersMost";


    public static String getEmailHash(@Nonnull LSDEntity entity) {
        return digester.digest(encodeEmail(entity.getAttribute(LSDAttribute.EMAIL_ADDRESS)));
    }

    @Nonnull
    private static String encodeEmail(String email) {
        return email + SALT;
    }

    public static boolean confirmEmailHash(@Nullable String emailAddress, @Nullable String hash) {
        if (emailAddress == null || hash == null) {
            return false;
        }
        return digester.matches(encodeEmail(emailAddress), hash);
    }
}
