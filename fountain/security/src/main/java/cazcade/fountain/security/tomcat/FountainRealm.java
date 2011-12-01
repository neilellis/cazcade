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
    private final static Logger log = Logger.getLogger(FountainRealm.class);

    @Nonnull
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public FountainRealm() throws Exception {
        super();
    }

    private Container container;

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @Nonnull
    public String getInfo() {
        return "Fountain Data Store Security Realm";
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    @Nullable
    public Principal authenticate(@Nonnull String username, String password) {
        try {
            return doAuthentication(username, password);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Nonnull
    public Principal authenticate(String s, byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public Principal authenticate(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public Principal authenticate(X509Certificate[] x509Certificates) {
        throw new UnsupportedOperationException();
    }

    public void backgroundProcess() {
    }

    @Nonnull
    public SecurityConstraint[] findSecurityConstraints(Request request, Context context) {
        return new SecurityConstraint[0];
    }

    public boolean hasResourcePermission(@Nonnull Request request, Response response, SecurityConstraint[] securityConstraints, Context context) throws IOException {
        if (request.getPathInfo() == null) {
            return false;
        }
        log.debug("hasResourcePermission() - request.getPathInfo() " + request.getPathInfo());
        if (request.getPrincipal().getName() == null || request.getPrincipal().getName().equals("anon")) {
            return ((request.getPathInfo().matches("/user/create[\\.a-z]*") && request.getMethod().equals("GET")) || (request.getPathInfo().matches("/user[\\.a-z]*") && request.getMethod().equals("POST")));
        } else {
            return true;
        }
    }

    public boolean hasRole(@Nonnull Principal principal, @Nonnull String role) {
        log.debug("hasRole(" + role + ")");
        if (principal.getName().equals("anon")) {
            return role.equals("anon");
        }
        return true;
    }

    public boolean hasUserDataPermission(Request request, Response response, SecurityConstraint[] securityConstraints) throws IOException {
        return true;
    }


    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }
}
