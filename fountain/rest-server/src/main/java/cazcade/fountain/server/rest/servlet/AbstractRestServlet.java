package cazcade.fountain.server.rest.servlet;

import cazcade.common.Logger;
import cazcade.fountain.common.error.CazcadeException;
import cazcade.fountain.server.rest.RestContext;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractRestServlet extends HttpServlet {
    @Nonnull
    private final static Logger log = Logger.getLogger(AbstractRestServlet.class);


    protected ClassPathXmlApplicationContext applicationContext;

    public AbstractRestServlet() {
        try {
            applicationContext = new ClassPathXmlApplicationContext("classpath:rest-server-spring-config.xml");
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public void destroy() {
        applicationContext.destroy();
        super.destroy();
    }

    @Override
    public void service(@Nonnull HttpServletRequest req, @Nonnull HttpServletResponse resp) throws ServletException, IOException {
        log.session(req.getPathInfo());
        String sessionId = req.getParameter("_session");
        log.addContext(req.getPathInfo());
        log.addContext(req.getQueryString());
        log.setSession(sessionId, req.getUserPrincipal() == null ? null : req.getUserPrincipal().getName());
        try {
            log.debug("Handling {0} request for URL {1}", req.getMethod(), req.getPathInfo());
            RestContext.clearContext();
            if (req.getUserPrincipal() != null) {
                RestContext.getContext().setCredentials(new LiquidSessionIdentifier(req.getUserPrincipal().getName()));
            } else {
                RestContext.getContext().setCredentials(new LiquidSessionIdentifier(null, null));
            }
            if (sessionId != null) {
                RestContext.getContext().getCredentials().setSession(sessionId);
            }
            String pathWithQuery = req.getPathInfo();
            final String[] pathAndQuery = pathWithQuery.split("\\?");
            String path = pathAndQuery[0];

            //The format parameter determines how we parse requests and provide responses.
            //The convention is to add .xyz to the end of the URL or explicitly add a
            //parameter _format. The default is XML
            String format = req.getParameter("_format");
            if (path.contains(".")) {
                String[] splitPath = path.split("\\.");
                path = splitPath[0];
                format = splitPath[1];
            }
            if (format == null) {
                format = "xml";
            }
            final String[] pathElements = path.substring(1).split("/");
            String serviceName = pathElements[0];
            List<LiquidUUID> uuids = new ArrayList<LiquidUUID>();
            String methodName = null;
            for (int i = 1; i < pathElements.length; i++) {
                if (pathElements[i].contains("-")) {
                    //No method name can contain a '-' symbol, so we take this to be a UUID
                    uuids.add(LiquidUUID.fromString(pathElements[i].toLowerCase()));
                } else if (methodName == null) {
                    //We have no method name so we use this as a method name.
                    methodName = pathElements[i];
                } else {
                    //Okay so we already have a method name so this is an error.
                    log.warn("Failed to parse rest path, unrecognized element {0}", pathElements[i]);
                    resp.sendError(400, "Failed to parse rest path, unrecognized element " + pathElements[i]);
                    return;
                }

            }

            //If no method was supplied we can use the default for the HTTP METHOD type.
            if (methodName == null) {
                if (req.getMethod().equals("GET")) {
                    methodName = "get";
                }
                if (req.getMethod().equals("DELETE")) {
                    methodName = "delete";
                }
                if (req.getMethod().equals("POST")) {
                    methodName = "create";
                }
                if (req.getMethod().equals("PUT")) {
                    methodName = "create";
                }


            }
            doRestCall(req, resp, pathWithQuery, serviceName, methodName, uuids, sessionId, format);
            log.debug("SUCCESS: Handled request for URL {0}?{1}", new Object[]{req.getPathInfo(), req.getQueryString()});
        } catch (CazcadeException e) {
            if (e.isClientException()) {
                log.warn(e, "{0}?{1} caused {2}", req.getRequestURL(), req.getQueryString(), e.getMessage());
            } else {
                log.error(e, "{0}?{1} caused {2}", req.getRequestURL(), req.getQueryString(), e.getMessage());
            }
            resp.sendError(500, "Server Error: " + e.getMessage() + ":" + ExceptionUtils.getFullStackTrace(e));
        } catch (Exception e) {
            log.error(e, "{0}?{1} caused {2}", req.getRequestURL(), req.getQueryString(), e.getMessage());
            resp.sendError(500, "Server Error: " + e.getMessage() + ":" + ExceptionUtils.getFullStackTrace(e));
//            LiquidResponseHelper.fromException(e, null);
        } finally {
            log.clearSession();
            log.clearContext();
        }
    }

    public abstract void doRestCall(HttpServletRequest req, HttpServletResponse resp, String pathWithQuery, String serviceName, String methodName, List<LiquidUUID> uuids, String sessionId, String format) throws Exception;

    public void doError(HttpServletRequest req, HttpServletResponse resp, String message) throws ServletException {
        throw new ServletException(message);
    }
}
