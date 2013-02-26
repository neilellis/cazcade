/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.checklist;

import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.pool.objects.PoolObjectView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public class ChecklistView extends PoolObjectView {

    private Runnable onChangeAction;


    public void setOnChangeAction(final Runnable onChangeAction) {
        this.onChangeAction = onChangeAction;
    }


    interface ChecklistViewUiBinder extends UiBinder<HTMLPanel, ChecklistView> {}

    private static final ChecklistViewUiBinder ourUiBinder = GWT.create(ChecklistViewUiBinder.class);
    @UiField VerticalPanel listPanel;


    public ChecklistView() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));

    }


    @Override
    public void onBrowserEvent(final Event event) {
        if (isEditing()) {
            ClientLog.log("Browser event while edit mode on.");
            //            label.onBrowserEvent(event);
        } else {
            super.onBrowserEvent(event);
        }
    }

    @Override
    public void addView(final Widget widget) {
        listPanel.add(widget);
    }

    @Override public int getDefaultZIndex() {
        return 1000;
    }


    @Override
    public void removeView(final Widget widget) {
        listPanel.remove(widget);
    }
}