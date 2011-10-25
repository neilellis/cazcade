package cazcade.fountain.datastore.impl;

import org.neo4j.graphdb.Node;

/**
 * @author neilellis@cazcade.com
 */
public interface NodeCallback {

    void call(Node node) throws Exception;
}
