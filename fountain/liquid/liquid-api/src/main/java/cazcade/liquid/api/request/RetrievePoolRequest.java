package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class RetrievePoolRequest extends AbstractRetrievalRequest {


    public RetrievePoolRequest() {
    }


    /**
     * @deprecated retrieve by URI not UUID.
     */
    public RetrievePoolRequest(LiquidUUID target, boolean contents, boolean orCreate) {
        this(null, null, target, null, LiquidRequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(LiquidURI uri, boolean contents, boolean orCreate) {
        this(null, null, null, uri, LiquidRequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(LiquidURI uri, ChildSortOrder order, boolean orCreate) {
        this(null, null, null, uri, LiquidRequestDetailLevel.NORMAL, true, orCreate, order);
    }

    /**
     * @deprecated retrieve by URI not UUID.
     */
    public RetrievePoolRequest(LiquidSessionIdentifier identity, LiquidUUID target, boolean contents, boolean orCreate) {
        this(null, identity, target, null, LiquidRequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(LiquidSessionIdentifier identity, LiquidURI uri, boolean contents, boolean orCreate) {
        this(null, identity, null, uri, LiquidRequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri, LiquidRequestDetailLevel detail, boolean contents, boolean orCreate, ChildSortOrder order) {
        this(id, identity, target, uri, detail, contents, orCreate, order, 50);
    }

    public RetrievePoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri, LiquidRequestDetailLevel detail, boolean contents, boolean orCreate, ChildSortOrder order, int max) {
        setOrCreate(orCreate);
        setSessionId(identity);
        setUri(uri);
        setDetail(detail);
        setContents(contents);
        setOrder(order);
        setTarget(target);
        setId(id);
        setMax(max);
    }

    public RetrievePoolRequest(LiquidURI uri, LiquidRequestDetailLevel detailLevel, boolean contents, boolean orCreate) {
        this(null, null, null, uri, detailLevel, contents, orCreate, null);
    }

    public RetrievePoolRequest(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, ChildSortOrder sortOrder, boolean orCreate) {
        this(null, sessionIdentifier, null, uri, LiquidRequestDetailLevel.NORMAL, true, orCreate, sortOrder);
    }

    public RetrievePoolRequest(LiquidSessionIdentifier identity, LiquidURI uri, LiquidRequestDetailLevel detail, boolean contents, boolean orCreate) {
        this(null, identity, null, uri, detail, contents, orCreate, null);
    }


    @Override
    public LiquidMessage copy() {
        return new RetrievePoolRequest(getId(), getSessionIdentifier(), super.getTarget(), getUri(), getDetail(), isContents(), isOrCreate(), getOrder());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (super.getTarget() != null) {
            return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW));
        } else {
            return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.VIEW));
        }
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_POOL;
    }


}
