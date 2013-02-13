/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.list;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.liquid.api.request.BoardQueryRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.gwt.util.client.StartupUtil;
import cazcade.vortex.gwt.util.client.history.HistoryAwareComposite;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import cazcade.vortex.widgets.client.panels.scroll.ShowMorePagerPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class BoardList extends HistoryAwareComposite {
    interface BoardListUiBinder extends UiBinder<HTMLPanel, BoardList> {}

    private static BoardListUiBinder ourUiBinder = GWT.create(BoardListUiBinder.class);
    private final CellList<LSDBaseEntity>   cellList;
    @UiField      ShowMorePagerPanel        pagerPanel;
    @UiField      HeadingElement            boardListTitle;
    private       AbstractRequest.QueryType queryType;
    private final Map<String, String> titleLookup = new HashMap<String, String>();

    public BoardList() {
        initWidget(ourUiBinder.createAndBindUi(this));
        queryType = BoardQueryRequest.QueryType.HISTORY;

        // Create a CellList.

        // Set a key provider that provides a unique key for each contact. If key is
        // used to identify contacts when fields (such as the name and address)
        // change.
        cellList = new CellList<LSDBaseEntity>(new BoardCell(), new ProvidesKey<LSDBaseEntity>() {
            @Nullable @Override public Object getKey(final LSDBaseEntity item) {
                return item == null ? null : item.getURI();
            }
        });
        cellList.setPageSize(10);
        cellList.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE);
        cellList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.BOUND_TO_SELECTION);
        cellList.setStyleName("board-list");
        // Add a selection model so we can select cells.
        final SingleSelectionModel<LSDBaseEntity> selectionModel = new SingleSelectionModel<LSDBaseEntity>();
        cellList.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                LSDBaseEntity selected = selectionModel.getSelectedObject();
                HistoryManager.navigate(selected.getURI().asBoardURL().asUrlSafe());
            }
        });

        AsyncDataProvider<LSDBaseEntity> dataProvider = new AsyncDataProvider<LSDBaseEntity>() {

            @Override protected void onRangeChanged(final HasData<LSDBaseEntity> display) {
                BusFactory.getInstance()
                          .send(new BoardQueryRequest(queryType, UserUtil.getCurrentAlias().getURI(), display.getVisibleRange()
                                                                                                             .getStart(), display.getVisibleRange()
                                                                                                                                 .getLength()), new AbstractResponseCallback<BoardQueryRequest>() {
                              @Override public void onSuccess(BoardQueryRequest message, BoardQueryRequest response) {
                                  super.onSuccess(message, response);
                                  updateRowData(display.getVisibleRange().getStart(), response.getResponse()
                                                                                              .getSubEntities(LSDAttribute.CHILD));
                              }
                          });

            }
        };
        dataProvider.addDataDisplay(cellList);

        pagerPanel.setDisplay(cellList);
        StartupUtil.showLiveVersion(getWidget().getElement());

        titleLookup.put("history", "Recently Visited");
        titleLookup.put("my", "Your Boards");
        titleLookup.put("popular", "Popular Boards");
        titleLookup.put("new", "New Boards");
        titleLookup.put("profile", "User Boards");
    }

    @Override public void onLocalHistoryTokenChanged(String token) {
        if (token != null && !token.isEmpty()) { queryType = AbstractRequest.QueryType.valueOf(token.toUpperCase()); }
        cellList.setVisibleRangeAndClearData(cellList.getVisibleRange(), true);
        boardListTitle.setInnerText(titleLookup.get(token));
    }
}