package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisitPoolRequest extends AbstractRetrievalRequest {

    public static final int DEFAULT_CHILD_LIMIT = 60;

    private boolean orCreate;
    private LiquidURI previous;
    private LSDType type;
    private int max = DEFAULT_CHILD_LIMIT;
    private boolean listed;
    private LiquidPermissionChangeType permission;

    public VisitPoolRequest() {
    }

    public VisitPoolRequest(LSDType type, LiquidURI uri, LiquidURI previous, boolean orCreate, boolean listed, LiquidPermissionChangeType permission) {
        this(null, null, type, uri, previous, orCreate, 60, listed, permission);
    }

    public VisitPoolRequest(LSDType type, LiquidURI uri, LiquidURI previous, boolean orCreate, boolean listed) {
        this(null, null, type, uri, previous, orCreate, listed);
    }

    public VisitPoolRequest(LiquidSessionIdentifier identity, LSDType type, LiquidURI uri, boolean orCreate, boolean listed) {
        this(null, identity, type, uri, null, orCreate, listed);
    }

    public VisitPoolRequest(LiquidSessionIdentifier identity, LiquidURI uri) {
        this(null, identity, null, uri, null, false, false);
    }


    private VisitPoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LSDType type, LiquidURI uri, LiquidURI previous, boolean orCreate, boolean listed) {
        this(id, identity, type, uri, previous, orCreate, 60, listed, null);
    }


    private VisitPoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LSDType type, LiquidURI uri, LiquidURI previous, boolean orCreate, int max, boolean listed, LiquidPermissionChangeType permission) {
        this.orCreate = orCreate;
        this.listed = listed;
        this.permission = permission;
        this.id = id;
        this.identity = identity;
        this.uri = uri;
        this.previous = previous;
        this.max = max;
        this.type = type;

    }


    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Override
    public LiquidMessage copy() {
        return new VisitPoolRequest(id, identity, type, uri, previous, orCreate, listed);
    }

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
        if (previous != null) {
            return Arrays.asList(uri.asReverseDNSString(), previous.asReverseDNSString());
        } else {
            return Arrays.asList(uri.asReverseDNSString());
        }
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.VISIT_POOL;
    }


    public boolean isOrCreate() {
        return orCreate;
    }

    public int getMax() {
        return max;
    }

    public LSDType getType() {
        return type;
    }

    public boolean isListed() {
        return listed;
    }

    public LiquidPermissionChangeType getPermission() {
        return permission;
    }
}