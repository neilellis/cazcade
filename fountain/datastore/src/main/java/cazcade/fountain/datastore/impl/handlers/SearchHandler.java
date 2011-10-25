package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.SearchRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.SearchRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;

import java.util.ArrayList;
import java.util.List;


/**
 * @author neilelliz@cazcade.com
 */
public class SearchHandler extends AbstractDataStoreHandler<SearchRequest> implements SearchRequestHandler {
    public SearchRequest handle(SearchRequest request) throws InterruptedException {
        Transaction transaction = fountainNeo.beginTx();
        try {

            IndexHits<Node> results = fountainNeo.freeTextSearch(request.getSearchText());
            LSDSimpleEntity searchResultEntity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.SEARCH_RESULTS, UUIDFactory.randomUUID());
            List<LSDEntity> resultEntities = new ArrayList<LSDEntity>();
            List<String> dedupUrls = new ArrayList<String>();
            for (Node result : results) {
                if (!dedupUrls.contains(((String) result.getProperty(FountainNeo.URI)))) {
                    resultEntities.add(fountainNeo.convertNodeToLSD(result, request.getDetail(), request.isInternal()));
                }
                dedupUrls.add(((String) result.getProperty(FountainNeo.URI)));
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