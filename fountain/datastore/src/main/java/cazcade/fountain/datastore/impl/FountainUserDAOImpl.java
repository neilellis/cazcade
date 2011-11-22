package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.api.DuplicateEntityException;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.api.RelationshipNotFoundException;
import cazcade.fountain.datastore.api.UserRestrictedException;
import cazcade.fountain.index.persistence.dao.AliasDAO;
import cazcade.fountain.index.persistence.entities.AliasEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

public class FountainUserDAOImpl implements FountainUserDAO {

    private final Logger log = LoggerFactory.getLogger(FountainUserDAOImpl.class);


    public static final boolean USER_MUST_CONFIRM_EMAIL = false;
    @Autowired
    private FountainNeo fountainNeo;

    @Autowired
    private AliasDAO aliasDAO;

    @Autowired
    private FountainEmailService emailService;

    private Node createUserInternal(LSDEntity entity, boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Node userNode = fountainNeo.createNode();
            userNode.setProperty(FountainNeo.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
            fountainNeo.timestamp(userNode);
            String username = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            userNode.setProperty(FountainNeo.URI, new LiquidURI(LiquidURIScheme.user, username).asString());
            fountainNeo.mergeProperties(userNode, entity, false, false, null);
            fountainNeo.setIDIfNotSetOnNode(userNode);
            fountainNeo.indexBy(userNode, FountainNeo.ID, FountainNeo.ID, true);
            fountainNeo.indexBy(userNode, FountainNeo.URI, FountainNeo.URI, true);

            //create the associated alias

            LiquidURI uri = new LiquidURI(LiquidURIScheme.alias, new LiquidURI("cazcade:" + username));
            Node existingNode = fountainNeo.findByURI(uri);
            if (existingNode != null) {
                throw new DuplicateEntityException("Attempted to create an alias for a user, but that alias already exists .");
            }

            LSDSimpleEntity alias = LSDSimpleEntity.createEmpty();
            alias.setValue(FountainNeo.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
            alias.setType(LSDDictionaryTypes.ALIAS);
            alias.setAttribute(LSDAttribute.FULL_NAME, entity.getAttribute(LSDAttribute.FULL_NAME));
            if (entity.hasAttribute(LSDAttribute.IMAGE_URL)) {
                alias.setAttribute(LSDAttribute.IMAGE_URL, entity.getAttribute(LSDAttribute.IMAGE_URL));
            }
            alias.setAttribute(LSDAttribute.NETWORK, "cazcade");
            alias.setAttribute(LSDAttribute.NAME, username);
            createAlias(userNode, alias, true, true, false, systemUser);
            return userNode;
        } finally {
            fountainNeo.end();
        }
    }


    @Override
    public void forEachUser(final UserCallback callback) {
        aliasDAO.forEachUser(new AliasDAO.UserDAOCallback() {
            @Override
            public void process(AliasEntity alias) throws InterruptedException {
                final String uri = alias.getUri();
                if (uri.startsWith("alias:cazcade:")) {
                    final Node aliasNode = fountainNeo.findByURI(new LiquidURI(alias.getUri()));
                    final LSDEntity aliasEntity = getAliasFromNode(aliasNode, true, LiquidRequestDetailLevel.COMPLETE);
                    final Relationship ownerRel = aliasNode.getSingleRelationship(FountainRelationships.ALIAS, Direction.OUTGOING);
                    if (ownerRel == null) {
                        log.warn("No owner for alias " + uri);
                    } else {
                        final LSDEntity userEntity = fountainNeo.convertNodeToLSD(ownerRel.getEndNode(), LiquidRequestDetailLevel.COMPLETE, true);
                        callback.process(userEntity, aliasEntity);
                    }
                }
            }
        });
    }


    @Override
    public Node createUser(LSDEntity entity, boolean systemUser) throws InterruptedException, UnsupportedEncodingException {
        fountainNeo.begin();
        try {

            Node userNode;
            String username = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            LiquidURI userURI = new LiquidURI(LiquidURIScheme.user, username);
            LiquidURI aliasURI = new LiquidURI(LiquidURIScheme.alias, "cazcade:" + username);
            if (fountainNeo.findByURI(userURI) != null) {
                throw new DuplicateEntityException("Attempted to create a user (" + username + ") that already exists.");
            }
            userNode = createUserInternal(entity, systemUser);
            if (!systemUser) {
                String plainPassword = entity.getAttribute(LSDAttribute.PLAIN_PASSWORD);
                StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);
                userNode.setProperty(LSDAttribute.HASHED_AND_SALTED_PASSWORD.getKeyName(), encryptedPassword);
                emailService.sendRegistrationEmail(entity);
            }

            return userNode;
        } finally {
            fountainNeo.end();
        }

    }

