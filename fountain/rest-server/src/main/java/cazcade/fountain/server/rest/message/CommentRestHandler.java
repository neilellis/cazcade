package cazcade.fountain.server.rest.message;

import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.AddCommentRequest;
import cazcade.liquid.api.request.RetrieveCommentsRequest;
import cazcade.liquid.impl.UUIDFactory;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class CommentRestHandler extends AbstractRestHandler {
    private FountainDataStoreFacade dataStore;

    @Nonnull
    public LiquidMessage create(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "text", "image", "uri");
        final String text = parameters.get("text")[0];
        final String image = parameters.get("image")[0];
        final String uri = parameters.get("uri")[0];
        final LSDTransferEntity message = LSDSimpleEntity.createNewTransferEntity(LSDDictionaryTypes.COMMENT,
                                                                                  UUIDFactory.randomUUID()
                                                                                 );
        message.setAttribute(LSDAttribute.TEXT_EXTENDED, text);
        message.setAttribute(LSDAttribute.IMAGE_URL, image);
        message.setAttribute(LSDAttribute.ICON_URL, image);
        return dataStore.process(new AddCommentRequest(username, new LiquidURI(uri), message));
    }

    @Nonnull
    public LiquidMessage get(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "uri");
        final String uri = parameters.get("uri")[0];

        return dataStore.process(new RetrieveCommentsRequest(username, new LiquidURI(uri), 20, false));
    }

    public void setDataStore(final FountainDataStoreFacade dataStore) {
        this.dataStore = dataStore;
    }
}
