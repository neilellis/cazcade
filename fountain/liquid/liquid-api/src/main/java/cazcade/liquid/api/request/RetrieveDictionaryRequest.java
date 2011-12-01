package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class RetrieveDictionaryRequest extends AbstractRetrievalRequest {

    public RetrieveDictionaryRequest() {
    }

    public RetrieveDictionaryRequest(@Nonnull Category category) {
        super();
        this.setCategory(category);
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
