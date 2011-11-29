package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.liquid.api.request.RetrieveCommentsRequest;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.*;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.common.client.UserUtil;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ChatStreamPanel extends Composite {
    public static final int INACTIVITY_TIMEOUT = 5000;

    public static final int UPDATE_LIEFTIME = 1200 * 1000;
    public static final boolean AUTOSCROLL = true;
    private Bus bus = BusFactory.getInstance();
    private int maxRows = 100;
    private long lastUpdate = System.currentTimeMillis() - UPDATE_LIEFTIME;
    private boolean showStatusUpdates = true;
    private VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();

    private List<LSDEntity> entryEntities = new ArrayList<LSDEntity>();
    private FormatUtil features;
    private VortexScrollPanel scrollPanel;
    private boolean initialized;
    private LiquidURI pool;
    private SoundController soundController;
    private Sound userEnteredSound;
    private Sound chatMessageSound;
    private Sound statusUpdateSound;

    private long lastUserAction;
    private boolean autoScrollOn = true;


    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public boolean isShowStatusUpdates() {
        return showStatusUpdates;
    }

    public void setShowStatusUpdates(boolean showStatusUpdates) {
        this.showStatusUpdates = showStatusUpdates;
    }

    public void clear() {
        WidgetUtil.removeAllChildren(parentPanel);
    }


    interface VortexStreamPanelUiBinder extends UiBinder<HTMLPanel, ChatStreamPanel> {
    }

    private static VortexStreamPanelUiBinder ourUiBinder = GWT.create(VortexStreamPanelUiBinder.class);

    VerticalPanel parentPanel;

    public ChatStreamPanel() {
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

        userEnteredSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3,
                "_audio/user_entered.mp3");
        userEnteredSound.setVolume(100);

        chatMessageSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3,
                "_audio/new_chat_message.mp3");
        chatMessageSound.setVolume(20);

        statusUpdateSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3,
                "_audio/status_update.mp3");
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

    public void init(final LiquidURI newPool, final FormatUtil features) {
        this.pool = newPool;
        this.features = features;
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
                        public void handle(LiquidMessage message) {
                            final LSDEntity response = message.getResponse();
                            if (response != null && response.isA(LSDDictionaryTypes.CHAT)
                                    && response.getAttribute(LSDAttribute.TEXT_BRIEF) != null && !response.getAttribute(LSDAttribute.TEXT_BRIEF).isEmpty()) {
                                addStreamEntry(new VortexStreamEntryPanel(response, features));
                                chatMessageSound.play();


                            }
                            if (response != null && message.getState() != LiquidMessageState.PROVISIONAL && message.getState() != LiquidMessageState.INITIAL && message.getState() != LiquidMessageState.FAIL && ((LiquidRequest) message).getRequestType() == LiquidRequestType.VISIT_POOL) {
                                addStreamEntry(new VortexPresenceNotificationPanel(response, pool, message.getId().toString()));
                                userEnteredSound.play();
                            }
                        }
                    });
                    BusFactory.getInstance().listenForURIAndSuccessfulRequestType(UserUtil.getCurrentAlias().getURI(), LiquidRequestType.SEND, new BusListener<SendRequest>() {
                        @Override
                        public void handle(SendRequest request) {
                            addStreamEntry(new DirectMessageStreamEntryPanel(request.getResponse(), features));
                        }
                    });

                }
            }.schedule(1000);

            /*
            if (ClientApplicationConfiguration.isRetrieveUpdates()) {
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
            public void onSuccess(AbstractRequest message, AbstractRequest response) {
                super.onSuccess(message, response);
            }
        });
    }

    private void autoScroll() {
        if (autoScrollOn) {
            scrollPanel.scrollToBottom();
        }
    }

    private void addStreamEntry(final StreamEntry vortexStreamContent) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
//        entryEntities.add(vortexStreamContent.getEntity());
                boolean inserted = false;
                boolean atBottom = scrollPanel.isAtBottom();
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

    private String entryComparisonString(LSDEntity entity) {
        final LSDEntity author = entity.getSubEntity(LSDAttribute.AUTHOR, true);
        return entity.getAttribute(LSDAttribute.TEXT_BRIEF) + (author == null ? "null" : author.getAttribute(LSDAttribute.NAME));
    }

    private class RetrieveStreamEntityCallback extends AbstractResponseCallback<AbstractRequest> {
        @Override
        public void onSuccess(AbstractRequest message, AbstractRequest response) {
            final List<LSDEntity> entries = response.getResponse().getSubEntities(LSDAttribute.CHILD);
            for (LSDEntity entry : entries) {
                if (entry.isA(LSDDictionaryTypes.COMMENT)
                        && entry.getAttribute(LSDAttribute.TEXT_BRIEF) != null && !entry.getAttribute(LSDAttribute.TEXT_BRIEF).isEmpty()) {
                    addStreamEntry(new VortexStreamEntryPanel(entry, features));
                } else {
                    if (entry.hasAttribute(LSDAttribute.SOURCE)) {
                        //TODO: This should all be done on the serverside (see LatestContentFinder).
                        final LSDEntity author = entry.getSubEntity(LSDAttribute.AUTHOR, true);
                        final boolean isMe = author.getAttribute(LSDAttribute.URI).equals(UserUtil.getIdentity().getAliasURL().asString());
                        final boolean isAnon = UserUtil.isAnonymousAliasURI(author.getAttribute(LSDAttribute.URI));
                        final LiquidURI sourceURI = new LiquidURI(entry.getAttribute(LSDAttribute.SOURCE));
                        final boolean isHere = sourceURI.getWithoutFragmentOrComment().equals(pool.getWithoutFragmentOrComment());
                        final boolean expired = entry.getPublished().getTime() < System.currentTimeMillis() - UPDATE_LIEFTIME;

                        if (!isAnon && !expired && !isMe && !isHere && LiquidBoardURL.isConvertable(sourceURI)) {
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