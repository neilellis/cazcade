/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.bus.client;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.PermissionChangeType;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Type;
import cazcade.liquid.api.request.ChangePermissionRequest;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.gwt.util.client.ClientLog;

import javax.annotation.Nonnull;

import static cazcade.liquid.api.PermissionChangeType.MAKE_PUBLIC_READONLY;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class Request {
    private static final BusService bus = Bus.get();

    public static void visit(Type type, LURI uri, LURI previousUri, boolean listed, final Callback<VisitPoolRequest> success, final Callback<VisitPoolRequest> failure, boolean loggedIn) {
        bus.send(new VisitPoolRequest(type, uri, previousUri, loggedIn, listed, listed
                                                                                        ? MAKE_PUBLIC_READONLY
                                                                                        : null), new AbstractMessageCallback<VisitPoolRequest>() {
            @Override public void onSuccess(VisitPoolRequest original, VisitPoolRequest message) {
                try {
                    success.handle(message);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }

            @Override public void onFailure(VisitPoolRequest original, @Nonnull VisitPoolRequest message) {
                try {
                    failure.handle(message);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        });
    }

    public static void visit(Type type, LURI uri, boolean listed, final Callback<VisitPoolRequest> callback) {
        bus.send(new VisitPoolRequest(type, uri, uri, true, listed),callback);
}

    public static void updatePool(TransferEntity entity, final Callback<UpdatePoolRequest> success, final Callback<UpdatePoolRequest> failure) {
        bus.send(new UpdatePoolRequest(entity), new AbstractMessageCallback<UpdatePoolRequest>() {
            @Override
            public void onSuccess(final UpdatePoolRequest original, @Nonnull final UpdatePoolRequest message) {
                try {
                    success.handle(message);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }

            @Override
            public void onFailure(final UpdatePoolRequest original, @Nonnull final UpdatePoolRequest message) {
                try {
                    failure.handle(message);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        });

    }

    public static void changePermission(LURI uri, PermissionChangeType change, final Callback<ChangePermissionRequest> onFailure) {
        Bus.get().send(new ChangePermissionRequest(uri, change), new AbstractMessageCallback<ChangePermissionRequest>() {
            @Override
            public void onFailure(final ChangePermissionRequest original, @Nonnull final ChangePermissionRequest message) {
                try {
                    onFailure.handle(message);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        });
    }

    public static void updatePoolObject(TransferEntity entity, Callback<UpdatePoolObjectRequest> callback) {
        Bus.get().send(new UpdatePoolObjectRequest(entity), callback );

}

    public static void updatePoolObject(TransferEntity entity, final Callback<UpdatePoolObjectRequest> success, final Callback<UpdatePoolObjectRequest> failure) {
        Bus.get().send(new UpdatePoolObjectRequest(entity),new AbstractMessageCallback<UpdatePoolObjectRequest>() {
            @Override public void onSuccess(UpdatePoolObjectRequest original, UpdatePoolObjectRequest message) {
                try {
                    success.handle(message);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }

            @Override public void onFailure(UpdatePoolObjectRequest original, @Nonnull UpdatePoolObjectRequest message) {
                try {
                    failure.handle(message);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        } );
    }
}
