package cazcade.vortex.widgets.client.toolbars;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class VortexIconSet extends Composite {
    interface VortexIconToolbarUiBinder extends UiBinder<HTMLPanel, VortexIconSet> {
    }

    private static final VortexIconToolbarUiBinder ourUiBinder = GWT.create(VortexIconToolbarUiBinder.class);

    public VortexIconSet() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));

    }
}