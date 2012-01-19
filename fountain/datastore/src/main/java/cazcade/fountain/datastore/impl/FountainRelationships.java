package cazcade.fountain.datastore.impl;

import org.neo4j.graphdb.RelationshipType;

/**
 * @author Neil Ellis
 */
public enum FountainRelationships implements RelationshipType {
    CHILD, CREATOR, HAS_SESSION, OWNER, AUTHOR, NETWORK_MEMBER, VIEW, CLAIMED, FORK_PARENT, VERSION_PARENT, VISITING,
    LINKED_CHILD, ALIAS, PREVIOUS, EDITOR, FOLLOW_ALIAS, FOLLOW_CONTENT, VISITED, LIKES, COMMENT
}
