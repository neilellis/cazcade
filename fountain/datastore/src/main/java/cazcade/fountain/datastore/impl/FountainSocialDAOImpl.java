package cazcade.fountain.datastore.impl;

import cazcade.common.Logger;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import org.neo4j.graphdb.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

public class FountainSocialDAOImpl implements FountainSocialDAO {

    private final static Logger log = Logger.getLogger(FountainSocialDAOImpl.class);

    @Autowired
    private FountainNeo fountainNeo;

    @Autowired
    private FountainPoolDAOImpl poolDAO;

    @Autowired
    private FountainUserDAOImpl userDAO;

    @Autowired
    private FountainIndexServiceImpl indexDAO;



    @Override
    public Collection<LSDEntity> getRosterNoTX(LiquidURI uri, boolean internal, LiquidSessionIdentifier identity, LiquidRequestDetailLevel request) throws InterruptedException {
        fountainNeo.begin();
        try {
            Node node = fountainNeo.findByURI(uri);
            if (node == null) {
                return null;
            }
            return getRosterNoTX(node, internal, identity, request);
        } finally {
            fountainNeo.end();
        }
    }

    Collection<LSDEntity> getRosterNoTX(Node poolNode, boolean internal, LiquidSessionIdentifier identity, LiquidRequestDetailLevel detail) {
        ArrayList<LSDEntity> entities = new ArrayList<LSDEntity>();
        Iterable<Relationship> visitingSessions = poolNode.getRelationships(FountainRelationships.VISITING, Direction.INCOMING);
        for (Relationship visitingSession : visitingSessions) {
            try {
                Node sessionNode = visitingSession.getOtherNode(poolNode);
                String updatedStr = (String) sessionNode.getProperty(FountainNeo.UPDATED);
                if (updatedStr != null) {
                    long updated = Long.valueOf(updatedStr);
                    if (updated < (System.currentTimeMillis() - FountainNeo.SESSION_INACTIVE_MILLI)) {
                        sessionNode.setProperty(LSDAttribute.ACTIVE.getKeyName(), "false");
                    }
                }
                if (sessionNode.hasProperty(LSDAttribute.ACTIVE.getKeyName()) && sessionNode.getProperty(LSDAttribute.ACTIVE.getKeyName()).equals("true")) {
                    Node aliasNode = sessionNode.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING).getOtherNode(sessionNode);
                    //If the alias has no profile image, take it from the profile pool!
                    fountainNeo.putProfileInformationIntoAlias(aliasNode);
                    LSDEntity aliasEntity = fountainNeo.convertNodeToLSD(aliasNode, detail, internal);
                    entities.add(aliasEntity);
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        return entities;
    }

    @Override
    public Collection<LSDEntity> getRosterNoTX(LiquidUUID target, boolean internal, LiquidSessionIdentifier identity, LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            Node node = fountainNeo.findByUUID(target);
            if (node == null) {
                return null;
            }
            return getRosterNoTX(node, internal, identity, detail);
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public boolean isFollowing(Node currentAlias, Node node) throws InterruptedException {
        fountainNeo.begin();
        try {
            boolean following = false;

            for (Relationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_ALIAS, FountainRelationships.FOLLOW_CONTENT)) {
                if (relationship.getEndNode().equals(node)) {
                    following = true;
                }

            }
            return following;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public LSDEntity followResourceTX(final LiquidSessionIdentifier sessionIdentifier, final LiquidURI uri, final LiquidRequestDetailLevel detail, final boolean internal) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<LSDEntity>() {
            @Override
            public LSDEntity call() throws Exception {
                Node currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias(), true);
                Node resourceToFollow = fountainNeo.findByURI(uri, true);

                deltaFollowersCount(resourceToFollow, 1);
                indexDAO.syncFollowerCount(resourceToFollow);


                if (uri.getScheme() == LiquidURIScheme.alias) {
                    if (!isFollowing(currentAlias, resourceToFollow)) {
                        currentAlias.createRelationshipTo(resourceToFollow, FountainRelationships.FOLLOW_ALIAS);
                        deltaFollowsAliasCount(currentAlias, 1);
                    }
                    return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
                } else {
                    if (!isFollowing(currentAlias, resourceToFollow)) {
                        currentAlias.createRelationshipTo(resourceToFollow, FountainRelationships.FOLLOW_CONTENT);
                        deltaFollowsResourcesCount(currentAlias, 1);
                    }
                    indexDAO.syncFollowCounts(currentAlias);
                    return poolDAO.getPoolObjectTx(sessionIdentifier, uri, internal, false, detail);
                }

            }
        });
    }

    private void deltaFollowersCount(Node node, int delta) {
        deltaCount(LSDAttribute.FOLLOWERS_COUNT, node, delta);
    }

    private void deltaFollowsAliasCount(Node node, int delta) {
        deltaCount(LSDAttribute.FOLLOWS_ALIAS_COUNT, node, delta);
    }

    private void deltaFollowsResourcesCount(Node node, int delta) {
        deltaCount(LSDAttribute.FOLLOWS_RESOURCES_COUNT, node, delta);
    }

    private void deltaCount(LSDAttribute attribute, Node resourceToFollow, int delta) {
        int followerCount;
        String attributeKey = attribute.getKeyName();
        if (resourceToFollow.hasProperty(attributeKey)) {
            followerCount = Integer.parseInt(resourceToFollow.getProperty(attributeKey).toString());
        } else {
            followerCount = 0;
        }
        log.debug("Count is now {0} for {1}", followerCount + delta, attribute);
        resourceToFollow.setProperty(attributeKey, String.valueOf(followerCount + delta));
    }

    @Override
    public LSDEntity unfollowResourceTX(final LiquidSessionIdentifier sessionIdentifier, final LiquidURI uri, final LiquidRequestDetailLevel detail, final boolean internal) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<LSDEntity>() {
            @Override
            public LSDEntity call() throws Exception {
                Node currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias(), true);
                Node resourceToFollow = fountainNeo.findByURI(uri, true);
                deltaFollowersCount(resourceToFollow, -1);
                indexDAO.syncFollowerCount(resourceToFollow);

                if (uri.getScheme() == LiquidURIScheme.alias) {
                    for (Relationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING)) {
                        if (relationship.getOtherNode(currentAlias).equals(resourceToFollow)) {
                            relationship.delete();
                            deltaFollowsAliasCount(currentAlias, -1);
                        }
                    }
                    return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
                } else {
                    for (Relationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_CONTENT, Direction.OUTGOING)) {
                        if (relationship.getOtherNode(currentAlias).equals(resourceToFollow)) {
                            relationship.delete();
                            deltaFollowsResourcesCount(currentAlias, -1);
                        }
                    }
                    indexDAO.syncFollowCounts(currentAlias);
                    return poolDAO.getPoolObjectTx(sessionIdentifier, uri, internal, false, detail);
                }
            }
        });
    }

    @Override
    public LSDEntity getAliasAsProfileTx(final LiquidSessionIdentifier sessionIdentifier, final LiquidURI uri, final boolean internal, final LiquidRequestDetailLevel detail) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<LSDEntity>() {
            @Override
            public LSDEntity call() throws Exception {
                return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
            }
        });

    }

    @Override
    public void recordChat(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, LSDEntity entity) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    LSDEntity getAliasAsProfile(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, LiquidRequestDetailLevel detail, boolean internal) throws InterruptedException {
        Node currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias());
        Node node = fountainNeo.findByURI(uri, true);
        LSDEntity result = fountainNeo.convertNodeToLSD(node, detail, internal);

//        int follows = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING).getAllNodes().size();
//        int followers = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, FountainRelationships.FOLLOW_ALIAS, Direction.INCOMING).getAllNodes().size();
//        result.setAttribute(LSDAttribute.FOLLOWS_ALIAS_COUNT, String.valueOf(follows));

        boolean following = isFollowing(currentAlias, node);
        result.setAttribute(LSDAttribute.FOLLOWING, following);
        fountainNeo.setPermissionFlagsOnEntity(sessionIdentifier, node, null, result);

        return result;
    }


}