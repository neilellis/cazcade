package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RetrievePoolRequest extends AbstractRetrievalRequest {


    public RetrievePoolRequest() {
        super();
    }


    /**
     * @deprecated retrieve by URI not UUID.
     */
    public RetrievePoolRequest(final LiquidUUID target, final boolean contents, final boolean orCreate) {
        this(null, null, target, null, LiquidRequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(final LiquidURI uri, final boolean contents, final boolean orCreate) {
        this(null, null, null, uri, LiquidRequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(final LiquidURI uri, final ChildSortOrder order, final boolean orCreate) {
        this(null, null, null, uri, LiquidRequestDetailLevel.NORMAL, true, orCreate, order);
    }

    /**
     * @deprecated retrieve by URI not UUID.
     */
    public RetrievePoolRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final boolean contents, final boolean orCreate) {
        this(null, identity, target, null, LiquidRequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final boolean contents, final boolean orCreate) {
        this(null, identity, null, uri, LiquidRequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, @Nullable final LiquidUUID target, @Nullable final LiquidURI uri, @Nonnull final LiquidRequestDetailLevel detail, final boolean contents, final boolean orCreate, @Nullable final ChildSortOrder order) {
        this(id, identity, target, uri, detail, contents, orCreate, order, 50);
    }

    public RetrievePoolRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidURI uri, @Nonnull final LiquidRequestDetailLevel detail, final boolean contents, final boolean orCreate, final ChildSortOrder order, final int max) {
        super();
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

    public RetrievePoolRequest(final LiquidURI uri, @Nonnull final LiquidRequestDetailLevel detailLevel, final boolean contents, final boolean orCreate) {
        this(null, null, null, uri, detailLevel, contents, orCreate, null);
    }

    public RetrievePoolRequest(final LiquidSessionIdentifier sessionIdentifier, final LiquidURI uri, final ChildSortOrder sortOrder, final boolean orCreate) {
        this(null, sessionIdentifier, null, uri, LiquidRequestDetailLevel.NORMAL, true, orCreate, sortOrder);
    }

    public RetrievePoolRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, @Nonnull final LiquidRequestDetailLevel detail, final boolean contents, final boolean orCreate) {
        this(null, identity, null, uri, detail, contents, orCreate, null);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrievePoolRequest(getId(), getSessionIdentifier(), getTarget(), getUri(), getDetail(), isContents(), isOrCreate(), getOrder());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getTarget() != null) {
            return Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.VIEW));
        } else {
            return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.VIEW));
        }
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_POOL;
    }


}
