package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.FountainEntity;
import cazcade.fountain.datastore.FountainEntityImpl;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.SearchRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.SearchRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


/**
 * @author neilelliz@cazcade.com
 */
public class SearchHandler extends AbstractDataStoreHandler<SearchRequest> implements SearchRequestHandler {
    @Nonnull
    public SearchRequest handle(@Nonnull final SearchRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {

            final IndexHits<org.neo4j.graphdb.Node> results = fountainNeo.freeTextSearch(request.getSearchText());
            final LSDSimpleEntity searchResultEntity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.SEARCH_RESULTS, UUIDFactory.randomUUID());
            final List<LSDEntity> resultEntities = new ArrayList<LSDEntity>();
            final List<String> dedupUrls = new ArrayList<String>();
            for (final org.neo4j.graphdb.Node r : results) {
                final FountainEntity result = new FountainEntityImpl(r);
                if (!dedupUrls.contains(result.getAttribute(LSDAttribute.URI))) {
                    resultEntities.add(result.convertNodeToLSD(request.getDetail(), request.isInternal()));
                }
                dedupUrls.add(result.getAttribute(LSDAttribute.URI));
            }
            searchResultEntity.addSubEntities(LSDAttribute.CHILD, resultEntities);
            return LiquidResponseHelper.forServerSuccess(request, searchResultEntity);
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}