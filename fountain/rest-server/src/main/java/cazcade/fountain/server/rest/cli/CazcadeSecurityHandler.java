package cazcade.fountain.server.rest.cli;

import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.UserIdentity;

import java.io.IOException;

/**
 * @author neilelliz@cazcade.com
 */
class CazcadeSecurityHandler extends SecurityHandler {
    @Override
    protected Object prepareConstraintInfo(String pathInContext, Request request) {
        return null;
    }

    @Override
    protected boolean checkUserDataPermissions(String pathInContext, Request request, Response response, Object constraintInfo) throws IOException {
        return true;
    }

    @Override
    protected boolean isAuthMandatory(Request request, Response response, Object constraintInfo) {
        return request.getPathInfo() != null
                && !(
                (request.getPathInfo().matches("/user/create[\\.a-z]*") && request.getMethod().equals("GET"))
                        || (request.getPathInfo().matches("/alias[\\.a-z]*") && request.getQueryString().matches("uri=.*") && request.getMethod().equals("GET"))
                        || request.getPathInfo().matches("/error/.*") ||
                        (request.getPathInfo().matches("/user[\\.a-z]*") && request.getMethod().equals("POST"))
        );
    }

    @Override
    protected boolean checkWebResourcePermissions(String pathInContext, Request request, Response response, Object constraintInfo, UserIdentity userIdentity) throws IOException {
        return true;
    }
}
