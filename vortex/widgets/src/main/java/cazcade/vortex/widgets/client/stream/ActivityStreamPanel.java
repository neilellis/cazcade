/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.BoardURL;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.RetrieveUpdatesRequest;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.UserUtil;
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
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ActivityStreamPanel extends HistoryAwareComposite {
    public static final int                      UPDATE_LIEFTIME        = 7 * 24 * 3600 * 1000;
    public static final int                      STATUS_CHECK_FREQUENCY = 30 * 1000;
    @Nonnull
    private final       Bus                      bus                    = BusFactory.get();
    private             int                      maxRows                = 10;
    private             long                     lastUpdate             = System.currentTimeMillis() - UPDATE_LIEFTIME;
    @Nonnull
    private final       VortexThreadSafeExecutor threadSafeExecutor     = new VortexThreadSafeExecutor();

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
            new Timer() {
                @Override
                public void run() {

                    BusFactory.get()
                              .listenForSuccess(UserUtil.currentAlias().uri(), RequestType.SEND, new BusListener<SendRequest>() {
                                  @Override
                                  public void handle(@Nonnull final SendRequest request) {
                                      final DirectMessageStreamEntryPanel content = new DirectMessageStreamEntryPanel(request.response());
                                      addToStream(content);
                                      chatMessageSound.play();
                                  }
                              });

                }
            }.schedule(1000);

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
        StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, content, true, true);
    }

    private void retrieveUpdates() {
        bus.send(new RetrieveUpdatesRequest(lastUpdate), new AbstractResponseCallback<RetrieveUpdatesRequest>() {
            @Override
            public void onSuccess(final RetrieveUpdatesRequest message, @Nonnull final RetrieveUpdatesRequest response) {
                lastUpdate = System.currentTimeMillis();
                final List<TransferEntity> entries = response.response().children(Dictionary.CHILD_A);
                Collections.reverse(entries);
                for (final TransferEntity entry : entries) {
                    if (entry.is(Types.T_COMMENT) && entry.has$(Dictionary.TEXT_BRIEF) && !entry.$(Dictionary.TEXT_BRIEF)
                                                                                                .isEmpty()) {
                        StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, new CommentEntryPanel(entry), false, true);
                    } else {
                        final Entity author = entry.child(Dictionary.AUTHOR_A, true);
                        final boolean isAnon = UserUtil.isAnonymousAliasURI(author.$(Dictionary.URI));
                        final LiquidURI sourceURI = new LiquidURI(entry.$(Dictionary.SOURCE));

                        if (!isAnon && BoardURL.isConvertable(sourceURI)) {
                            StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, new VortexStatusUpdatePanel(entry, true), false, true);
                            //  statusUpdateSound.play();
                        }
                    }
                }
            }
        });
        //        bus.send(new RetrieveUpdatesRequest(lastUpdate), new RetrieveStreamEntityCallback(FormatUtil.getInstance(), maxRows, parentPanel, null, threadSafeExecutor, true) {
        //            @Override
        //            public void onSuccess(AbstractRequest message, AbstractRequest response) {
        //                lastUpdate = response.response().updated().getTime();
        //                Window.alert("Success");
        //                super.onSuccess(message, response);
        //            }
        //        });
    }

}