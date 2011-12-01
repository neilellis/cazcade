package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Neil Ellis
 */

public class LSDEntityTest extends TestCase {

    public void test() throws IOException {
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("test.properties"));
        final HashMap<String, String> propMap = new HashMap(props);
//        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("liquid-spring-config.xml");
        LSDSimpleEntity entity = LSDSimpleEntity.createFromProperties(propMap);
//        ((LSDEntityFactory)applicationContext.getBean("LSDEntityFactory")).marshall(LSDFormat.plist, entity, System.out);
//        ((LSDEntityFactory)applicationContext.getBean("LSDEntityFactory")).marshall(LSDFormat.plist, entity, new FileOutputStream(System.getProperty("user.home")+"/Desktop/liquid_test.plist"));
//        LSDNode lsdNode = entity.asFormatIndependentTree();
        System.out.println(entity.dump());
        List<LSDEntity> lsdEntities = entity.getSubEntities(LSDAttribute.CHILD);
        for (LSDEntity lsdEntity : lsdEntities) {
            System.out.printf("****** %s ******%n", lsdEntity.dump());
            Map<String, String> stringMap = lsdEntity.getMap();
            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                System.out.printf("%s=%s%n", entry.getKey(), entry.getValue());
            }
        }
        assertEquals("600E8400-ABCD-1234-5678-446677889900", lsdEntities.get(0).getUUID().toString().toUpperCase());

    }

    public void testUnMarshall() {
        String entityString = " <entity>\n" +
                "      <icon>\n" +
                "        <url>http://mashable.com/wp-content/authors/Ben%20Parr-507.jpg</url>\n" +
                "      </icon>\n" +
                "      <text>\n" +
                "        <extended>Hello all</extended>\n" +
                "      </text>\n" +
                "      <modifiable>true</modifiable>\n" +
                "      <deletable>false</deletable>\n" +
                "      <image>\n" +
                "        <url>http://mashable.com/wp-content/authors/Ben%20Parr-507.jpg</url>\n" +
                "      </image>\n" +
                "      <type>Text.Message.Cazcade.Chat</type>\n" +
                "      <editable>false</editable>\n" +
                "      <version>1</version>\n" +
                "      <id>4D13571A-03E1-420C-99C7-623D60617FF6</id>\n" +
                "      <author>\n" +
                "        <id>f028e5de-9af2-4039-b0b6-d164e586c47d</id>\n" +
                "        <updated>1283564230830</updated>\n" +
                "        <name>neil</name>\n" +
                "        <type>Identity.Person.Alias</type>\n" +
                "        <published>1283564230830</published>\n" +
                "        <uri>alias:cazcade:neil</uri>\n" +
                "        <fn>Neil Ellis</fn>\n" +
                "        <network>cazcade</network>\n" +
                "        <version>1</version>\n" +
                "      </author>\n" +
                "      <selected>true</selected>\n" +
                "      <view2d>\n" +
                "        <y>7.969713426389326</y>\n" +
                "        <x>-14.25622360082787</x>\n" +
                "      </view2d>\n" +
                "      <updated>1283784835108</updated>\n" +
                "      <pinned>false</pinned>\n" +
                "      <name>pasted_by_test1283784678000</name>\n" +
                " \n" +
                " <view>\n" +
                "        <id>486ad203-39b5-4757-8dd9-3af35a6c5054</id>\n" +
                "        <view2d>\n" +
                "          <radius>16.332674106084767</radius>\n" +
                "          <z>0.0</z>\n" +
                "          <y>-72</y>\n" +
                "          <x>24</x>\n" +
                "        </view2d>\n" +
                "        <updated>1283565248614</updated>\n" +
                "        <type>System.View.PoolObjectView</type>\n" +
                "        <published>1283565248614</published>\n" +
                "        <uri>pool:///people/neil/public#object4743170470225689522:view</uri>\n" +
                "        <version>1</version>\n" +
                "      </view>\n" +
                "      <published>1283565248612</published>\n" +
                "    </entity>";
        final LSDSimpleEntity entity = (LSDSimpleEntity) LiquidXStreamFactory.getXstream().fromXML(entityString);
        entity.asMapForPersistence(false, true);
        final String entityReSerialized = LiquidXStreamFactory.getXstream().toXML(entity);
        final LSDSimpleEntity finalEntity = (LSDSimpleEntity) LiquidXStreamFactory.getXstream().fromXML(entityReSerialized);
        finalEntity.asMapForPersistence(false, true);
    }

}