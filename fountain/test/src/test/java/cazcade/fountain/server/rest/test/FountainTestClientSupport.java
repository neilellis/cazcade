package cazcade.fountain.server.rest.test;

import cazcade.common.Logger;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.impl.LSDMarshallerFactory;
import cazcade.liquid.impl.LSDUnmarshallerFactory;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.io.IOUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author neilelliz@cazcade.com
 */
public class FountainTestClientSupport {
    private final static Logger log = Logger.getLogger(FountainTestClientSupport.class);

    static LSDUnmarshaler unmarshaler;
    static LSDMarshaler marshaler;
    static String host;
    static int port = 80;
    public static final AuthScope AUTH_SCOPE;
    static String rootUrl;
    static String rootPath;

    static {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("client-spring-config.xml");
        unmarshaler = ((LSDUnmarshallerFactory) applicationContext.getBean("unmarshalerFactory")).getUnmarshalers().get("xml");
        marshaler = ((LSDMarshallerFactory) applicationContext.getBean("marshalerFactory")).getMarshalers().get("xml");
        host = System.getProperty("test.host", "localhost");
        port = Integer.parseInt(System.getProperty("test.port", "8088"));
        rootPath = System.getProperty("test.root.path", "/liquid/rest/1.0/");
        rootUrl = "http://" + FountainTestClientSupport.host + ":" + FountainTestClientSupport.port + FountainTestClientSupport.rootPath;
        AUTH_SCOPE = new AuthScope(host, port, AuthScope.ANY_REALM);
    }

