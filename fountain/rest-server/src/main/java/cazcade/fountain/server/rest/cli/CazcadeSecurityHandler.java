package cazcade.fountain.server.rest.cli;

import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.UserIdentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author neilelliz@cazcade.com
 */
class CazcadeSecurityHandler extends SecurityHandler {
    @Override
    protected boolean checkUserDataPermissions(final String pathInContext, final Request request, final Response response,
                                               final Object constraintInfo) throws IOException {
        return true;
    }

    @Override
    protected boolean checkWebResourcePermissions(final String pathInContext, final Request request, final Response response,
                                                  final Object constraintInfo, final UserIdentity userIdentity) throws IOException {
        return true;
    }

    @Override
    protected boolean isAuthMandatory(@Nonnull final Request request, final Response response, final Object constraintInfo) {
        return request.getPathInfo() != null
               && !(
                request.getPathInfo().matches("/user/create[\\.a-z]*") && "GET".equals(request.getMethod())
                || request.getPathInfo().matches("/alias[\\.a-z]*") && request.getQueryString().matches("uri=.*") && "GET".equals(
                        request.getMethod()
                                                                                                                                 )
                || request.getPathInfo().matches("/error/.*") ||
                request.getPathInfo().matches("/user[\\.a-z]*") && "POST".equals(request.getMethod())
        );
    }

    @Nullable
    @Override
    protected Object prepareConstraintInfo(final String pathInContext, final Request request) {
        return null;
    }
}
