package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class RetrieveDictionaryRequest extends AbstractRetrievalRequest {

    public RetrieveDictionaryRequest() {
        super();
    }

    public RetrieveDictionaryRequest(@Nonnull final Category category) {
        super();
        setCategory(category);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrieveDictionaryRequest(getCategory());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_DICTIONARY;
    }


}
