/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.fountain.datastore.api.DuplicateEntityException;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.api.RelationshipNotFoundException;
import cazcade.fountain.datastore.api.UserRestrictedException;
import cazcade.fountain.datastore.impl.*;
import cazcade.fountain.index.persistence.dao.AliasDAO;
import cazcade.fountain.index.persistence.entities.AliasEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

import static cazcade.liquid.api.PermissionScope.WORLD_SCOPE;
import static org.neo4j.graphdb.Direction.OUTGOING;

public class FountainUserDAOImpl implements FountainUserDAO {
    @Nonnull
    public static final  String                 USER_HASH_SALT          = "EverythingThatArisesMustPass";
    public static final  boolean                USER_MUST_CONFIRM_EMAIL = false;
    @Nonnull
    private static final StandardStringDigester digester                = new StandardStringDigester();
    private final        Logger                 log                     = LoggerFactory.getLogger(FountainUserDAOImpl.class);
    @Autowired
    private FountainNeo          fountainNeo;
    @Autowired
    private AliasDAO             aliasDAO;
    @Autowired
    private FountainEmailService emailService;

    public void addAuthorToNodeNoTX(@Nonnull final LiquidURI author, final boolean createAuthor, @Nonnull final PersistedEntity persistedEntity) throws InterruptedException {
        final PersistedEntity authorPersistedEntityImpl = fountainNeo.find(author);
        if (authorPersistedEntityImpl == null) {
            if (createAuthor) {
                throw new UnsupportedOperationException("Feature no longer supported.");
                /*
                SimpleEntity alias = SimpleEntity.createEmpty();
                LiquidURI aliasSubURI = author.sub();
                alias.setValue(FountainNeo.PERMISSIONS, PermissionSet.getMinimalPermissionSet().toString());
                alias.setType(Types.ALIAS);
                alias.$(Attribute.NETWORK, aliasSubURI.schemeString());
                alias.$(Attribute.NAME, aliasSubURI.sub().asString());
                authorPersistedEntityImpl = createAlias(null, alias, false, true, false, false);
                */
            } else {
                throw new EntityNotFoundException("Could not locate the author %s", author);
            }
        }
        persistedEntity.relate(authorPersistedEntityImpl, FountainRelationships.AUTHOR);
    }

