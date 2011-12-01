package cazcade.fountain.server.rest.servlet;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.fountain.datastore.api.AuthorizationStatus;
import cazcade.fountain.server.rest.RestContext;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Nonnull;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cazcade.common.CommonConstants.QUEUE_ATTRIBUTE;

/**
 * @author Neil Ellis
 */
public class LiquidNotificationServlet extends AbstractRestServlet {

    @Nonnull
    private static final Logger log = Logger.getLogger(LiquidNotificationServlet.class);

    private RabbitAdmin rabbitAdmin;
    private TopicExchange exchange;
    private RabbitTemplate rabbitTemplate;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        rabbitAdmin = (RabbitAdmin) applicationContext.getBean("rabbitAdmin");
        rabbitTemplate = (RabbitTemplate) applicationContext.getBean("rabbitTemplate");
        exchange = (TopicExchange) applicationContext.getBean("mainExchange");
    }

    @Override
    public void doRestCall(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp, final String pathWithQuery, final String serviceName, final String methodName, @Nonnull final List<LiquidUUID> uuids, final String sessionId, final String format) throws RuntimeException, ServletException, IOException {
        final LSDTransferEntity sessionStateEntity = LSDSimpleEntity.createEmpty();
        sessionStateEntity.setType(LSDDictionaryTypes.SESSION);
        final LiquidUUID sessionUUID = LiquidUUID.fromString(sessionId);
        sessionStateEntity.setID(sessionUUID);
        final Queue queue = getQueue(req);
        declareBindings(queue, RestContext.getContext().getCredentials());
        for (final LiquidUUID uuid : uuids) {
            authorize(resp, uuid);
            addLocation(queue, uuid);
        }
        collect(queue, resp);
    }


    @Nonnull
    public ArrayList<LiquidMessage> collect(final Queue queue, @Nonnull final HttpServletResponse response) {
        try {
            final ArrayList<LiquidMessage> result = new ArrayList<LiquidMessage>();
            LiquidMessage message;
            int count = 0;

            while (count == 0) {

                message = (LiquidMessage) rabbitTemplate.receiveAndConvert();

                if (message instanceof VisitPoolRequest && ((VisitPoolRequest) message).getSessionIdentifier().getSession().toString().equals(RestContext.getContext().getCredentials().getSession().toString())) {
                    log.debug("**** Pool visit, so now switching pools. ****");
                    handlePoolVisit(message, queue);

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

        }

    }


    private boolean authorize(@Nonnull final HttpServletResponse resp, final LiquidUUID uuid) {
        try {
            final AuthorizationService authorizationService = (AuthorizationService) applicationContext.getBean("authorizationService");
            final AuthorizationStatus authorizationStatus = authorizationService.authorize(RestContext.getContext().getCredentials(), uuid, LiquidPermission.EDIT);
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


    private Queue getQueue(@Nonnull final HttpServletRequest request) {
        final HttpSession session = request.getSession(true);
        Queue queue = (Queue) session.getAttribute(QUEUE_ATTRIBUTE);
        if (queue == null) {
            queue = rabbitAdmin.declareQueue();
            session.setAttribute(QUEUE_ATTRIBUTE, queue);
        }
        return queue;
    }

    private void declareBindings(final Queue queue, @Nonnull final LiquidSessionIdentifier identity) {
        rabbitAdmin.declareBinding(new Binding(queue, exchange, "session." + identity.getSession()));
        rabbitAdmin.declareBinding(new Binding(queue, exchange, "user." + identity.getUserURL()));
        rabbitAdmin.declareBinding(new Binding(queue, exchange, "alias." + identity.getAliasURL()));
    }

    private void handlePoolVisit(final Object messageObject, final Queue queue) throws IOException {
        //We have visited a pool so we now need to listen to events there.
        final VisitPoolRequest request = (VisitPoolRequest) messageObject;
        if (request.getState() == LiquidMessageState.SUCCESS) {
            log.debug("Switching pools....");
            final LiquidUUID uuid = request.getResponse().getUUID();
            addLocation(queue, uuid);
            final LiquidURI uri = request.getResponse().getURI();
            addLocation(queue, uri);
        }
    }

    private void addLocation(final Queue queue, final LiquidURI uri) {
        rabbitAdmin.declareBinding(new Binding(queue, exchange, "location." + uri));
    }

    private void addLocation(final Queue queue, final LiquidUUID uuid) {
        rabbitAdmin.declareBinding(new Binding(queue, exchange, "location." + uuid));
    }

}
