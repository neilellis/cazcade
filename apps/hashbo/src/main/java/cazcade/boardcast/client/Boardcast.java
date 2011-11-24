package cazcade.boardcast.client;

import cazcade.boardcast.client.main.version.VersionNumberChecker;
import cazcade.boardcast.client.main.widgets.board.BoardcastChatView;
import cazcade.boardcast.client.main.widgets.board.CreateBoardDialog;
import cazcade.boardcast.client.main.widgets.board.PublicBoard;
import cazcade.boardcast.client.main.widgets.board.SnapshotBoard;
import cazcade.boardcast.client.main.widgets.login.HashboLoginOrRegisterPanel;
import cazcade.boardcast.client.preflight.PreflightCheck;
import cazcade.boardcast.client.resources.HashboClientBundle;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.RetrieveAliasRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.comms.datastore.client.GWTDataStore;
import cazcade.vortex.gwt.util.client.ClientApplicationConfiguration;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.analytics.Track;
import cazcade.vortex.gwt.util.client.history.AbstractLazyHistoryAwareFactory;
import cazcade.vortex.gwt.util.client.history.HistoryAware;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author neilellis@cazcade.com
 */
public class Boardcast implements EntryPoint {

    public static final String MAIN_PANEL_ID = "main-panel";
    public static final String LOGIN_PANEL_ID = "login-panel";
    public static final String PUBLIC_BOARD_PANEL_ID = "board-panel";
    public static final String SNAPSHOT_PANEL_ID = "snapshot-panel";
    private HashboLoginOrRegisterPanel loginOrRegisterPanel;
    private HistoryManager historyManager;
    private boolean registerRequest;
    private boolean createRequest;
    private boolean loginRequest;
    private boolean createUnlistedRequest;

