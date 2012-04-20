package cazcade.common;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public interface CommonConstants {

    //    String ADMIN_SECRET = "abcd";
    @Nonnull
    String ADMIN_SECRET = "puq5cedah8sTaw6EjAteVuhaxA5achubaDuDrUS4c4adE9tUqEwrEs5p9UBre8re";
    @Nonnull
    String YOUTUBE_DEVELOPER_KEY = "AI39si6nEOatwuN8NwnwLjm7eqblmrA4zT4rxG04DTZ0X5tfw7Nyie5YFGThlM8naM1cBwAa_bwPODUM-OHJ_lfINABw8XP32A";
    String CAZCADE_HOME = System.getProperty("cazcade.home", System.getProperty("user.home"));
    String DATASTORE_SESSION_LOGS = System.getProperty("session.logs", System.getProperty("user.home")+"/data/log/sessions");

    @Nonnull
    String IDENTITY_ATTRIBUTE = "identity";
    @Nonnull
    String NEW_USER_ATTRIBUTE = "newly.registered.user";
    @Nonnull
    String NEW_USER_PASSWORD_ATTRIBUTE = "newly.registered.user.password";


    @Nonnull
    String RPC_EXCHANGE = "rpc-topic";
    @Nonnull
    String SERVICE_STORE = "service.store";
    @Nonnull
    String STRING_ENCODING = "utf-8";
    boolean IS_PRODUCTION = System.getProperty("dev") == null;
    @Nonnull
    String QUEUE_ATTRIBUTE = "queue";
    @Nonnull
    String NEO_BACKUP_DIR = CAZCADE_HOME + "/backup";

    @Nonnull
    String ANONYMOUS_ALIAS = "alias:cazcade:anon";
    @Nonnull
    String CAZCADE_ALIAS_PREFIX = "alias:cazcade:";

    int MINUTE = 60000;
    int HOUR = 3600000;
    int SECOND = 1000;
}
