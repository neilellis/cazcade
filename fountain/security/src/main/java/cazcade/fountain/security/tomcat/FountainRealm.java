/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.security.tomcat;

import cazcade.common.Logger;
import cazcade.fountain.security.SecurityProvider;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Realm;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.SecurityConstraint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;

/**
 * @author neilelliz@cazcade.com
 */
public class FountainRealm extends SecurityProvider implements Realm {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainRealm.class);

    @Nonnull
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private Container container;

    public FountainRealm() throws Exception {
        super();
    }

    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    @Nonnull
    public Principal authenticate(final String s, final String s1, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public Principal authenticate(@Nonnull final String username, final String password) {
        try {
            return doAuthentication(username, password);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @Nonnull
    public Principal authenticate(final String s, final byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public Principal authenticate(final X509Certificate[] x509Certificates) {
        throw new UnsupportedOperationException();
    }

    public void backgroundProcess() {
    }

    @Nonnull
    public SecurityConstraint[] findSecurityConstraints(final Request request, final Context context) {
        return new SecurityConstraint[0];
    }

    @Nonnull
    public String getInfo() {
        return "Fountain Data Store Security Realm";
    }

    public boolean hasResourcePermission(@Nonnull final Request request, final Response response, final SecurityConstraint[] securityConstraints, final Context context) throws IOException {
        if (request.getPathInfo() == null) {
            return false;
        }
        log.debug("hasResourcePermission() - request.getPathInfo() " + request.getPathInfo());
        if (request.getPrincipal().getName() == null || "anon".equals(request.getPrincipal().getName())) {
            return request.getPathInfo().matches("/user/create[\\.a-z]*") && "GET".equals(request.getMethod())
                   || request.getPathInfo().matches("/user[\\.a-z]*") && "POST".equals(request.getMethod());
        }
        else {
            return true;
        }
    }

    public boolean hasRole(@Nonnull final Principal principal, @Nonnull final String role) {
        log.debug("hasRole(" + role + ")");
        if ("anon".equals(principal.getName())) {
            return "anon".equals(role);
        }
        return true;
    }

    public boolean hasUserDataPermission(final Request request, final Response response, final SecurityConstraint[] securityConstraints) throws IOException {
        return true;
    }

    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(final Container container) {
        this.container = container;
    }
}
