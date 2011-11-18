package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;

/**
 * @author neilellis@cazcade.com
 */
public class RetrieveDictionaryRequest extends AbstractRetrievalRequest {

    public RetrieveDictionaryRequest() {
    }

    public RetrieveDictionaryRequest(Category category) {
        super();
        this.setCategory(category);
    }

    @Override
    public LiquidMessage copy() {
        return new RetrieveDictionaryRequest(getCategory());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_DICTIONARY;
    }


}
