package cazcade.vortex.pool.objects.richtext;

import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.widgets.client.image.CachedImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class NoteView extends RichTextView {

    protected static NoteViewUIBinder ourUiBinder = GWT.create(NoteViewUIBinder.class);

    interface NoteViewUIBinder extends UiBinder<HTMLPanel, RichTextView> {
    }

    public NoteView() {
        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void onAddToPool() {
        super.onAddToPool();
          if (size != null) {
            if (size.equals(SMALL)) {
                getWidget().setWidth(SMALL_WIDTH + "px");
//                getWidget().setHeight(SMALL_HEIGHT + "px");
            }
            if (size.equals(MEDIUM)) {
                getWidget().setWidth(MEDIUM_WIDTH + "px");
//                getWidget().setHeight(MEDIUM_HEIGHT + "px");
            }
            if (size.equals(LARGE)) {
                getWidget().setWidth(LARGE_WIDTH + "px");
//                getWidget().setHeight(LARGE_HEIGHT + "px");
            }
        }
    }

    public NoteView(FormatUtil formatter) {
        initWidget(ourUiBinder.createAndBindUi(this));
        label.setFormatter(formatter);

    }


}