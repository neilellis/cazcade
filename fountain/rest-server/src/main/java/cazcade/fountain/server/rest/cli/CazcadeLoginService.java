package cazcade.fountain.server.rest.cli;

import cazcade.common.Logger;
import cazcade.fountain.security.SecurityProvider;
import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.server.UserIdentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.Subject;
import java.security.Principal;

/**
 * @author neilelliz@cazcade.com
 */
class CazcadeLoginService implements LoginService {
    @Nonnull
    private final static Logger log = Logger.getLogger(CazcadeLoginService.class);


    @Nonnull
    private final SecurityProvider securityProvider;
    public IdentityService identityService;

    CazcadeLoginService() throws Exception {
        securityProvider = new SecurityProvider();
    }


    @Nonnull
    public String getName() {
        return "Liquid REST Api";
    }

    @Nullable
    public UserIdentity login(@Nonnull String user, @Nonnull Object password) {

        try {
            log.debug("user: " + user);
            Principal principal = securityProvider.doAuthentication(user, password.toString());
            if (principal != null) {
                return new DefaultUserIdentity(new Subject(), principal, new String[]{"restapi"});
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public boolean validate(UserIdentity userIdentity) {
        return true;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }
}
