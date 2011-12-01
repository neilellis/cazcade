package cazcade.fountain.datastore.impl.email;

import javax.annotation.Nonnull;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Basic authenticator for use with smtp solution.
 */
public class SMTPAuthenticator extends Authenticator {

    private String username;
    private String password;


    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Nonnull
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}
