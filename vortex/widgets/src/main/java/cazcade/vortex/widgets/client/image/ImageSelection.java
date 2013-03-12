/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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

    public void selected(final ImageOption imageOption) {
        if (selectionAction != null) {
            selectionAction.onSelect(imageOption);
        }
    }

    interface ImageSelectionUiBinder extends UiBinder<HTMLPanel, ImageSelection> {}

    private static final ImageSelectionUiBinder ourUiBinder = GWT.create(ImageSelectionUiBinder.class);

    @UiField HTMLPanel grid;

    public ImageSelection() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
    }


    @UiChild(tagname = "option")
    public void addOption(@Nonnull final ImageOption option) {
        option.setImageSelection(this);
        grid.add(option);
    }

    public void setSelectionAction(final SelectionAction selectionAction) {
        this.selectionAction = selectionAction;
    }
}