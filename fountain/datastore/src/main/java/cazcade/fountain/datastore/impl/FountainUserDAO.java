/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;

/**
 * Note the Tx suffix means that a transaction is created for the method call.
 *
 * @author neilellis@cazcade.com
 */
public interface FountainUserDAO {
    void addAuthorToNodeNoTX(LiquidURI author, boolean createAuthor, LSDPersistedEntity persistedEntity) throws InterruptedException;

    boolean confirmHash(LiquidURI author, String changePasswordSecurityHash) throws Exception;

    @Nonnull
    LSDPersistedEntity createAlias(LSDPersistedEntity userPersistedEntity, LSDTransferEntity entity, boolean me, boolean orupdate, boolean claim, boolean systemUser) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity createSession(LiquidURI aliasUri, ClientApplicationIdentifier clientApplicationIdentifier) throws InterruptedException;

    //    LSDPersistedEntity createAlias(LSDTransferEntity entity) throws InterruptedException;

    LSDPersistedEntity createUser(LSDTransferEntity user, boolean systemUser) throws InterruptedException, UnsupportedEncodingException;

    /**
     * Performs some action on each user in the system, there is no guarantees of sequence or timeliness here, just that
     * it will be executed eventually. This is a readonly method.
     *
     * @param callback the callback to call.
     */
    void forEachUser(UserCallback callback);


    //Migrate from LSDPersistedEntity based methods, parameters should include URIs and return LSDTransferEntity where appropriate

    @Nonnull
    LSDTransferEntity getAliasFromNode(LSDPersistedEntity persistedEntity, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

    void sendPasswordChangeRequest(LiquidURI userURL) throws Exception;


    @Nullable
    LSDTransferEntity unlinkAliasTX(LiquidSessionIdentifier identity, LiquidUUID target, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    interface UserCallback {
        /**
         * @param userEntity  the entity for the user.
         * @param aliasEntity the entity for the primary alias node.
         */
        void process(LSDTransferEntity userEntity, LSDTransferEntity aliasEntity) throws Exception;
    }
}
