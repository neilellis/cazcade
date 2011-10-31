package cazcade.boardcast.client.main.widgets.board;

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

/**
 * @author neilellis@cazcade.com
 */
public class CreateBoardDialog extends DialogBox implements HistoryAware {

    private Runnable onComplete;
    private HistoryManager historyManager;
    private String historyToken;


    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    public boolean isListed() {
        return listed.getValue();
    }

    @Override
    public void onLocalHistoryTokenChanged(String token) {
        center();
        show();
    }

    @Override
    public void setHistoryManager(HistoryManager historyManager) {

        this.historyManager = historyManager;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void setHistoryToken(String historyToken) {
        this.historyToken = historyToken;
    }

    @Override
    public String getHistoryToken() {
        return historyToken;
    }

    @Override
    public boolean addToRootPanel() {
        return false;
    }


    interface NewBoardDialogUiBinder extends UiBinder<HTMLPanel, CreateBoardDialog> {
    }

    private static NewBoardDialogUiBinder ourUiBinder = GWT.create(NewBoardDialogUiBinder.class);

    @UiField
    HashtagTextBox tagBox;
    @UiField
    DivElement shortnameArea;
    @UiField
    CheckBox listed;
    @UiField
    Button cancelButton;
    @UiField
    Button createButton;

    @UiHandler("createButton")
    public void createClick(ClickEvent e) {
        onComplete.run();
    }

    @UiHandler("cancelButton")
    public void cancelClick(ClickEvent e) {
        hide();
        History.back();
    }

    public CreateBoardDialog() {
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
                });

            }
        });
        setGlassEnabled(true);
//        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
        setAnimationEnabled(true);
        setWidth("600px");
        setHeight("340px");
        setModal(false);
        setText("Create New Board");
        listed.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                shortnameArea.getStyle().setDisplay(booleanValueChangeEvent.getValue() ? Style.Display.BLOCK : Style.Display.NONE);
                setHeight(booleanValueChangeEvent.getValue() ? "340px" : "180px");
                setWidth(booleanValueChangeEvent.getValue() ? "600px" : "350px");
                center();
            }
        });
    }


    public String getBoard() {
//        return "xyz";
        return tagBox.getValue();
    }

}