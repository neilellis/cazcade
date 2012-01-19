package cazcade.fountain.datastore.impl;


/**
 * @author neilellis@cazcade.com
 */
public interface NodeCallback {
    void call(LSDPersistedEntity persistedEntity) throws Exception;
}
