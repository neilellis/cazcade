package cazcade.fountain.index.model;

import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class PositionBase extends BaseBase{

    protected String positionId;
    protected String resourceUri;
    protected Date lastRead;
    protected Date lastWrote;


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
