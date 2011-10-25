package cazcade.liquid.api;

import cazcade.liquid.api.request.*;

/**
 * @author neilelliz@cazcade.com
 */
public enum LiquidRequestType {

    CREATE_POOL(CreatePoolRequest.class),
    CREATE_POOL_OBJECT(CreatePoolObjectRequest.class),
    CREATE_USER(CreateUserRequest.class),
    CREATE_SESSION(CreateSessionRequest.class),
    CREATE_ALIAS(CreateAliasRequest.class),

    RETRIEVE_POOL(RetrievePoolRequest.class),
    RETRIEVE_POOL_OBJECT(RetrievePoolObjectRequest.class),
    RETRIEVE_USER(RetrieveUserRequest.class),
    RETRIEVE_SESSION(RetrieveSessionRequest.class),
    RETRIEVE_ALIAS(RetrieveAliasRequest.class),

    UPDATE_POOL(UpdatePoolRequest.class),
    UPDATE_POOL_OBJECT(UpdatePoolObjectRequest.class),
    UPDATE_USER(UpdateUserRequest.class),
    UPDATE_SESSION(UpdateSessionRequest.class),
    UPDATE_ALIAS(UpdateAliasRequest.class),

    DELETE_POOL(DeletePoolRequest.class),
    DELETE_POOL_OBJECT(DeletePoolObjectRequest.class),
    DELETE_USER(DeleteUserRequest.class),
    DELETE_SESSION(DeleteSessionRequest.class),
    DELETE_ALIAS(UnlinkAliasRequest.class),

    MOVE_POOL_OBJECT(MovePoolObjectRequest.class),
    ROTATE_XY_POOL_OBJECT(RotateXYPoolObjectRequest.class),
    RESIZE_POOL_OBJECT(ResizePoolObjectRequest.class),

    AUTHORIZATION_REQUEST(AuthorizationRequest.class),
    CHANGE_PASSWORD(ChangePasswordRequest.class),
    CHANGE_PERMISSION(ChangePermissionRequest.class),
    ADMIN_COMMAND(AdminCommandRequest.class),

    RESPONSE(LiquidMessage.class),
    RETRIEVE_DICTIONARY(RetrieveDictionaryRequest.class),
    RETRIEVE_COMMENTS(RetrieveCommentsRequest.class),
    CLAIM_ALIAS(ClaimAliasRequest.class),
    LINK_POOL_OBJECT(LinkPoolObjectRequest.class),
    VISIT_POOL(VisitPoolRequest.class),
    RETRIEVE_POOL_ROSTER(RetrievePoolRosterRequest.class),
    RETRIEVE_UPDATES(RetrieveUpdatesRequest.class),
    SELECT_POOL_OBJECT(SelectPoolObjectRequest.class),
    ADD_COMMENT(AddCommentRequest.class),
    CHAT(ChatRequest.class),
    FOLLOW(FollowRequest.class),
    SEND(SendRequest.class),
    SEARCH(SearchRequest.class),
    BOARD_QUERY(BoardQueryRequest.class);

    private Class<? extends LiquidMessage> requestClass;


    LiquidRequestType(Class<? extends LiquidMessage> requestClass) {
        this.requestClass = requestClass;
    }

    public Class<? extends LiquidMessage> getRequestClass() {
        return requestClass;
    }
}
