package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Neil Ellis
 */

public class LSDXMLTest extends TestCase {
    public void test() throws IOException {
        final Properties props = new Properties();
        props.load(getClass().getResourceAsStream("test.properties"));
        final HashMap<String, String> propMap = new HashMap(props);
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("liquid-spring-config.xml");
        final LSDSimpleEntity entity = LSDSimpleEntity.createFromProperties(propMap);
//        ((LSDEntityFactory)applicationContext.getBean("LSDEntityFactory")).marshall(LSDFormat.plist, entity, System.out);
//        ((LSDEntityFactory)applicationContext.getBean("LSDEntityFactory")).marshall(LSDFormat.plist, entity, new FileOutputStream(System.getProperty("user.home")+"/Desktop/liquid_test.plist"));
//        LSDNode lsdNode = entity.asFormatIndependentTree();
        final String xmlEntity = LiquidXStreamFactory.getXstream().toXML(entity);


        System.out.println(xmlEntity);
        final LSDSimpleEntity convertedEntity = (LSDSimpleEntity) LiquidXStreamFactory.getXstream().fromXML(xmlEntity);
        final Map<String, String> convertedMap = convertedEntity.getMap();
        for (final Map.Entry<String, String> entry : convertedMap.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
        for (final Map.Entry<String, String> entry : propMap.entrySet()) {
            System.out.println(entry.getKey());
            assertEquals(entry.getValue(), convertedMap.get(entry.getKey()));
        }
    }
}