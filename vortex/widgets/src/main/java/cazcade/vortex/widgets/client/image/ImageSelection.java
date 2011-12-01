package cazcade.vortex.widgets.client.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;


/**
 * @author neilellis@cazcade.com
 */
public class ImageSelection extends Composite {

    public interface SelectionAction {
        void onSelect(ImageOption imageOption);
    }

    private SelectionAction selectionAction;

    public void selected(ImageOption imageOption) {
        if (selectionAction != null) {
            selectionAction.onSelect(imageOption);
        }
    }

    interface ImageSelectionUiBinder extends UiBinder<HTMLPanel, ImageSelection> {
    }

    private static final ImageSelectionUiBinder ourUiBinder = GWT.create(ImageSelectionUiBinder.class);

    @UiField
    HTMLPanel panel;
    @UiField
    HTMLPanel grid;

    public ImageSelection() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }


    @UiChild(tagname = "option")
    public void addOption(@Nonnull ImageOption option) {
        option.setImageSelection(this);
        grid.add(option);
    }

    public void setSelectionAction(SelectionAction selectionAction) {
        this.selectionAction = selectionAction;
    }
}