    @Override
    public boolean confirmHash(@Nonnull final LiquidURI user, final String changePasswordSecurityHash) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final PersistedEntity userPersistedEntity = fountainNeo.findOrFail(user);
                final String hashString = createUserHashableString(userPersistedEntity);
                return FountainUserDAOImpl.digester.matches(hashString, changePasswordSecurityHash);
            }
        });
    }

    @Nonnull @Override
    public PersistedEntity createAlias(@Nonnull final PersistedEntity user, @Nonnull final TransferEntity aliasEntity, final boolean me, final boolean orupdate, final boolean claim, final boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LiquidURI aliasURI;
            final String name = aliasEntity.$(Dictionary.NAME).toLowerCase();
            final String network = aliasEntity.has$(Dictionary.NETWORK) ? aliasEntity.$(Dictionary.NETWORK) : "cazcade";
            aliasURI = new LiquidURI(LiquidURIScheme.alias, network + ':' + name);
            final PersistedEntity existingPersistedEntityImpl = fountainNeo.find(aliasURI);
            if (existingPersistedEntityImpl != null && !orupdate) {
                throw new DuplicateEntityException("Attempted to create an alias that exists already without first setting the 'orupdate' flag to 'true'.");
            }
            final PersistedEntity alias;
            final String uriString = aliasURI.asString();
            if (existingPersistedEntityImpl == null) {
                alias = fountainNeo.createNode();
                if (me) {
                    alias.relate(user, FountainRelationships.ALIAS);
                    alias.relate(user, FountainRelationships.OWNER);
                }

                alias.mergeProperties(aliasEntity, false, false, null);
                fountainNeo.freeTextIndexNoTx(alias);

                alias.$(Dictionary.PERMISSIONS, PermissionSet.getMinimalPermissionSet().toString());
                alias.setIDIfNotSetOnNode();
                alias.$(Dictionary.URI, uriString);
                final LiquidURI networkURI = new LiquidURI(LiquidURIScheme.network, network);
                PersistedEntity networkPersistedEntityImpl = fountainNeo.find(networkURI);
                if (networkPersistedEntityImpl == null) {
                    networkPersistedEntityImpl = createSocialNetwork(networkURI);
                }
                alias.relate(networkPersistedEntityImpl, FountainRelationships.NETWORK_MEMBER);
                fountainNeo.indexBy(alias, Dictionary.ID, Dictionary.ID, true);
                fountainNeo.indexBy(alias, Dictionary.URI, Dictionary.URI, true);
            } else {
                alias = existingPersistedEntityImpl;
                alias.mergeProperties(aliasEntity, true, false, null);
                fountainNeo.freeTextIndexNoTx(alias);
                if (me) {
                    Iterable<FountainRelationship> relationships = alias.relationships(FountainRelationships.ALIAS, OUTGOING);
                    for (final FountainRelationship relationship : relationships) {
                        //todo: throw an exception instead!
                        relationship.delete();
                    }
                    alias.relate(user, FountainRelationships.ALIAS);
                }
            }
            if (claim) {
                user.relate(alias, FountainRelationships.CLAIMED);
            }
            alias.timestamp();
            return alias;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public PersistedEntity createSession(@Nonnull final LiquidURI aliasUri, @Nonnull final ClientApplicationIdentifier cai) throws InterruptedException {
        fountainNeo.begin();
        try {
            final FountainEntity persistedEntity = fountainNeo.createNode();
            final PermissionSet sessionPermissionSet = PermissionSet.getMinimalPermissionSet();
            PermissionSet.addReadPermissions(WORLD_SCOPE, sessionPermissionSet);

            persistedEntity.setIDIfNotSetOnNode()
                           .$(Dictionary.TYPE, Types.T_SESSION.getValue())
                           .$(Dictionary.CLIENT_APPLICATION_NAME, cai.getName())
                           .$(Dictionary.CLIENT_APPLICATION_KEY, cai.getKey())
                           .$(Dictionary.CLIENT_HOST, cai.getHostinfo())
                           .$(Dictionary.PERMISSIONS, sessionPermissionSet.toString())
                           .$(Dictionary.ACTIVE, true);

            final PersistedEntity owner;
            final PersistedEntity user;
            if ("cazcade".equals(aliasUri.sub().schemeString())) {
                owner = fountainNeo.findOrFail(aliasUri);
                user = fountainNeo.findOrFail(new LiquidURI(LiquidURIScheme.user, aliasUri.sub().sub()));
            } else {
                final PersistedEntity otherNetworkAlias = fountainNeo.findOrFail(aliasUri);
                final FountainRelationship userRelationship = otherNetworkAlias.relationship(FountainRelationships.ALIAS, OUTGOING);
                assert userRelationship != null;
                user = userRelationship.end();
                owner = fountainNeo.find(new LiquidURI(LiquidURIScheme.alias, "cazcade:" + user.$(Dictionary.NAME)));
                if (owner == null) {
                    throw new EntityNotFoundException("Could not owner for alias %s", aliasUri);
                }
            }


            //noinspection PointlessBooleanExpression,ConstantConditions
            if (user.has$(Dictionary.SECURITY_RESTRICTED) && USER_MUST_CONFIRM_EMAIL) {
                final String restricted = user.$(Dictionary.SECURITY_RESTRICTED);
                if ("true".equals(restricted)) {
                    throw new UserRestrictedException("User account for alias %s is restricted.", aliasUri);
                }
            }

            //Remove stale sessions
            final Iterable<FountainRelationship> existingSessionRelationships = owner.relationships(FountainRelationships.HAS_SESSION, OUTGOING);
            for (final FountainRelationship existingSessionRelationship : existingSessionRelationships) {
                final PersistedEntity session = existingSessionRelationship.other(owner);
                final long updated = session.updated().getTime();
                final boolean active = session.$bool(Dictionary.ACTIVE);
                if (updated < System.currentTimeMillis() - FountainNeoImpl.SESSION_EXPIRES_MILLI) {
                    if (!active) {
                        existingSessionRelationship.delete();
                        Iterable<FountainRelationship> relationships = session.relationships();
                        for (final FountainRelationship relationship : relationships) {
                            relationship.delete();
                        }
                        session.hardDelete();
                    }
                } else if (updated < System.currentTimeMillis() - FountainNeoImpl.SESSION_INACTIVE_MILLI) {
                    session.$(Dictionary.ACTIVE, false);
                }
            }
            persistedEntity.$(Dictionary.NAME, user.$(Dictionary.NAME))
                           .$(Dictionary.URI, new LiquidURI(LiquidURIScheme.session, persistedEntity.$(Dictionary.ID)).toString())
                           .timestamp()
                           .relate(owner, FountainRelationships.OWNER);
            owner.relate(persistedEntity, FountainRelationships.HAS_SESSION);
            fountainNeo.indexBy(persistedEntity, Dictionary.ID, Dictionary.ID, true);
            fountainNeo.indexBy(persistedEntity, Dictionary.URI, Dictionary.URI, true);
            return persistedEntity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public PersistedEntity createUser(@Nonnull final TransferEntity entity, final boolean systemUser) throws InterruptedException, UnsupportedEncodingException {
        fountainNeo.begin();
        try {
            final PersistedEntity userPersistedEntity;
            final String username = entity.$(Dictionary.NAME).toLowerCase();
            final LiquidURI userURI = new LiquidURI(LiquidURIScheme.user, username);
            final LiquidURI aliasURI = new LiquidURI(LiquidURIScheme.alias, "cazcade:" + username);
            if (fountainNeo.find(userURI) != null) {
                throw new DuplicateEntityException("Attempted to create a user (" + username + ") that already exists.");
            }
            userPersistedEntity = createUserInternal(entity, systemUser);
            if (!systemUser) {
                final String plainPassword = entity.$(Dictionary.PLAIN_PASSWORD);
                final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                final String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);
                userPersistedEntity.$(Dictionary.HASHED_AND_SALTED_PASSWORD, encryptedPassword);
                emailService.sendRegistrationEmail(entity);
            }

            return userPersistedEntity;
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
                    final PersistedEntity aliasPersistedEntity = fountainNeo.find(new LiquidURI(alias.getUri()));
                    if (aliasPersistedEntity == null) {
                        log.warn("Skipping " + uri + " as alias node not found.");
                        return;
                    }
                    final TransferEntity aliasEntity = getAliasFromNode(aliasPersistedEntity, true, RequestDetailLevel.COMPLETE);
                    final FountainRelationship ownerRel = aliasPersistedEntity.relationship(FountainRelationships.ALIAS, OUTGOING);
                    if (ownerRel == null) {
                        log.warn("No owner for alias " + uri);
                    } else {
                        final TransferEntity userEntity = ownerRel.end().toTransfer(RequestDetailLevel.COMPLETE, true);
                        callback.process(userEntity, aliasEntity);
                    }
                }
            }
        });
    }

    @Nonnull @Override
    public TransferEntity getAliasFromNode(@Nonnull final PersistedEntity persistedEntity, final boolean internal, final RequestDetailLevel detail) throws Exception {
        return fountainNeo.doInBeginBlock(new Callable<TransferEntity>() {
            @Override public TransferEntity call() throws Exception {
                return persistedEntity.toTransfer(detail, internal);
            }
        });
    }

    @Override
    public void sendPasswordChangeRequest(@Nonnull final LiquidURI userURI) throws Exception {
        fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
            @Nullable @Override
            public Object call() throws Exception {
                final PersistedEntity user = fountainNeo.findOrFail(userURI);
                final TransferEntity userEntity = user.toTransfer(RequestDetailLevel.COMPLETE, true);

                emailService.sendChangePasswordRequest(userEntity, FountainUserDAOImpl.digester
                                                                                      .digest(createUserHashableString(user)));
                return null;
            }
        });
    }

    @Nullable @Override @Deprecated()
    public TransferEntity unlinkAliasTX(@Nonnull final SessionIdentifier identity, @Nonnull final LiquidUUID targetUUID, final boolean internal, final RequestDetailLevel detail) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<TransferEntity>() {
            @Override public TransferEntity call() throws Exception {
                final PersistedEntity target = fountainNeo.find(targetUUID);
                final PersistedEntity owner = fountainNeo.find(identity.userURL());
                boolean deleted = false;
                Iterable<FountainRelationship> relationships = target.relationships(FountainRelationships.ALIAS, OUTGOING);
                for (final FountainRelationship relationship : relationships) {
                    if (relationship.other(target).equals(owner)) {
                        deleted = true;
                        relationship.delete();
                    }
                }
                if (!deleted) {
                    throw new RelationshipNotFoundException("{0} does not have alias {1}", identity.name(), target.$(Dictionary.URI));
                }
                return target.toTransfer(detail, internal);
            }
        });
    }

    @Nonnull
    private String createUserHashableString(@Nonnull final PersistedEntity user) {
        return user.$(Dictionary.ID)
               + ":"
               + user.$(Dictionary.HASHED_AND_SALTED_PASSWORD)
               + ":"
               + FountainUserDAOImpl.USER_HASH_SALT;
    }

    @Nonnull
    private PersistedEntity createUserInternal(@Nonnull final TransferEntity entity, final boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {
            final String username = entity.$(Dictionary.NAME).toLowerCase();
            final PersistedEntity user = fountainNeo.createNode()
                                                    .$(Dictionary.PERMISSIONS, PermissionSet.getMinimalPermissionSet().toString())
                                                    .timestamp()
                                                    .$(Dictionary.URI, new LiquidURI(LiquidURIScheme.user, username).asString())
                                                    .mergeProperties(entity, false, false, null)
                                                    .setIDIfNotSetOnNode();
            fountainNeo.freeTextIndexNoTx(user);
            fountainNeo.indexBy(user, Dictionary.ID, Dictionary.ID, true);
            fountainNeo.indexBy(user, Dictionary.URI, Dictionary.URI, true);

            //create the associated alias

            if (fountainNeo.find(new LiquidURI(LiquidURIScheme.alias, new LiquidURI("cazcade:" + username))) != null) {
                throw new DuplicateEntityException("Attempted to create an alias for a user, but that alias already exists .");
            }

            createAlias(user, SimpleEntity.create(Types.T_ALIAS)
                                          .$(Dictionary.PERMISSIONS, PermissionSet.getMinimalPermissionSet().toString())
                                          .$(Dictionary.FULL_NAME, entity.$(Dictionary.FULL_NAME))
                                          .$(Dictionary.NETWORK, "cazcade")
                                          .$(Dictionary.NAME, username)
                                          .$(entity, Dictionary.IMAGE_URL), true, true, false, systemUser);
            return user;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull PersistedEntity createSocialNetwork(@Nonnull final LiquidURI uri) throws InterruptedException {
        final PersistedEntity entity = fountainNeo.createNode()
                                                  .$(Dictionary.PERMISSIONS, PermissionSet.getMinimalPermissionSet().toString())
                                                  .setIDIfNotSetOnNode()
                                                  .$(Dictionary.TYPE, Types.T_SOCIAL_NETWORK.getValue())
                                                  .$(Dictionary.NAME, uri.sub().asString())
                                                  .$(Dictionary.URI, uri.asString())
                                                  .timestamp();
        fountainNeo.indexBy(entity, Dictionary.ID, Dictionary.ID, true);
        fountainNeo.indexBy(entity, Dictionary.URI, Dictionary.URI, true);
        return entity;
    }

    public void setFountainNeo(final FountainNeoImpl fountainNeo) {
        this.fountainNeo = fountainNeo;
    }
}