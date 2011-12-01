package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel;
import cazcade.vortex.widgets.client.image.ImageOption;
import cazcade.vortex.widgets.client.image.ImageSelection;
import cazcade.vortex.widgets.client.popup.PopupEditPanel;
import cazcade.vortex.widgets.client.popup.VortexPopupPanel;
import cazcade.vortex.widgets.client.profile.Bindable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ChangeBackgroundDialog extends Composite implements Bindable, PopupEditPanel {
    private LSDBaseEntity oldEntity;


    @Override
    public void bind(@Nonnull final LSDTransferEntity entity, final LSDAttribute attribute, final String referenceDataPrefix) {
        oldEntity = entity.copy();
        changeBackgroundPanel.bind(entity, attribute, referenceDataPrefix);
    }

    @Override
    public void setOnChangeAction(@Nonnull final Runnable onChangeAction) {
        changeBackgroundPanel.setOnChangeAction(onChangeAction);
    }

    @Override
    public void setErrorMessage(final String message) {
        Window.alert(message);
    }

    @Override
    public LSDTransferEntity getEntityDiff() {
        final LSDTransferEntity updateEntity = changeBackgroundPanel.getEntityDiff();
//        final String oldIconUrl = oldEntity.getAttribute(LSDAttribute.ICON_URL);
//        if (oldIconUrl == null || oldIconUrl.equals(oldEntity.getAttribute(LSDAttribute.IMAGE_URL))) {
//            updateEntity.setAttribute(LSDAttribute.ICON_URL, changeBackgroundPanel.getStringValue());
//        }
        return updateEntity;
    }

    @Override
    public boolean isValid() {
        return changeBackgroundPanel.isValid();
    }

    interface ChangeBackgroundDialogUiBinder extends UiBinder<HTMLPanel, ChangeBackgroundDialog> {
    }

    private static final ChangeBackgroundDialogUiBinder ourUiBinder = GWT.create(ChangeBackgroundDialogUiBinder.class);
    @UiField
    ChangeImageUrlPanel changeBackgroundPanel;
    @UiField
    ImageSelection imageSelector;


    public void show() {
        final VortexPopupPanel vortexPopupPanel = new VortexPopupPanel();
        vortexPopupPanel.setWidget(this);
        vortexPopupPanel.setAutoHideEnabled(true);
        vortexPopupPanel.setAutoHideOnHistoryEventsEnabled(true);
        vortexPopupPanel.setGlassEnabled(false);
        vortexPopupPanel.setWidth("800px");
        vortexPopupPanel.setHeight("400px");
        vortexPopupPanel.center();
        vortexPopupPanel.show();
        vortexPopupPanel.setOnFinishAction(new Runnable() {
            @Override
            public void run() {
                vortexPopupPanel.hide();
            }
        });
    }


    public ChangeBackgroundDialog() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        imageSelector.setSelectionAction(new ImageSelection.SelectionAction() {
            @Override
            public void onSelect(@Nonnull final ImageOption imageOption) {
                changeBackgroundPanel.setValue(imageOption.getUrl());
                changeBackgroundPanel.callOnChangeAction();
            }
        });

    }
}