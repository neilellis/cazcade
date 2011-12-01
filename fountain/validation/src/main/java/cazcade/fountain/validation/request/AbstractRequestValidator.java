package cazcade.fountain.validation.request;

import cazcade.fountain.validation.FountainEntityValidator;
import cazcade.fountain.validation.api.FountainRequestValidator;
import cazcade.fountain.validation.api.ValidationException;
import cazcade.liquid.api.LiquidRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractRequestValidator<T extends LiquidRequest> implements FountainRequestValidator<T> {

    protected FountainEntityValidator entityValidator;

    public void setEntityValidator(FountainEntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }

    protected void validPoolObject(@Nonnull T request) {
        boolean validType = true;
        //TODO: validate object types
//        for (LSDTypes type : LSDTypeGroups.CORE_POOL_OBJECT_TYPES) {
//            if (request.getEntity().canBe(type)) {
//                validType = true;
//                break;
//            }
//        }
        if (!validType) {
            throw new ValidationException("Unsupported type " + request.getRequestEntity().getTypeDef().asString() + " for pools.");
        }
    }
}
