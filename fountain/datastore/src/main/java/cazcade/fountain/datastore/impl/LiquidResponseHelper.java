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
        }
        else {
            log.warn(e, "{0}", e.getMessage());
            final T message = (T) request.copy();
            message.setState(LiquidMessageState.FAIL);
            message.setOrigin(LiquidMessageOrigin.SERVER);

            final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
            entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.EXCEPTION.getValue() + "." + e.getClass().getSimpleName());
            entity.setAttribute(LSDAttribute.ID, UUIDFactory.randomUUID().toString());
            entity.setAttributeConditonally(LSDAttribute.TITLE,
                                            e.getMessage() != null ? e.getMessage() : e.getClass().getCanonicalName()
                                           );
            entity.setAttributeConditonally(LSDAttribute.DESCRIPTION,
                                            e.getMessage() != null ? e.getMessage() : e.getClass().getCanonicalName()
                                           );
            entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));

            if (!CommonConstants.IS_PRODUCTION) {
                entity.setAttribute(LSDAttribute.TEXT, ExceptionUtils.getFullStackTrace(e));
            }
            entity.setAttribute(LSDAttribute.SOURCE, request.getId().toString());
            entity.setAttribute(LSDAttribute.URI, e.getClass().getCanonicalName());
            message.setResponse(entity);
            return message;
        }
    }

    @Nonnull
    public static <T extends LiquidRequest> T forResourceNotFound(final String description, @Nonnull final T request) {
        final T message = (T) request.copy();
        final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
        entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.RESOURCE_NOT_FOUND.getValue());
        entity.setAttribute(LSDAttribute.ID, UUIDFactory.randomUUID().toString());
        entity.setAttribute(LSDAttribute.TITLE, "Resource Not Found (40)");
        entity.setAttribute(LSDAttribute.DESCRIPTION, description);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.SOURCE, request.getId().toString());
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        message.setResponse(entity);
        message.setState(LiquidMessageState.FAIL);
        message.setOrigin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forFailure(@Nonnull final T request, @Nonnull final LiquidRequest failure) {
        final T message = (T) request.copy();
        message.setState(LiquidMessageState.FAIL);
        message.setOrigin(LiquidMessageOrigin.SERVER);
        final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
        entity.setAttribute(LSDAttribute.TYPE, failure.getResponse().getTypeDef().asString());
        entity.setAttribute(LSDAttribute.ID, UUIDFactory.randomUUID().toString());
        entity.setAttributeConditonally(LSDAttribute.TITLE, failure.getResponse().getAttribute(LSDAttribute.TITLE));
        entity.setAttributeConditonally(LSDAttribute.DESCRIPTION, failure.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.SOURCE, request.getId().toString());
        message.setResponse(entity);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forEmptyResultResponse(@Nonnull final T request) {
        final T message = (T) request.copy();
        final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
        entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.EMPTY_RESULT.getValue());
        entity.setAttribute(LSDAttribute.ID, UUIDFactory.randomUUID().toString());
        entity.setAttribute(LSDAttribute.TITLE, "Empty");
        entity.setAttribute(LSDAttribute.DESCRIPTION, "Empty result, query returned no result.");
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.SOURCE, request.getId().toString());
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        message.setResponse(entity);
        message.setState(LiquidMessageState.SUCCESS);
        message.setOrigin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forDuplicateResource(final String description, @Nonnull final T request) {
        final T message = (T) request.copy();
        final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
        entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.DUPLICATE_RESOURCE_ERROR.getValue());
        entity.setAttribute(LSDAttribute.ID, UUIDFactory.randomUUID().toString());
        entity.setAttribute(LSDAttribute.TITLE, "Duplicate Resource (409)");
        entity.setAttribute(LSDAttribute.DESCRIPTION, description);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.SOURCE, request.getId().toString());
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        message.setResponse(entity);
        message.setState(LiquidMessageState.FAIL);
        message.setOrigin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forServerSuccess(@Nonnull final T request, final LSDTransferEntity entity) {
        final T message = (T) request.copy();
        message.setResponse(entity);
        message.setState(LiquidMessageState.SUCCESS);
        message.setOrigin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forServerSuccess(@Nonnull final T request) {
        final T message = (T) request.copy();
        message.setState(LiquidMessageState.SUCCESS);
        message.setOrigin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forServerSuccessWithReferenceOnly(@Nonnull final T request, final String id,
                                                                                final String timestamp) {
        final T message = (T) request.copy();
        final LSDTransferEntity response = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.DATA_STORE_REFERENCE_RESULT);
        response.setAttribute(LSDAttribute.ID, id);
        response.setAttribute(LSDAttribute.UPDATED, timestamp);
        message.setResponse(response);
        message.setState(LiquidMessageState.SUCCESS);
        message.setOrigin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forServerSuccessWithReferenceOnly(@Nonnull final T request,
                                                                                @Nonnull final LSDBaseEntity entity) {
        final T message = (T) request.copy();
        final LSDTransferEntity response = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.DATA_STORE_REFERENCE_RESULT);
        response.setID(entity.getUUID());
        response.setAttribute(LSDAttribute.UPDATED, entity.getAttribute(LSDAttribute.UPDATED));
        message.setResponse(response);
        message.setState(LiquidMessageState.SUCCESS);
        message.setOrigin(LiquidMessageOrigin.SERVER);
        return message;
    }

    @Nonnull
    public static <T extends LiquidRequest> T forDeferral(@Nonnull final T request) {
        final T message = (T) request.copy();
        final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
        entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.DATA_STORE_DEFERRED_RESULT.getValue());
        entity.setAttribute(LSDAttribute.ID, UUIDFactory.randomUUID().toString());
        entity.setAttribute(LSDAttribute.CORRELATION_ID, request.getId().toString());
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        message.setResponse(entity);
        message.setState(LiquidMessageState.DEFERRED);
        message.setOrigin(LiquidMessageOrigin.SERVER);
        return message;
    }
}
