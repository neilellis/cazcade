/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.RetrieveCommentsRequest;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.AbstractBusListener;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusService;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.User;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.widgets.client.panels.list.dm.DirectMessageStreamEntryPanel;
import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class CommentPanel extends Composite {
    interface VortexStreamPanelUiBinder extends UiBinder<VerticalPanel, CommentPanel> {}

    public static final  int                       UPDATE_LIEFTIME = 1200 * 1000;
    private static final VortexStreamPanelUiBinder ourUiBinder     = GWT.create(VortexStreamPanelUiBinder.class);
    final VerticalPanel parentPanel;
    @Nonnull
    private final BusService               bus                = Bus.get();
    private final long                     lastUpdate         = System.currentTimeMillis() - UPDATE_LIEFTIME;
    @Nonnull
    private final VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();
    @Nonnull
    private final SoundController soundController;
    private final Sound           userEnteredSound;
    private final Sound           chatMessageSound;
    private final Sound           statusUpdateSound;
    private int     maxRows           = 100;
    private boolean showStatusUpdates = true;
    private           boolean initialized;
    @Nullable private LURI    pool;

    public CommentPanel() {
        super();
        final VerticalPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        parentPanel = widget;
        soundController = new SoundController();

        userEnteredSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3, "/_static/_audio/user_entered.mp3");
        userEnteredSound.setVolume(100);

        chatMessageSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3, "/_static/_audio/new_chat_message.mp3");
        chatMessageSound.setVolume(20);

        statusUpdateSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3, "/_static/_audio/status_update.mp3");
        statusUpdateSound.setVolume(50);


    }

    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
    }

    public boolean isShowStatusUpdates() {
        return showStatusUpdates;
    }

    public void setShowStatusUpdates(final boolean showStatusUpdates) {
        this.showStatusUpdates = showStatusUpdates;
    }

    public void clear() {
        WidgetUtil.removeAllChildren(parentPanel);
        pool = null;
    }

    public void init(final LURI newPool) {
        pool = newPool;

        if (!initialized) {
            new Timer() {
                @Override
                public void run() {
                    bus.listen(new AbstractBusListener() {
                        @Override
                        public void handle(@Nonnull final LiquidMessage message) {
                            if (message.hasResponse()) {
                                final TransferEntity resp = message.response();
                                if (resp.is(Types.T_COMMENT) && resp.has(Dictionary.TEXT_BRIEF) && !resp.$(Dictionary.TEXT_BRIEF)
                                                                                                         .isEmpty()) {
                                    addStreamEntry(new CommentEntryPanel(resp));
                                    chatMessageSound.play();
                                }

                            }
                            //                            if (message.response() != null && message.state() != MessageState.PROVISIONAL && message.state() != MessageState.INITIAL && message.state() != MessageState.FAIL && ((LiquidRequest) message).requestType() == RequestType.VISIT_POOL) {
                            //                                addStreamEntry(new VortexPresenceNotificationPanel(message.response(), pool, message.id().toString()));
                            //                                userEnteredSound.play();
                            //                            }
                        }
                    });
                    Bus.get()
                              .listenForSuccess(User.currentAlias().uri(), RequestType.R_SEND, new BusListener<SendRequest>() {
                                  @Override
                                  public void handle(@Nonnull final SendRequest request) {
                                      addStreamEntry(new DirectMessageStreamEntryPanel(request.response()));
                                  }
                              });

                }
            }.schedule(1000);

            //            /* Moving this to seperate page ??? */
            //            if (Config.isRetrieveUpdates()) {
            //                new Timer() {
            //                    @Override
            //                    public void run() {
            //                        //todo: relying on system time is a bad idea
            //                        bus.send(new RetrieveUpdatesRequest(lastUpdate), new RetrieveStreamEntityCallback(features, maxRows, parentPanel, pool, threadSafeExecutor, false));
            //                        lastUpdate = System.currentTimeMillis();
            //                        this.schedule(10000);
            //                    }
            //                }.schedule(1000);
            //            }
            initialized = true;
        }
        bus.send(new RetrieveCommentsRequest(pool, 50), new RetrieveStreamEntityCallback(maxRows, parentPanel, pool, threadSafeExecutor, false));
    }

    private void addStreamEntry(@Nonnull final StreamEntry vortexStreamContent) {
        StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, vortexStreamContent, false, true);

    }


}