    public void onModuleLoad() {
//        Window.alert(History.getToken());
        HashboClientBundle.INSTANCE.css().ensureInjected();
        ClientApplicationConfiguration.init();
        ClientLog.setDebugMode(ClientApplicationConfiguration.isDebug());


        RootPanel logPanel = RootPanel.get("log-panel");
        if (ClientApplicationConfiguration.isDebug()) {
            ClientLog.logWidget = ((FrameElement) logPanel.getElement().cast()).getContentDocument().getBody();
            logPanel.setHeight("400px");
            logPanel.setVisible(true);
            logPanel.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        } else {
            logPanel.setHeight("0px");
            logPanel.setVisible(false);
        }


        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                ClientLog.log(e);
            }
        });


        VersionNumberChecker.start();
        if (ClientApplicationConfiguration.isDebug()) {
//            Window.alert("Debugging build " + VersionNumberChecker.getBuildNumber());
        }


        if (ClientApplicationConfiguration.isPreflight()) {
            PreflightCheck.check();
        }


        registerRequest = Window.Location.getPath().startsWith("/_login-register");
        loginRequest = Window.Location.getPath().startsWith("/_login-login");
        createRequest = Window.Location.getPath().startsWith("/_create-");
        createUnlistedRequest = Window.Location.getPath().startsWith("/_create-unlisted");

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
                ClientLog.log(reason);
            }

            @Override
            public void onSuccess() {
                injectChildren();
            }
        });

        if (Window.Location.getParameter("justRegistered") != null) {
            RootPanel.get("page-title").getElement().setInnerText("Registered successfully");
        }


    }

    private void injectChildren() {
        if (!ClientApplicationConfiguration.isDebug()) {
            History.addValueChangeHandler(new Track("UA-25104667-1"));
        }

//        addLogPanel();

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
            loginOrRegisterPanel = new HashboLoginOrRegisterPanel(registerRequest, new Runnable() {
                @Override
                public void run() {
                    UserUtil.storeIdentity(loginOrRegisterPanel.getIdentity());
                    Window.Location.assign("./" + Window.Location.getQueryString());
                }
            }, new Runnable() {
                @Override
                public void run() {
                    Window.Location.assign("/_welcome?justRegistered=true");
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

    private void addCreateDialog() {

        historyManager.registerTopLevelComposite("create", new AbstractLazyHistoryAwareFactory() {
            @Override
            protected HistoryAware getInstanceInternal() {
                final CreateBoardDialog createBoardDialog = new CreateBoardDialog();
                createBoardDialog.setOnComplete(new Runnable() {
                    @Override
                    public void run() {
                        createBoardDialog.hide();
                        String board = createBoardDialog.getBoard();
                        final boolean listed = createBoardDialog.isListed();
                        if (!listed) {
                            BusFactory.getInstance().retrieveUUID(new Bus.UUIDCallback() {
                                @Override
                                public void callback(LiquidUUID uuid) {
                                    final String unlistedShortUrl = "-" + uuid.toString().toLowerCase() + "~" + UserUtil.getCurrentAlias().getAttribute(LSDAttribute.NAME);
                                    historyManager.navigate(unlistedShortUrl);
                                }
                            });
                        } else {
                            historyManager.navigate(board);
                        }
                    }
                });
                return createBoardDialog;
            }
        });
    }

    private void addPublicBoard() {
        historyManager = new HistoryManager(PUBLIC_BOARD_PANEL_ID);
        historyManager.registerTopLevelComposite("default", new AbstractLazyHistoryAwareFactory() {
            @Override
            protected HistoryAware getInstanceInternal() {
                final PublicBoard board = new PublicBoard();
                RootPanel.get(PUBLIC_BOARD_PANEL_ID).add(board);
                return board;
            }
        });
        historyManager.registerTopLevelComposite("chat", new AbstractLazyHistoryAwareFactory() {
            @Override
            protected HistoryAware getInstanceInternal() {
                return new BoardcastChatView();
            }
        });
    }

    private void addSnapshotBoard() {
        historyManager = new HistoryManager(SNAPSHOT_PANEL_ID);
        final AbstractLazyHistoryAwareFactory historyAwareFactory = new AbstractLazyHistoryAwareFactory() {
            @Override
            protected HistoryAware getInstanceInternal() {
                final SnapshotBoard board = new SnapshotBoard();
                RootPanel.get(SNAPSHOT_PANEL_ID).add(board);
                return board;
            }
        };
        historyManager.registerTopLevelComposite("snapshot", historyAwareFactory);
        historyManager.registerTopLevelComposite("embed", historyAwareFactory);
    }

    private void createLoginPanel(final Runnable loginAction) {
        loginOrRegisterPanel = new HashboLoginOrRegisterPanel(registerRequest, new Runnable() {
            @Override
            public void run() {
                loginUser(loginOrRegisterPanel.getIdentity(), loginAction);
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (registerRequest || loginRequest) {
                    Window.Location.assign("/_welcome?justRegistered=true");
                } else {
                    Window.Location.reload();
                }
            }
        }
        );
    }

    private void checkUserLoggedIn(final Runnable loginAction) {
        final LiquidSessionIdentifier identity = UserUtil.retrieveUser();
        if (identity == null || identity.getSession() == null || registerRequest || (createRequest && UserUtil.isAnonymousOrLoggedOut())) {
            DataStoreService.App.getInstance().loginQuick(!ClientApplicationConfiguration.isLoginRequired() && !createRequest && !registerRequest, new AsyncCallback<LiquidSessionIdentifier>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientLog.log(caught);
                }

                @Override
                public void onSuccess(LiquidSessionIdentifier result) {
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

    private void loginUser(final LiquidSessionIdentifier identity, final Runnable onLogin) {
        UserUtil.setIdentity(identity);
        UserUtil.storeIdentity(identity);
        GWTDataStore dataStore = new GWTDataStore(identity, new Runnable() {
            @Override
            public void run() {
                BusFactory.getInstance().start();
                BusFactory.getInstance().send(new RetrieveAliasRequest(identity.getAliasURL()), new AbstractResponseCallback<RetrieveAliasRequest>() {
                    @Override
                    public void onSuccess(RetrieveAliasRequest message, RetrieveAliasRequest response) {
                        UserUtil.setCurrentAlias(response.getResponse());
                        RootPanel.get().addStyleName("app-mode");
                        loginOrRegisterPanel.hide();
                        new Timer() {
                            @Override
                            public void run() {
                                GWT.runAsync(new RunAsyncCallback() {
                                    @Override
                                    public void onFailure(Throwable reason) {
                                        ClientLog.log(reason);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        onLogin.run();

                                    }
                                });
                            }
                        }.schedule(200);
                    }

                    @Override
                    public void onFailure(RetrieveAliasRequest message, RetrieveAliasRequest response) {
                        ClientLog.log(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                    }
                });
            }
        }, new Runnable() {
            @Override
            public void run() {
                UserUtil.removeIdentity();
                Window.Location.reload();

            }
        }
        );

    }


//    protected void addLogPanel() {
//        if (RootPanel.get("log-panel") != null) {
//            if (Window.Location.getParameterMap().containsKey("debug") && Window.Location.getParameter("debug").equals("true")) {
//                HTMLPanel logPanel = new HTMLPanel("log");
//                ScrollPanel scrollPanel = new ScrollPanel(logPanel);
//                RootPanel.get("log-panel").add(scrollPanel);
//                RootPanel.get("log-panel").setWidth("100%");
//                RootPanel.get("log-panel").setHeight("200");
//                ClientLog.logWidget = logPanel.getElement();
//            } else {
//                RootPanel.get("log-panel").addStyleName("invisible");
//            }
//        }
//    }

}
