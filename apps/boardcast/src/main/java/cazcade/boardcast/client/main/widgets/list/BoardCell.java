/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.list;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class BoardCell extends AbstractCell<Entity> {

    private static String IMAGE_PREFIX = "http://cache.snapito.com/api?_cache_redirect=true&width=120&height=90&image&url=";

    public interface BoardCellTemplates extends SafeHtmlTemplates {
        @Template("<img class='board-list-item-icon' width='120' height='90' src='{0}'/>") SafeHtml icon(String icon);
    }

    private static final BoardCellTemplates TEMPLATES = GWT.create(BoardCellTemplates.class);

    public BoardCell() {
        super("click");
    }

    @Override public void render(final Context context, final Entity board, final SafeHtmlBuilder sb) {
        if (board == null) {
            return;
        }
        sb.appendHtmlConstant("<div class='board-list-item'>");
        sb.appendHtmlConstant("<div class='board-list-item-photo-area'>");
        if (board.has(Dictionary.ICON_URL)) {
            sb.append(TEMPLATES.icon(IMAGE_PREFIX + board.$(Dictionary.ICON_URL)));
        } else if (board.has(Dictionary.IMAGE_URL)) {
            sb.append(TEMPLATES.icon(IMAGE_PREFIX + board.$(Dictionary.IMAGE_URL)));
        } else {
            sb.append(TEMPLATES.icon("/_static/_images/blank.png"));
        }
        sb.appendHtmlConstant("</div>");
        sb.appendHtmlConstant("<div class='board-list-item-main-area'>");
        if (board.has(Dictionary.TITLE)) {
            sb.appendHtmlConstant("<h2 class='board-list-item-title'>")
              .appendEscaped(board.$(Dictionary.TITLE))
              .appendHtmlConstant("</h2>");
        }
        if (board.has(Dictionary.DESCRIPTION)) {
            sb.appendHtmlConstant("<h3 class='board-list-item-description'>")
              .appendEscaped(board.$(Dictionary.DESCRIPTION))
              .appendHtmlConstant("</h3>");
        }
        if (board.has(Dictionary.TEXT_EXTENDED)) {
            sb.appendHtmlConstant("<p class='board-list-item-text'>")
              .appendEscaped(board.$(Dictionary.TEXT_EXTENDED))
              .appendHtmlConstant("</p>");
        }
        sb.appendHtmlConstant("</div>");
        sb.appendHtmlConstant("<div class='board-list-item-metadata-area'>");
        Entity author = board.child(Dictionary.A_OWNER, false);
        sb.appendHtmlConstant("<div class='board-list-item-author'>")
          .appendEscaped(author.$(Dictionary.FULL_NAME))
          .appendHtmlConstant("</div>");
        if (board.has(Dictionary.COMMENT_COUNT)) {
            final int commentCount = board.$i(Dictionary.COMMENT_COUNT);
            final String commentText;
            if (commentCount == 0) {
                commentText = "No comments";
            } else if (commentCount == 1) {
                commentText = "1 comment";
            } else {
                commentText = commentCount + " comments";
            }
            sb.appendHtmlConstant("<div class='board-list-item-comment-count'>")
              .appendEscaped(commentText)
              .appendHtmlConstant("</div>");
        }
        sb.appendHtmlConstant("</div>");
        sb.appendHtmlConstant("</div>");
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, Entity value, NativeEvent event, ValueUpdater<Entity> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if ("click".equals(event.getType())) {
            HistoryManager.get().navigate(value.uri().board().safe());
        }
    }
}
