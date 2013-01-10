/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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
    private static final Logger log = Logger.getLogger(AbstractRestServlet.class);


    protected static ClassPathXmlApplicationContext applicationContext;

    static {
        try {
            applicationContext = new ClassPathXmlApplicationContext("classpath:rest-server-spring-config.xml");
        } catch (Exception e) {
            log.error(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();
                applicationContext.destroy();
            }
        });
    }

    public AbstractRestServlet() {
        super();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public void doError(final HttpServletRequest req, final HttpServletResponse resp, final String message) throws ServletException {
        throw new ServletException(message);
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {

    }

    @Override
    public void service(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws ServletException, IOException {
        log.session(req.getPathInfo());
        final String sessionId = req.getParameter("_session");
        log.addContext(req.getPathInfo());
        log.addContext(req.getQueryString());
        log.setSession(sessionId, req.getUserPrincipal() == null ? null : req.getUserPrincipal().getName());
        try {
            log.debug("Handling {0} request for URL {1}", req.getMethod(), req.getPathInfo());
            RestContext.clearContext();
            if (req.getUserPrincipal() != null) {
                RestContext.getContext().setCredentials(new LiquidSessionIdentifier(req.getUserPrincipal().getName()));
            }
            else {
                RestContext.getContext().setCredentials(new LiquidSessionIdentifier(null, null));
            }
            if (sessionId != null) {
                RestContext.getContext().getCredentials().setSession(sessionId);
            }
            final String pathWithQuery = req.getPathInfo();
            final String[] pathAndQuery = pathWithQuery.split("\\?");
            String path = pathAndQuery[0];

            //The format parameter determines how we parse requests and provide responses.
            //The convention is to add .xyz to the end of the URL or explicitly add a
            //parameter _format. The default is XML
            String format = req.getParameter("_format");
            if (path.contains(".")) {
                final String[] splitPath = path.split("\\.");
                path = splitPath[0];
                format = splitPath[1];
            }
            if (format == null) {
                format = "xml";
            }
            final String[] pathElements = path.substring(1).split("/");
            final String serviceName = pathElements[0];
            final List<LiquidUUID> uuids = new ArrayList<LiquidUUID>();
            String methodName = null;
            for (int i = 1; i < pathElements.length; i++) {
                if (pathElements[i].contains("-")) {
                    //No method name can contain a '-' symbol, so we take this to be a UUID
                    uuids.add(LiquidUUID.fromString(pathElements[i].toLowerCase()));
                }
                else if (methodName == null) {
                    //We have no method name so we use this as a method name.
                    methodName = pathElements[i];
                }
                else {
                    //Okay so we already have a method name so this is an error.
                    log.warn("Failed to parse rest path, unrecognized element {0}", pathElements[i]);
                    resp.sendError(400, "Failed to parse rest path, unrecognized element " + pathElements[i]);
                    return;
                }
            }

            //If no method was supplied we can use the default for the HTTP METHOD type.
            if (methodName == null) {
                if ("GET".equals(req.getMethod())) {
                    methodName = "get";
                }
                if ("DELETE".equals(req.getMethod())) {
                    methodName = "delete";
                }
                if ("POST".equals(req.getMethod())) {
                    methodName = "create";
                }
                if ("PUT".equals(req.getMethod())) {
                    methodName = "create";
                }
            }
            doRestCall(req, resp, pathWithQuery, serviceName, methodName, uuids, sessionId, format);
            log.debug("SUCCESS: Handled request for URL {0}?{1}", new Object[]{req.getPathInfo(), req.getQueryString()});
        } catch (CazcadeException e) {
            if (e.isClientException()) {
                log.warn(e, "{0}?{1} caused {2}", req.getRequestURL(), req.getQueryString(), e.getMessage());
            }
            else {
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
}
