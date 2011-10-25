package cazcade.fountain.datastore.api;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.request.*;

/**
 * @author Neil Ellis
 */

public interface FountainDataStoreFacade {

    // Create Operations (C of CRUD)

    LiquidMessage process(CreateUserRequest createUserRequest);

    LiquidMessage process(CreatePoolRequest createPoolRequest);

    LiquidMessage process(CreatePoolObjectRequest createPoolObjectRequest);

    LiquidMessage process(CreateSessionRequest createSessionRequest);

    LiquidMessage process(CreateAliasRequest createAliasRequest);

    // Retrieve Operations (R of CRUD)

    LiquidMessage process(RetrieveUserRequest retrieveUserRequest);

    LiquidMessage process(RetrievePoolRequest retrievePoolRequest);

    LiquidMessage process(RetrievePoolObjectRequest retrievePoolObjectRequest);

    LiquidMessage process(RetrieveSessionRequest retrieveSessionRequest);

    LiquidMessage process(RetrieveAliasRequest retrieveAliasRequest);

    // Update Operations (U of CRUD)

    LiquidMessage process(UpdateUserRequest updateUserRequest);

    LiquidMessage process(UpdatePoolRequest updatePoolRequest);

    LiquidMessage process(UpdatePoolObjectRequest updatePoolObjectRequest);

    LiquidMessage process(UpdateSessionRequest updateSessionRequest);

    LiquidMessage process(UpdateAliasRequest updateAliasRequest);

    // Delete Operations (D of CRUD)

    LiquidMessage process(DeleteUserRequest deleteUserRequest);

    LiquidMessage process(DeletePoolRequest deletePoolRequest);

    LiquidMessage process(DeletePoolObjectRequest deletePoolObjectRequest);

    LiquidMessage process(DeleteSessionRequest deleteSessionRequest);

    LiquidMessage process(UnlinkAliasRequest unlinkAliasRequest);

    //Object Transformations

    LiquidMessage process(MovePoolObjectRequest movePoolObjectRequest);

    LiquidMessage process(ResizePoolObjectRequest resizePoolObjectRequest);

    LiquidMessage process(RotateXYPoolObjectRequest rotateXYPoolObjectRequest);


    LiquidMessage process(ClaimAliasRequest claimAliasRequest);

    LiquidMessage process(ChangePasswordRequest changePasswordRequest);

    LiquidMessage process(LinkPoolObjectRequest linkPoolObjectRequest);

    LiquidMessage process(VisitPoolRequest visitPoolRequest);

    LiquidMessage process(RetrievePoolRosterRequest retrievePoolRosterRequest);

    LiquidMessage process(SelectPoolObjectRequest selectPoolObjectRequest);

    LiquidMessage process(AddCommentRequest addCommentRequest);

    LiquidMessage process(LinkPoolRequest linkPoolRequest);

    LiquidMessage process(RetrieveUpdatesRequest retrieveUpdatesRequest);

    LiquidMessage process(RetrieveCommentsRequest retrieveCommentsRequest);
}
