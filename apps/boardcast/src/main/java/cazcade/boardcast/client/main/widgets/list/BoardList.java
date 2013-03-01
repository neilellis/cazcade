/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.list;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.liquid.api.request.BoardQueryRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.common.client.User;
import cazcade.vortex.gwt.util.client.StartupUtil;
import cazcade.vortex.gwt.util.client.history.HistoryAwareComposite;
import cazcade.vortex.widgets.client.panels.scroll.InfiniteScrollPagerPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class BoardList extends HistoryAwareComposite {
    interface BoardListUiBinder extends UiBinder<HTMLPanel, BoardList> {}

    public static final int               PAGE_SIZE   = 20;
    private static      BoardListUiBinder ourUiBinder = GWT.create(BoardListUiBinder.class);
    private final CellList<Entity> cellList;
    private final Map<String, String> titleLookup = new HashMap<String, String>();
    private final     AsyncDataProvider<Entity>    dataProvider;
    @UiField          InfiniteScrollPagerPanel     pagerPanel;
    @UiField          HeadingElement               boardListTitle;
    private           AbstractRequest.QueryType    queryType;
    @Nullable private NoSelectionModel<Entity>     selectionModel;
    private           HandlerRegistration          selectionChangeHandler;
    private           SelectionChangeEvent.Handler handler;

    public BoardList() {
        initWidget(ourUiBinder.createAndBindUi(this));
        queryType = BoardQueryRequest.QueryType.HISTORY;

        // Create a CellList.

        // Set a key provider that provides a unique key for each contact. If key is
        // used to identify contacts when fields (such as the name and address)
        // change.
        cellList = new CellList<Entity>(new BoardCell(), new LSDBaseEntityProvidesKey());
        cellList.setPageSize(PAGE_SIZE);
        cellList.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE);
        cellList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.BOUND_TO_SELECTION);
        cellList.setStyleName("board-list");
        // Add a selection model so we can select cells.

        dataProvider = new AsyncDataProvider<Entity>() {

            @Override protected void onRangeChanged(final HasData<Entity> display) {
                //                Window.alert(queryType.toString());
                Bus.get()
                          .send(new BoardQueryRequest(queryType, User.currentAlias().uri(), display.getVisibleRange()
                                                                                                       .getStart(), display.getVisibleRange()
                                                                                                                           .getLength()), new AbstractMessageCallback<BoardQueryRequest>() {
                              @Override public void onSuccess(BoardQueryRequest original, BoardQueryRequest message) {
                                  super.onSuccess(original, message);
                                  List<Entity> subEntities = message.response().children(Dictionary.CHILD_A);
                                  updateRowData(display.getVisibleRange().getStart(), subEntities);
                                  if (subEntities.size() < original.getMax()) {
                                      cellList.setRowCount(original.getStart() + subEntities.size());
                                  }
                              }
                          });

            }
        };
        StartupUtil.showLiveVersion(getWidget().getElement());

        titleLookup.put("history", "Recently Visited");
        titleLookup.put("my", "Your Boards");
        titleLookup.put("popular", "Popular Boards");
        titleLookup.put("recent", "New Boards");
        titleLookup.put("profile", "User Boards");


        selectionModel = new NoSelectionModel<Entity>(new LSDBaseEntityProvidesKey());
        cellList.setSelectionModel(selectionModel);
        pagerPanel.setDisplay(cellList);


    }

    @Override public void onLocalHistoryTokenChanged(String token) {
        if (token != null && !token.isEmpty()) {
            queryType = AbstractRequest.QueryType.valueOf(token.toUpperCase());
            dataProvider.addDataDisplay(cellList);
            cellList.setVisibleRangeAndClearData(new Range(0, PAGE_SIZE), true);
            boardListTitle.setInnerText(titleLookup.get(token));
        }

    }

    @Override public void beforeInactive() {
        cellList.setVisibleRangeAndClearData(new Range(0, 0), true);
        dataProvider.removeDataDisplay(cellList);
    }

    private static class LSDBaseEntityProvidesKey implements ProvidesKey<Entity> {
        @Nullable @Override public Object getKey(final Entity item) {
            return item == null ? null : item.uri();
        }
    }
}