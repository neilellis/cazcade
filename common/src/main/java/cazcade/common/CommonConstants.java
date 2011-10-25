package cazcade.common;

/**
 * @author neilelliz@cazcade.com
 */
public interface CommonConstants {

    //    String ADMIN_SECRET = "abcd";
    String ADMIN_SECRET = "puq5cedah8sTaw6EjAteVuhaxA5achubaDuDrUS4c4adE9tUqEwrEs5p9UBre8re";
    String YOUTUBE_DEVELOPER_KEY = "AI39si6nEOatwuN8NwnwLjm7eqblmrA4zT4rxG04DTZ0X5tfw7Nyie5YFGThlM8naM1cBwAa_bwPODUM-OHJ_lfINABw8XP32A";
    String RABBITMQ_USERNAME = System.getProperty("fountain.rabbitmq.username", "cazcade");
    String RABBITMQ_SECRET = System.getProperty("fountain.rabbitmq.password", "puq5cedah8sTaw6EjAteVuhaxA5achubaDuD");
    String RABBITMQ_VHOST = System.getProperty("fountain.rabbitmq.vhost", "/");
    String RABBITMQ_HOST = System.getProperty("fountain.rabbitmq.host", "localhost");
    String CAZCADE_HOME = System.getProperty("cazcade.home", "/Library/Cazcade");
    String DATASTORE_SESSION_LOGS = System.getProperty("session.logs", "/var/log/cazcade/sessions");

    String IDENTITY_ATTRIBUTE = "identity";
    String NEW_USER_ATTRIBUTE = "newly.registered.user";
    String NEW_USER_PASSWORD_ATTRIBUTE = "newly.registered.user.password";


    String RABBITMQ_ASYNC_REQUEST_EXCHANGE_NAME = "async-request-exchange";
    String RABBITMQ_RPC_EXCHANGE_NAME = "rpc-exchange";
    String RABBITMQ_FOUNTAIN_STORE_REQUEST_KEY = "service.store";
    String RABBITMQ_SYNC_REQUEST_KEY = "service.store.rpc";
    String RABBITMQ_USER_NOTIFICATION_EXCHANGE = "user-notification-queue";
    String STRING_ENCODING = "utf-8";
    boolean IS_PRODUCTION = System.getProperty("production") != null && !System.getProperty("production").equals("false");
    int NOTIFICATION_TIMEOUT = 30 * 1000;
    String CHANNEL_ATTRIBUTE = "channel";
    String MESSAGES_ATTRIBUTE = "messages";
    String OUTPUT_STREAM_ATTRIBUTE = "output";
    String QUEUE_ATTRIBUTE = "queue";
    String POOL_UUID_ATTRIBUTE = "pool-uuid";
    String POOL_URI_ATTRIBUTE = "pool-uri";
    String NEO_BACKUP_DIR = CAZCADE_HOME + "/backup";
    
    String LIQUID_CHANNEL_CONFIGURATION = "liquid.channel.configuration";
    String ANONYMOUS_ALIAS = "alias:cazcade:anon";
    String CAZCADE_ALIAS_PREFIX = "alias:cazcade:";
    int MINUTE = 60000;
    int HOUR = 3600000;
    int SECOND = 1000;
}
