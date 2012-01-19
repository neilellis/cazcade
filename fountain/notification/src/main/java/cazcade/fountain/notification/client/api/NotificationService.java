package cazcade.fountain.notification.client.api;

import cazcade.fountain.common.service.ServiceStateMachine;

/**
 * @author neilelliz@cazcade.com
 */
public interface NotificationService extends ServiceStateMachine {
    void send(RequestNotification notification);
}
