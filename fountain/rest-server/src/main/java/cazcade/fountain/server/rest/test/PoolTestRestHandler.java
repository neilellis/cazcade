package cazcade.fountain.server.rest.test;

import cazcade.common.Logger;
import cazcade.fountain.server.rest.RestHandler;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDEntityFactory;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Neil Ellis
 */

public class PoolTestRestHandler implements RestHandler {

    private static final Logger log = Logger.getLogger(PoolTestRestHandler.class);

    private LSDEntityFactory lsdEntityFactory;

    public LSDEntity update(LiquidUUID poolId, LSDEntity lsdEntity, Map<String, String[]> parameters) {
        log.debug("Update method called.");
        return lsdEntityFactory.createFromServletProperties(parameters);
    }

    public LSDEntity create(LSDEntity lsdEntity, Map<String, String[]> parameters) {
        log.debug("Create method called with url of. " + parameters.get("url")[0]);
        return lsdEntityFactory.createFromServletProperties(parameters);
    }

    public LSDEntity get(Map<String, String[]> parameters) {
        final String url = parameters.get("url")[0];
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("test.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        final HashMap propMap = new HashMap(props);
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("liquid-spring-config.xml");
        log.debug("Get method called with url of. " + url);
        return LSDSimpleEntity.createFromProperties(propMap);
    }

    public void setLsdFactory(LSDEntityFactory lsdEntityFactory) {
        this.lsdEntityFactory = lsdEntityFactory;
    }

    public LSDEntityFactory getLsdFactory() {
        return lsdEntityFactory;
    }
}