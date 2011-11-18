package cazcade.liquid.api;

import cazcade.liquid.api.request.*;
import com.google.gwt.core.client.GWT;

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

    public AbstractRequest createInGWT() {
        if (this == CREATE_POOL) {
            return GWT.create(CreatePoolRequest.class);
        }
        if (this == CREATE_POOL_OBJECT) {
            return GWT.create(CreatePoolObjectRequest.class);
        }
        if (this == CREATE_USER) {
            return GWT.create(CreateUserRequest.class);
        }
        if (this == CREATE_SESSION) {
            return GWT.create(CreateSessionRequest.class);
        }
        if (this == CREATE_ALIAS) {
            return GWT.create(CreateAliasRequest.class);
        }
        if (this == RETRIEVE_POOL) {
            return GWT.create(RetrievePoolRequest.class);
        }
        if (this == RETRIEVE_POOL_OBJECT) {
            return GWT.create(RetrievePoolObjectRequest.class);
        }
        if (this == RETRIEVE_USER) {
            return GWT.create(RetrieveUserRequest.class);
        }
        if (this == RETRIEVE_SESSION) {
            return GWT.create(RetrieveSessionRequest.class);
        }
        if (this == RETRIEVE_ALIAS) {
            return GWT.create(RetrieveAliasRequest.class);
        }
        if (this == UPDATE_POOL) {
            return GWT.create(UpdatePoolRequest.class);
        }
        if (this == UPDATE_POOL_OBJECT) {
            return GWT.create(UpdatePoolObjectRequest.class);
        }
        if (this == UPDATE_USER) {
            return GWT.create(UpdateUserRequest.class);
        }
        if (this == UPDATE_SESSION) {
            return GWT.create(UpdateSessionRequest.class);
        }
        if (this == UPDATE_ALIAS) {
            return GWT.create(UpdateAliasRequest.class);
        }
        if (this == DELETE_POOL) {
            return GWT.create(DeletePoolRequest.class);
        }
        if (this == DELETE_POOL_OBJECT) {
            return GWT.create(DeletePoolObjectRequest.class);
        }
        if (this == DELETE_USER) {
            return GWT.create(DeleteUserRequest.class);
        }
        if (this == DELETE_SESSION) {
            return GWT.create(DeleteSessionRequest.class);
        }
        if (this == DELETE_ALIAS) {
            return GWT.create(UnlinkAliasRequest.class);
        }
        if (this == MOVE_POOL_OBJECT) {
            return GWT.create(MovePoolObjectRequest.class);
        }
        if (this == ROTATE_XY_POOL_OBJECT) {
            return GWT.create(RotateXYPoolObjectRequest.class);
        }
        if (this == RESIZE_POOL_OBJECT) {
            return GWT.create(ResizePoolObjectRequest.class);
        }
        if (this == AUTHORIZATION_REQUEST) {
            return GWT.create(AuthorizationRequest.class);
        }
        if (this == CHANGE_PASSWORD) {
            return GWT.create(ChangePasswordRequest.class);
        }
        if (this == CHANGE_PERMISSION) {
            return GWT.create(ChangePermissionRequest.class);
        }
        if (this == ADMIN_COMMAND) {
            return GWT.create(AdminCommandRequest.class);
        }
        if (this == RETRIEVE_DICTIONARY) {
            return GWT.create(RetrieveDictionaryRequest.class);
        }
        if (this == RETRIEVE_COMMENTS) {
            return GWT.create(RetrieveCommentsRequest.class);
        }
        if (this == CLAIM_ALIAS) {
            return GWT.create(ClaimAliasRequest.class);
        }
        if (this == LINK_POOL_OBJECT) {
            return GWT.create(LinkPoolObjectRequest.class);
        }
        if (this == VISIT_POOL) {
            return GWT.create(VisitPoolRequest.class);
        }
        if (this == RETRIEVE_POOL_ROSTER) {
            return GWT.create(RetrievePoolRosterRequest.class);
        }
        if (this == RETRIEVE_UPDATES) {
            return GWT.create(RetrieveUpdatesRequest.class);
        }
        if (this == SELECT_POOL_OBJECT) {
            return GWT.create(SelectPoolObjectRequest.class);
        }
        if (this == ADD_COMMENT) {
            return GWT.create(AddCommentRequest.class);
        }
        if (this == CHAT) {
            return GWT.create(ChatRequest.class);
        }
        if (this == FOLLOW) {
            return GWT.create(FollowRequest.class);
        }
        if (this == SEND) {
            return GWT.create(SendRequest.class);
        }
        if (this == SEARCH) {
            return GWT.create(SearchRequest.class);
        }
        if (this == BOARD_QUERY) {
            return GWT.create(BoardQueryRequest.class);
        }
        return null;
    }
}
