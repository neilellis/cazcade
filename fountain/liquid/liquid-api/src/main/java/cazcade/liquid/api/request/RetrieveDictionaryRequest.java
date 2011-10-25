package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;

/**
* @author neilellis@cazcade.com
*/
public class RetrieveDictionaryRequest extends AbstractRetrievalRequest {
    private Category category;

    public static enum Category {
        KEYS, TYPES
    }
    public RetrieveDictionaryRequest(Category category) {
        super();
        this.category = category;
    }

    @Override
    public LiquidMessage copy() {
        return new RetrieveDictionaryRequest(category);
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_DICTIONARY;
    }

    public Category getCategory() {
        return category;
    }
}
