package cazcade.vortex.widgets.client.image;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class EditableImage extends Composite implements Bindable {
    private Runnable onChangeAction;
    protected LSDTransferEntity entity;
    private LSDAttribute attribute;
    protected boolean editable = true;

    @Override
    public void bind(@Nonnull final LSDTransferEntity entity, final LSDAttribute attribute, final String referenceDataPrefix) {
        this.entity = entity;
        this.attribute = attribute;
        image.setUrl(entity.getAttribute(attribute));
    }

    @Override
    public void setOnChangeAction(final Runnable onChangeAction) {
        this.onChangeAction = onChangeAction;
//        Window.alert("On Change Action is "+onChangeAction);
    }

    @Override
    public void setErrorMessage(final String message) {
        Window.alert(message);
    }


    @Nonnull
    @Override
    public LSDTransferEntity getEntityDiff() {
        final LSDTransferEntity result = entity.asUpdateEntity();
        result.setAttribute(attribute, image.getUnCachedUrl());
        return result;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    interface EditableImageUiBinder extends UiBinder<HTMLPanel, EditableImage> {
    }

    private static final EditableImageUiBinder ourUiBinder = GWT.create(EditableImageUiBinder.class);
    @UiField
    CachedImage image;
    @UiField
    SpanElement editText;

    public EditableImage() {
        super();
        final HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        image.setOnChangeAction(onChangeAction);
    }

    @UiHandler("image")
    public void onClick(final ClickEvent e) {
        if (entity.getBooleanAttribute(LSDAttribute.EDITABLE) && editable) {
            final ImageEditorDialogBox imageEditorDialogBox = new ImageEditorDialogBox();
            imageEditorDialogBox.addCloseHandler(new CloseHandler<PopupPanel>() {
                @Override
                public void onClose(final CloseEvent<PopupPanel> popupPanelCloseEvent) {
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

    public void setSize(final String size) {
        image.setSize(size);
    }

    @Override
    public void setWidth(@Nonnull final String width) {
        image.setWidth(width);
        super.setWidth(width);
    }


    @Override
    public void setHeight(@Nonnull final String height) {
        super.setHeight(height);
        image.setHeight(height);
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
        editText.getStyle().setVisibility(editable ? Style.Visibility.VISIBLE : Style.Visibility.HIDDEN);
    }


    private class ImageEditorDialogBox extends VortexPopupPanel {
        private ImageEditorDialogBox() {
            super();
            final ImageEditor editor = new ImageEditor(image);
            setWidget(editor);
            setWidth("600px");
            setHeight("380px");
            setOnFinishAction(new Runnable() {
                @Override
                public void run() {
                    hide();
                }
            });
        }

    }

}