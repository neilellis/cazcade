/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.BoardURL;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.RetrieveUpdatesRequest;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.*;
import cazcade.vortex.common.client.User;
import cazcade.vortex.gwt.util.client.$;
import cazcade.vortex.gwt.util.client.StartupUtil;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.gwt.util.client.history.HistoryAwareComposite;
import cazcade.vortex.widgets.client.panels.list.dm.DirectMessageStreamEntryPanel;
import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import javax.annotation.Nonnull;

import static cazcade.liquid.api.RequestType.*;
import static cazcade.liquid.api.lsd.Dictionary.*;
import static cazcade.liquid.api.lsd.Types.*;

/**
 * @author neilellis@cazcade.com
 */
public class ActivityStreamPanel extends HistoryAwareComposite {
    public static final int                      UPDATE_LIEFTIME        = 7 * 24 * 3600 * 1000;
    public static final int                      STATUS_CHECK_FREQUENCY = 30 * 1000;
    @Nonnull
    private final       BusService               bus                    = Bus.get();
    private             int                      maxRows                = 10;
    private             long                     lastUpdate             = System.currentTimeMillis() - UPDATE_LIEFTIME;
    @Nonnull
    private final       VortexThreadSafeExecutor executor               = new VortexThreadSafeExecutor();

    private       boolean         initialized;
    @Nonnull
    private final SoundController soundController;
    private final Sound           statusUpdateSound;
    private final Sound           chatMessageSound;


    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
    }

    public void clear() {
        WidgetUtil.removeAllChildren(parentPanel);
    }

    @Override
    public void onLocalHistoryTokenChanged(final String token) {
        super.onLocalHistoryTokenChanged(token);
        init();
    }

    interface VortexStreamPanelUiBinder extends UiBinder<HTMLPanel, ActivityStreamPanel> {}

    private static final VortexStreamPanelUiBinder ourUiBinder = GWT.create(VortexStreamPanelUiBinder.class);

    @UiField VerticalPanel parentPanel;

    public ActivityStreamPanel() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        soundController = new SoundController();


        statusUpdateSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3, "/_static/_audio/status_update.mp3");
        statusUpdateSound.setVolume(50);

        chatMessageSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3, "/_static/_audio/new_chat_message.mp3");
        chatMessageSound.setVolume(20);

    }

    public void init() {
        if (!initialized) {
            $.delay(1000, new Runnable() {
                public void run() {
                    Bus.get().listenForSuccess(User.currentAlias().uri(), R_SEND, new BusListener<SendRequest>() {
                        @Override
                        public void handle(@Nonnull final SendRequest message) {
                            addToStream(new DirectMessageStreamEntryPanel(message.response()));
                            chatMessageSound.play();
                        }
                    });
                }
            });

            new Timer() {
                @Override
                public void run() {
                    retrieveUpdates();
                }

            }.scheduleRepeating(STATUS_CHECK_FREQUENCY);
            retrieveUpdates();
            StartupUtil.showLiveVersion(getWidget().getElement().getParentElement());
            WidgetUtil.showGracefully(getWidget(), false);
            initialized = true;
        }

    }

    private void addToStream(@Nonnull final StreamEntry content) {
        StreamUtil.addStreamEntry(maxRows, parentPanel, executor, content, true, true);
    }

    private void retrieveUpdates() {
        Bus.get().send(new RetrieveUpdatesRequest(lastUpdate), new Callback<RetrieveUpdatesRequest>() {
            @Override public void handle(RetrieveUpdatesRequest message) throws Exception {
                lastUpdate = System.currentTimeMillis();
                message.response().children().reverse().each(new CollectionCallback<TransferEntity>() {
                    @Override public void call(TransferEntity entry) {
                        if (entry.is(T_COMMENT) && entry.has(TEXT_BRIEF)) {
                            StreamUtil.addStreamEntry(maxRows, parentPanel, executor, new CommentEntryPanel(entry), false, true);
                        } else if (!entry.child(AUTHOR_A, true).uri().anon() && BoardURL.isConvertable(entry.$uri(SOURCE))) {
                            StreamUtil.addStreamEntry(maxRows, parentPanel, executor, new VortexStatusUpdatePanel(entry, true), false, true);
                            //  statusUpdateSound.play();
                        }
                    }
                });
            }
        });
    }

}