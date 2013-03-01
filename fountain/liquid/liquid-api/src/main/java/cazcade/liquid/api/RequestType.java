/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import cazcade.liquid.api.request.*;
import com.google.gwt.core.client.GWT;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public enum RequestType {
    R_CREATE_POOL(CreatePoolRequest.class),
    R_CREATE_POOL_OBJECT(CreatePoolObjectRequest.class),
    R_CREATE_USER(CreateUserRequest.class),
    R_CREATE_SESSION(CreateSessionRequest.class),
    R_CREATE_ALIAS(CreateAliasRequest.class),
    R_RETRIEVE_POOL(RetrievePoolRequest.class),
    R_RETRIEVE_POOL_OBJECT(RetrievePoolObjectRequest.class),
    R_RETRIEVE_USER(RetrieveUserRequest.class),
    R_RETRIEVE_SESSION(RetrieveSessionRequest.class),
    R_RETRIEVE_ALIAS(RetrieveAliasRequest.class),
    R_UPDATE_POOL(UpdatePoolRequest.class),
    R_UPDATE_POOL_OBJECT(UpdatePoolObjectRequest.class),
    R_UPDATE_USER(UpdateUserRequest.class),
    R_UPDATE_SESSION(UpdateSessionRequest.class),
    R_UPDATE_ALIAS(UpdateAliasRequest.class),
    R_DELETE_POOL(DeletePoolRequest.class),
    R_DELETE_POOL_OBJECT(DeletePoolObjectRequest.class),
    R_DELETE_USER(DeleteUserRequest.class),
    R_DELETE_SESSION(DeleteSessionRequest.class),
    R_DELETE_ALIAS(UnlinkAliasRequest.class),
    R_MOVE_POOL_OBJECT(MovePoolObjectRequest.class),
    R_ROTATE_XY_POOL_OBJECT(RotateXYPoolObjectRequest.class),
    R_RESIZE_POOL_OBJECT(ResizePoolObjectRequest.class),
    R_AUTHORIZATION_REQUEST(AuthorizationRequest.class),
    R_CHANGE_PASSWORD(ChangePasswordRequest.class),
    R_CHANGE_PERMISSION(ChangePermissionRequest.class),
    R_ADMIN_COMMAND(AdminCommandRequest.class),
    R_RESPONSE(LiquidMessage.class),
    R_RETRIEVE_DICTIONARY(RetrieveDictionaryRequest.class),
    R_RETRIEVE_COMMENTS(RetrieveCommentsRequest.class),
    R_CLAIM_ALIAS(ClaimAliasRequest.class),
    R_LINK_POOL_OBJECT(LinkPoolObjectRequest.class),
    R_VISIT_POOL(VisitPoolRequest.class),
    R_RETRIEVE_POOL_ROSTER(RetrievePoolRosterRequest.class),
    R_RETRIEVE_UPDATES(RetrieveUpdatesRequest.class),
    R_SELECT_POOL_OBJECT(SelectPoolObjectRequest.class),
    R_ADD_COMMENT(AddCommentRequest.class),
    R_CHAT(ChatRequest.class),
    R_FOLLOW(FollowRequest.class),
    R_SEND(SendRequest.class),
    R_SEARCH(SearchRequest.class),
    R_BOARD_QUERY(BoardQueryRequest.class);

    private final Class<? extends LiquidMessage> requestClass;


    RequestType(final Class<? extends LiquidMessage> requestClass) {
        this.requestClass = requestClass;
    }

    @Nonnull
    public AbstractRequest createInGWT() {
        if (this == R_CREATE_POOL) {
            return GWT.create(CreatePoolRequest.class);
        }
        if (this == R_CREATE_POOL_OBJECT) {
            return GWT.create(CreatePoolObjectRequest.class);
        }
        if (this == R_CREATE_USER) {
            return GWT.create(CreateUserRequest.class);
        }
        if (this == R_CREATE_SESSION) {
            return GWT.create(CreateSessionRequest.class);
        }
        if (this == R_CREATE_ALIAS) {
            return GWT.create(CreateAliasRequest.class);
        }
        if (this == R_RETRIEVE_POOL) {
            return GWT.create(RetrievePoolRequest.class);
        }
        if (this == R_RETRIEVE_POOL_OBJECT) {
            return GWT.create(RetrievePoolObjectRequest.class);
        }
        if (this == R_RETRIEVE_USER) {
            return GWT.create(RetrieveUserRequest.class);
        }
        if (this == R_RETRIEVE_SESSION) {
            return GWT.create(RetrieveSessionRequest.class);
        }
        if (this == R_RETRIEVE_ALIAS) {
            return GWT.create(RetrieveAliasRequest.class);
        }
        if (this == R_UPDATE_POOL) {
            return GWT.create(UpdatePoolRequest.class);
        }
        if (this == R_UPDATE_POOL_OBJECT) {
            return GWT.create(UpdatePoolObjectRequest.class);
        }
        if (this == R_UPDATE_USER) {
            return GWT.create(UpdateUserRequest.class);
        }
        if (this == R_UPDATE_SESSION) {
            return GWT.create(UpdateSessionRequest.class);
        }
        if (this == R_UPDATE_ALIAS) {
            return GWT.create(UpdateAliasRequest.class);
        }
        if (this == R_DELETE_POOL) {
            return GWT.create(DeletePoolRequest.class);
        }
        if (this == R_DELETE_POOL_OBJECT) {
            return GWT.create(DeletePoolObjectRequest.class);
        }
        if (this == R_DELETE_USER) {
            return GWT.create(DeleteUserRequest.class);
        }
        if (this == R_DELETE_SESSION) {
            return GWT.create(DeleteSessionRequest.class);
        }
        if (this == R_DELETE_ALIAS) {
            return GWT.create(UnlinkAliasRequest.class);
        }
        if (this == R_MOVE_POOL_OBJECT) {
            return GWT.create(MovePoolObjectRequest.class);
        }
        if (this == R_ROTATE_XY_POOL_OBJECT) {
            return GWT.create(RotateXYPoolObjectRequest.class);
        }
        if (this == R_RESIZE_POOL_OBJECT) {
            return GWT.create(ResizePoolObjectRequest.class);
        }
        if (this == R_AUTHORIZATION_REQUEST) {
            return GWT.create(AuthorizationRequest.class);
        }
        if (this == R_CHANGE_PASSWORD) {
            return GWT.create(ChangePasswordRequest.class);
        }
        if (this == R_CHANGE_PERMISSION) {
            return GWT.create(ChangePermissionRequest.class);
        }
        if (this == R_ADMIN_COMMAND) {
            return GWT.create(AdminCommandRequest.class);
        }
        if (this == R_RETRIEVE_DICTIONARY) {
            return GWT.create(RetrieveDictionaryRequest.class);
        }
        if (this == R_RETRIEVE_COMMENTS) {
            return GWT.create(RetrieveCommentsRequest.class);
        }
        if (this == R_CLAIM_ALIAS) {
            return GWT.create(ClaimAliasRequest.class);
        }
        if (this == R_LINK_POOL_OBJECT) {
            return GWT.create(LinkPoolObjectRequest.class);
        }
        if (this == R_VISIT_POOL) {
            return GWT.create(VisitPoolRequest.class);
        }
        if (this == R_RETRIEVE_POOL_ROSTER) {
            return GWT.create(RetrievePoolRosterRequest.class);
        }
        if (this == R_RETRIEVE_UPDATES) {
            return GWT.create(RetrieveUpdatesRequest.class);
        }
        if (this == R_SELECT_POOL_OBJECT) {
            return GWT.create(SelectPoolObjectRequest.class);
        }
        if (this == R_ADD_COMMENT) {
            return GWT.create(AddCommentRequest.class);
        }
        if (this == R_CHAT) {
            return GWT.create(ChatRequest.class);
        }
        if (this == R_FOLLOW) {
            return GWT.create(FollowRequest.class);
        }
        if (this == R_SEND) {
            return GWT.create(SendRequest.class);
        }
        if (this == R_SEARCH) {
            return GWT.create(SearchRequest.class);
        }
        if (this == R_BOARD_QUERY) {
            return GWT.create(BoardQueryRequest.class);
        }
        throw new IllegalArgumentException("Unrecognized request " + this);
    }

    public Class<? extends LiquidMessage> getRequestClass() {
        return requestClass;
    }


}
