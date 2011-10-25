package cazcade.vortex.pool.objects.richtext;

import cazcade.vortex.common.client.FormatUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class CaptionView extends RichTextView {

    protected static CaptionViewUIBinder ourUiBinder = GWT.create(CaptionViewUIBinder.class);

    interface CaptionViewUIBinder extends UiBinder<HTMLPanel, RichTextView> {
    }

    public CaptionView() {
        initWidget(ourUiBinder.createAndBindUi(this));

    }

    public CaptionView(FormatUtil formatter) {
        initWidget(ourUiBinder.createAndBindUi(this));
        label.setFormatter(formatter);

    }


}