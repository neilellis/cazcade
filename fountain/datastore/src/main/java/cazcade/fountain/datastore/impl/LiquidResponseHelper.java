/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.liquid.api.LiquidMessageOrigin;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.impl.UUIDFactory;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class LiquidResponseHelper {
    @Nonnull
    private static final Logger log = Logger.getLogger(LiquidResponseHelper.class);

    @Nonnull
    public static <T extends LiquidRequest> T forException(@Nonnull final Exception e, @Nonnull final T request) {
        if (e instanceof EntityNotFoundException) {
            return forResourceNotFound(e.getMessage(), request);
        } else {
            log.warn(e, "{0}", e.getMessage());
            final T message = (T) request.copy();
            message.state(LiquidMessageState.FAIL);
            message.origin(LiquidMessageOrigin.SERVER);


            message.response(SimpleEntity.createEmpty()
                                         .$(Dictionary.TYPE, Types.T_EXCEPTION.getValue() + "." + e.getClass().getSimpleName())
                                         .$(Dictionary.ID, UUIDFactory.randomUUID().toString())
                                         .$(Dictionary.TITLE, e.getMessage() != null
                                                              ? e.getMessage()
                                                              : e.getClass().getCanonicalName())
                                         .$(Dictionary.DESCRIPTION, e.getMessage() != null
                                                                    ? e.getMessage()
                                                                    : e.getClass().getCanonicalName())
                                         .$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()))
                                         .$(Dictionary.SOURCE, request.id().toString())
                                         .$(Dictionary.URI, e.getClass().getCanonicalName())

                                         .$(Dictionary.TEXT, CommonConstants.IS_PRODUCTION
                                                             ? ""
                                                             : ExceptionUtils.getFullStackTrace(e)));
            return message;
        }
    }

    @Nonnull
    public static <T extends LiquidRequest> T forResourceNotFound(final String description, @Nonnull final T request) {
        final T message = (T) request.copy();
        message.response(SimpleEntity.createEmpty()
                                     .$(Dictionary.TYPE, Types.T_RESOURCE_NOT_FOUND.getValue())
                                     .$(Dictionary.ID, UUIDFactory.randomUUID().toString())
                                     .$(Dictionary.TITLE, "Resource Not Found (40)")
                                     .$(Dictionary.DESCRIPTION, description)
                                     .$(Dictionary.UPDATED, System.currentTimeMillis())
                                     .$(Dictionary.SOURCE, request.id().toString())
                                     .$(Dictionary.UPDATED, System.currentTimeMillis()));
        message.state(LiquidMessageState.FAIL);
        message.origin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forFailure(@Nonnull final T request, @Nonnull final LiquidRequest failure) {
        final T message = (T) request.copy();
        message.state(LiquidMessageState.FAIL);
        message.origin(LiquidMessageOrigin.SERVER);
        message.response(SimpleEntity.createEmpty()
                                     .$(Dictionary.TYPE, failure.response().type().asString())
                                     .$(Dictionary.ID, UUIDFactory.randomUUID().toString())
                                     .$(failure.response(), Dictionary.TITLE)
                                     .$(failure.response(), Dictionary.DESCRIPTION)
                                     .$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()))
                                     .$(Dictionary.SOURCE, request.id().toString()));
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forEmptyResultResponse(@Nonnull final T request) {
        final T message = (T) request.copy();
        message.response(SimpleEntity.createEmpty()
                                     .$(Dictionary.TYPE, Types.T_EMPTY_RESULT.getValue())
                                     .$(Dictionary.ID, UUIDFactory.randomUUID().toString())
                                     .$(Dictionary.TITLE, "Empty")
                                     .$(Dictionary.DESCRIPTION, "Empty result, query returned no result.")
                                     .$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()))
                                     .$(Dictionary.SOURCE, request.id().toString())
                                     .$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis())));
        message.state(LiquidMessageState.SUCCESS);
        message.origin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forDuplicateResource(final String description, @Nonnull final T request) {
        final T message = (T) request.copy();
        message.response(SimpleEntity.createEmpty()
                                     .$(Dictionary.TYPE, Types.T_DUPLICATE_RESOURCE_ERROR.getValue())
                                     .$(Dictionary.ID, UUIDFactory.randomUUID().toString())
                                     .$(Dictionary.TITLE, "Duplicate Resource (409)")
                                     .$(Dictionary.DESCRIPTION, description)
                                     .$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()))
                                     .$(Dictionary.SOURCE, request.id().toString())
                                     .$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis())));
        message.state(LiquidMessageState.FAIL);
        message.origin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forServerSuccess(@Nonnull final T request, final TransferEntity entity) {
        final T message = (T) request.copy();
        message.response(entity);
        message.state(LiquidMessageState.SUCCESS);
        message.origin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forServerSuccess(@Nonnull final T request) {
        final T message = (T) request.copy();
        message.state(LiquidMessageState.SUCCESS);
        message.origin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forServerSuccessWithReferenceOnly(@Nonnull final T request, final String id, final String timestamp) {
        final T message = (T) request.copy();
        message.response(SimpleEntity.create(Types.T_DATA_STORE_REFERENCE_RESULT)
                                     .$(Dictionary.ID, id)
                                     .$(Dictionary.UPDATED, timestamp));
        message.state(LiquidMessageState.SUCCESS);
        message.origin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forServerSuccessWithReferenceOnly(@Nonnull final T request, @Nonnull final Entity entity) {
        final T message = (T) request.copy();
        message.response(SimpleEntity.create(Types.T_DATA_STORE_REFERENCE_RESULT).id(entity.id()).$(entity, Dictionary.UPDATED));
        message.state(LiquidMessageState.SUCCESS);
        message.origin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forDeferral(@Nonnull final T request) {
        final T message = (T) request.copy();
        message.response(SimpleEntity.createEmpty()
                                     .$(Dictionary.TYPE, Types.T_DATA_STORE_DEFERRED_RESULT.getValue())
                                     .$(Dictionary.ID, UUIDFactory.randomUUID().toString())
                                     .$(Dictionary.CORRELATION_ID, request.id().toString())
                                     .$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis())));
        message.state(LiquidMessageState.DEFERRED);
        message.origin(LiquidMessageOrigin.SERVER);
        return message;
    }
}
