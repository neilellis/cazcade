package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisitPoolRequest extends AbstractRetrievalRequest {


    public VisitPoolRequest() {
    }

    public VisitPoolRequest(@Nonnull LSDType type, LiquidURI uri, LiquidURI previous, boolean orCreate, boolean listed, LiquidPermissionChangeType permission) {
        this(null, null, type, uri, previous, orCreate, 60, listed, permission);
    }

    public VisitPoolRequest(@Nonnull LSDType type, LiquidURI uri, LiquidURI previous, boolean orCreate, boolean listed) {
        this(null, null, type, uri, previous, orCreate, listed);
    }

    public VisitPoolRequest(LiquidSessionIdentifier identity, @Nonnull LSDType type, LiquidURI uri, boolean orCreate, boolean listed) {
        this(null, identity, type, uri, null, orCreate, listed);
    }

    public VisitPoolRequest(LiquidSessionIdentifier identity, LiquidURI uri) {
        this(null, identity, null, uri, null, false, false);
    }


    private VisitPoolRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, @Nonnull LSDType type, LiquidURI uri, @Nullable LiquidURI previous, boolean orCreate, boolean listed) {
        this(id, identity, type, uri, previous, orCreate, 60, listed, null);
    }


    private VisitPoolRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, @Nonnull LSDType type, LiquidURI uri, LiquidURI previous, boolean orCreate, int max, boolean listed, @Nullable LiquidPermissionChangeType permission) {
        this.setOrCreate(orCreate);
        this.setListed(listed);
        this.setPermission(permission);
        this.setId(id);
        this.setSessionId(identity);
        this.setUri(uri);
        this.setPreviousPool(previous);
        this.setMax(max);
        this.setPoolType(type);

    }


    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new VisitPoolRequest(getId(), getSessionIdentifier(), getType(), getUri(), getPreviousPool(), isOrCreate(), isListed());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        //TODO:
        return new ArrayList<AuthorizationRequest>();

//        if (orCreate) {
//            return Arrays.asList(new AuthorizationRequest(uri.getParentURI(), LiquidPermission.MODIFY));
//        } else {
//            return Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.VIEW));
//        }
    }


    public List<String> getNotificationLocations() {
        if (getPreviousPool() != null) {
            return Arrays.asList(getUri().asReverseDNSString(), getPreviousPool().asReverseDNSString());
        } else {
            return Arrays.asList(getUri().asReverseDNSString());
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.VISIT_POOL;
    }


}