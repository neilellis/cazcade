package cazcade.fountain.datastore.impl.email;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import org.jasypt.digest.StandardStringDigester;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class EmailUtil {

    @Nonnull
    private static final StandardStringDigester digester = new StandardStringDigester();
    @Nonnull
    public static final String SALT = "MindPrecedesEverythingMindMattersMost";


    public static String getEmailHash(@Nonnull final LSDEntity entity) {
        return digester.digest(encodeEmail(entity.getAttribute(LSDAttribute.EMAIL_ADDRESS)));
    }

    @Nonnull
    private static String encodeEmail(final String email) {
        return email + SALT;
    }

    public static boolean confirmEmailHash(@Nullable final String emailAddress, @Nullable final String hash) {
        if (emailAddress == null || hash == null) {
            return false;
        }
        return digester.matches(encodeEmail(emailAddress), hash);
    }
}
