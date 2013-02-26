/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.PermissionChangeType;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Type;
import cazcade.liquid.api.request.ChangePermissionRequest;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.gwt.util.client.ClientLog;

import javax.annotation.Nonnull;

import static cazcade.liquid.api.PermissionChangeType.MAKE_PUBLIC_READONLY;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class RequestUtil {
    private static final Bus bus = BusFactory.get();

    public static void visit(Type type, LiquidURI uri, LiquidURI previousUri, boolean listed, final Callback<VisitPoolRequest> success, final Callback<VisitPoolRequest> failure) {
        bus.send(new VisitPoolRequest(type, uri, previousUri, !UserUtil.anon(), listed, listed
                                                                                        ? MAKE_PUBLIC_READONLY
                                                                                        : null), new AbstractResponseCallback<VisitPoolRequest>() {
            @Override public void onSuccess(VisitPoolRequest message, VisitPoolRequest response) {
                try {
                    success.handle(response);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }

            @Override public void onFailure(VisitPoolRequest message, @Nonnull VisitPoolRequest response) {
                try {
                    failure.handle(response);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        });
    }

    public static final void visit(Type type, LiquidURI uri, boolean listed, final Callback<VisitPoolRequest> callback) {
        bus.send(new VisitPoolRequest(type, uri, uri, true, listed), new AbstractResponseCallback<VisitPoolRequest>() {
            @Override public void onSuccess(VisitPoolRequest message, VisitPoolRequest response) {
                try {
                    callback.handle(response);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        });
    }

    public static final void updatePool(TransferEntity entity, final Callback<UpdatePoolRequest> success, final Callback<UpdatePoolRequest> failure) {
        bus.send(new UpdatePoolRequest(), new AbstractResponseCallback<UpdatePoolRequest>() {
            @Override
            public void onSuccess(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                try {
                    success.handle(response);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }

            @Override
            public void onFailure(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                try {
                    failure.handle(response);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        });

    }

    public static final void changePermission(LiquidURI uri, PermissionChangeType change, final Callback<ChangePermissionRequest> onFailure) {
        BusFactory.get().send(new ChangePermissionRequest(uri, change), new AbstractResponseCallback<ChangePermissionRequest>() {
            @Override
            public void onFailure(final ChangePermissionRequest message, @Nonnull final ChangePermissionRequest response) {
                try {
                    onFailure.handle(response);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        });
    }

}
