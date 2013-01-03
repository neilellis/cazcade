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
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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
    @Nonnull
    public static final String USER_HASH_SALT = "EverythingThatArisesMustPass";


    public static final boolean USER_MUST_CONFIRM_EMAIL = false;

    @Nonnull
    private static final StandardStringDigester digester = new StandardStringDigester();

    private final Logger log = LoggerFactory.getLogger(FountainUserDAOImpl.class);

    @Autowired
    private FountainNeo fountainNeo;

    @Autowired
    private AliasDAO aliasDAO;

    @Autowired
    private FountainEmailService emailService;

    public void addAuthorToNodeNoTX(@Nonnull final LiquidURI author, final boolean createAuthor,
                                    @Nonnull final LSDPersistedEntity persistedEntity) throws InterruptedException {
        final LSDPersistedEntity authorPersistedEntityImpl = fountainNeo.findByURI(author);
        if (authorPersistedEntityImpl == null) {
            if (createAuthor) {
                throw new UnsupportedOperationException("Feature no longer supported.");
                /*
                LSDSimpleEntity alias = LSDSimpleEntity.createEmpty();
                LiquidURI aliasSubURI = author.getSubURI();
                alias.setValue(FountainNeo.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
                alias.setType(LSDDictionaryTypes.ALIAS);
                alias.setAttribute(LSDAttribute.NETWORK, aliasSubURI.getSchemeAsString());
                alias.setAttribute(LSDAttribute.NAME, aliasSubURI.getSubURI().asString());
                authorPersistedEntityImpl = createAlias(null, alias, false, true, false, false);
                */
            }
            else {
                throw new EntityNotFoundException("Could not locate the author %s", author);
            }
        }
        persistedEntity.createRelationshipTo(authorPersistedEntityImpl, FountainRelationships.AUTHOR);
    }

    @Override
    public boolean confirmHash(@Nonnull final LiquidURI user, final String changePasswordSecurityHash) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final LSDPersistedEntity userPersistedEntity = fountainNeo.findByURI(user);
                final String hashString = createUserHashableString(userPersistedEntity);
                return FountainUserDAOImpl.digester.matches(hashString, changePasswordSecurityHash);
            }
        }
                                                       );
    }

    @Nonnull
    private String createUserHashableString(@Nonnull final LSDPersistedEntity userPersistedEntity) {
        final Object id = userPersistedEntity.getAttribute(LSDAttribute.ID);
        final Object password = userPersistedEntity.getAttribute(LSDAttribute.HASHED_AND_SALTED_PASSWORD);
        return id + ":" + password + ":" + FountainUserDAOImpl.USER_HASH_SALT;
    }

    @Nonnull
    @Override
    public LSDPersistedEntity createSession(@Nonnull final LiquidURI aliasUri,
                                            @Nonnull final ClientApplicationIdentifier clientApplicationIdentifier)
            throws InterruptedException {
        fountainNeo.begin();
        try {
            final LSDPersistedEntity persistedEntity = fountainNeo.createNode();

            persistedEntity.setIDIfNotSetOnNode();
            persistedEntity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.SESSION.getValue());
            persistedEntity.setAttribute(LSDAttribute.CLIENT_APPLICATION_NAME, clientApplicationIdentifier.getName());
            persistedEntity.setAttribute(LSDAttribute.CLIENT_APPLICATION_KEY, clientApplicationIdentifier.getKey());
            persistedEntity.setAttribute(LSDAttribute.CLIENT_HOST, clientApplicationIdentifier.getHostinfo());
            final LiquidPermissionSet sessionPermissionSet = LiquidPermissionSet.getMinimalPermissionSet();
            LiquidPermissionSet.addReadPermissions(LiquidPermissionScope.WORLD, sessionPermissionSet);
            persistedEntity.setAttribute(LSDAttribute.PERMISSIONS, sessionPermissionSet.toString());
            persistedEntity.setAttribute(LSDAttribute.ACTIVE, true);

            final LSDPersistedEntity ownerPersistedEntityImpl;
            final LSDPersistedEntity userPersistedEntity;
            if ("cazcade".equals(aliasUri.getSubURI().getSchemeAsString())) {
                ownerPersistedEntityImpl = fountainNeo.findByURI(aliasUri);
                if (ownerPersistedEntityImpl == null) {
                    throw new EntityNotFoundException("Could not find alias for %s", aliasUri);
                }
                userPersistedEntity = fountainNeo.findByURI(new LiquidURI(LiquidURIScheme.user, aliasUri.getSubURI().getSubURI()));
                if (userPersistedEntity == null) {
                    throw new EntityNotFoundException("Could not find user for %s", aliasUri);
                }
            }
            else {
                final LSDPersistedEntity otherNetworkAlias = fountainNeo.findByURI(aliasUri);
                if (otherNetworkAlias == null) {
                    throw new EntityNotFoundException("Could not find alias for %s", aliasUri);
                }
                final FountainRelationship userRelationship = otherNetworkAlias.getSingleRelationship(FountainRelationships.ALIAS,
                                                                                                      Direction.OUTGOING
                                                                                                     );
                userPersistedEntity = userRelationship.getEndNode();
                ownerPersistedEntityImpl = fountainNeo.findByURI(new LiquidURI(LiquidURIScheme.alias,
                                                                               "cazcade:" + userPersistedEntity.getAttribute(
                                                                                       LSDAttribute.NAME
                                                                                                                            )
                )
                                                                );
                if (ownerPersistedEntityImpl == null) {
                    throw new EntityNotFoundException("Could not owner for alias %s", aliasUri);
                }
            }


            //noinspection PointlessBooleanExpression
            if (userPersistedEntity.hasAttribute(LSDAttribute.SECURITY_RESTRICTED) && USER_MUST_CONFIRM_EMAIL) {
                final String restricted = userPersistedEntity.getAttribute(LSDAttribute.SECURITY_RESTRICTED);
                if ("true".equals(restricted)) {
                    throw new UserRestrictedException("User account for alias %s is restricted.", aliasUri);
                }
            }

            //Remove stale sessions
            final Iterable<FountainRelationship> existingSessionRelationships = ownerPersistedEntityImpl.getRelationships(
                    FountainRelationships.HAS_SESSION, Direction.OUTGOING
                                                                                                                         );
            for (final FountainRelationship existingSessionRelationship : existingSessionRelationships) {
                final LSDPersistedEntity sessionPersistedEntity = existingSessionRelationship.getOtherNode(ownerPersistedEntityImpl
                                                                                                          );
                final long updated = sessionPersistedEntity.getUpdated().getTime();
                final boolean active = sessionPersistedEntity.getBooleanAttribute(LSDAttribute.ACTIVE);
                if (updated < System.currentTimeMillis() - FountainNeoImpl.SESSION_EXPIRES_MILLI) {
                    if (!active) {
                        existingSessionRelationship.delete();
                        for (final FountainRelationship relationship : sessionPersistedEntity.getRelationships()) {
                            relationship.delete();
                        }
                        sessionPersistedEntity.hardDelete();
                    }
                }
                else if (updated < System.currentTimeMillis() - FountainNeoImpl.SESSION_INACTIVE_MILLI) {
                    sessionPersistedEntity.setAttribute(LSDAttribute.ACTIVE, false);
                }
            }
            persistedEntity.setAttribute(LSDAttribute.NAME, userPersistedEntity.getAttribute(LSDAttribute.NAME));
            persistedEntity.createRelationshipTo(ownerPersistedEntityImpl, FountainRelationships.OWNER);
            ownerPersistedEntityImpl.createRelationshipTo(persistedEntity, FountainRelationships.HAS_SESSION);
            persistedEntity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.session, persistedEntity.getAttribute(
                    LSDAttribute.ID
                                                                                                                              )
            ).toString()
                                        );
            fountainNeo.indexBy(persistedEntity, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(persistedEntity, LSDAttribute.URI, LSDAttribute.URI, true);
            persistedEntity.timestamp();
            return persistedEntity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    @Override
    public LSDPersistedEntity createUser(@Nonnull final LSDTransferEntity entity, final boolean systemUser)
            throws InterruptedException, UnsupportedEncodingException {
        fountainNeo.begin();
        try {
            final LSDPersistedEntity userPersistedEntity;
            final String username = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            final LiquidURI userURI = new LiquidURI(LiquidURIScheme.user, username);
            final LiquidURI aliasURI = new LiquidURI(LiquidURIScheme.alias, "cazcade:" + username);
            if (fountainNeo.findByURI(userURI) != null) {
                throw new DuplicateEntityException("Attempted to create a user (" + username + ") that already exists.");
            }
            userPersistedEntity = createUserInternal(entity, systemUser);
            if (!systemUser) {
                final String plainPassword = entity.getAttribute(LSDAttribute.PLAIN_PASSWORD);
                final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                final String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);
                userPersistedEntity.setAttribute(LSDAttribute.HASHED_AND_SALTED_PASSWORD, encryptedPassword);
                emailService.sendRegistrationEmail(entity);
            }

            return userPersistedEntity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    private LSDPersistedEntity createUserInternal(@Nonnull final LSDTransferEntity entity, final boolean systemUser)
            throws InterruptedException {
        fountainNeo.begin();
        try {
            final LSDPersistedEntity userPersistedEntityImpl = fountainNeo.createNode();
            userPersistedEntityImpl.setAttribute(LSDAttribute.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString()
                                                );
            userPersistedEntityImpl.timestamp();
            final String username = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            userPersistedEntityImpl.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.user, username).asString());
            userPersistedEntityImpl.mergeProperties(entity, false, false, null);
            fountainNeo.freeTextIndexNoTx(userPersistedEntityImpl);

            userPersistedEntityImpl.setIDIfNotSetOnNode();
            fountainNeo.indexBy(userPersistedEntityImpl, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(userPersistedEntityImpl, LSDAttribute.URI, LSDAttribute.URI, true);

            //create the associated alias

            final LiquidURI uri = new LiquidURI(LiquidURIScheme.alias, new LiquidURI("cazcade:" + username));
            final LSDPersistedEntity existingPersistedEntity = fountainNeo.findByURI(uri);
            if (existingPersistedEntity != null) {
                throw new DuplicateEntityException("Attempted to create an alias for a user, but that alias already exists .");
            }

            final LSDTransferEntity alias = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.ALIAS);
            alias.setAttribute(LSDAttribute.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString());
            alias.setAttribute(LSDAttribute.FULL_NAME, entity.getAttribute(LSDAttribute.FULL_NAME));
            if (entity.hasAttribute(LSDAttribute.IMAGE_URL)) {
                alias.setAttribute(LSDAttribute.IMAGE_URL, entity.getAttribute(LSDAttribute.IMAGE_URL));
            }
            alias.setAttribute(LSDAttribute.NETWORK, "cazcade");
            alias.setAttribute(LSDAttribute.NAME, username);
            createAlias(userPersistedEntityImpl, alias, true, true, false, systemUser);
            return userPersistedEntityImpl;
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable
    @Override
    public LSDPersistedEntity createAlias(@Nonnull final LSDPersistedEntity userPersistedEntityImpl,
                                          @Nonnull final LSDTransferEntity entity, final boolean me, final boolean orupdate,
                                          final boolean claim, final boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LiquidURI aliasURI;
            final String name = entity.getAttribute(LSDAttribute.NAME).toLowerCase();
            final String network;
            if (entity.hasAttribute(LSDAttribute.NETWORK)) {
                network = entity.getAttribute(LSDAttribute.NETWORK);
            }
            else {
                network = "cazcade";
            }
            aliasURI = new LiquidURI(LiquidURIScheme.alias, network + ':' + name);
            final LSDPersistedEntity existingPersistedEntityImpl = fountainNeo.findByURI(aliasURI);
            if (existingPersistedEntityImpl != null && !orupdate) {
                throw new DuplicateEntityException(
                        "Attempted to create an alias that exists already without first setting the 'orupdate' flag to 'true'."
                );
            }
            final LSDPersistedEntity persistedEntityImpl;
            final String uriString = aliasURI.asString();
            if (existingPersistedEntityImpl == null) {
                persistedEntityImpl = fountainNeo.createNode();
                if (me) {
                    persistedEntityImpl.createRelationshipTo(userPersistedEntityImpl, FountainRelationships.ALIAS);
                    persistedEntityImpl.createRelationshipTo(userPersistedEntityImpl, FountainRelationships.OWNER);
                }

                persistedEntityImpl.mergeProperties(entity, false, false, null);
                fountainNeo.freeTextIndexNoTx(persistedEntityImpl);

                persistedEntityImpl.setAttribute(LSDAttribute.PERMISSIONS, LiquidPermissionSet.getMinimalPermissionSet().toString()
                                                );
                persistedEntityImpl.setIDIfNotSetOnNode();
                persistedEntityImpl.setAttribute(LSDAttribute.URI, uriString);
                final LiquidURI networkURI = new LiquidURI(LiquidURIScheme.network, network);
                LSDPersistedEntity networkPersistedEntityImpl = fountainNeo.findByURI(networkURI);
                if (networkPersistedEntityImpl == null) {
                    networkPersistedEntityImpl = createSocialNetwork(networkURI);
                }
                persistedEntityImpl.createRelationshipTo(networkPersistedEntityImpl, FountainRelationships.NETWORK_MEMBER);
                fountainNeo.indexBy(persistedEntityImpl, LSDAttribute.ID, LSDAttribute.ID, true);
                fountainNeo.indexBy(persistedEntityImpl, LSDAttribute.URI, LSDAttribute.URI, true);
            }
            else {
                persistedEntityImpl = existingPersistedEntityImpl;
                persistedEntityImpl.mergeProperties(entity, true, false, null);
                fountainNeo.freeTextIndexNoTx(persistedEntityImpl);
                if (me) {
                    for (final FountainRelationship relationship : persistedEntityImpl.getRelationships(FountainRelationships.ALIAS,
                                                                                                        Direction.OUTGOING
                                                                                                       )) {
                        //todo: throw an exception instead!
                        relationship.delete();
                    }
                    persistedEntityImpl.createRelationshipTo(userPersistedEntityImpl, FountainRelationships.ALIAS);
                }
            }
            if (claim) {
                userPersistedEntityImpl.createRelationshipTo(persistedEntityImpl, FountainRelationships.CLAIMED);
            }
            persistedEntityImpl.timestamp();
            return persistedEntityImpl;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    LSDPersistedEntity createSocialNetwork(@Nonnull final LiquidURI uri) throws InterruptedException {
        final LSDPersistedEntity entity = fountainNeo.createNode();
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

    @Override
    public void forEachUser(@Nonnull final UserCallback callback) {
        aliasDAO.forEachUser(new AliasDAO.UserDAOCallback() {
            @Override
            public void process(@Nonnull final AliasEntity alias) throws Exception {
                final String uri = alias.getUri();
                if (uri.startsWith("alias:cazcade:")) {
                    final LSDPersistedEntity aliasPersistedEntity = fountainNeo.findByURI(new LiquidURI(alias.getUri()));
                    if (aliasPersistedEntity == null) {
                        log.warn("Skipping " + uri + " as alias node not found.");
                        return;
                    }
                    final LSDTransferEntity aliasEntity = getAliasFromNode(aliasPersistedEntity, true,
                                                                           LiquidRequestDetailLevel.COMPLETE
                                                                          );
                    final FountainRelationship ownerRel = aliasPersistedEntity.getSingleRelationship(FountainRelationships.ALIAS,
                                                                                                     Direction.OUTGOING
                                                                                                    );
                    if (ownerRel == null) {
                        log.warn("No owner for alias " + uri);
                    }
                    else {
                        final LSDTransferEntity userEntity = ownerRel.getEndNode().convertNodeToLSD(
                                LiquidRequestDetailLevel.COMPLETE, true
                                                                                                   );
                        callback.process(userEntity, aliasEntity);
                    }
                }
            }
        }
                            );
    }

    @Nullable
    @Override
    public LSDTransferEntity getAliasFromNode(@Nonnull final LSDPersistedEntity persistedEntity, final boolean internal,
                                              final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            return persistedEntity.convertNodeToLSD(detail, internal);
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public void sendPasswordChangeRequest(@Nonnull final LiquidURI userURI) throws Exception {
        fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                final LSDPersistedEntity userPersistedEntity = fountainNeo.findByURI(userURI);
                final LSDTransferEntity userEntity = userPersistedEntity.convertNodeToLSD(LiquidRequestDetailLevel.COMPLETE, true);

                emailService.sendChangePasswordRequest(userEntity, FountainUserDAOImpl.digester.digest(createUserHashableString(
                        userPersistedEntity
                                                                                                                               )
                                                                                                      )
                                                      );
                return null;
            }
        }
                                                );
    }

    @Nullable
    @Override
    public LSDTransferEntity unlinkAliasTX(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidUUID target,
                                           final boolean internal, final LiquidRequestDetailLevel detail)
            throws InterruptedException {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = fountainNeo.findByUUID(target);
                final LSDPersistedEntity ownerPersistedEntity = fountainNeo.findByURI(identity.getUserURL());
                final Iterable<FountainRelationship> relationships = persistedEntity.getRelationships(FountainRelationships.ALIAS,
                                                                                                      Direction.OUTGOING
                                                                                                     );
                boolean deleted = false;
                for (final FountainRelationship relationship : relationships) {
                    if (relationship.getOtherNode(persistedEntity).equals(ownerPersistedEntity)) {
                        deleted = true;
                        relationship.delete();
                    }
                }
                if (!deleted) {
                    throw new RelationshipNotFoundException("{0} does not have alias {1}", identity.getName(),
                                                            persistedEntity.getAttribute(LSDAttribute.URI)
                    );
                }
                transaction.success();
                return persistedEntity.convertNodeToLSD(detail, internal);
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