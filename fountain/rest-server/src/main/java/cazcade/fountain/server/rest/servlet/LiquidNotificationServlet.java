/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.servlet;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.fountain.datastore.api.AuthorizationStatus;
import cazcade.fountain.messaging.FountainPubSub;
import cazcade.fountain.server.rest.RestContext;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;

import javax.annotation.Nonnull;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cazcade.common.CommonConstants.QUEUE_ATTRIBUTE;

/**
 * @author Neil Ellis
 */
public class LiquidNotificationServlet extends AbstractRestServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(LiquidNotificationServlet.class);
    private FountainPubSub pubSub;


    @Override
    public void doRestCall(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp, final String pathWithQuery, final String serviceName, final String methodName, @Nonnull final List<LiquidUUID> uuids, final String sessionId, final String format) throws RuntimeException, ServletException, IOException {
        final TransferEntity sessionStateEntity = SimpleEntity.create(Types.T_SESSION);
        final LiquidUUID sessionUUID = LiquidUUID.fromString(sessionId);
        sessionStateEntity.id(sessionUUID);
        final String queue = getQueue(req);
        for (final LiquidUUID uuid : uuids) {
            authorize(resp, uuid);
        }
        collect(queue, uuids, resp, RestContext.getContext().getCredentials());
    }

    private String getQueue(@Nonnull final HttpServletRequest request) {
        final HttpSession session = request.getSession(true);
        String queue = (String) session.getAttribute(QUEUE_ATTRIBUTE);
        if (queue == null) {
            queue = UUID.randomUUID().toString();
            session.setAttribute(QUEUE_ATTRIBUTE, queue);
        }
        return queue;
    }


    private boolean authorize(@Nonnull final HttpServletResponse resp, final LiquidUUID uuid) {
        try {
            final AuthorizationService authorizationService = (AuthorizationService) applicationContext.getBean("authorizationService");
            final AuthorizationStatus authorizationStatus = authorizationService.authorize(RestContext.getContext()
                                                                                                      .getCredentials(), uuid, Permission.P_EDIT);
            if (!(authorizationStatus == AuthorizationStatus.ACCEPTED)) {
                doAuthorizationError(resp);
                return false;
            }
        } catch (Exception e) {
            log.error(e);
            return false;
        }
        return true;
    }

    private void doAuthorizationError(@Nonnull final HttpServletResponse resp) throws IOException {
        resp.sendError(401, "You are not authorized to listen to notifications from this resource.");
    }


    @Nonnull
    public ArrayList<LiquidMessage> collect(final String queue, List<LiquidUUID> uuids, @Nonnull final HttpServletResponse response, SessionIdentifier identity) {
        final ArrayList<String> queues = new ArrayList<String>();
        for (LiquidUUID uuid : uuids) {
            queues.add("location." + uuid.toString());
        }
        queues.add("session." + identity.session());
        queues.add("user." + identity.userURL());
        queues.add("alias." + identity.aliasURI());
        FountainPubSub.Collector collector = pubSub.createCollector(queues);

        try {
            final ArrayList<LiquidMessage> result = new ArrayList<LiquidMessage>();
            LiquidMessage message;
            int count = 0;

            while (count == 0) {
                message = (LiquidMessage) collector.readSingle();

                if (message instanceof VisitPoolRequest && ((VisitPoolRequest) message).session()
                                                                                       .session()
                                                                                       .toString()
                                                                                       .equals(RestContext.getContext()
                                                                                                          .getCredentials()
                                                                                                          .session()
                                                                                                          .toString())) {
                    log.debug("**** Pool visit, so now switching pools. ****");
                    //We have visited a pool so we now need to listen to events there.
                    final VisitPoolRequest request = (VisitPoolRequest) message;
                    if (request.state() == MessageState.SUCCESS) {
                        log.debug("Switching pools....");
                        queues.add(request.response().id().toString());
                        queues.add(request.response().uri().toString());
                    }
                }
                response.getWriter().write(LiquidXStreamFactory.getXstream().toXML(message));

                if (message != null) {
                    result.add(message);
                    count++;
                    if (count == 0) {
                        Thread.sleep(1000);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error(e);
            return new ArrayList<LiquidMessage>();
        } finally {
            collector.close();
        }
    }


    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        pubSub = (FountainPubSub) applicationContext.getBean("pubSub");
    }
}
