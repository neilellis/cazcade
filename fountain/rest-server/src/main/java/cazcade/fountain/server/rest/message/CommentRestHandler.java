package cazcade.fountain.server.rest.message;

import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.RetrieveCommentsRequest;
import cazcade.liquid.api.request.AddCommentRequest;
import cazcade.liquid.impl.UUIDFactory;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class CommentRestHandler extends AbstractRestHandler {
    private FountainDataStoreFacade dataStore;

    public LiquidMessage create(Map<String, String[]> parameters) throws URISyntaxException {
        LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "text", "image", "uri");
        final String text = parameters.get("text")[0];
        final String image = parameters.get("image")[0];
        final String uri = parameters.get("uri")[0];
        LSDSimpleEntity message = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.COMMENT, UUIDFactory.randomUUID());
        message.setAttribute(LSDAttribute.TEXT_EXTENDED, text);
        message.setAttribute(LSDAttribute.IMAGE_URL, image);
        message.setAttribute(LSDAttribute.ICON_URL, image);
        return dataStore.process(new AddCommentRequest(username, new LiquidURI(uri), message));
    }

    public LiquidMessage get(Map<String, String[]> parameters) throws URISyntaxException {
        LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "uri");
        final String uri = parameters.get("uri")[0];

        return dataStore.process(new RetrieveCommentsRequest(username, new LiquidURI(uri), 20, false));
    }

    public void setDataStore(FountainDataStoreFacade dataStore) {
        this.dataStore = dataStore;
    }

}
