package cazcade.fountain.server.rest.test;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.impl.LSDMarshaler;
import cazcade.liquid.impl.LSDMarshallerFactory;
import cazcade.liquid.impl.LSDUnmarshaler;
import cazcade.liquid.impl.LSDUnmarshallerFactory;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author neilelliz@cazcade.com
 */
public class PListRountripTestClient {

    public static void main(final String[] args) throws IOException {
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("client-spring-config.xml");
        final HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        final Credentials defaultcreds = new UsernamePasswordCredentials("neil", "neil");
        client.getState().setCredentials(new AuthScope("localhost", 8088, AuthScope.ANY_REALM), defaultcreds);
        final HttpMethod getMethod = new GetMethod("http://localhost:8080/liquid/rest/1.0/pool?url=pool:///people/neil");
        getMethod.setDoAuthentication(true);
        client.executeMethod(getMethod);
        LSDEntity poolEntity;
        try {
            final InputStream bodyAsStream = getMethod.getResponseBodyAsStream();
            final LSDUnmarshallerFactory lsdUnmarshallerFactory = (LSDUnmarshallerFactory) applicationContext.getBean("unmarshalerFactory");
            final LSDUnmarshaler unmarshaler = lsdUnmarshallerFactory.getUnmarshalers().get("xml");
            poolEntity = unmarshaler.unmarshal(bodyAsStream);
            IOUtils.closeQuietly(bodyAsStream);
        } finally {
            getMethod.releaseConnection();
        }
        Integer counter = 0;
        if (poolEntity.hasAttribute(LSDAttribute.TEST_COUNTER)) {
            counter = Integer.parseInt(poolEntity.getRawValue(LSDAttribute.TEST_COUNTER));
        }
        counter++;
        poolEntity.setAttribute(LSDAttribute.TEST_COUNTER, counter.toString());
        final LSDMarshallerFactory lsdMarshalerFactory = (LSDMarshallerFactory) applicationContext.getBean("marshalerFactory");
        final LSDMarshaler marshaler = lsdMarshalerFactory.getMarshalers().get("xml");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        marshaler.marshal(poolEntity, byteArrayOutputStream);
        final String postURL = "http://localhost:8080/liquid/rest/1.0/pool/" + poolEntity.getUUID().toString();
        System.out.println("Calling " + postURL);
        final PostMethod postMethod = new PostMethod(postURL);
        System.out.println("Sending: +" + byteArrayOutputStream.toString());
        postMethod.setRequestBody(byteArrayOutputStream.toString());
        client.executeMethod(postMethod);
        try {
            final InputStream bodyAsStream = postMethod.getResponseBodyAsStream();
            IOUtils.copy(bodyAsStream, System.out);
            IOUtils.closeQuietly(bodyAsStream);
        } finally {
            getMethod.releaseConnection();
        }

    }
}
