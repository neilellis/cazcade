/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.liquid.api.request.RetrieveCommentsRequest;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.*;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.common.client.User;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.widgets.client.panels.list.dm.DirectMessageStreamEntryPanel;
import cazcade.vortex.widgets.client.panels.scroll.VortexScrollPanel;
import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ChatStreamPanel extends Composite {
    public static final int INACTIVITY_TIMEOUT = 5000;

    public static final int                      UPDATE_LIEFTIME    = 1200 * 1000;
    public static final boolean                  AUTOSCROLL         = true;
    @Nonnull
    private final       BusService               bus                = Bus.get();
    private             int                      maxRows            = 100;
    private final       long                     lastUpdate         = System.currentTimeMillis() - UPDATE_LIEFTIME;
    private             boolean                  showStatusUpdates  = true;
    @Nonnull
    private final       VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();

    @Nonnull
    private final List<Entity> entryEntities = new ArrayList<Entity>();
    @Nonnull
    private final VortexScrollPanel scrollPanel;
    private       boolean           initialized;
    private       LURI              pool;
    @Nonnull
    private final SoundController   soundController;
    private final Sound             userEnteredSound;
    private final Sound             chatMessageSound;
    private final Sound             statusUpdateSound;

    private long lastUserAction;
    private boolean autoScrollOn = true;


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
    }


    interface VortexStreamPanelUiBinder extends UiBinder<HTMLPanel, ChatStreamPanel> {}

    private static final VortexStreamPanelUiBinder ourUiBinder = GWT.create(VortexStreamPanelUiBinder.class);

    @Nonnull
    final VerticalPanel parentPanel;

    public ChatStreamPanel() {
        super();
        final HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        parentPanel = new VerticalPanel();
        parentPanel.setWidth("100%");
        scrollPanel = new VortexScrollPanel(parentPanel, false, true, false, new Runnable() {
            @Override
            public void run() {
                lastUserAction = System.currentTimeMillis();
            }
        });
        widget.add(scrollPanel);
        soundController = new SoundController();

        userEnteredSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3, "/_static/_audio/user_entered.mp3");
        userEnteredSound.setVolume(100);

        chatMessageSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3, "/_static/_audio/new_chat_message.mp3");
        chatMessageSound.setVolume(20);

        statusUpdateSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3, "/_static/_audio/status_update.mp3");
        statusUpdateSound.setVolume(50);

        if (AUTOSCROLL) {
            new Timer() {
                @Override
                public void run() {
                    autoScrollOn = lastUserAction < System.currentTimeMillis() - INACTIVITY_TIMEOUT;
                }
            }.schedule(1000);
        }

    }

    public void init(final LURI newPool) {
        pool = newPool;
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                clear();
                final VerticalPanel filler = new VerticalPanel();
                filler.add(new Label(""));
                parentPanel.add(filler);
                filler.setHeight(getOffsetHeight() + "px");
            }
        });


        if (!initialized) {
            new Timer() {
                @Override
                public void run() {
                    bus.listen(new AbstractBusListener() {
                        @Override
                        public void handle(@Nonnull final LiquidMessage message) {
                            if (message.hasResponse()) {
                                final Entity response = message.response();
                                if (response.is(Types.T_CHAT)
                                    && response.$(Dictionary.TEXT_BRIEF) != null
                                    && !response.$(Dictionary.TEXT_BRIEF).isEmpty()) {
                                    addStreamEntry(new VortexStreamEntryPanel(response, FormatUtil.getInstance()));
                                    chatMessageSound.play();


                                }
                                if (message.state() != MessageState.PROVISIONAL
                                    && message.state() != MessageState.INITIAL
                                    && message.state() != MessageState.FAIL
                                    && ((LiquidRequest) message).requestType() == RequestType.R_VISIT_POOL) {
                                    addStreamEntry(new VortexPresenceNotificationPanel(response, pool, message.id().toString()));
                                    userEnteredSound.play();
                                }
                            }
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

            /*
            if (Config.isRetrieveUpdates()) {
                new Timer() {
                    @Override
                    public void run() {
                        //todo: relying on system time is a bad idea
                        bus.send(new RetrieveUpdatesRequest(lastUpdate), new RetrieveStreamEntityCallback());
                        lastUpdate = System.currentTimeMillis();
                        this.schedule(10000);
                    }
                }.schedule(1000);
            }
            */
            initialized = true;
        }
        //warm it up with a few and then ask for the rest later :-)
        bus.send(new RetrieveCommentsRequest(pool, 50), new RetrieveStreamEntityCallback() {
            @Override
            public void onSuccess(final AbstractRequest original, @Nonnull final AbstractRequest message) {
                super.onSuccess(original, message);
            }
        });
    }

    private void autoScroll() {
        if (autoScrollOn) {
            scrollPanel.scrollToBottom();
        }
    }

    private void addStreamEntry(@Nonnull final StreamEntry vortexStreamContent) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //        entryEntities.add(vortexStreamContent.$());
                boolean inserted = false;
                final boolean atBottom = scrollPanel.isAtBottom();
                int i = parentPanel.getWidgetCount();
                while (i > 0) {
                    i--;
                    if (!(parentPanel.getWidget(i) instanceof StreamEntry)) {
                        continue;
                    }
                    final StreamEntry panel = (StreamEntry) parentPanel.getWidget(i);
                    if (vortexStreamContent.getStreamIdentifier().equals(panel.getStreamIdentifier())) {
                        parentPanel.remove(panel);
                        break;
                    }
                }

                i = parentPanel.getWidgetCount();
                while (i > 0) {
                    i--;
                    if (!(parentPanel.getWidget(i) instanceof StreamEntry)) {
                        continue;
                    }
                    final StreamEntry panel = (StreamEntry) parentPanel.getWidget(i);
                    if (panel.getSortDate().before(vortexStreamContent.getSortDate())) {
                        parentPanel.insert(vortexStreamContent, i + 1);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
                    if (parentPanel.getWidgetCount() > 1) {
                        parentPanel.insert(vortexStreamContent, 1);
                    } else {
                        parentPanel.add(vortexStreamContent);
                    }
                }
                if (parentPanel.getWidgetCount() > maxRows) {
                    final Widget widgetToRemove = parentPanel.getWidget(1);
                    widgetToRemove.removeFromParent();
                }
                autoScroll();
            }
        });

    }

    @Override
    protected void onAttach() {
        super.onAttach();
        scrollPanel.scrollToBottom();
    }

    @Nonnull
    private String entryComparisonString(@Nonnull final Entity entity) {
        final Entity author = entity.child(Dictionary.AUTHOR_A, true);
        return entity.$(Dictionary.TEXT_BRIEF) + (author == null ? "null" : author.$(Dictionary.NAME));
    }

    private class RetrieveStreamEntityCallback extends AbstractMessageCallback<AbstractRequest> {
        @Override
        public void onSuccess(final AbstractRequest original, @Nonnull final AbstractRequest message) {
            final List<Entity> entries = message.response().children(Dictionary.CHILD_A);
            for (final Entity entry : entries) {
                if (entry.is(Types.T_COMMENT) && entry.$(Dictionary.TEXT_BRIEF) != null && !entry.$(Dictionary.TEXT_BRIEF)
                                                                                                 .isEmpty()) {
                    addStreamEntry(new VortexStreamEntryPanel(entry, FormatUtil.getInstance()));
                } else {
                    if (entry.has(Dictionary.SOURCE)) {
                        //TODO: This should all be done on the serverside (see LatestContentFinder).
                        final Entity author = entry.child(Dictionary.AUTHOR_A, true);
                        final boolean isMe = author.$(Dictionary.URI).equals(User.getIdentity().aliasURI().asString());
                        final boolean isAnon = User.isAnonymousAliasURI(author.$(Dictionary.URI));
                        final LURI sourceURI = new LURI(entry.$(Dictionary.SOURCE));
                        final boolean isHere = sourceURI.withoutFragmentOrComment().equals(pool.withoutFragmentOrComment());
                        final boolean expired = entry.published().getTime() < System.currentTimeMillis() - UPDATE_LIEFTIME;

                        if (!isAnon && !expired && !isMe && !isHere && BoardURL.isConvertable(sourceURI)) {
                            addStreamEntry(new VortexStatusUpdatePanel(entry, false));
                            //  statusUpdateSound.play();
                        }
                    }

                }
            }
            scrollPanel.scrollToBottom();

        }
    }
}