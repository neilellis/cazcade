package cazcade.fountain.datastore.impl;


import cazcade.fountain.datastore.Node;

/**
 * @author neilellis@cazcade.com
 */
public interface NodeCallback {

    void call(Node node) throws Exception;
}
