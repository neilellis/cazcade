/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.apps.twitter.auth.signin;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractTwitterServlet extends HttpServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(AbstractTwitterServlet.class);
    private   ClassPathXmlApplicationContext applicationContext;
    protected FountainDataStore              dataStore;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        applicationContext = new ClassPathXmlApplicationContext("classpath:datastore-client-spring-config.xml");
        dataStore = (FountainDataStore) applicationContext.getBean("remoteDataStore");
        try {
            dataStore.startIfNotStarted();
        } catch (Exception e) {
            log.error(e);
        }

    }

    @Override
    public void destroy() {
        dataStore.stopIfNotStopped();
        super.destroy();
    }
}
