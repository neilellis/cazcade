package cazcade.fountain.security;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.RetrieveUserRequest;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.Principal;

/**
 * @author neilelliz@cazcade.com
 */
public class SecurityProvider {
    @Nonnull
    private final static Logger log = Logger.getLogger(SecurityProvider.class);

    private final FountainDataStore dataStore;

    @Nonnull
    private static final LiquidSessionIdentifier ANON_IDENTITY = new LiquidSessionIdentifier("anon");

    public SecurityProvider(FountainDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public SecurityProvider() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:datastore-client-spring-config.xml");
        dataStore = (FountainDataStore) applicationContext.getBean("authDataStore");
        dataStore.start();
    }

    @Nullable
    public Principal doAuthentication(@Nonnull String username, String password) throws Exception {
        if (username.equals("anon")) {
            return new LiquidPrincipal(ANON_IDENTITY.getName());

        }
        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        try {
            LSDEntity lsdEntity = loadUserInternal(username);
            String hashedPassword = lsdEntity.getAttribute(LSDAttribute.HASHED_AND_SALTED_PASSWORD);
            if (!lsdEntity.getBooleanAttribute(LSDAttribute.SECURITY_BLOCKED) && passwordEncryptor.checkPassword(password, hashedPassword)) {
                return new LiquidPrincipal(new LiquidSessionIdentifier(username).getName());

            } else {
                return null;
            }
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public LSDEntity loadUserInternal(@Nonnull String username) throws Exception {
        LiquidMessage message = dataStore.process(new RetrieveUserRequest(new LiquidSessionIdentifier(username), new LiquidURI(LiquidURIScheme.user, username), true));
        LSDEntity lsdEntity = message.getResponse();
        return lsdEntity;
    }
}
