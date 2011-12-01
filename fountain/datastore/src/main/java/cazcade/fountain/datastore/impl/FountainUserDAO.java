package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;

/**
 * Note the Tx suffix means that a transaction is created for the method call.
 *
 * @author neilellis@cazcade.com
 */
public interface FountainUserDAO {


    @Nullable
    LSDEntity unlinkAliasTX(LiquidSessionIdentifier identity, LiquidUUID target, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    //Migrate from FountainEntity based methods, parameters should include URIs and return LSDEntity where appropriate

    @Nullable
    LSDEntity getAliasFromNode(FountainEntity fountainEntity, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

//    FountainEntity createAlias(LSDEntity entity) throws InterruptedException;

    FountainEntity createUser(LSDEntity user, boolean systemUser) throws InterruptedException, UnsupportedEncodingException;

    @Nullable
    FountainEntity createAlias(FountainEntity userFountainEntity, LSDEntity entity, boolean me, boolean orupdate, boolean claim, boolean systemUser) throws InterruptedException;

    @Nonnull
    FountainEntity createSession(LiquidURI aliasUri, ClientApplicationIdentifier clientApplicationIdentifier) throws InterruptedException;

    void addAuthorToNodeNoTX(LiquidURI author, boolean createAuthor, FountainEntity fountainEntity) throws InterruptedException;

    /**
     * Performs some action on each user in the system, there is no guarantees of sequence or timeliness here, just that
     * it will be executed eventually. This is a readonly method.
     *
     * @param callback the callback to call.
     */
    void forEachUser(UserCallback callback);

    boolean confirmHash(LiquidURI author, String changePasswordSecurityHash) throws Exception;

    void sendPasswordChangeRequest(LiquidURI userURL) throws Exception;


    interface UserCallback {
        /**
         * @param userEntity  the entity for the user.
         * @param aliasEntity the entity for the primary alias node.
         */
        void process(LSDEntity userEntity, LSDEntity aliasEntity) throws Exception;
    }


}
