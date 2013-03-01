/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.security;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
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
    private static final Logger            log           = Logger.getLogger(SecurityProvider.class);
    @Nonnull
    private static final SessionIdentifier ANON_IDENTITY = new SessionIdentifier("anon");
    private final FountainDataStore dataStore;

    public SecurityProvider(final FountainDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public SecurityProvider() throws Exception {
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:datastore-client-spring-config.xml");
        dataStore = (FountainDataStore) applicationContext.getBean("authDataStore");
        dataStore.start();
    }

    @Nullable
    public Principal doAuthentication(@Nonnull final String username, @Nonnull final String password) throws Exception {
        if ("anon".equals(username)) {
            return new LiquidPrincipal(ANON_IDENTITY.name());
        }
        final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        try {
            final Entity lsdEntity = loadUserInternal(username);
            if (lsdEntity.has(Dictionary.HASHED_AND_SALTED_PASSWORD)) {
                final String hashedPassword = lsdEntity.$(Dictionary.HASHED_AND_SALTED_PASSWORD);
                if ((!lsdEntity.has(Dictionary.SECURITY_BLOCKED) || !lsdEntity.$bool(Dictionary.SECURITY_BLOCKED))
                    && passwordEncryptor.checkPassword(password, hashedPassword)) {
                    return new LiquidPrincipal(new SessionIdentifier(username).name());
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Nonnull public Entity loadUserInternal(@Nonnull final String username) throws Exception {
        final LiquidMessage message = dataStore.process(new RetrieveUserRequest(new SessionIdentifier(username), new LURI(LiquidURIScheme.user, username), true));
        return message.response();
    }
}
