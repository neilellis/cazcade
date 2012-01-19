package cazcade.fountain.common.app;

/**
 * @author neilelliz@cazcade.com
 */
public interface ApplicationLifecycleListener {
    void shutdown() throws Exception;
}
