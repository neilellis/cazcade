package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.RetrieveCommentsRequest;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.AbstractBusListener;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.common.client.UserUtil;
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

/**
 * @author neilellis@cazcade.com
 */
public class CommentPanel extends Composite {
    public static final int UPDATE_LIEFTIME = 1200 * 1000;
    private Bus bus = BusFactory.getInstance();
    private int maxRows = 100;
    private long lastUpdate = System.currentTimeMillis() - UPDATE_LIEFTIME;
    private boolean showStatusUpdates = true;
    private VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();

    private FormatUtil features;
    private boolean initialized;
    private LiquidURI pool;
    private SoundController soundController;
    private Sound userEnteredSound;
    private Sound chatMessageSound;
    private Sound statusUpdateSound;


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


    interface VortexStreamPanelUiBinder extends UiBinder<VerticalPanel, CommentPanel> {
    }

    private static VortexStreamPanelUiBinder ourUiBinder = GWT.create(VortexStreamPanelUiBinder.class);

    VerticalPanel parentPanel;

    public CommentPanel() {
        final VerticalPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        parentPanel = widget;
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


    }

    public void init(final LiquidURI newPool, final FormatUtil features) {
        this.pool = newPool;
        this.features = features;

        if (!initialized) {
            new Timer() {
                @Override
                public void run() {
                    bus.listen(new AbstractBusListener() {
                        @Override
                        public void handle(LiquidMessage message) {
                            final LSDEntity response = message.getResponse();
                            if (response != null && response.isA(LSDDictionaryTypes.COMMENT)
                                    && response.getAttribute(LSDAttribute.TEXT_BRIEF) != null && !response.getAttribute(LSDAttribute.TEXT_BRIEF).isEmpty()) {
                                addStreamEntry(new CommentEntryPanel(response));
                                chatMessageSound.play();


                            }
//                            if (message.getResponse() != null && message.getState() != LiquidMessageState.PROVISIONAL && message.getState() != LiquidMessageState.INITIAL && message.getState() != LiquidMessageState.FAIL && ((LiquidRequest) message).getRequestType() == LiquidRequestType.VISIT_POOL) {
//                                addStreamEntry(new VortexPresenceNotificationPanel(message.getResponse(), pool, message.getId().toString()));
//                                userEnteredSound.play();
//                            }
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

//            /* Moving this to seperate page ??? */
//            if (ClientApplicationConfiguration.isRetrieveUpdates()) {
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
        bus.send(new RetrieveCommentsRequest(pool, 50), new RetrieveStreamEntityCallback(features, maxRows, parentPanel, pool, threadSafeExecutor, false));
    }


    private void addStreamEntry(final StreamEntry vortexStreamContent) {
        StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, vortexStreamContent, false);

    }


}