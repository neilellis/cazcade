package cazcade.vortex.comms.datastore.server;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.messaging.continuations.ClientSessionMessageListener;
import cazcade.fountain.messaging.session.ClientSession;
import cazcade.fountain.messaging.session.ClientSessionManager;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.CreateSessionRequest;
import cazcade.liquid.api.request.CreateUserRequest;
import cazcade.liquid.api.request.RetrieveAliasRequest;
import cazcade.liquid.impl.UUIDFactory;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Properties;

/**
 * @author neilellis@cazcade.com
 */
public class LoginUtil {
    private final static Logger log = Logger.getLogger(LoginUtil.class);
    public static final String SESSION_KEY = "sessionId";
    public static final String ALIAS_KEY = "alias_entity";
    public static final String ALIAS_KEY_FOR_JSP = "alias";
    private static final String USERNAME_KEY = "username";


    public static final String APP_KEY = "123";

    public static LiquidSessionIdentifier login(ClientSessionManager clientSessionManager, FountainDataStore dataStore, LiquidURI alias, HttpSession session) throws Exception {
        LiquidMessage response = dataStore.process(new CreateSessionRequest(alias, new ClientApplicationIdentifier("GWT Client", APP_KEY, "UNKNOWN")));
        log.debug(LiquidXStreamFactory.getXstream().toXML(response));

        if (response.getResponse().isA(LSDDictionaryTypes.SESSION)) {
            LiquidSessionIdentifier serverSession = new LiquidSessionIdentifier(alias.getSubURI().getSubURI().asString(), response.getResponse().getID());
            createClientSession(clientSessionManager, serverSession, true);
            if (!serverSession.isAnon()) {
                placeServerSessionInHttpSession(dataStore, session, serverSession);
            }
            return serverSession;
        } else {
            log.error("{0}", response.getResponse().asFreeText());
            throw new RuntimeException("Unexpected result " + response.getResponse().getTypeDef());
        }
    }

    public static void placeServerSessionInHttpSession(FountainDataStore dataStore, HttpSession session, LiquidSessionIdentifier serverSession) {
        final LSDEntity aliasEntity;
        try {
            aliasEntity = dataStore.process(new RetrieveAliasRequest(serverSession, serverSession.getAliasURL())).getResponse();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }
        session.setAttribute(SESSION_KEY, serverSession);
        session.setAttribute(USERNAME_KEY, serverSession.getName());
        session.setAttribute(ALIAS_KEY, aliasEntity);
        session.setAttribute(ALIAS_KEY_FOR_JSP, aliasEntity.getCamelCaseMap());
    }

    public static ClientSession createClientSession(ClientSessionManager clientSessionManager, LiquidSessionIdentifier identity, boolean create) {
        ClientSession clientSession;
        if (!clientSessionManager.hasSession(identity.getSession().toString()) && create) {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
            PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
            Properties properties = new Properties();
            properties.setProperty("username", identity.getName());
            properties.setProperty("client.device", "web");
            properties.setProperty("client.type", "web");
            properties.setProperty("session.id", identity.getSession().toString());
            properties.setProperty("alias.url", identity.getAlias().toString());
            properties.setProperty("user.url", identity.getUserURL().toString());
            configurer.setProperties(properties);
            configurer.setLocation(new ClassPathResource("rabbit.properties"));
            context.addBeanFactoryPostProcessor(configurer);
            context.setConfigLocation("classpath:messaging-session-context.xml");
            context.refresh();

            //The session manager looks after spring contexts and expires them.
            clientSession = new ClientSession(context, new Date());
            ClientSessionMessageListener listener = (ClientSessionMessageListener) context.getBean("clientSessionMessageListener");
            listener.setSessionManager(clientSessionManager);
            clientSessionManager.addSession(identity.getSession().toString(), clientSession);
        } else {
            clientSession = clientSessionManager.getSession(identity.getSession().toString());
        }
        return clientSession;
    }

    public static LSDEntity register(HttpSession session, FountainDataStore theDataStore, String fullname, String username, String password, String emailAddress, boolean restricted) {
        LSDEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.USER, UUIDFactory.randomUUID());
        entity.setAttribute(LSDAttribute.FULL_NAME, fullname);
        entity.setAttribute(LSDAttribute.NAME, username);
        entity.setAttribute(LSDAttribute.PLAIN_PASSWORD, password);
        entity.setAttribute(LSDAttribute.EMAIL_ADDRESS, emailAddress);
        entity.setAttribute(LSDAttribute.SECURITY_RESTRICTED, restricted);
        try {
            LiquidMessage response = theDataStore.process(new CreateUserRequest(new LiquidSessionIdentifier(username), entity));
            if (response.getState() == LiquidMessageState.SUCCESS) {
                session.setAttribute(CommonConstants.NEW_USER_ATTRIBUTE, response.getResponse());
                session.setAttribute(CommonConstants.NEW_USER_PASSWORD_ATTRIBUTE, password);
                return response.getResponse();
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
