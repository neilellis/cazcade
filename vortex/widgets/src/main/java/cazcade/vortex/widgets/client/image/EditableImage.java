package cazcade.vortex.widgets.client.image;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.widgets.client.popup.VortexPopupPanel;
import cazcade.vortex.widgets.client.profile.Bindable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author neilellis@cazcade.com
 */
public class EditableImage extends Composite implements Bindable {
    private Runnable onChangeAction;
    protected LSDEntity entity;
    private LSDAttribute attribute;
    protected boolean editable = true;

    @Override
    public void bind(LSDEntity entity, LSDAttribute attribute, String referenceDataPrefix) {
        this.entity = entity;
        this.attribute = attribute;
        image.setUrl(entity.getAttribute(attribute));
    }

    @Override
    public void setOnChangeAction(Runnable onChangeAction) {
        this.onChangeAction = onChangeAction;
//        Window.alert("On Change Action is "+onChangeAction);
    }

    @Override
    public void setErrorMessage(String message) {
        Window.alert(message);
    }


    @Override
    public LSDEntity getEntityDiff() {
        final LSDEntity result = entity.asUpdateEntity();
        result.setAttribute(attribute, image.getUnCachedUrl());
        return result;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    interface EditableImageUiBinder extends UiBinder<HTMLPanel, EditableImage> {
    }

    private static EditableImageUiBinder ourUiBinder = GWT.create(EditableImageUiBinder.class);
    @UiField
    CachedImage image;
    @UiField
    SpanElement editText;

    public EditableImage() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        image.setOnChangeAction(onChangeAction);
    }

    @UiHandler("image")
    public void onClick(ClickEvent e) {
        if (entity.getBooleanAttribute(LSDAttribute.EDITABLE) && editable) {
            final ImageEditorDialogBox imageEditorDialogBox = new ImageEditorDialogBox();
            imageEditorDialogBox.addCloseHandler(new CloseHandler<PopupPanel>() {
                @Override
                public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            onChangeAction.run();
                        }
                    });
                }
            });
            imageEditorDialogBox.showRelativeTo(image);

        }
    }

    public void setSize(String size) {
        image.setSize(size);
    }

    @Override
    public void setWidth(String width) {
        image.setWidth(width);
        super.setWidth(width);
    }


    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        image.setHeight(height);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        editText.getStyle().setVisibility(editable ? Style.Visibility.VISIBLE : Style.Visibility.HIDDEN);
    }


    private class ImageEditorDialogBox extends VortexPopupPanel {
        private ImageEditorDialogBox() {
            super();
            final ImageEditor editor = new ImageEditor(image);
            setWidget(editor);
            setWidth("600px");
            setHeight("350px");
        }

    }

}