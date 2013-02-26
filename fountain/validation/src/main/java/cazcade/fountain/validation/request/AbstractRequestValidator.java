/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.validation.request;

import cazcade.fountain.validation.FountainEntityValidator;
import cazcade.fountain.validation.api.FountainRequestValidator;
import cazcade.liquid.api.LiquidRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractRequestValidator<T extends LiquidRequest> implements FountainRequestValidator<T> {
    protected FountainEntityValidator entityValidator;

    protected void validPoolObject(@Nonnull final T request) {
        final boolean validType = true;
        //TODO: validate object types
        //        for (Types type : TypeGroups.CORE_POOL_OBJECT_TYPES) {
        //            if (request.getEntity().canBe(type)) {
        //                validType = true;
        //                break;
        //            }
        //        }
        //        if (!validType) {
        //            throw new ValidationException("Unsupported type " + request.request().type().asString() + " for pools.");
        //        }
    }

    public void setEntityValidator(final FountainEntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }
}
