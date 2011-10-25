package cazcade.hashbo.util;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author neilellis@cazcade.com
 */
public class DataStoreFactory {
    public static FountainDataStore dataStore;

    private final static Logger log = Logger.getLogger(DataStoreFactory.class);

    static {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:datastore-client-spring-config.xml");
        dataStore = (FountainDataStore) applicationContext.getBean("remoteDataStore");
        try {
            dataStore.startIfNotStarted();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static FountainDataStore getDataStore() {
       return dataStore;
    }
}
