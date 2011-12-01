package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.Relationship;
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
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

public class FountainUserDAOImpl implements FountainUserDAO {

    private final Logger log = LoggerFactory.getLogger(FountainUserDAOImpl.class);

    @Nonnull
    private static final org.jasypt.digest.StandardStringDigester digester = new org.jasypt.digest.StandardStringDigester();
    @Nonnull
    public static final String USER_HASH_SALT = "EverythingThatArisesMustPass";


    public static final boolean USER_MUST_CONFIRM_EMAIL = false;

    @Autowired
    private FountainNeo fountainNeo;

    @Autowired
    private AliasDAO aliasDAO;

    @Autowired
    private FountainEmailService emailService;

    @Nonnull
    private Node createUserInternal(@Nonnull final LSDEntity entity, final boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Node userNode = fountainNeo.createNode();
            userNode.setProperty(LSDAttribute.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
            userNode.timestamp();
            final String username = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            userNode.setProperty(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.user, username).asString());
            userNode.mergeProperties(entity, false, false, null);
            fountainNeo.freeTextIndexNoTx(userNode);

            userNode.setIDIfNotSetOnNode();
            fountainNeo.indexBy(userNode, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(userNode, LSDAttribute.URI, LSDAttribute.URI, true);

            //create the associated alias

            final LiquidURI uri = new LiquidURI(LiquidURIScheme.alias, new LiquidURI("cazcade:" + username));
            final Node existingNode = fountainNeo.findByURI(uri);
            if (existingNode != null) {
                throw new DuplicateEntityException("Attempted to create an alias for a user, but that alias already exists .");
            }

            final LSDSimpleEntity alias = LSDSimpleEntity.createEmpty();
            alias.setAttribute(LSDAttribute.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
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
    public void forEachUser(@Nonnull final UserCallback callback) {
        aliasDAO.forEachUser(new AliasDAO.UserDAOCallback() {
            @Override
            public void process(@Nonnull final AliasEntity alias) throws Exception {
                final String uri = alias.getUri();
                if (uri.startsWith("alias:cazcade:")) {
                    final Node aliasNode = fountainNeo.findByURI(new LiquidURI(alias.getUri()));
                    if (aliasNode == null) {
                        log.warn("Skipping " + uri + " as alias node not found.");
                        return;
                    }
                    final LSDEntity aliasEntity = getAliasFromNode(aliasNode, true, LiquidRequestDetailLevel.COMPLETE);
                    final Relationship ownerRel = aliasNode.getSingleRelationship(FountainRelationships.ALIAS, Direction.OUTGOING);
                    if (ownerRel == null) {
                        log.warn("No owner for alias " + uri);
                    } else {
                        final LSDEntity userEntity = ownerRel.getEndNode().convertNodeToLSD(LiquidRequestDetailLevel.COMPLETE, true);
                        callback.process(userEntity, aliasEntity);
                    }
                }
            }
        });
    }

    @Override
    public boolean confirmHash(@Nonnull final LiquidURI user, final String changePasswordSecurityHash) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final Node userNode = fountainNeo.findByURI(user);
                final String hashString = createUserHashableString(userNode);
                return FountainUserDAOImpl.digester.matches(hashString, changePasswordSecurityHash);
            }
        });
    }

    @Nonnull
    private String createUserHashableString(@Nonnull final Node userNode) {
        final Object id = userNode.getProperty(LSDAttribute.ID);
        final Object password = userNode.getProperty(LSDAttribute.HASHED_AND_SALTED_PASSWORD);
        return id + ":" + password + ":" + FountainUserDAOImpl.USER_HASH_SALT;
    }

    @Override
    public void sendPasswordChangeRequest(@Nonnull final LiquidURI userURI) throws Exception {
        fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                final Node userNode = fountainNeo.findByURI(userURI);
                final LSDEntity userEntity = userNode.convertNodeToLSD(LiquidRequestDetailLevel.COMPLETE, true);

                emailService.sendChangePasswordRequest(userEntity, FountainUserDAOImpl.digester.digest(createUserHashableString(userNode)));
                return null;
            }
        });
    }


    @Nonnull
    @Override
    public Node createUser(@Nonnull final LSDEntity entity, final boolean systemUser) throws InterruptedException, UnsupportedEncodingException {
        fountainNeo.begin();
        try {

            final Node userNode;
            final String username = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            final LiquidURI userURI = new LiquidURI(LiquidURIScheme.user, username);
            final LiquidURI aliasURI = new LiquidURI(LiquidURIScheme.alias, "cazcade:" + username);
            if (fountainNeo.findByURI(userURI) != null) {
                throw new DuplicateEntityException("Attempted to create a user (" + username + ") that already exists.");
            }
            userNode = createUserInternal(entity, systemUser);
            if (!systemUser) {
                final String plainPassword = entity.getAttribute(LSDAttribute.PLAIN_PASSWORD);
                final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                final String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);
                userNode.setProperty(LSDAttribute.HASHED_AND_SALTED_PASSWORD, encryptedPassword);
                emailService.sendRegistrationEmail(entity);
            }

            return userNode;
        } finally {
            fountainNeo.end();
        }

    }

    @Nullable
    @Override
    public Node createAlias(@Nonnull final Node userNode, @Nonnull final LSDEntity entity, final boolean me, final boolean orupdate, final boolean claim, final boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LiquidURI aliasURI;
            final String name = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            final String network;
            if (entity.hasAttribute(LSDAttribute.NETWORK)) {
                network = entity.getAttribute(LSDAttribute.NETWORK);
            } else {
                network = "cazcade";
            }
            aliasURI = new LiquidURI(LiquidURIScheme.alias, network + ':' + name);
            final Node existingNode = fountainNeo.findByURI(aliasURI);
            if (existingNode != null && !orupdate) {
                throw new DuplicateEntityException("Attempted to create an alias that exists already without first setting the 'orupdate' flag to 'true'.");
            }
            final Node node;
            final String uriString = aliasURI.asString();
            if (existingNode == null) {
                node = fountainNeo.createNode();
                if (me) {
                    node.createRelationshipTo(userNode, FountainRelationships.ALIAS);
                    node.createRelationshipTo(userNode, FountainRelationships.OWNER);
                }

                node.mergeProperties(entity, false, false, null);
                fountainNeo.freeTextIndexNoTx(node);

                node.setProperty(LSDAttribute.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
                node.setIDIfNotSetOnNode();
                node.setProperty(LSDAttribute.URI, uriString);
                final LiquidURI networkURI = new LiquidURI(LiquidURIScheme.network, network);
                Node networkNode = fountainNeo.findByURI(networkURI);
                if (networkNode == null) {
                    networkNode = createSocialNetwork(networkURI);
                }
                node.createRelationshipTo(networkNode, FountainRelationships.NETWORK_MEMBER);
                fountainNeo.indexBy(node, LSDAttribute.ID, LSDAttribute.ID, true);
                fountainNeo.indexBy(node, LSDAttribute.URI, LSDAttribute.URI, true);


            } else {
                node = existingNode;
                node.mergeProperties(entity, true, false, null);
                fountainNeo.freeTextIndexNoTx(node);
                if (me) {
                    for (final Relationship relationship : node.getRelationships(FountainRelationships.ALIAS, Direction.OUTGOING)) {
                        //todo: throw an exception instead!
                        relationship.delete();
                    }
                    node.createRelationshipTo(userNode, FountainRelationships.ALIAS);
                }

            }
            if (claim) {
                userNode.createRelationshipTo(node, FountainRelationships.CLAIMED);
            }
            node.timestamp();
            return node;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    Node createSocialNetwork(@Nonnull final LiquidURI uri) throws InterruptedException {
        final Node node = fountainNeo.createNode();
        node.setProperty(LSDAttribute.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
        node.setIDIfNotSetOnNode();
        node.setProperty(LSDAttribute.TYPE, LSDDictionaryTypes.SOCIAL_NETWORK.getValue());
        node.setProperty(LSDAttribute.NAME, uri.getSubURI().asString());
        node.setProperty(LSDAttribute.URI, uri.asString());
        fountainNeo.indexBy(node, LSDAttribute.ID, LSDAttribute.ID, true);
        fountainNeo.indexBy(node, LSDAttribute.URI, LSDAttribute.URI, true);
        node.timestamp();
        return node;
    }

    public void addAuthorToNodeNoTX(@Nonnull final LiquidURI author, final boolean createAuthor, @Nonnull final Node node) throws InterruptedException {
        final Node authorNode = fountainNeo.findByURI(author);
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

    @Nonnull
    @Override
    public Node createSession(@Nonnull final LiquidURI aliasUri, @Nonnull final ClientApplicationIdentifier clientApplicationIdentifier) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Node node = fountainNeo.createNode();

            node.setIDIfNotSetOnNode();
            node.setProperty(LSDAttribute.TYPE, LSDDictionaryTypes.SESSION.getValue());
            node.setProperty(LSDAttribute.CLIENT_APPLICATION_NAME, clientApplicationIdentifier.getName());
            node.setProperty(LSDAttribute.CLIENT_APPLICATION_KEY, clientApplicationIdentifier.getKey());
            node.setProperty(LSDAttribute.CLIENT_HOST, clientApplicationIdentifier.getHostinfo());
            final LiquidPermissionSet sessionPermissionSet = LiquidPermissionSet.getMinimalPermissionSet();
            LiquidPermissionSet.addReadPermissions(LiquidPermissionScope.WORLD, sessionPermissionSet);
            node.setProperty(LSDAttribute.PERMISSIONS, sessionPermissionSet.toString());
            node.setAttribute(LSDAttribute.ACTIVE, true);

            final Node ownerNode;
            final Node userNode;
            if ("cazcade".equals(aliasUri.getSubURI().getSchemeAsString())) {
                ownerNode = fountainNeo.findByURI(aliasUri);
                if (ownerNode == null) {
                    throw new EntityNotFoundException("Could not find alias for %s", aliasUri);
                }
                userNode = fountainNeo.findByURI(new LiquidURI(LiquidURIScheme.user, aliasUri.getSubURI().getSubURI()));
                if (userNode == null) {
                    throw new EntityNotFoundException("Could not find user for %s", aliasUri);
                }
            } else {
                final Node otherNetworkAlias = fountainNeo.findByURI(aliasUri);
                if (otherNetworkAlias == null) {
                    throw new EntityNotFoundException("Could not find alias for %s", aliasUri);
                }
                final Relationship userRelationship = otherNetworkAlias.getSingleRelationship(FountainRelationships.ALIAS, Direction.OUTGOING);
                userNode = userRelationship.getEndNode();
                ownerNode = fountainNeo.findByURI(new LiquidURI(LiquidURIScheme.alias, "cazcade:" + userNode.getAttribute(LSDAttribute.NAME)));
                if (ownerNode == null) {
                    throw new EntityNotFoundException("Could not owner for alias %s", aliasUri);
                }
            }


            //noinspection PointlessBooleanExpression
            if (userNode.hasAttribute(LSDAttribute.SECURITY_RESTRICTED) && USER_MUST_CONFIRM_EMAIL) {
                final String restricted = userNode.getAttribute(LSDAttribute.SECURITY_RESTRICTED);
                if ("true".equals(restricted)) {
                    throw new UserRestrictedException("User account for alias %s is restricted.", aliasUri);
                }
            }

            //Remove stale sessions
            final Iterable<Relationship> existingSessionRelationships = ownerNode.getRelationships(FountainRelationships.HAS_SESSION, Direction.OUTGOING);
            for (final Relationship existingSessionRelationship : existingSessionRelationships) {
                final Node sessionNode = existingSessionRelationship.getOtherNode(ownerNode);
                final long updated = sessionNode.getUpdated().getTime();
                final boolean active = sessionNode.getBooleanAttribute(LSDAttribute.ACTIVE);
                if (updated < System.currentTimeMillis() - FountainNeo.SESSION_EXPIRES_MILLI) {
                    if (!active) {
                        existingSessionRelationship.delete();
                        for (final Relationship relationship : sessionNode.getRelationships()) {
                            relationship.delete();
                        }
                        sessionNode.deleteNeo();
                    }
                } else if (updated < System.currentTimeMillis() - FountainNeo.SESSION_INACTIVE_MILLI) {
                    sessionNode.setAttribute(LSDAttribute.ACTIVE, false);
                }
            }
            node.setProperty(LSDAttribute.NAME, userNode.getAttribute(LSDAttribute.NAME));
            node.createRelationshipTo(ownerNode, FountainRelationships.OWNER);
            ownerNode.createRelationshipTo(node, FountainRelationships.HAS_SESSION);
            node.setProperty(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.session, node.getAttribute(LSDAttribute.ID)).toString());
            fountainNeo.indexBy(node, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(node, LSDAttribute.URI, LSDAttribute.URI, true);
            node.timestamp();
            return node;
        } finally {
            fountainNeo.end();
        }
    }


    @Nullable
    @Override
    public LSDEntity getAliasFromNode(@Nonnull final Node node, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            return node.convertNodeToLSD(detail, internal);
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable
    @Override
    public LSDEntity unlinkAliasTX(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidUUID target, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final Node node = fountainNeo.findByUUID(target);
                final Node ownerNode = fountainNeo.findByURI(identity.getUserURL());
                final Iterable<Relationship> relationships = node.getRelationships(FountainRelationships.ALIAS, Direction.OUTGOING);
                boolean deleted = false;
                for (final Relationship relationship : relationships) {
                    if (relationship.getOtherNode(node).equals(ownerNode)) {
                        deleted = true;
                        relationship.delete();
                    }
                }
                if (!deleted) {
                    throw new RelationshipNotFoundException("{0} does not have alias {1}", identity.getName(), node.getAttribute(LSDAttribute.URI));
                }
                transaction.success();
                return node.convertNodeToLSD(detail, internal);
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


    public void setFountainNeo(final FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }
}