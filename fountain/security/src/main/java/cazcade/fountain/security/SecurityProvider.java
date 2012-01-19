package cazcade.fountain.security;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
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
    private static final Logger log = Logger.getLogger(SecurityProvider.class);

    @Nonnull
    private static final LiquidSessionIdentifier ANON_IDENTITY = new LiquidSessionIdentifier("anon");

    private final FountainDataStore dataStore;

    public SecurityProvider(final FountainDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public SecurityProvider() throws Exception {
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath:datastore-client-spring-config.xml"
        );
        dataStore = (FountainDataStore) applicationContext.getBean("authDataStore");
        dataStore.start();
    }

    @Nullable
    public Principal doAuthentication(@Nonnull final String username, final String password) throws Exception {
        if ("anon".equals(username)) {
            return new LiquidPrincipal(ANON_IDENTITY.getName());
        }
        final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        try {
            final LSDBaseEntity lsdEntity = loadUserInternal(username);
            final String hashedPassword = lsdEntity.getAttribute(LSDAttribute.HASHED_AND_SALTED_PASSWORD);
            if (!lsdEntity.getBooleanAttribute(LSDAttribute.SECURITY_BLOCKED) && passwordEncryptor.checkPassword(password,
                                                                                                                 hashedPassword
                                                                                                                )) {
                return new LiquidPrincipal(new LiquidSessionIdentifier(username).getName());
            }
            else {
                return null;
            }
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public LSDBaseEntity loadUserInternal(@Nonnull final String username) throws Exception {
        final LiquidMessage message = dataStore.process(new RetrieveUserRequest(new LiquidSessionIdentifier(username),
                                                                                new LiquidURI(LiquidURIScheme.user, username), true
        )
                                                       );
        final LSDBaseEntity lsdEntity = message.getResponse();
        return lsdEntity;
    }
}
