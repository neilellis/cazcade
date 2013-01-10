/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.api;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.request.*;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public interface FountainDataStoreFacade {
    // Create Operations (C of CRUD)

    @Nonnull LiquidMessage process(CreateUserRequest createUserRequest);

    @Nonnull LiquidMessage process(CreatePoolRequest createPoolRequest);

    @Nonnull LiquidMessage process(CreatePoolObjectRequest createPoolObjectRequest);

    @Nonnull LiquidMessage process(CreateSessionRequest createSessionRequest);

    @Nonnull LiquidMessage process(CreateAliasRequest createAliasRequest);

    // Retrieve Operations (R of CRUD)

    @Nonnull LiquidMessage process(RetrieveUserRequest retrieveUserRequest);

    @Nonnull LiquidMessage process(RetrievePoolRequest retrievePoolRequest);

    @Nonnull LiquidMessage process(RetrievePoolObjectRequest retrievePoolObjectRequest);

    @Nonnull LiquidMessage process(RetrieveSessionRequest retrieveSessionRequest);

    @Nonnull LiquidMessage process(RetrieveAliasRequest retrieveAliasRequest);

    // Update Operations (U of CRUD)

    @Nonnull LiquidMessage process(UpdateUserRequest updateUserRequest);

    @Nonnull LiquidMessage process(UpdatePoolRequest updatePoolRequest);

    @Nonnull LiquidMessage process(UpdatePoolObjectRequest updatePoolObjectRequest);

    @Nonnull LiquidMessage process(UpdateSessionRequest updateSessionRequest);

    @Nonnull LiquidMessage process(UpdateAliasRequest updateAliasRequest);

    // Delete Operations (D of CRUD)

    @Nonnull LiquidMessage process(DeleteUserRequest deleteUserRequest);

    @Nonnull LiquidMessage process(DeletePoolRequest deletePoolRequest);

    @Nonnull LiquidMessage process(DeletePoolObjectRequest deletePoolObjectRequest);

    @Nonnull LiquidMessage process(DeleteSessionRequest deleteSessionRequest);

    @Nonnull LiquidMessage process(UnlinkAliasRequest unlinkAliasRequest);

    //Object Transformations

    @Nonnull LiquidMessage process(MovePoolObjectRequest movePoolObjectRequest);

    @Nonnull LiquidMessage process(ResizePoolObjectRequest resizePoolObjectRequest);

    @Nonnull LiquidMessage process(RotateXYPoolObjectRequest rotateXYPoolObjectRequest);


    @Nonnull LiquidMessage process(ClaimAliasRequest claimAliasRequest);

    @Nonnull LiquidMessage process(ChangePasswordRequest changePasswordRequest);

    @Nonnull LiquidMessage process(LinkPoolObjectRequest linkPoolObjectRequest);

    @Nonnull LiquidMessage process(VisitPoolRequest visitPoolRequest);

    @Nonnull LiquidMessage process(RetrievePoolRosterRequest retrievePoolRosterRequest);

    @Nonnull LiquidMessage process(SelectPoolObjectRequest selectPoolObjectRequest);

    @Nonnull LiquidMessage process(AddCommentRequest addCommentRequest);

    @Nonnull LiquidMessage process(LinkPoolRequest linkPoolRequest);

    @Nonnull LiquidMessage process(RetrieveUpdatesRequest retrieveUpdatesRequest);

    @Nonnull LiquidMessage process(RetrieveCommentsRequest retrieveCommentsRequest);
}