    @Override
    public Node createAlias(Node userNode, LSDEntity entity, boolean me, boolean orupdate, boolean claim, boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {
            LiquidURI aliasURI;
            String name = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            String network;
            if (entity.hasAttribute(LSDAttribute.NETWORK)) {
                network = entity.getAttribute(LSDAttribute.NETWORK);
            } else {
                network = "cazcade";
            }
            aliasURI = new LiquidURI(LiquidURIScheme.alias, network + ":" + name);
            Node existingNode = fountainNeo.findByURI(aliasURI);
            if (existingNode != null && !orupdate) {
                throw new DuplicateEntityException("Attempted to create an alias that exists already without first setting the 'orupdate' flag to 'true'.");
            }
            final Node node;
            String uriString = aliasURI.asString();
            if (existingNode == null) {
                node = fountainNeo.createNode();
                if (me) {
                    node.createRelationshipTo(userNode, FountainRelationships.ALIAS);
                    node.createRelationshipTo(userNode, FountainRelationships.OWNER);
                }

                fountainNeo.mergeProperties(node, entity, false, false, null);
                node.setProperty(FountainNeo.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
                fountainNeo.setIDIfNotSetOnNode(node);
                node.setProperty(FountainNeo.URI, uriString);
                LiquidURI networkURI = new LiquidURI(LiquidURIScheme.network, network);
                Node networkNode = fountainNeo.findByURI(networkURI);
                if (networkNode == null) {
                    networkNode = createSocialNetwork(networkURI);
                }
                node.createRelationshipTo(networkNode, FountainRelationships.NETWORK_MEMBER);
                fountainNeo.indexBy(node, FountainNeo.ID, FountainNeo.ID, true);
                fountainNeo.indexBy(node, FountainNeo.URI, FountainNeo.URI, true);


            } else {
                node = existingNode;
                fountainNeo.mergeProperties(node, entity, true, false, null);
                if (me) {
                    for (Relationship relationship : node.getRelationships(FountainRelationships.ALIAS, Direction.OUTGOING)) {
                        //todo: throw an exception instead!
                        relationship.delete();
                    }
                    node.createRelationshipTo(userNode, FountainRelationships.ALIAS);
                }

            }
            if (claim) {
                userNode.createRelationshipTo(node, FountainRelationships.CLAIMED);
            }
            fountainNeo.timestamp(node);
            return node;
        } finally {
            fountainNeo.end();
        }
    }

    Node createSocialNetwork(LiquidURI uri) throws InterruptedException {
        Node node = fountainNeo.createNode();
        node.setProperty(FountainNeo.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
        fountainNeo.setIDIfNotSetOnNode(node);
        node.setProperty(FountainNeo.TYPE, LSDDictionaryTypes.SOCIAL_NETWORK.getValue());
        node.setProperty(FountainNeo.NAME, uri.getSubURI().asString());
        node.setProperty(FountainNeo.URI, uri.asString());
        fountainNeo.indexBy(node, FountainNeo.ID, FountainNeo.ID, true);
        fountainNeo.indexBy(node, FountainNeo.URI, FountainNeo.URI, true);
        fountainNeo.timestamp(node);
        return node;
    }

    public void addAuthorToNodeNoTX(LiquidURI author, boolean createAuthor, Node node) throws InterruptedException {
        Node authorNode = fountainNeo.findByURI(author);
        if (authorNode == null) {
            if (createAuthor) {
                throw new UnsupportedOperationException("Feature no longer supported.");
                /*
                LSDSimpleEntity alias = LSDSimpleEntity.createEmpty();
                LiquidURI aliasSubURI = author.getSubURI();
                alias.setValue(FountainNeo.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
                alias.setType(LSDDictionaryTypes.ALIAS);
                alias.setAttribute(LSDAttribute.NETWORK, aliasSubURI.getSchemeAsString());
                alias.setAttribute(LSDAttribute.NAME, aliasSubURI.getSubURI().asString());
                authorNode = createAlias(null, alias, false, true, false, false);
                */
            } else {
                throw new EntityNotFoundException("Could not locate the author %s", author);
            }
        }
        node.createRelationshipTo(authorNode, FountainRelationships.AUTHOR);
    }

    @Override
    public Node createSession(LiquidURI aliasUri, ClientApplicationIdentifier clientApplicationIdentifier) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Node node = fountainNeo.createNode();

            fountainNeo.setIDIfNotSetOnNode(node);
            node.setProperty(FountainNeo.TYPE, LSDDictionaryTypes.SESSION.getValue());
            node.setProperty(LSDAttribute.CLIENT_APPLICATION_NAME.getKeyName(), clientApplicationIdentifier.getName());
            node.setProperty(LSDAttribute.CLIENT_APPLICATION_KEY.getKeyName(), clientApplicationIdentifier.getKey());
            node.setProperty(LSDAttribute.CLIENT_HOST.getKeyName(), clientApplicationIdentifier.getHostinfo());
            final LiquidPermissionSet sessionPermissionSet = LiquidPermissionSet.getMinimalPermissionSet();
            LiquidPermissionSet.addReadPermissions(LiquidPermissionScope.WORLD, sessionPermissionSet);
            node.setProperty(FountainNeo.PERMISSIONS, sessionPermissionSet.toString());
            node.setProperty(LSDAttribute.ACTIVE.getKeyName(), "true");

            Node ownerNode;
            Node userNode;
            if (aliasUri.getSubURI().getSchemeAsString().equals("cazcade")) {
                ownerNode = fountainNeo.findByURI(aliasUri);
                if (ownerNode == null) {
                    throw new EntityNotFoundException("Could not find alias for %s", aliasUri);
                }
                userNode = fountainNeo.findByURI(new LiquidURI(LiquidURIScheme.user, aliasUri.getSubURI().getSubURI()));
                if (userNode == null) {
                    throw new EntityNotFoundException("Could not find user for %s", aliasUri);
                }
            } else {
                Node otherNetworkAlias = fountainNeo.findByURI(aliasUri);
                if (otherNetworkAlias == null) {
                    throw new EntityNotFoundException("Could not find alias for %s", aliasUri);
                }
                Relationship userRelationship = otherNetworkAlias.getSingleRelationship(FountainRelationships.ALIAS, Direction.OUTGOING);
                userNode = userRelationship.getEndNode();
                ownerNode = fountainNeo.findByURI(new LiquidURI(LiquidURIScheme.alias, "cazcade:" + userNode.getProperty(FountainNeo.NAME)));
                if (ownerNode == null) {
                    throw new EntityNotFoundException("Could not owner for alias %s", aliasUri);
                }
            }


            if (userNode.hasProperty(LSDAttribute.SECURITY_RESTRICTED.getKeyName()) && USER_MUST_CONFIRM_EMAIL) {
                final String restricted = (String) userNode.getProperty(LSDAttribute.SECURITY_RESTRICTED.getKeyName());
                if ("true".equals(restricted)) {
                    throw new UserRestrictedException("User account for alias %s is restricted.", aliasUri);
                }
            }

            //Remove stale sessions
            Iterable<Relationship> existingSessionRelationships = ownerNode.getRelationships(FountainRelationships.HAS_SESSION, Direction.OUTGOING);
            for (Relationship existingSessionRelationship : existingSessionRelationships) {
                Node sessionNode = existingSessionRelationship.getOtherNode(ownerNode);
                String updatedStr = (String) sessionNode.getProperty(FountainNeo.UPDATED);
                if (updatedStr != null) {
                    long updated = Long.valueOf(updatedStr);
                    Object active = null;
                    if (sessionNode.hasProperty(LSDAttribute.ACTIVE.getKeyName())) {
                        active = sessionNode.getProperty(LSDAttribute.ACTIVE.getKeyName());
                    }
                    if (updated < (System.currentTimeMillis() - FountainNeo.SESSION_EXPIRES_MILLI)) {
                        if (active == null || active.equals("false")) {
                            existingSessionRelationship.delete();
                            for (Relationship relationship : sessionNode.getRelationships()) {
                                relationship.delete();
                            }
                            sessionNode.delete();
                        }
                    } else if (updated < (System.currentTimeMillis() - FountainNeo.SESSION_INACTIVE_MILLI)) {
                        sessionNode.setProperty(LSDAttribute.ACTIVE.getKeyName(), "false");
                    }
                }
            }
            node.setProperty(FountainNeo.NAME, userNode.getProperty(FountainNeo.NAME));
            node.createRelationshipTo(ownerNode, FountainRelationships.OWNER);
            ownerNode.createRelationshipTo(node, FountainRelationships.HAS_SESSION);
            node.setProperty(FountainNeo.URI, new LiquidURI(LiquidURIScheme.session, (String) node.getProperty(FountainNeo.ID)).toString());
            fountainNeo.indexBy(node, FountainNeo.ID, FountainNeo.ID, true);
            fountainNeo.indexBy(node, FountainNeo.URI, FountainNeo.URI, true);
            fountainNeo.timestamp(node);
            return node;
        } finally {
            fountainNeo.end();
        }
    }


    @Override
    public LSDEntity getAliasFromNode(Node node, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            return fountainNeo.convertNodeToLSD(node, detail, internal);
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public LSDEntity unlinkAliasTX(LiquidSessionIdentifier identity, LiquidUUID target, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.getNeo().beginTx();
            try {
                Node node = fountainNeo.findByUUID(target);
                Node ownerNode = fountainNeo.findByURI(identity.getUserURL());
                Iterable<Relationship> relationships = node.getRelationships(FountainRelationships.ALIAS, Direction.OUTGOING);
                boolean deleted = false;
                for (Relationship relationship : relationships) {
                    if (relationship.getOtherNode(node).equals(ownerNode)) {
                        deleted = true;
                        relationship.delete();
                    }
                }
                if (!deleted) {
                    throw new RelationshipNotFoundException("{0} does not have alias {1}", identity.getName(), node.getProperty(FountainNeo.URI));
                }
                transaction.success();
                return fountainNeo.convertNodeToLSD(node, detail, internal);
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            fountainNeo.end();
        }
    }


    public void setFountainNeo(FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }
}