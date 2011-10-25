package cazcade.fountain.datastore.api;

import cazcade.fountain.common.service.ServiceStateMachine;
import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilelliz@cazcade.com
 */
public interface FountainDataStore extends ServiceStateMachine {

    <T extends LiquidRequest> T process(T request) throws Exception;

}
