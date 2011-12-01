package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import com.google.gwt.user.client.Command;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractCreateCommand implements Command {
    protected LSDDictionaryTypes type;
    protected final LiquidURI pool;
    protected AbstractCreateCommand.Size size;
    protected String theme;

    public AbstractCreateCommand(final LiquidURI pool, final LSDDictionaryTypes type) {
        this.pool = pool;
        this.type = type;
    }


    protected AbstractCreateCommand(final LiquidURI pool, final LSDDictionaryTypes type, final Size size, final String theme) {
        this.type = type;
        this.pool = pool;
        this.size = size;
        this.theme = theme;
    }

    public LSDDictionaryTypes getType() {
        return type;
    }

    public void setType(final LSDDictionaryTypes type) {
        this.type = type;
    }


    protected interface BuildCallback {
        void onBuilt(LSDEntity entity);

    }

    public enum Size {
        THUMBNAIL, SMALL, MEDIUM, LARGE, DEFAULT
    }
}
