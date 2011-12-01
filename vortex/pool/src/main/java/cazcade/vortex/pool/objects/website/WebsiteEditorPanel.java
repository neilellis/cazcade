package cazcade.vortex.pool.objects.website;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.pool.objects.edit.AbstractPoolObjectEditorPanel;
import cazcade.vortex.widgets.client.form.fields.RegexTextBox;
import cazcade.vortex.widgets.client.form.fields.UrlField;
import cazcade.vortex.widgets.client.form.fields.VortexTextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class WebsiteEditorPanel extends AbstractPoolObjectEditorPanel {

    @UiField
    Button done;

    @UiHandler("done")
    public void doneClicked(final ClickEvent e) {
        onFinishAction.run();
    }


    @Override
    public void bind(final LSDTransferEntity entity) {
        super.bind(entity);
        addBinding(urlField, LSDAttribute.SOURCE);
        addBinding(description, LSDAttribute.DESCRIPTION);
        addBinding(title, LSDAttribute.TITLE);
    }

    @Override
    public int getHeight() {
        return 400;
    }

    @Override
    public int getWidth() {
        return 700;
    }

    interface PhotoEditorUiBinder extends UiBinder<HTMLPanel, WebsiteEditorPanel> {
    }


    private static final PhotoEditorUiBinder ourUiBinder = GWT.create(PhotoEditorUiBinder.class);
    @UiField
    VortexTextArea description;
    @UiField
    RegexTextBox title;
    @UiField
    UrlField urlField;


    public WebsiteEditorPanel(final LSDTransferEntity entity) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        bind(entity);
    }
}