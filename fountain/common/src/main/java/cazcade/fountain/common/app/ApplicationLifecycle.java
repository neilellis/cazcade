package cazcade.fountain.common.app;

/**
 * @author neilelliz@cazcade.com
 */

public interface ApplicationLifecycle {

    void register(ApplicationLifecycleListener listener);
}
