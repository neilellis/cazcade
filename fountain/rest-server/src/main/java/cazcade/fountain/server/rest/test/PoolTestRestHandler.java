/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.test;

import cazcade.common.Logger;
import cazcade.fountain.server.rest.RestHandler;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.EntityFactory;
import cazcade.liquid.api.lsd.SimpleEntity;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Neil Ellis
 */

public class PoolTestRestHandler implements RestHandler {
    @Nonnull
    private static final Logger log = Logger.getLogger(PoolTestRestHandler.class);

    private EntityFactory entityFactory;

    @Nonnull
    public Entity create(final Entity lsdEntity, @Nonnull final Map<String, String[]> parameters) {
        log.debug("Create method called with url of. " + parameters.get("url")[0]);
        return entityFactory.createFromServletProperties(parameters);
    }

    @Nonnull
    public Entity get(@Nonnull final Map<String, String[]> parameters) {
        final String url = parameters.get("url")[0];
        final Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("test.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        final HashMap propMap = new HashMap(props);
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("liquid-spring-config.xml");
        log.debug("Get method called with url of. " + url);
        return SimpleEntity.createFromProperties(propMap);
    }

    public EntityFactory getLsdFactory() {
        return entityFactory;
    }

    public void setLsdFactory(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Nonnull
    public Entity update(final LiquidUUID poolId, final Entity lsdEntity, final Map<String, String[]> parameters) {
        log.debug("Update method called.");
        return entityFactory.createFromServletProperties(parameters);
    }
}