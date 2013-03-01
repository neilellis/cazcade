/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client;

import cazcade.boardcast.client.main.version.VersionNumberChecker;
import cazcade.boardcast.client.main.widgets.TopBar;
import cazcade.boardcast.client.main.widgets.board.BoardcastChatView;
import cazcade.boardcast.client.main.widgets.board.CreateBoardDialog;
import cazcade.boardcast.client.main.widgets.board.PublicBoard;
import cazcade.boardcast.client.main.widgets.board.SnapshotBoard;
import cazcade.boardcast.client.main.widgets.list.BoardList;
import cazcade.boardcast.client.main.widgets.login.BoardcastLoginOrRegisterPanel;
import cazcade.boardcast.client.preflight.PreflightCheck;
import cazcade.boardcast.client.resources.BoardcastClientBundle;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.RetrieveAliasRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusService;
import cazcade.vortex.common.client.User;
import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.comms.datastore.client.GWTDataStore;
import cazcade.vortex.gwt.util.client.*;
import cazcade.vortex.gwt.util.client.analytics.Track;
import cazcade.vortex.gwt.util.client.history.AbstractLazyHistoryAwareFactory;
import cazcade.vortex.gwt.util.client.history.HistoryAware;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import cazcade.vortex.widgets.client.stream.ActivityStreamPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class Boardcast implements EntryPoint {
    @Nonnull
    public static final String MAIN_PANEL_ID         = "main-panel";
    @Nonnull
    public static final String LOGIN_PANEL_ID        = "login-panel";
    @Nonnull
    public static final String PUBLIC_BOARD_PANEL_ID = "board-panel";
    @Nonnull
    public static final String SNAPSHOT_PANEL_ID     = "snapshot-panel";


    private BoardcastLoginOrRegisterPanel loginOrRegisterPanel;
    private HistoryManager                historyManager;
    private boolean                       registerRequest;
    private boolean                       createRequest;
    private boolean                       loginRequest;
    private boolean                       createUnlistedRequest;
    private Track                         tracker;

    public void onModuleLoad() {
        //        Window.alert(History.getToken());
        BoardcastClientBundle.INSTANCE.css().ensureInjected();
        Config.init();
        ClientLog.setDebugMode(Config.debug());
        ClientLog.setDebugMode(Config.dev());


        final RootPanel logPanel = RootPanel.get("log-panel");
        if (Config.debug()) {
            ClientLog.logWidget = logPanel.getElement();
            logPanel.setHeight("400px");
            logPanel.setVisible(true);
            logPanel.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        } else {
            logPanel.setHeight("0px");
            logPanel.setVisible(false);
        }


        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(final Throwable e) {
                ClientLog.log(e);
            }
        });

        if (!Config.isSnapshotMode()) {
            $.async(new Runnable() {
                @Override public void run() {
                    VersionNumberChecker.start();
                }
            });
        }


        if (Config.isPreflight() && !Config.isSnapshotMode()) {
            PreflightCheck.check();
        }


        registerRequest = Window.Location.getPath().startsWith("/_login-register");
        loginRequest = Window.Location.getPath().startsWith("/_login-login");
        createRequest = Window.Location.getPath().startsWith("/_create-");
        createUnlistedRequest = Window.Location.getPath().startsWith("/_create-unlisted");

        $.async(new Runnable() {
            @Override public void run() {
                injectChildren();
            }
        });

        if (Window.Location.getParameter("justRegistered") != null) {
            RootPanel.get("page-title").getElement().setInnerText("Registered successfully");
        }
    }

    private static class SnapshotBoardFactory extends AbstractLazyHistoryAwareFactory {
        private final boolean embedded;

        private SnapshotBoardFactory(final boolean embedded) {
            super();

            this.embedded = embedded;
        }

        @Nonnull @Override
        protected HistoryAware getInstanceInternal() {
            final SnapshotBoard board = new SnapshotBoard(embedded);
            RootPanel.get(SNAPSHOT_PANEL_ID).add(board);
            return board;
        }
    }

    private void injectChildren() {
        if (!Config.debug() && !Config.isSnapshotMode()) {
            Track.setGoogleId("UA-27340178-1");
        }
        tracker = Track.getInstance();
        History.addValueChangeHandler(tracker);

        if (RootPanel.get(PUBLIC_BOARD_PANEL_ID) != null) {
            final Runnable loginAction = new Runnable() {
                @Override
                public void run() {
                    addPublicBoard();
                    addCreateDialog();
                    historyManager.fireCurrentHistoryState();
                }
            };

            createLoginPanel(loginAction);
            checkUserLoggedIn(loginAction);
        } else if (RootPanel.get(SNAPSHOT_PANEL_ID) != null) {
            final Runnable loginAction = new Runnable() {
                @Override
                public void run() {
                    addSnapshotBoard();
                    historyManager.fireCurrentHistoryState();
                }
            };
            createLoginPanel(loginAction);
            checkUserLoggedIn(loginAction);
        } else if (RootPanel.get(LOGIN_PANEL_ID) != null) {
            loginOrRegisterPanel = new BoardcastLoginOrRegisterPanel(registerRequest, new Runnable() {
                @Override
                public void run() {
                    User.storeIdentity(loginOrRegisterPanel.getIdentity());
                    Window.Location.assign("./" + Window.Location.getQueryString());
                }
            }, new Runnable() {
                @Override
                public void run() {
                    Window.Location.assign("/welcome?justRegistered=true");
                }
            }
            );
            loginOrRegisterPanel.center();
            StartupUtil.showLiveVersion(RootPanel.get(LOGIN_PANEL_ID).getElement());
            loginOrRegisterPanel.show();
        } else {
            StartupUtil.showLiveVersion(RootPanel.get(MAIN_PANEL_ID).getElement());
            final Runnable loginAction = new Runnable() {
                @Override
                public void run() {
                    RootPanel.get(MAIN_PANEL_ID).add(new BoardcastChatView());
                    loginOrRegisterPanel.hide();
                    historyManager.fireCurrentHistoryState();
                }
            };
            createLoginPanel(loginAction);
            checkUserLoggedIn(loginAction);
        }
    }

    private void addPublicBoard() {
        historyManager = new HistoryManager(PUBLIC_BOARD_PANEL_ID);
        historyManager.registerTopLevelComposite("default", new AbstractLazyHistoryAwareFactory() {
            @Nonnull @Override
            protected HistoryAware getInstanceInternal() {
                final PublicBoard board = new PublicBoard();
                RootPanel.get(PUBLIC_BOARD_PANEL_ID).add(board);
                return board;
            }
        });
        historyManager.registerTopLevelComposite("chat", new AbstractLazyHistoryAwareFactory() {
            @Nonnull @Override
            protected HistoryAware getInstanceInternal() {
                return new BoardcastChatView();
            }
        });
        historyManager.registerTopLevelComposite("activity", new AbstractLazyHistoryAwareFactory() {
            @Nonnull @Override
            protected HistoryAware getInstanceInternal() {
                return new ActivityStreamPanel();
            }
        });
        historyManager.registerTopLevelComposite("list", new AbstractLazyHistoryAwareFactory() {
            @Nonnull @Override
            protected HistoryAware getInstanceInternal() {
                return new BoardList();
            }
        });
    }

    private void addCreateDialog() {
        historyManager.registerTopLevelComposite("create", new AbstractLazyHistoryAwareFactory() {
            @Nonnull @Override
            protected HistoryAware getInstanceInternal() {
                final CreateBoardDialog createBoardDialog = new CreateBoardDialog();
                createBoardDialog.setOnComplete(new Runnable() {
                    @Override
                    public void run() {
                        createBoardDialog.hide();
                        final String board = createBoardDialog.getBoard();
                        final boolean listed = createBoardDialog.isListed();
                        final String url = Window.Location.getParameter("url");
                        if (!listed || url != null) {
                            Bus.get().retrieveUUID(new BusService.UUIDCallback() {
                                @Override
                                public void callback(@Nonnull final LiquidUUID uuid) {
                                    final String unlistedShortUrl = "-" +
                                                                    uuid.toString().toLowerCase() +
                                                                    "~" +
                                                                    User.currentAlias().$(Dictionary.NAME);
                                    HistoryManager.get().navigate(unlistedShortUrl);
                                }
                            });
                        } else {
                            HistoryManager.get().navigate(board);
                        }
                    }
                });
                return createBoardDialog;
            }
        });
    }

    private void addSnapshotBoard() {
        historyManager = new HistoryManager(SNAPSHOT_PANEL_ID);
        historyManager.registerTopLevelComposite("snapshot", new SnapshotBoardFactory(false));
        historyManager.registerTopLevelComposite("embed", new SnapshotBoardFactory(true));
    }

    private void createLoginPanel(@Nonnull final Runnable loginAction) {
        loginOrRegisterPanel = new BoardcastLoginOrRegisterPanel(registerRequest, new Runnable() {
            @Override
            public void run() {
                final SessionIdentifier identity = loginOrRegisterPanel.getIdentity();
                assert identity != null;
                loginUser(identity, loginAction);
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (registerRequest || loginRequest) {
                    Window.Location.assign("/welcome?justRegistered=true");
                } else {
                    Window.Location.reload();
                }
            }
        }
        );
    }

    private void loginUser(@Nonnull final SessionIdentifier identity, @Nonnull final Runnable onLogin) {
        User.setIdentity(identity);
        User.storeIdentity(identity);
        final GWTDataStore dataStore = new GWTDataStore(identity, new Runnable() {
            @Override
            public void run() {
                Bus.get().start();
                Bus.get()
                          .send(new RetrieveAliasRequest(identity.aliasURI()), new AbstractMessageCallback<RetrieveAliasRequest>() {
                              @Override
                              public void onSuccess(final RetrieveAliasRequest original, @Nonnull final RetrieveAliasRequest message) {
                                  final TransferEntity alias = message.response();
                                  User.setCurrentAlias(alias);
                                  final Map<String, String> propertyMap = new HashMap<String, String>();
                                  propertyMap.putAll(alias.map());
                                  propertyMap.put("app.version", VersionNumberChecker.getBuildNumber());
                                  propertyMap.put("alpha.mode", Config.alpha() ? "true" : "false");
                                  final String name = alias.$(Dictionary.NAME);
                                  final String fn = alias.$(Dictionary.FULL_NAME);
                                  tracker.registerUser(name, fn, propertyMap);

                                  RootPanel.get().addStyleName("app-mode");
                                  loginOrRegisterPanel.hide();
                                  $.async(new Runnable() {
                                      @Override public void run() {
                                          RootPanel.get("topbar-menu-container").add(new TopBar());
                                      }
                                  });
                                  GWTUtil.delayAsync(200, new Runnable() {
                                      @Override public void run() { onLogin.run(); }
                                  });
                              }


                              @Override
                              public void onFailure(final RetrieveAliasRequest original, @Nonnull final RetrieveAliasRequest message) {
                                  ClientLog.log(message.response().$(Dictionary.DESCRIPTION));
                              }
                          });
            }
        }, new Runnable() {
            @Override
            public void run() {
                User.removeIdentity();
                Window.Location.reload();
            }
        }
        );
    }

    private void checkUserLoggedIn(@Nonnull final Runnable loginAction) {
        final SessionIdentifier identity = User.retrieveUser();
        if (identity == null ||
            identity.session() == null ||
            registerRequest ||
            createRequest && User.anon()) {
            DataStoreService.App
                            .getInstance()
                            .loginQuick(!Config.isLoginRequired()
                                        && !createRequest
                                        && !registerRequest, new AsyncCallback<SessionIdentifier>() {
                                @Override
                                public void onFailure(final Throwable caught) {
                                    ClientLog.log(caught);
                                }

                                @Override
                                public void onSuccess(@Nullable final SessionIdentifier result) {
                                    if (result == null) {
                                        //                        Window.alert("Login required.");
                                        loginOrRegisterPanel.center();
                                        loginOrRegisterPanel.show();
                                        //                            Window.Location.assign("./" + (Window.Location.getQueryString().length() > 1 ? ("?" + Window.Location.getQueryString().substring(1)) : ""));
                                    } else {
                                        loginUser(result, loginAction);
                                    }
                                }
                            });
        } else {
            loginUser(identity, loginAction);
        }
    }


}
