package cazcade.fountain.server.rest.servlet;

import cazcade.common.Logger;
import cazcade.fountain.common.error.NormalFlowException;
import cazcade.fountain.server.rest.RestHandler;
import cazcade.fountain.server.rest.RestHandlerFactory;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.impl.LSDMarshaler;
import cazcade.liquid.impl.LSDMarshallerFactory;
import cazcade.liquid.impl.LSDUnmarshallerFactory;
import com.thoughtworks.xstream.io.StreamException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Neil Ellis
 */
public class LiquidRestServlet extends AbstractRestServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(LiquidRestServlet.class);

    public LiquidRestServlet() {
        super();
    }

    @Override
    public void doRestCall(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp,
                           final String pathWithQuery, @Nonnull final String serviceName, @Nonnull final String methodName,
                           @Nonnull final List<LiquidUUID> uuids, @Nullable final String sessionId, final String format)
            throws Exception, InvocationTargetException, IllegalAccessException {
        if (sessionId == null) {
            if (("create".equals(methodName) || "get".equals(methodName)) && ("user".equals(serviceName) ||
                                                                              "alias".equals(serviceName
                                                                                            ) ||
                                                                              "session".equals(serviceName))) {
                //then all is well
            }
            else {
                resp.sendError(400, "Session must be supplied as a parameter (_session).");
                return;
            }
        }
        if (applicationContext == null) {
            resp.sendError(500, "Spring context was null, failed to initialize the server correctly.");
            log.error("No Spring context.");
            return;
        }
        final RestHandlerFactory handlerFactory = (RestHandlerFactory) applicationContext.getBean("restHandlerFactory");
        final RestHandler restHandler = handlerFactory.getHandlers().get(serviceName);
        if (restHandler == null) {
            log.warn("Unrecognized service{0}", serviceName);
            resp.sendError(400, "Unrecognized service" + serviceName);
            return;
        }
        final Class<? extends RestHandler> restHandlerClass = restHandler.getClass();
        final Method[] methods = restHandlerClass.getDeclaredMethods();
        final boolean handlerCalled = false;
        final LSDTransferEntity entity = buildLSDObject(format, req);
        log.addContext(entity);
        if (log.isDebugEnabled()) {
            log.debug("Entity passed in was {0}", entity.dump());
        }
        //All entities must be timestamped at source on the server side. We don't trust client applications to have the correct time on them!
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        //Look for methods which explicitly specify the HTTP method
        for (final Method method : methods) {
            if (matchMethod(req, resp, restHandler, uuids, methodName + req.getMethod(), method, format, entity)) {
                return;
            }
        }
        for (final Method method : methods) {
            if (matchMethod(req, resp, restHandler, uuids, methodName, method, format, entity)) {
                return;
            }
        }
        log.warn("Failed to find method on handler for  {0}", pathWithQuery);
        resp.sendError(400, "Failed to find method on handler for  " + pathWithQuery);
    }

    @Nonnull
    private LSDTransferEntity buildLSDObject(final String format, @Nonnull final HttpServletRequest req) throws IOException {
        final String method = req.getMethod();
        if ("PUT".equals(method)) {
            return ((LSDUnmarshallerFactory) applicationContext.getBean("unmarshalerFactory")).getUnmarshalers()
                                                                                              .get(format)
                                                                                              .unmarshal(req.getInputStream());
        }
        else {
            final Map parameters = req.getParameterMap();
            return ((LSDEntityFactory) applicationContext.getBean("LSDFactory")).createFromServletProperties(parameters);
        }
    }

    private boolean matchMethod(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp,
                                final RestHandler restHandler, @Nonnull final List<LiquidUUID> uuids, final String methodName,
                                @Nonnull final Method method, final String format, final LSDBaseEntity lsdEntity)
            throws Exception, InvocationTargetException, IllegalAccessException {
        final List<Object> arguments = new ArrayList<Object>();
        if (Modifier.isPublic(method.getModifiers())) {
            if (method.getName().equals(methodName)) {
                int pos = 0;
                final Class<?>[] methodParamTypes = method.getParameterTypes();
                if (!uuids.isEmpty()) {
                    for (int i = 0; i < uuids.size(); i++) {
                        if (i >= methodParamTypes.length || !methodParamTypes[i].equals(LiquidUUID.class)) {
                            log.debug("UUID match failed on {0}, with {1} UUIDs .", method.getName(), uuids.size());
                            return false;
                        }
                        else {
                            arguments.add(uuids.get(i));
                        }
                        pos++;
                    }
                }
                log.debug("SUCCESS UUID match succeeded on {0}, with {1} UUIDs .", method.getName(), uuids.size());
                if (arguments.size() < methodParamTypes.length && methodParamTypes[pos].equals(LSDTransferEntity.class)) {
                    arguments.add(lsdEntity);
                    pos++;
                }
                if (arguments.size() < methodParamTypes.length && methodParamTypes[pos].equals(Map.class)) {
                    arguments.add(extractRestParameters(req.getParameterMap()));
                    pos++;
                }
                if (arguments.size() < methodParamTypes.length && methodParamTypes[pos].equals(String.class)) {
                    arguments.add(req.getMethod());
                    pos++;
                }
                if (arguments.size() < methodParamTypes.length) {
                    log.debug("pos was {0} params were {1}", pos, methodParamTypes.length);
                    return false;
                }
                invoke(resp, restHandler, method, format, arguments);
                return true;
            }
            else {
                log.info("Did not match method {0} to {1}", methodName, method.getName());
                return false;
            }
        }
        else {
            log.info("Method " + methodName + " is not public.");
            return false;
        }
    }

    private Map<String, String> extractRestParameters(final Map parameterMap) {
        return parameterMap;
    }

    private void invoke(@Nonnull final HttpServletResponse resp, final RestHandler restHandler, @Nonnull final Method method,
                        final String format, @Nonnull final List<Object> arguments) throws Exception {
        log.debug("Invoking {0}{1}", method.getName(), arguments.toArray());
        Object result = null;
        try {
            result = method.invoke(restHandler, arguments.toArray());
        } catch (InvocationTargetException ite) {
            final Throwable targetException = ite.getTargetException();
            final String targetMessage = targetException.getMessage();
            if (targetException instanceof NormalFlowException) {
                log.warn(targetException, targetMessage);
                return;
            }
            else if (targetException instanceof LSDValidationException) {
                log.warn(targetException, targetMessage);
                resp.sendError(400, targetMessage);
                return;
            }
            else if (targetException instanceof Exception) {
                throw (Exception) targetException;
            }
        }
        log.debug("SUCCESS invoked.");
        if (method.getReturnType().equals(LiquidMessage.class)) {
            final LiquidMessage message = (LiquidMessage) result;
            if (message == null) {
                throw new NullPointerException("FAIL The method " + method.getName() + " returned a null message.");
            }
            final LSDTransferEntity responseEntity = message.getResponse();
            if (responseEntity == null) {
                throw new NullPointerException(
                        "FAIL The method " + method.getName() + " returned a message with a null response entity."
                );
            }
            doLSDResponse(responseEntity, format, resp);
        }
        else if (method.getReturnType().equals(LSDTransferEntity.class)) {
            final LSDTransferEntity entity = (LSDTransferEntity) result;
            if (entity == null) {
                throw new NullPointerException("FAIL The method " + method.getName() + " returned a null message.");
            }
            doLSDResponse(entity, format, resp);
        }
    }

    private void doLSDResponse(@Nonnull final LSDTransferEntity responseEntity, final String format,
                               @Nonnull final HttpServletResponse resp) throws IOException {
        //todo: separate this code out
        if (responseEntity.isA(LSDDictionaryTypes.RESOURCE_NOT_FOUND)) {
            resp.sendError(404, responseEntity.getAttribute(LSDAttribute.DESCRIPTION));
        }
        else if (responseEntity.isA(LSDDictionaryTypes.AUTHORIZATION_DENIAL)) {
            resp.sendError(403, responseEntity.getAttribute(LSDAttribute.DESCRIPTION));
        }
        else if (responseEntity.isA(LSDDictionaryTypes.AUTHORIZATION_NOT_REQUIRED)) {
            resp.sendError(400, responseEntity.getAttribute(LSDAttribute.DESCRIPTION));
        }
        else if (responseEntity.isA(LSDDictionaryTypes.AUTHORIZATION_INVALID)) {
            resp.sendError(400, responseEntity.getAttribute(LSDAttribute.DESCRIPTION));
        }

        final LSDMarshaler marshaler = ((LSDMarshallerFactory) applicationContext.getBean("marshalerFactory")).getMarshalers().get(
                format
                                                                                                                                  );
        resp.setContentType(marshaler.getMimeType());
        log.session(responseEntity.dump());
        try {
            marshaler.marshal(responseEntity, resp.getOutputStream());
        } catch (StreamException e) {
            //Probably the client went away, so leave a warn in the logs and leave it at that.
            log.warn(e, "Failed to marshall response.");
        }
        log.debug("Marshalled.");
    }
}
