package cazcade.vortex.pool.objects.richtext;

import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.pool.objects.PoolObjectView;
import cazcade.vortex.widgets.client.misc.EditableLabel;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

/**
 * @author neilellis@cazcade.com
 */
public class RichTextView extends PoolObjectView {
    private Runnable onChangeAction;
    @UiField
    EditableLabel label;
    @UiField
    Label dateTime;
    @UiField
    Label location;
    @UiField
    Label authorName;

    @Override
    protected void onLoad() {
        super.onLoad();
        getWidget().sinkEvents(Event.MOUSEEVENTS);
        getWidget().addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                Window.alert("edit.");
                label.startEdit();
            }
        }, DoubleClickEvent.getType());
    }

    public void setText(String value) {
//        label.setWordWrap(true);
//        label.setText(SimpleHtmlSanitizer.sanitizeHtml(value));
        label.setText(value);
        label.setEditable(isEditable());
        label.setOnEditAction(new Runnable() {
            @Override
            public void run() {
                editMode();
            }
        });
        label.setOnEditEndAction(new Runnable() {
            @Override
            public void run() {
                viewMode();
                onChangeAction.run();
            }
        });
    }

    public void setOnChangeAction(Runnable onChangeAction) {
        this.onChangeAction = onChangeAction;
    }

    public String getText() {
        return label.getText();
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (isEditing()) {
            ClientLog.log("Browser event while edit mode on.");
//            label.onBrowserEvent(event);
        } else {
            super.onBrowserEvent(event);
        }
    }
}
