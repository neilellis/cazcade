package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.fountain.datastore.api.DuplicateEntityException;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.api.RelationshipNotFoundException;
import cazcade.fountain.datastore.api.UserRestrictedException;
import cazcade.fountain.datastore.impl.*;
import cazcade.fountain.index.persistence.dao.AliasDAO;
import cazcade.fountain.index.persistence.entities.AliasEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import org.jasypt.digest.StandardStringDigester;
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
    private static final StandardStringDigester digester = new StandardStringDigester();
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
    private FountainEntity createUserInternal(@Nonnull final LSDEntity entity, final boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {
            final FountainEntity userFountainEntityImpl = fountainNeo.createNode();
            userFountainEntityImpl.setAttribute(LSDAttribute.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
            userFountainEntityImpl.timestamp();
            final String username = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            userFountainEntityImpl.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.user, username).asString());
            userFountainEntityImpl.mergeProperties(entity, false, false, null);
            fountainNeo.freeTextIndexNoTx(userFountainEntityImpl);

            userFountainEntityImpl.setIDIfNotSetOnNode();
            fountainNeo.indexBy(userFountainEntityImpl, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(userFountainEntityImpl, LSDAttribute.URI, LSDAttribute.URI, true);

            //create the associated alias

            final LiquidURI uri = new LiquidURI(LiquidURIScheme.alias, new LiquidURI("cazcade:" + username));
            final FountainEntity existingFountainEntity = fountainNeo.findByURI(uri);
            if (existingFountainEntity != null) {
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
            createAlias(userFountainEntityImpl, alias, true, true, false, systemUser);
            return userFountainEntityImpl;
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
                    final FountainEntity aliasFountainEntity = fountainNeo.findByURI(new LiquidURI(alias.getUri()));
                    if (aliasFountainEntity == null) {
                        log.warn("Skipping " + uri + " as alias node not found.");
                        return;
                    }
                    final LSDEntity aliasEntity = getAliasFromNode(aliasFountainEntity, true, LiquidRequestDetailLevel.COMPLETE);
                    final FountainRelationship ownerRel = aliasFountainEntity.getSingleRelationship(FountainRelationships.ALIAS, Direction.OUTGOING);
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
                final FountainEntity userFountainEntity = fountainNeo.findByURI(user);
                final String hashString = createUserHashableString(userFountainEntity);
                return FountainUserDAOImpl.digester.matches(hashString, changePasswordSecurityHash);
            }
        });
    }

    @Nonnull
    private String createUserHashableString(@Nonnull final FountainEntity userFountainEntity) {
        final Object id = userFountainEntity.getAttribute(LSDAttribute.ID);
        final Object password = userFountainEntity.getAttribute(LSDAttribute.HASHED_AND_SALTED_PASSWORD);
        return id + ":" + password + ":" + FountainUserDAOImpl.USER_HASH_SALT;
    }

    @Override
    public void sendPasswordChangeRequest(@Nonnull final LiquidURI userURI) throws Exception {
        fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                final FountainEntity userFountainEntity = fountainNeo.findByURI(userURI);
                final LSDEntity userEntity = userFountainEntity.convertNodeToLSD(LiquidRequestDetailLevel.COMPLETE, true);

                emailService.sendChangePasswordRequest(userEntity, FountainUserDAOImpl.digester.digest(createUserHashableString(userFountainEntity)));
                return null;
            }
        });
    }


    @Nonnull
    @Override
    public FountainEntity createUser(@Nonnull final LSDEntity entity, final boolean systemUser) throws InterruptedException, UnsupportedEncodingException {
        fountainNeo.begin();
        try {

            final FountainEntity userFountainEntity;
            final String username = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            final LiquidURI userURI = new LiquidURI(LiquidURIScheme.user, username);
            final LiquidURI aliasURI = new LiquidURI(LiquidURIScheme.alias, "cazcade:" + username);
            if (fountainNeo.findByURI(userURI) != null) {
                throw new DuplicateEntityException("Attempted to create a user (" + username + ") that already exists.");
            }
            userFountainEntity = createUserInternal(entity, systemUser);
            if (!systemUser) {
                final String plainPassword = entity.getAttribute(LSDAttribute.PLAIN_PASSWORD);
                final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                final String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);
                userFountainEntity.setAttribute(LSDAttribute.HASHED_AND_SALTED_PASSWORD, encryptedPassword);
                emailService.sendRegistrationEmail(entity);
            }

            return userFountainEntity;
        } finally {
            fountainNeo.end();
        }

    }

    @Nullable
    @Override
    public FountainEntity createAlias(@Nonnull final FountainEntity userFountainEntityImpl, @Nonnull final LSDEntity entity, final boolean me, final boolean orupdate, final boolean claim, final boolean systemUser) throws InterruptedException {
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
            final FountainEntity existingFountainEntityImpl = fountainNeo.findByURI(aliasURI);
            if (existingFountainEntityImpl != null && !orupdate) {
                throw new DuplicateEntityException("Attempted to create an alias that exists already without first setting the 'orupdate' flag to 'true'.");
            }
            final FountainEntity fountainEntityImpl;
            final String uriString = aliasURI.asString();
            if (existingFountainEntityImpl == null) {
                fountainEntityImpl = fountainNeo.createNode();
                if (me) {
                    fountainEntityImpl.createRelationshipTo(userFountainEntityImpl, FountainRelationships.ALIAS);
                    fountainEntityImpl.createRelationshipTo(userFountainEntityImpl, FountainRelationships.OWNER);
                }

                fountainEntityImpl.mergeProperties(entity, false, false, null);
                fountainNeo.freeTextIndexNoTx(fountainEntityImpl);

                fountainEntityImpl.setAttribute(LSDAttribute.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
                fountainEntityImpl.setIDIfNotSetOnNode();
                fountainEntityImpl.setAttribute(LSDAttribute.URI, uriString);
                final LiquidURI networkURI = new LiquidURI(LiquidURIScheme.network, network);
                FountainEntity networkFountainEntityImpl = fountainNeo.findByURI(networkURI);
                if (networkFountainEntityImpl == null) {
                    networkFountainEntityImpl = createSocialNetwork(networkURI);
                }
                fountainEntityImpl.createRelationshipTo(networkFountainEntityImpl, FountainRelationships.NETWORK_MEMBER);
                fountainNeo.indexBy(fountainEntityImpl, LSDAttribute.ID, LSDAttribute.ID, true);
                fountainNeo.indexBy(fountainEntityImpl, LSDAttribute.URI, LSDAttribute.URI, true);


            } else {
                fountainEntityImpl = existingFountainEntityImpl;
                fountainEntityImpl.mergeProperties(entity, true, false, null);
                fountainNeo.freeTextIndexNoTx(fountainEntityImpl);
                if (me) {
                    for (final FountainRelationship relationship : fountainEntityImpl.getRelationships(FountainRelationships.ALIAS, Direction.OUTGOING)) {
                        //todo: throw an exception instead!
                        relationship.delete();
                    }
                    fountainEntityImpl.createRelationshipTo(userFountainEntityImpl, FountainRelationships.ALIAS);
                }

            }
            if (claim) {
                userFountainEntityImpl.createRelationshipTo(fountainEntityImpl, FountainRelationships.CLAIMED);
            }
            fountainEntityImpl.timestamp();
            return fountainEntityImpl;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    FountainEntity createSocialNetwork(@Nonnull final LiquidURI uri) throws InterruptedException {
        final FountainEntity entity = fountainNeo.createNode();
        entity.setAttribute(LSDAttribute.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
        entity.setIDIfNotSetOnNode();
        entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.SOCIAL_NETWORK.getValue());
        entity.setAttribute(LSDAttribute.NAME, uri.getSubURI().asString());
        entity.setAttribute(LSDAttribute.URI, uri.asString());
        fountainNeo.indexBy(entity, LSDAttribute.ID, LSDAttribute.ID, true);
        fountainNeo.indexBy(entity, LSDAttribute.URI, LSDAttribute.URI, true);
        entity.timestamp();
        return entity;
    }

    public void addAuthorToNodeNoTX(@Nonnull final LiquidURI author, final boolean createAuthor, @Nonnull final FountainEntity fountainEntity) throws InterruptedException {
        final FountainEntity authorFountainEntityImpl = fountainNeo.findByURI(author);
        if (authorFountainEntityImpl == null) {
            if (createAuthor) {
                throw new UnsupportedOperationException("Feature no longer supported.");
                /*
                LSDSimpleEntity alias = LSDSimpleEntity.createEmpty();
                LiquidURI aliasSubURI = author.getSubURI();
                alias.setValue(FountainNeo.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
                alias.setType(LSDDictionaryTypes.ALIAS);
                alias.setAttribute(LSDAttribute.NETWORK, aliasSubURI.getSchemeAsString());
                alias.setAttribute(LSDAttribute.NAME, aliasSubURI.getSubURI().asString());
                authorFountainEntityImpl = createAlias(null, alias, false, true, false, false);
                */
            } else {
                throw new EntityNotFoundException("Could not locate the author %s", author);
            }
        }
        fountainEntity.createRelationshipTo(authorFountainEntityImpl, FountainRelationships.AUTHOR);
    }

    @Nonnull
    @Override
    public FountainEntity createSession(@Nonnull final LiquidURI aliasUri, @Nonnull final ClientApplicationIdentifier clientApplicationIdentifier) throws InterruptedException {
        fountainNeo.begin();
        try {
            final FountainEntity fountainEntity = fountainNeo.createNode();

            fountainEntity.setIDIfNotSetOnNode();
            fountainEntity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.SESSION.getValue());
            fountainEntity.setAttribute(LSDAttribute.CLIENT_APPLICATION_NAME, clientApplicationIdentifier.getName());
            fountainEntity.setAttribute(LSDAttribute.CLIENT_APPLICATION_KEY, clientApplicationIdentifier.getKey());
            fountainEntity.setAttribute(LSDAttribute.CLIENT_HOST, clientApplicationIdentifier.getHostinfo());
            final LiquidPermissionSet sessionPermissionSet = LiquidPermissionSet.getMinimalPermissionSet();
            LiquidPermissionSet.addReadPermissions(LiquidPermissionScope.WORLD, sessionPermissionSet);
            fountainEntity.setAttribute(LSDAttribute.PERMISSIONS, sessionPermissionSet.toString());
            fountainEntity.setAttribute(LSDAttribute.ACTIVE, true);

            final FountainEntity ownerFountainEntityImpl;
            final FountainEntity userFountainEntity;
            if ("cazcade".equals(aliasUri.getSubURI().getSchemeAsString())) {
                ownerFountainEntityImpl = fountainNeo.findByURI(aliasUri);
                if (ownerFountainEntityImpl == null) {
                    throw new EntityNotFoundException("Could not find alias for %s", aliasUri);
                }
                userFountainEntity = fountainNeo.findByURI(new LiquidURI(LiquidURIScheme.user, aliasUri.getSubURI().getSubURI()));
                if (userFountainEntity == null) {
                    throw new EntityNotFoundException("Could not find user for %s", aliasUri);
                }
            } else {
                final FountainEntity otherNetworkAlias = fountainNeo.findByURI(aliasUri);
                if (otherNetworkAlias == null) {
                    throw new EntityNotFoundException("Could not find alias for %s", aliasUri);
                }
                final FountainRelationship userRelationship = otherNetworkAlias.getSingleRelationship(FountainRelationships.ALIAS, Direction.OUTGOING);
                userFountainEntity = userRelationship.getEndNode();
                ownerFountainEntityImpl = fountainNeo.findByURI(new LiquidURI(LiquidURIScheme.alias, "cazcade:" + userFountainEntity.getAttribute(LSDAttribute.NAME)));
                if (ownerFountainEntityImpl == null) {
                    throw new EntityNotFoundException("Could not owner for alias %s", aliasUri);
                }
            }


            //noinspection PointlessBooleanExpression
            if (userFountainEntity.hasAttribute(LSDAttribute.SECURITY_RESTRICTED) && USER_MUST_CONFIRM_EMAIL) {
                final String restricted = userFountainEntity.getAttribute(LSDAttribute.SECURITY_RESTRICTED);
                if ("true".equals(restricted)) {
                    throw new UserRestrictedException("User account for alias %s is restricted.", aliasUri);
                }
            }

            //Remove stale sessions
            final Iterable<FountainRelationship> existingSessionRelationships = ownerFountainEntityImpl.getRelationships(FountainRelationships.HAS_SESSION, Direction.OUTGOING);
            for (final FountainRelationship existingSessionRelationship : existingSessionRelationships) {
                final FountainEntity sessionFountainEntity = existingSessionRelationship.getOtherNode(ownerFountainEntityImpl);
                final long updated = sessionFountainEntity.getUpdated().getTime();
                final boolean active = sessionFountainEntity.getBooleanAttribute(LSDAttribute.ACTIVE);
                if (updated < System.currentTimeMillis() - FountainNeoImpl.SESSION_EXPIRES_MILLI) {
                    if (!active) {
                        existingSessionRelationship.delete();
                        for (final FountainRelationship relationship : sessionFountainEntity.getRelationships()) {
                            relationship.delete();
                        }
                        sessionFountainEntity.deleteNeo();
                    }
                } else if (updated < System.currentTimeMillis() - FountainNeoImpl.SESSION_INACTIVE_MILLI) {
                    sessionFountainEntity.setAttribute(LSDAttribute.ACTIVE, false);
                }
            }
            fountainEntity.setAttribute(LSDAttribute.NAME, userFountainEntity.getAttribute(LSDAttribute.NAME));
            fountainEntity.createRelationshipTo(ownerFountainEntityImpl, FountainRelationships.OWNER);
            ownerFountainEntityImpl.createRelationshipTo(fountainEntity, FountainRelationships.HAS_SESSION);
            fountainEntity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.session, fountainEntity.getAttribute(LSDAttribute.ID)).toString());
            fountainNeo.indexBy(fountainEntity, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(fountainEntity, LSDAttribute.URI, LSDAttribute.URI, true);
            fountainEntity.timestamp();
            return fountainEntity;
        } finally {
            fountainNeo.end();
        }
    }


    @Nullable
    @Override
    public LSDEntity getAliasFromNode(@Nonnull final FountainEntity fountainEntity, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            return fountainEntity.convertNodeToLSD(detail, internal);
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
                final FountainEntity fountainEntity = fountainNeo.findByUUID(target);
                final FountainEntity ownerFountainEntity = fountainNeo.findByURI(identity.getUserURL());
                final Iterable<FountainRelationship> relationships = fountainEntity.getRelationships(FountainRelationships.ALIAS, Direction.OUTGOING);
                boolean deleted = false;
                for (final FountainRelationship relationship : relationships) {
                    if (relationship.getOtherNode(fountainEntity).equals(ownerFountainEntity)) {
                        deleted = true;
                        relationship.delete();
                    }
                }
                if (!deleted) {
                    throw new RelationshipNotFoundException("{0} does not have alias {1}", identity.getName(), fountainEntity.getAttribute(LSDAttribute.URI));
                }
                transaction.success();
                return fountainEntity.convertNodeToLSD(detail, internal);
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


    public void setFountainNeo(final FountainNeoImpl fountainNeo) {
        this.fountainNeo = fountainNeo;
    }
}