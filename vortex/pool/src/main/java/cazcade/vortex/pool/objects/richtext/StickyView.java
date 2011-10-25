package cazcade.vortex.pool.objects.richtext;

import cazcade.vortex.common.client.FormatUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class StickyView extends RichTextView {

    protected static StickyViewUiBinder ourUiBinder = GWT.create(StickyViewUiBinder.class);

    interface StickyViewUiBinder extends UiBinder<HTMLPanel, RichTextView> {
    }

    public StickyView() {
        initWidget(ourUiBinder.createAndBindUi(this));

    }

    public StickyView(FormatUtil formatter) {
        initWidget(ourUiBinder.createAndBindUi(this));
        label.setFormatter(formatter);

    }


}