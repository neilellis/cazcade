package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.AbstractBusListener;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.common.client.UserUtil;
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

/**
 * @author neilellis@cazcade.com
 */
public class NotificationPanel extends Composite {
    public static final int UPDATE_LIEFTIME = 1200 * 1000;
    public static final int STATUS_CHECK_FREQUENCY = 30000;
    private Bus bus = BusFactory.getInstance();
    private int maxRows = 10;
    private long lastUpdate = System.currentTimeMillis() - UPDATE_LIEFTIME;
    private VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();

    private FormatUtil features;
    private boolean initialized;
    private LiquidURI pool;
    private SoundController soundController;
    private Sound userEnteredSound;
    private Sound statusUpdateSound;


    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public void clear() {
        WidgetUtil.removeAllChildren(parentPanel);
    }


    interface VortexStreamPanelUiBinder extends UiBinder<HTMLPanel, NotificationPanel> {
    }

    private static VortexStreamPanelUiBinder ourUiBinder = GWT.create(VortexStreamPanelUiBinder.class);

    @UiField
    HorizontalPanel parentPanel;

    public NotificationPanel() {
        final HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        soundController = new SoundController();

        userEnteredSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3,
                "_audio/user_entered.mp3");
        userEnteredSound.setVolume(100);

        statusUpdateSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3,
                "_audio/status_update.mp3");
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

    public void init(final LiquidURI newPool, final FormatUtil formatter) {
        this.pool = newPool;
        this.features = formatter;
        clear();

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
                                addToStream(new CommentEntryPanel(response, features));
                            }
                            if (response != null && message.getState() != LiquidMessageState.PROVISIONAL && message.getState() != LiquidMessageState.INITIAL && message.getState() != LiquidMessageState.FAIL && ((LiquidRequest) message).getRequestType() == LiquidRequestType.VISIT_POOL
                                    && !UserUtil.isAnonymousAliasURI(response.getSubEntity(LSDAttribute.VISITOR, false).getURI().toString())
                                    ) {
                                VortexPresenceNotificationPanel content = new VortexPresenceNotificationPanel(response, pool, message.getId().toString());
                                addToStream(content);
                                try {
                                    userEnteredSound.play();
                                } catch (Exception e) {
                                    ClientLog.log(e);
                                }
                            }
                        }
                    });

                    BusFactory.getInstance().listenForURIAndSuccessfulRequestType(UserUtil.getCurrentAlias().getURI(), LiquidRequestType.SEND, new BusListener<SendRequest>() {
                        @Override
                        public void handle(SendRequest request) {
                            DirectMessageStreamEntryPanel content = new DirectMessageStreamEntryPanel(request.getResponse(), formatter);
                            addToStream(content);
                        }
                    });

                }
            }.schedule(1000);

//            if (ClientApplicationConfiguration.isRetrieveUpdates()) {
//                new Timer() {
//                    @Override
//                    public void run() {
//                        bus.send(new RetrieveUpdatesRequest(lastUpdate), new RetrieveStreamEntityCallback(formatter, maxRows, parentPanel, pool, threadSafeExecutor, true) {
//                            @Override
//                            public void onSuccess(AbstractRequest message, AbstractRequest response) {
//                                //relying on system time is a bad idea, so we use server time.
//                                lastUpdate = response.getResponse().getUpdated().getTime();
//                                super.onSuccess(message, response);
//                            }
//                        });
//                    }
//                }.scheduleRepeating(STATUS_CHECK_FREQUENCY);
//            }
            initialized = true;
        }
    }

    private void addToStream(final StreamEntry content) {
        StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, content, true);
    }

}