package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.liquid.api.request.RetrieveUpdatesRequest;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.FormatUtil;
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
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author neilellis@cazcade.com
 */
public class ActivityStreamPanel extends HistoryAwareComposite {
    public static final int UPDATE_LIEFTIME = 1200 * 1000;
    public static final int STATUS_CHECK_FREQUENCY = 30 * 1000;
    private Bus bus = BusFactory.getInstance();
    private int maxRows = 10;
    //    private long lastUpdate = System.currentTimeMillis() - UPDATE_LIEFTIME;
    private long lastUpdate = 0;
    private VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();

    private boolean initialized;
    private SoundController soundController;
    private Sound statusUpdateSound;
    private Sound chatMessageSound;


    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public void clear() {
        WidgetUtil.removeAllChildren(parentPanel);
    }

    @Override
    public void onLocalHistoryTokenChanged(String token) {
        super.onLocalHistoryTokenChanged(token);
        init();
    }

    interface VortexStreamPanelUiBinder extends UiBinder<HTMLPanel, ActivityStreamPanel> {
    }

    private static VortexStreamPanelUiBinder ourUiBinder = GWT.create(VortexStreamPanelUiBinder.class);

    @UiField
    HorizontalPanel parentPanel;

    public ActivityStreamPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        soundController = new SoundController();


        statusUpdateSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3,
                "_audio/status_update.mp3");
        statusUpdateSound.setVolume(50);

        chatMessageSound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3,
                "_audio/new_chat_message.mp3");
        chatMessageSound.setVolume(20);

    }

    public void init() {
        clear();

        if (!initialized) {
            new Timer() {
                @Override
                public void run() {

                    BusFactory.getInstance().listenForURIAndSuccessfulRequestType(UserUtil.getCurrentAlias().getURI(), LiquidRequestType.SEND, new BusListener<SendRequest>() {
                        @Override
                        public void handle(SendRequest request) {
                            DirectMessageStreamEntryPanel content = new DirectMessageStreamEntryPanel(request.getResponse(), FormatUtil.getInstance());
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
            initialized = true;
        }
        StartupUtil.showLiveVersion(getWidget().getElement().getParentElement());
        WidgetUtil.showGracefully(getWidget(), false);

    }

    private void addToStream(final StreamEntry content) {
        StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, content, true);
    }

    private void retrieveUpdates() {
        bus.send(new RetrieveUpdatesRequest(lastUpdate), new RetrieveStreamEntityCallback(FormatUtil.getInstance(), maxRows, parentPanel, null, threadSafeExecutor, true) {
            @Override
            public void onSuccess(AbstractRequest message, AbstractRequest response) {
                lastUpdate = response.getResponse().getUpdated().getTime();
                super.onSuccess(message, response);
            }
        });
    }

}