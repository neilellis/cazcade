package cazcade.fountain.datastore.impl;


import cazcade.fountain.datastore.FountainEntity;

/**
 * @author neilellis@cazcade.com
 */
public interface NodeCallback {

    void call(FountainEntity fountainEntity) throws Exception;
}
