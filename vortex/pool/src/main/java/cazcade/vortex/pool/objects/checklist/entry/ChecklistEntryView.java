package cazcade.vortex.pool.objects.checklist.entry;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.widgets.client.misc.EditableLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ChecklistEntryView extends Composite {

    public static final String BALLOT = String.valueOf((char) 0x2610);
    public static final String BALLOT_TICKED = String.valueOf((char) 0x2611);
    public static final String BALLOT_CROSSED = String.valueOf((char) 0x2612);

    private boolean checked;
    @Nonnull
    private final LSDTransferEntity entity;

    interface ChecklistEntryViewUiBinder extends UiBinder<HTMLPanel, ChecklistEntryView> {
    }

    private static final ChecklistEntryViewUiBinder ourUiBinder = GWT.create(ChecklistEntryViewUiBinder.class);
    @UiField
    EditableLabel label;
    @UiField
    Label checkbox;
    @UiField
    Label author;

    public ChecklistEntryView(@Nonnull final LSDTransferEntity newEntity) {
        super();
        entity = newEntity;
        initWidget(ourUiBinder.createAndBindUi(this));
        label.setShowBrief(true);
        update(newEntity);
        BusFactory.getInstance().listenForResponsesForURIAndType(newEntity.getURI(), LiquidRequestType.UPDATE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (message.getState() != LiquidMessageState.PROVISIONAL) {
                    update(message.getResponse().copy());
                }
            }
        });

        checkbox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                checked = !checked;
                updateCheckStatus();
                final LSDTransferEntity updateEntity = entity.asUpdateEntity();
                updateEntity.setAttribute(LSDAttribute.CHECKED, checked);
                BusFactory.getInstance().send(new UpdatePoolObjectRequest(updateEntity), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                });
            }
        });

        label.setOnEditEndAction(new Runnable() {
            @Override
            public void run() {
                final LSDTransferEntity updateEntity = entity.asUpdateEntity();
                updateEntity.setAttribute(LSDAttribute.TEXT_EXTENDED, label.getText());
                BusFactory.getInstance().send(new UpdatePoolObjectRequest(updateEntity), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                });
            }
        });
    }

    private void updateCheckStatus() {
        if (checked) {
            checkbox.setText(BALLOT_TICKED);
        } else {
            checkbox.setText(BALLOT);
        }
    }

    private void update(@Nonnull final LSDBaseEntity entity) {
        if (entity.hasAttribute(LSDAttribute.CHECKED)) {
            checked = entity.getBooleanAttribute(LSDAttribute.CHECKED);
        }
        if (entity.hasAttribute(LSDAttribute.TEXT_EXTENDED)) {
            label.setText(entity.getAttribute(LSDAttribute.TEXT_EXTENDED));
        }
        final LSDBaseEntity authorEntity = entity.getSubEntity(LSDAttribute.AUTHOR, true);
        if (authorEntity != null) {
            author.setText(authorEntity.getAttribute(LSDAttribute.NAME));
        } else {
            author.setText("unknown");
        }

        updateCheckStatus();
    }
}