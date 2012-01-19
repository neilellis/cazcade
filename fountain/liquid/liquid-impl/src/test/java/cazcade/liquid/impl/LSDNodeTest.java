package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.*;
import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author Neil Ellis
 */

@SuppressWarnings({"UseOfSystemOutOrSystemErr", "IOResourceOpenedButNotSafelyClosed"})
public class LSDNodeTest extends TestCase {
    private LSDMarshallerFactory marshallerFactory;
    private LSDUnmarshallerFactory unmarshallerFactory;

    public void test() throws IOException {
        final Properties props = new Properties();
        props.load(getClass().getResourceAsStream("test.properties"));
        final Map<String, String> propMap = (HashMap<String, String>) new HashMap(props);
        final LSDSimpleEntity entity = LSDSimpleEntity.createFromProperties(propMap);
        final LSDNode lsdNode = entity.asFormatIndependentTree();

        final LSDSimpleEntity convertedEntity = LSDSimpleEntity.createFromNode(lsdNode);
        marshallerFactory.getMarshalers().get("plist").marshal(entity, System.out);
        marshallerFactory.getMarshalers().get("plist").marshal(entity, new FileOutputStream(System.getProperty("java.io.tmpdir") +
                                                                                            "/liquid_test.plist"
        )
                                                              );
        final Map<String, String> convertedMap = convertedEntity.getMap();
        for (final Map.Entry<String, String> entry : convertedMap.entrySet()) {
            System.out.println(entry.getKey() + '=' + entry.getValue());
        }
        for (final Map.Entry<String, String> entry : propMap.entrySet()) {
            System.out.println(entry.getKey());
            assertEquals(entry.getValue(), convertedMap.get(entry.getKey()));
        }
    }

    public void testArrayMarshalling() throws IOException {
        final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
        entity.setValues(LSDAttribute.valueOf("x.test_with_underscore"), Arrays.asList("1", "2", "3"));
        System.err.println(entity);
        final LSDNode lsdNode = entity.asFormatIndependentTree();
        System.err.println(lsdNode);
        final List<LSDNode> children = lsdNode.getChildren().get(1).getChildren();
        for (final LSDNode child : children) {
            System.out.println(child.getName());
            final List<LSDNode> subChildren = child.getChildren();
            for (final LSDNode subChild : subChildren) {
                System.out.println('+' + subChild.getName());
            }
        }
        assertEquals("3", lsdNode.getChildren().get(1).getChildren().get(0).getChildren().get(2).getLeafValue());
        final LSDSimpleEntity convertedEntity = LSDSimpleEntity.createFromNode(lsdNode);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        marshallerFactory.getMarshalers().get("xml").marshal(entity, byteArrayOutputStream);
        System.err.println(new String(byteArrayOutputStream.toByteArray(), "utf8"));
        final LSDBaseEntity unmarshalledEntity = unmarshallerFactory.getUnmarshalers().get("xml").unmarshal(
                new ByteArrayInputStream(byteArrayOutputStream.toByteArray())
                                                                                                           );
        final List<String> values = unmarshalledEntity.getAttributeAsList(LSDAttribute.valueOf("x.test_with_underscore"));
        assertEquals("1", values.get(0));
        assertEquals("2", values.get(1));
        assertEquals("3", values.get(2));
        assertEquals(3, values.size());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("liquid-spring-config.xml");
        marshallerFactory = (LSDMarshallerFactory) applicationContext.getBean("marshalerFactory");
        unmarshallerFactory = (LSDUnmarshallerFactory) applicationContext.getBean("unmarshalerFactory");
    }
}
