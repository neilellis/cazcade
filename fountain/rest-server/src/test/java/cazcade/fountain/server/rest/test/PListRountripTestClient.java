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

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("client-spring-config.xml");
        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        Credentials defaultcreds = new UsernamePasswordCredentials("neil", "neil");
        client.getState().setCredentials(new AuthScope("localhost", 8088, AuthScope.ANY_REALM), defaultcreds);
        HttpMethod getMethod = new GetMethod("http://localhost:8080/liquid/rest/1.0/pool?url=pool:///people/neil");
        getMethod.setDoAuthentication(true);
        client.executeMethod(getMethod);
        LSDEntity poolEntity;
        try {
            InputStream bodyAsStream = getMethod.getResponseBodyAsStream();
            LSDUnmarshallerFactory lsdUnmarshallerFactory = (LSDUnmarshallerFactory) applicationContext.getBean("unmarshalerFactory");
            LSDUnmarshaler unmarshaler = lsdUnmarshallerFactory.getUnmarshalers().get("xml");
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
        LSDMarshallerFactory lsdMarshalerFactory = (LSDMarshallerFactory) applicationContext.getBean("marshalerFactory");
        LSDMarshaler marshaler = lsdMarshalerFactory.getMarshalers().get("xml");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        marshaler.marshal(poolEntity, byteArrayOutputStream);
        String postURL = "http://localhost:8080/liquid/rest/1.0/pool/" + poolEntity.getUUID().toString();
        System.out.println("Calling " + postURL);
        PostMethod postMethod = new PostMethod(postURL);
        System.out.println("Sending: +" + byteArrayOutputStream.toString());
        postMethod.setRequestBody(byteArrayOutputStream.toString());
        client.executeMethod(postMethod);
        try {
            InputStream bodyAsStream = postMethod.getResponseBodyAsStream();
            IOUtils.copy(bodyAsStream, System.out);
            IOUtils.closeQuietly(bodyAsStream);
        } finally {
            getMethod.releaseConnection();
        }

    }
}
