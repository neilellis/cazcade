/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;

/**
 * Note the Tx suffix means that a transaction is created for the method call.
 *
 * @author neilellis@cazcade.com
 */
public interface FountainUserDAO {
    void addAuthorToNodeNoTX(LURI author, boolean createAuthor, PersistedEntity persistedEntity) throws InterruptedException;

    boolean confirmHash(LURI author, String changePasswordSecurityHash) throws Exception;

    @Nonnull
    PersistedEntity createAlias(PersistedEntity userPersistedEntity, TransferEntity entity, boolean me, boolean orupdate, boolean claim, boolean systemUser) throws InterruptedException;

    @Nonnull
    PersistedEntity createSession(LURI aliasUri, ClientApplicationIdentifier clientApplicationIdentifier) throws InterruptedException;

    //    PersistedEntity createAlias(TransferEntity entity) throws InterruptedException;

    PersistedEntity createUser(TransferEntity user, boolean systemUser) throws InterruptedException, UnsupportedEncodingException;

    /**
     * Performs some action on each user in the system, there is no guarantees of sequence or timeliness here, just that
     * it will be executed eventually. This is a readonly method.
     *
     * @param callback the callback to call.
     */
    void forEachUser(UserCallback callback);


    //Migrate from PersistedEntity based methods, parameters should include URIs and return TransferEntity where appropriate

    @Nonnull
    TransferEntity getAliasFromNode(PersistedEntity persistedEntity, boolean internal, RequestDetailLevel detail) throws Exception;

    void sendPasswordChangeRequest(LURI userURL) throws Exception;


    @Nullable
    TransferEntity unlinkAliasTX(SessionIdentifier identity, LiquidUUID target, boolean internal, RequestDetailLevel detail) throws Exception;


    interface UserCallback {
        /**
         * @param userEntity  the entity for the user.
         * @param aliasEntity the entity for the primary alias node.
         */
        void process(TransferEntity userEntity, TransferEntity aliasEntity) throws Exception;
    }
}
