package cazcade.boardcast.util;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class DataStoreFactory {
    @Nonnull
    public static final FountainDataStore dataStore;

    @Nonnull
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

    @Nonnull
    public static FountainDataStore getDataStore() {
        return dataStore;
    }
}