    static LSDSimpleEntity createEntityFromPropertyfile(String entityName) {
        Properties props = new Properties();
        try {
            props.load(FountainTestDataBuilderClient.class.getResourceAsStream("entities/" + entityName + ".properties"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        final HashMap<String, String> propMap = new HashMap(props);
        return LSDSimpleEntity.createFromProperties(propMap);
    }

    public static LSDEntity postEntityToURL(ClientSession clientSession, String postURL, LSDEntity poolEntity, String author) throws IOException {
        postURL = addParameterToURL("_session", clientSession.getSessionId(), addParameterToURL("author", author, postURL));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        marshaler.marshal(poolEntity, byteArrayOutputStream);
        log.debug("POST " + rootUrl + postURL);
        PutMethod putmethod = new PutMethod(rootUrl + postURL);
        log.debug("Sending: +" + byteArrayOutputStream.toString());
        putmethod.setRequestBody(byteArrayOutputStream.toString());
        clientSession.getClient().executeMethod(putmethod);
        try {
            InputStream bodyAsStream = putmethod.getResponseBodyAsStream();
            if (putmethod.getStatusCode() != 200) {
                throw new ClientTestStatusCodeException("Returned status code of " + putmethod.getStatusCode() + " : " + putmethod.getStatusText());
            }
            byte[] bytes = IOUtils.toByteArray(bodyAsStream);
            log.debug(new String(bytes));
            LSDEntity returnEntity = unmarshaler.unmarshal(new ByteArrayInputStream(bytes));
            IOUtils.closeQuietly(bodyAsStream);
            return returnEntity;
        } finally {
            putmethod.releaseConnection();
        }
    }

    static String addParameterToURL(String key, String value, String postURL) {
        if (value != null) {
            if (postURL.contains("?")) {
                String[] parts = postURL.split("\\?");
                postURL = parts[0]+"?" + key + "=" + value+"&"+parts[1];
            } else {
                postURL += "?" + key + "=" + value;
            }
        }
        return postURL;
    }

    public static LSDEntity callRESTApiWithGet(ClientSession clientSession, String url) throws IOException {
        String geturl = rootUrl + addParameterToURL("_session", clientSession.getSessionId(), url);
        log.debug("GET " + geturl);
        HttpMethod getMethod = new GetMethod(geturl);
        getMethod.setDoAuthentication(true);
        clientSession.getClient().executeMethod(getMethod);
        LSDEntity poolEntity;
        try {
            InputStream bodyAsStream = getMethod.getResponseBodyAsStream();
            if (getMethod.getStatusCode() != 200) {
                throw new ClientTestStatusCodeException("Returned status code of " + getMethod.getStatusCode() + " : " + getMethod.getStatusText());
            }
            byte[] bytes = IOUtils.toByteArray(bodyAsStream);
            log.debug(new String(bytes));
            poolEntity = unmarshaler.unmarshal(new ByteArrayInputStream(bytes));
            IOUtils.closeQuietly(bodyAsStream);
        } finally {
            getMethod.releaseConnection();
        }
        return poolEntity;
    }

    public static void initStream(ClientSession clientSession) throws IOException {
        LSDEntity streamEntity = callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" + clientSession.getTestUsername() + "/stream");
        String streamId = streamEntity.getID().toString();
        postEntityToURL(clientSession, "pool/" + streamId + ".xml", createEntityFromPropertyfile("TwitterFeed"), "alias:cazcade:" + clientSession.getTestUsername());
        postEntityToURL(clientSession, "pool/" + streamId + ".xml", createEntityFromPropertyfile("RSSFeed1"), "alias:cazcade:" + clientSession.getTestUsername());
        postEntityToURL(clientSession, "pool/" + streamId + ".xml", createEntityFromPropertyfile("RSSFeed2"), "alias:cazcade:" + clientSession.getTestUsername());
        postEntityToURL(clientSession, "pool/" + streamId + ".xml", createEntityFromPropertyfile("RSSFeed3"), "alias:cazcade:" + clientSession.getTestUsername());
    }


    public static LSDEntity writeTestPool(ClientSession clientSession, String LiquidURI) throws IOException {
        String entityId = convertLiquidURIToId(clientSession, LiquidURI);
        return postEntityToURL(clientSession, "pool/" + entityId + ".xml", createTestPoolObject(), "alias:cazcade:" + clientSession.getTestUsername());
    }

    public static LSDEntity writeThenGetTestPoolObject(ClientSession clientSession, String LiquidURI) throws IOException {
        String poolId = convertLiquidURIToId(clientSession, LiquidURI);
        return writeThenGetTestPoolObject(clientSession, LiquidURI, poolId);
    }

    public static LSDEntity getTestPoolObject(ClientSession clientSession, String LiquidURI, String poolObjectName) throws IOException {
        String poolId = convertLiquidURIToId(clientSession, LiquidURI);
        return callRESTApiWithGet(clientSession, "pool.xml?url=" + LiquidURI + "%23" + poolObjectName);
    }


    public static LSDEntity createTestPoolObject() {
        LSDEntity objectEntity = createEntityFromPropertyfile("TestObject1");
        objectEntity.setAttribute(LSDAttribute.NAME, ("TestObject" + Math.random()).replace('.', '_'));
        return objectEntity;
    }

    public static LSDEntity writeThenDeleteTestPoolObject(ClientSession clientSession, String uri) throws IOException {
        String poolId = convertLiquidURIToId(clientSession, uri);
        LSDEntity toDeleteEntity = writeThenGetTestPoolObject(clientSession, uri, poolId);
        callRESTApiWithGet(clientSession, "pool/delete.xml?uri="+ URLEncoder.encode(toDeleteEntity.getURI().toString(), "utf8"));
        return toDeleteEntity;
    }

    public static String convertLiquidURIToId(ClientSession clientSession, String uri) throws IOException {
        return callRESTApiWithGet(clientSession, "pool.xml?uri=" + uri).getID().toString();
    }

    public static LSDEntity writeThenGetTestPoolObject(ClientSession clientSession, String uri, String poolId) throws IOException {
        LSDEntity poolObject = createTestPoolObject();
        postEntityToURL(clientSession, "pool/" + poolId + ".xml", poolObject, "alias:cazcade:" + clientSession.getTestUsername());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        return callRESTApiWithGet(clientSession, "pool.xml?url=" + uri + "%23" + poolObject.getAttribute(LSDAttribute.NAME));
    }

    public static void initHomePool(ClientSession clientSession) throws IOException {
        LSDEntity poolEntity = callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" + clientSession.getTestUsername());
        String entityId = poolEntity.getID().toString();
        postEntityToURL(clientSession, "pool/" + entityId + ".xml", createEntityFromPropertyfile("TestObject1"), "alias:cazcade:" + clientSession.getTestUsername());
        postEntityToURL(clientSession, "pool/" + entityId + ".xml", createEntityFromPropertyfile("TestObject2"), "alias:cazcade:" + clientSession.getTestUsername());
        postEntityToURL(clientSession, "pool/" + entityId + ".xml", createEntityFromPropertyfile("TestObject3"), "alias:cazcade:" + clientSession.getTestUsername());
        postEntityToURL(clientSession, "pool/" + entityId + ".xml", createEntityFromPropertyfile("TestObject4"), "alias:cazcade:" + clientSession.getTestUsername());
    }

    public static Thread listenToSession(final String sessionId, final String LiquidURI, final Credentials credentials) throws IOException {
        Thread thread = new Thread(new Runnable() {

            public void run() {
                try {
                    HttpClient listenClient = new HttpClient();
                    listenClient.getState().setCredentials(AUTH_SCOPE, credentials);
                    listenClient.getParams().setAuthenticationPreemptive(true);
                    String geturl = "http://" + FountainTestClientSupport.host + ":" + FountainTestClientSupport.port + "/liquid/notification/1.0/session/" + sessionId;
                    geturl = addParameterToURL("_session", sessionId, geturl);
                    log.debug("GET " + geturl);
                    HttpMethod getMethod = new GetMethod(geturl);
                    getMethod.setDoAuthentication(true);
                    listenClient.executeMethod(getMethod);
                    LSDEntity poolEntity;
                    try {
                        InputStream bodyAsStream = getMethod.getResponseBodyAsStream();
                        log.debug(IOUtils.toString(bodyAsStream));
                        IOUtils.closeQuietly(bodyAsStream);
                    } finally {
                        getMethod.releaseConnection();
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
        thread.start();
        return thread;
    }
}
