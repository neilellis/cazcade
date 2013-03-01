/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.AbstractBusListener;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusService;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.User;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.widgets.client.panels.list.dm.DirectMessageStreamEntryPanel;
import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class NotificationPanel extends Composite {
    interface VortexStreamPanelUiBinder extends UiBinder<HTMLPanel, NotificationPanel> {}

    public static final  int                       UPDATE_LIEFTIME        = 1200 * 1000;
    public static final  int                       STATUS_CHECK_FREQUENCY = 30000;
    private static final VortexStreamPanelUiBinder ourUiBinder            = GWT.create(VortexStreamPanelUiBinder.class);
    @Nonnull
    private final        BusService                bus                    = Bus.get();
    private final        long                      lastUpdate             = System.currentTimeMillis() - UPDATE_LIEFTIME;
    @Nonnull
    private final        VortexThreadSafeExecutor  threadSafeExecutor     = new VortexThreadSafeExecutor();
    @Nonnull
    private final SoundController soundController;
    private final Sound           userEnteredSound;
    private final Sound           statusUpdateSound;
    @UiField      HorizontalPanel parentPanel;
    private int maxRows = 10;
    private boolean initialized;
    private LURI    pool;

    public NotificationPanel() {
        super();
        final HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        soundController = new SoundController();

        userEnteredSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3, "/_static/_audio/user_entered.mp3");
        userEnteredSound.setVolume(100);

        statusUpdateSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3, "/_static/_audio/status_update.mp3");
        statusUpdateSound.setVolume(50);
        new Timer() {
            @Override
            public void run() {
                if (parentPanel.getWidgetCount() == 0) {
                    removeStyleName("show");
                } else {
                    addStyleName("show");
                }

            }
        }.scheduleRepeating(2000);

    }

    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
    }

    public void clear() {
        WidgetUtil.removeAllChildren(parentPanel);
    }

    public void init(final LURI newPool) {
        pool = newPool;
        clear();

        if (!initialized) {
            new Timer() {
                @Override
                public void run() {
                    bus.listen(new AbstractBusListener() {
                        @Override
                        public void handle(@Nonnull final LiquidMessage message) {
                            if (message.hasResponse()) {
                                final TransferEntity response = message.response();
                                if (response.is(Types.T_COMMENT)
                                    && response.$(Dictionary.TEXT_BRIEF) != null
                                    && !response.$(Dictionary.TEXT_BRIEF).isEmpty()) {
                                    addToStream(new CommentEntryPanel(response));
                                }
                                if (message.state() != MessageState.PROVISIONAL
                                    && message.state() != MessageState.INITIAL
                                    && message.state() != MessageState.FAIL
                                    && ((LiquidRequest) message).requestType() == RequestType.R_VISIT_POOL
                                    && !User.isAnonymousAliasURI(response.child(Dictionary.VISITOR_A, false).uri().toString())) {
                                    final VortexPresenceNotificationPanel content = new VortexPresenceNotificationPanel(response, pool, message
                                            .id()
                                            .toString());
                                    addToStream(content);
                                    try {
                                        userEnteredSound.play();
                                    } catch (Exception e) {
                                        ClientLog.log(e);
                                    }
                                }
                            }
                        }
                    });

                    Bus.get()
                              .listenForSuccess(User.currentAlias().uri(), RequestType.R_SEND, new BusListener<SendRequest>() {
                                  @Override
                                  public void handle(@Nonnull final SendRequest request) {
                                      final DirectMessageStreamEntryPanel content = new DirectMessageStreamEntryPanel(request.response());
                                      addToStream(content);
                                  }
                              });

                }
            }.schedule(1000);

            //            if (Config.isRetrieveUpdates()) {
            //                new Timer() {
            //                    @Override
            //                    public void run() {
            //                        bus.send(new RetrieveUpdatesRequest(lastUpdate), new RetrieveStreamEntityCallback(formatter, maxRows, parentPanel, pool, threadSafeExecutor, true) {
            //                            @Override
            //                            public void onSuccess(AbstractRequest message, AbstractRequest response) {
            //                                //relying on system time is a bad idea, so we use server time.
            //                                lastUpdate = response.response().updated().getTime();
            //                                super.onSuccess(message, response);
            //                            }
            //                        });
            //                    }
            //                }.scheduleRepeating(STATUS_CHECK_FREQUENCY);
            //            }
            initialized = true;
        }
    }

    private void addToStream(@Nonnull final StreamEntry content) {
        StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, content, true, true);
    }

}