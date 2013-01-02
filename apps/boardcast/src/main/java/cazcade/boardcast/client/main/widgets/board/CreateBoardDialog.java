package cazcade.boardcast.client.main.widgets.board;

import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.gwt.util.client.history.HistoryAware;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import cazcade.vortex.widgets.client.form.fields.HashtagTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CreateBoardDialog extends DialogBox implements HistoryAware {
    private static final NewBoardDialogUiBinder ourUiBinder = GWT.create(NewBoardDialogUiBinder.class);

    @UiField
    HashtagTextBox tagBox;
    @UiField
    DivElement shortnameArea;
    @UiField
    CheckBox listedCheckBox;
    @UiField
    Button cancelButton;
    @UiField
    Button createButton;

    private Runnable onComplete;
    private HistoryManager historyManager;
    private String historyToken;
    private boolean unlistedToken;

    public CreateBoardDialog() {
        super();
        setWidget(ourUiBinder.createAndBindUi(this));
        tagBox.sinkEvents(Event.KEYEVENTS);
        tagBox.setOnChangeAction(new Runnable() {
            @Override
            public void run() {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        onComplete.run();
                    }
                }
                                                );
            }
        }
                                );
        setGlassEnabled(true);
//        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
        setAnimationEnabled(true);
        setWidth("600px");
        setHeight("220px");
        setModal(false);
        setText("Create New Board");
        listedCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(@Nonnull final ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                final Boolean listed = booleanValueChangeEvent.getValue();
                showListed(listed);
            }
        }
                                            );

        tagBox.setValue(UserUtil.getCurrentAliasName() + "-" + Integer.toString(WidgetUtil.secondsFromBeginningOfBoardcastEpoch(),
                                                                                36
                                                                               )
                       );
    }

    private void showListed(final boolean listed) {
        shortnameArea.getStyle().setDisplay(listed ? Style.Display.BLOCK : Style.Display.NONE);
        setHeight(listed ? "220px" : "200px");
        setWidth(listed ? "600px" : "600px");
        center();
    }

    @Override
    public boolean addToRootPanel() {
        return false;
    }

    @UiHandler("cancelButton")
    public void cancelClick(final ClickEvent e) {
        hide();
        History.back();
    }

    @UiHandler("createButton")
    public void createClick(final ClickEvent e) {
        onComplete.run();
    }

    public String getBoard() {
//        return "xyz";
        return tagBox.getValue();
    }

    public boolean isListed() {
        if (unlistedToken) {
            return false;
        }
        else {
            return listedCheckBox.getValue();
        }
    }

    @Override
    public void onLocalHistoryTokenChanged(@Nonnull final String token) {
        //unlisted boards don't actually need the dialog, we just create them
        if ("unlisted".equals(token)) {
            unlistedToken = true;
            onComplete.run();
        }
        else {
            unlistedToken = false;
            center();
            show();
        }
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void setHistoryManager(final HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public String getHistoryToken() {
        return historyToken;
    }

    @Override
    public void setHistoryToken(final String historyToken) {
        this.historyToken = historyToken;
    }

    public void setOnComplete(final Runnable onComplete) {
        this.onComplete = onComplete;
    }

    interface NewBoardDialogUiBinder extends UiBinder<HTMLPanel, CreateBoardDialog> {
    }
}