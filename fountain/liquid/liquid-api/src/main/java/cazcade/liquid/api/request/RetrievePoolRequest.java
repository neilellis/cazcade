package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class RetrievePoolRequest extends AbstractRetrievalRequest {
    private boolean contents;
    private boolean orCreate;
    private ChildSortOrder order;
    private int max;
    private boolean listed;


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
        this.orCreate = orCreate;
        this.identity = identity;
        this.uri = uri;
        this.detail = detail;
        this.contents = contents;
        this.order = order;
        this.target = target;
        this.id = id;
        this.max= max;
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


    public boolean isContents() {
        return contents;
    }

    @Override
    public LiquidMessage copy() {
        return new RetrievePoolRequest(id, identity, target, uri, detail, contents, orCreate, order);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (target != null) {
            return Arrays.asList(new AuthorizationRequest(target, LiquidPermission.VIEW));
        } else {
            return Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.VIEW));
        }
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_POOL;
    }


    public boolean isOrCreate() {
        return orCreate;
    }

    public void setContents(boolean contents) {
        this.contents = contents;
    }

    public void setOrCreate(boolean orCreate) {
        this.orCreate = orCreate;
    }

    public ChildSortOrder getOrder() {
        return order;
    }

    public void setOrder(ChildSortOrder order) {
        this.order = order;
    }

    public int getMax() {
        return max;
    }

    public boolean isListed() {
        return listed;
    }
}
