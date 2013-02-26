/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import com.google.gwt.user.client.Command;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractCreateCommand implements Command {
    public enum Size {
        THUMBNAIL, SMALL, MEDIUM, LARGE, DEFAULT
    }

    protected       Types                      type;
    protected final LiquidURI                  pool;
    protected       AbstractCreateCommand.Size size;
    protected       String                     theme;


    protected AbstractCreateCommand(final LiquidURI pool, final Types type, final Size size, final String theme) {
        this.type = type;
        this.pool = pool;
        this.size = size;
        this.theme = theme;
    }

    public AbstractCreateCommand(final LiquidURI pool, final Types type) {
        this.pool = pool;
        this.type = type;
    }

    public Types getType() {
        return type;
    }

    public void setType(final Types type) {
        this.type = type;
    }

    protected interface BuildCallback {
        void onBuilt(TransferEntity entity);
    }
}
