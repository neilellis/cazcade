/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.list;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
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
public class BoardCell extends AbstractCell<LSDBaseEntity> {

    private static String IMAGE_PREFIX = "http://cache.snapito.com/api?_cache_redirect=true&width=120&height=90&image&url=";

    public interface BoardCellTemplates extends SafeHtmlTemplates {
        @Template("<img class='board-list-item-icon' width='120' height='90' src='{0}'/>") SafeHtml icon(String icon);
    }

    private static final BoardCellTemplates TEMPLATES = GWT.create(BoardCellTemplates.class);

    public BoardCell() {
        super("click");
    }

    @Override public void render(final Context context, final LSDBaseEntity board, final SafeHtmlBuilder sb) {
        if (board == null) {
            return;
        }
        sb.appendHtmlConstant("<div class='board-list-item'>");
        sb.appendHtmlConstant("<div class='board-list-item-photo-area'>");
        if (board.hasAttribute(LSDAttribute.ICON_URL)) {
            sb.append(TEMPLATES.icon(IMAGE_PREFIX + board.getAttribute(LSDAttribute.ICON_URL)));
        } else if (board.hasAttribute(LSDAttribute.IMAGE_URL)) {
            sb.append(TEMPLATES.icon(IMAGE_PREFIX + board.getAttribute(LSDAttribute.IMAGE_URL)));
        } else {
            sb.append(TEMPLATES.icon("/_static/_images/blank.png"));
        }
        sb.appendHtmlConstant("</div>");
        sb.appendHtmlConstant("<div class='board-list-item-main-area'>");
        if (board.hasAttribute(LSDAttribute.TITLE)) {
            sb.appendHtmlConstant("<h2 class='board-list-item-title'>")
              .appendEscaped(board.getAttribute(LSDAttribute.TITLE))
              .appendHtmlConstant("</h2>");
        }
        if (board.hasAttribute(LSDAttribute.DESCRIPTION)) {
            sb.appendHtmlConstant("<h3 class='board-list-item-description'>")
              .appendEscaped(board.getAttribute(LSDAttribute.DESCRIPTION))
              .appendHtmlConstant("</h3>");
        }
        if (board.hasAttribute(LSDAttribute.TEXT_EXTENDED)) {
            sb.appendHtmlConstant("<p class='board-list-item-text'>")
              .appendEscaped(board.getAttribute(LSDAttribute.TEXT_EXTENDED))
              .appendHtmlConstant("</p>");
        }
        sb.appendHtmlConstant("</div>");
        sb.appendHtmlConstant("<div class='board-list-item-metadata-area'>");
        LSDBaseEntity author = board.getSubEntity(LSDAttribute.OWNER, false);
        sb.appendHtmlConstant("<div class='board-list-item-author'>")
          .appendEscaped(author.getAttribute(LSDAttribute.FULL_NAME))
          .appendHtmlConstant("</div>");
        if (board.hasAttribute(LSDAttribute.COMMENT_COUNT)) {
            final int commentCount = board.getIntegerAttribute(LSDAttribute.COMMENT_COUNT);
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
    public void onBrowserEvent(Context context, Element parent, LSDBaseEntity value, NativeEvent event, ValueUpdater<LSDBaseEntity> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if ("click".equals(event.getType())) {
            HistoryManager.get().navigate(value.getURI().asBoardURL().asUrlSafe());
        }
    }
}
