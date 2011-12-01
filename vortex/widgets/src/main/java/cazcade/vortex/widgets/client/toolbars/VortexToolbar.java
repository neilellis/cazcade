package cazcade.vortex.widgets.client.toolbars;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class VortexToolbar extends Composite {
    interface VortexToolbarUiBinder extends UiBinder<HTMLPanel, VortexToolbar> {
    }

    private static final VortexToolbarUiBinder ourUiBinder = GWT.create(VortexToolbarUiBinder.class);
    @UiField
    HeadingElement title;

    public VortexToolbar() {
        initWidget(ourUiBinder.createAndBindUi(this));

    }
}