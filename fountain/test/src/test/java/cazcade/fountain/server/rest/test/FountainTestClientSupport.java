/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.test;

import cazcade.common.Logger;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.impl.LSDMarshaler;
import cazcade.liquid.impl.LSDMarshallerFactory;
import cazcade.liquid.impl.LSDUnmarshaler;
import cazcade.liquid.impl.LSDUnmarshallerFactory;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.io.IOUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nonnull
    public static final AuthScope      AUTH_SCOPE;
    static final        LSDUnmarshaler unmarshaler;
    static final        LSDMarshaler   marshaler;
    static final        String         host;
    @Nonnull
    static final        String         rootUrl;
    static final        String         rootPath;
    static final        int            port;

    static {
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("client-spring-config.xml");
        unmarshaler = ((LSDUnmarshallerFactory) applicationContext.getBean("unmarshalerFactory")).getUnmarshalers().get("xml");
        marshaler = ((LSDMarshallerFactory) applicationContext.getBean("marshalerFactory")).getMarshalers().get("xml");
        host = System.getProperty("test.host", "localhost");
        port = Integer.parseInt(System.getProperty("test.port", "8088"));
        rootPath = System.getProperty("test.root.path", "/liquid/rest/1.0/");
        rootUrl = "http://" +
                  FountainTestClientSupport.host +
                  ":" +
                  FountainTestClientSupport.port +
                  FountainTestClientSupport.rootPath;
        AUTH_SCOPE = new AuthScope(host, port, AuthScope.ANY_REALM);
    }

    @Nonnull
    private static final Logger log = Logger.getLogger(FountainTestClientSupport.class);

    public static void initStream(@Nonnull final ClientSession clientSession) throws IOException {
        final Entity streamEntity = callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" +
                                                                      clientSession.getTestUsername() +
                                                                      "/stream");
        final String streamId = streamEntity.id().toString();
        putEntityToURL(clientSession, "pool/" + streamId + ".xml", createEntityFromPropertyfile("TwitterFeed"), "alias:cazcade:"
                                                                                                                + clientSession.getTestUsername());
        putEntityToURL(clientSession, "pool/" + streamId + ".xml", createEntityFromPropertyfile("RSSFeed1"), "alias:cazcade:"
                                                                                                             + clientSession.getTestUsername());
        putEntityToURL(clientSession, "pool/" + streamId + ".xml", createEntityFromPropertyfile("RSSFeed2"), "alias:cazcade:"
                                                                                                             + clientSession.getTestUsername());
        putEntityToURL(clientSession, "pool/" + streamId + ".xml", createEntityFromPropertyfile("RSSFeed3"), "alias:cazcade:"
                                                                                                             + clientSession.getTestUsername());
    }

    @Nonnull
    static SimpleEntity createEntityFromPropertyfile(final String entityName) {
        final Properties props = new Properties();
        try {
            props.load(FountainTestDataBuilderClient.class.getResourceAsStream("entities/" + entityName + ".properties"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        final HashMap<String, String> propMap = new HashMap(props);
        return SimpleEntity.fromProperties(propMap);
    }

    @Nonnull
    public static Entity putEntityToURL(@Nonnull final ClientSession clientSession, String postURL, final TransferEntity poolEntity, final String author) throws IOException {
        postURL = addParameterToURL("_session", clientSession.getSessionId(), addParameterToURL("author", author, postURL));
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        marshaler.marshal(poolEntity, byteArrayOutputStream);
        log.debug("PUT " + rootUrl + postURL);
        final PutMethod putmethod = new PutMethod(rootUrl + postURL);
        log.debug("Sending: +" + byteArrayOutputStream.toString());
        putmethod.setRequestBody(byteArrayOutputStream.toString());
        clientSession.getClient().executeMethod(putmethod);
        try {
            final InputStream bodyAsStream = putmethod.getResponseBodyAsStream();
            if (putmethod.getStatusCode() != 200) {
                throw new ClientTestStatusCodeException("Returned status code of "
                                                        + putmethod.getStatusCode()
                                                        + " : "
                                                        + putmethod.getStatusText());
            }
            final byte[] bytes = IOUtils.toByteArray(bodyAsStream);
            log.debug(new String(bytes));
            final Entity returnEntity = unmarshaler.unmarshal(new ByteArrayInputStream(bytes));
            IOUtils.closeQuietly(bodyAsStream);
            return returnEntity;
        } finally {
            putmethod.releaseConnection();
        }
    }

    public static TransferEntity callRESTApiWithGet(@Nonnull final ClientSession clientSession, @Nonnull final String url) throws IOException {
        final String geturl = rootUrl + addParameterToURL("_session", clientSession.getSessionId(), url);
        log.debug("GET " + geturl);
        final HttpMethod getMethod = new GetMethod(geturl);
        getMethod.setDoAuthentication(true);
        clientSession.getClient().executeMethod(getMethod);
        TransferEntity poolEntity;
        try {
            final InputStream bodyAsStream = getMethod.getResponseBodyAsStream();
            if (getMethod.getStatusCode() != 200) {
                throw new ClientTestStatusCodeException("Returned status code of "
                                                        + getMethod.getStatusCode()
                                                        + " : "
                                                        + getMethod.getStatusText());
            }
            final byte[] bytes = IOUtils.toByteArray(bodyAsStream);
            log.debug(new String(bytes));
            poolEntity = unmarshaler.unmarshal(new ByteArrayInputStream(bytes));
            IOUtils.closeQuietly(bodyAsStream);
        } finally {
            getMethod.releaseConnection();
        }
        return poolEntity;
    }

    @Nonnull
    static String addParameterToURL(final String key, @Nullable final String value, @Nonnull String postURL) {
        if (value != null) {
            if (postURL.contains("?")) {
                final String[] parts = postURL.split("\\?");
                postURL = parts[0] + "?" + key + "=" + value + "&" + parts[1];
            } else {
                postURL += "?" + key + "=" + value;
            }
        }
        return postURL;
    }

    @Nonnull
    public static Entity writeTestPool(@Nonnull final ClientSession clientSession, final String LiquidURI) throws IOException {
        final String entityId = convertLiquidURIToId(clientSession, LiquidURI);
        return putEntityToURL(clientSession, "pool/" + entityId + ".xml", createTestPoolObject(), "alias:cazcade:"
                                                                                                  + clientSession.getTestUsername());
    }

    @Nonnull
    public static TransferEntity createTestPoolObject() {
        final TransferEntity objectEntity = createEntityFromPropertyfile("TestObject1");
        objectEntity.$(Dictionary.NAME, ("TestObject" + Math.random()).replace('.', '_'));
        return objectEntity;
    }

    public static String convertLiquidURIToId(@Nonnull final ClientSession clientSession, final String uri) throws IOException {
        return callRESTApiWithGet(clientSession, "pool.xml?uri=" + uri).id().toString();
    }

    public static Entity getTestPoolObject(@Nonnull final ClientSession clientSession, final String LiquidURI, final String poolObjectName) throws IOException {
        final String poolId = convertLiquidURIToId(clientSession, LiquidURI);
        return callRESTApiWithGet(clientSession, "pool.xml?url=" + LiquidURI + "%23" + poolObjectName);
    }

    public static Entity writeThenDeleteTestPoolObject(@Nonnull final ClientSession clientSession, final String uri) throws IOException {
        final String poolId = convertLiquidURIToId(clientSession, uri);
        final Entity toDeleteEntity = writeThenGetTestPoolObject(clientSession, uri, poolId);
        callRESTApiWithGet(clientSession, "pool/delete.xml?uri=" + URLEncoder.encode(toDeleteEntity.uri().toString(), "utf8"));
        return toDeleteEntity;
    }

    public static Entity writeThenGetTestPoolObject(@Nonnull final ClientSession clientSession, final String uri, final String poolId) throws IOException {
        final TransferEntity poolObject = createTestPoolObject();
        putEntityToURL(clientSession, "pool/" + poolId + ".xml", poolObject, "alias:cazcade:" + clientSession.getTestUsername());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        return callRESTApiWithGet(clientSession, "pool.xml?url=" + uri + "%23" + poolObject.$(Dictionary.NAME));
    }

    public static Entity writeThenGetTestPoolObject(@Nonnull final ClientSession clientSession, final String LiquidURI) throws IOException {
        final String poolId = convertLiquidURIToId(clientSession, LiquidURI);
        return writeThenGetTestPoolObject(clientSession, LiquidURI, poolId);
    }

    public static void initHomePool(@Nonnull final ClientSession clientSession) throws IOException {
        final Entity poolEntity = callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/"
                                                                    + clientSession.getTestUsername());
        final String entityId = poolEntity.id().toString();
        putEntityToURL(clientSession, "pool/" + entityId + ".xml", createEntityFromPropertyfile("TestObject1"), "alias:cazcade:"
                                                                                                                + clientSession.getTestUsername());
        putEntityToURL(clientSession, "pool/" + entityId + ".xml", createEntityFromPropertyfile("TestObject2"), "alias:cazcade:"
                                                                                                                + clientSession.getTestUsername());
        putEntityToURL(clientSession, "pool/" + entityId + ".xml", createEntityFromPropertyfile("TestObject3"), "alias:cazcade:"
                                                                                                                + clientSession.getTestUsername());
        putEntityToURL(clientSession, "pool/" + entityId + ".xml", createEntityFromPropertyfile("TestObject4"), "alias:cazcade:"
                                                                                                                + clientSession.getTestUsername());
    }

    @Nonnull
    public static Thread listenToSession(final String sessionId, final String LiquidURI, final Credentials credentials) throws IOException {
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    final HttpClient listenClient = new HttpClient();
                    listenClient.getState().setCredentials(AUTH_SCOPE, credentials);
                    listenClient.getParams().setAuthenticationPreemptive(true);
                    String geturl = "http://" +
                                    FountainTestClientSupport.host +
                                    ":" +
                                    FountainTestClientSupport.port +
                                    "/liquid/notification/1.0/session/" +
                                    sessionId;
                    geturl = addParameterToURL("_session", sessionId, geturl);
                    log.debug("GET " + geturl);
                    final HttpMethod getMethod = new GetMethod(geturl);
                    getMethod.setDoAuthentication(true);
                    listenClient.executeMethod(getMethod);
                    Entity poolEntity;
                    try {
                        final InputStream bodyAsStream = getMethod.getResponseBodyAsStream();
                        log.debug(IOUtils.toString(bodyAsStream));
                        IOUtils.closeQuietly(bodyAsStream);
                    } finally {
                        getMethod.releaseConnection();
                    }
                } catch (IOException e) {
                    log.error(e);
                }
            }
        });
        thread.start();
        return thread;
    }
}
