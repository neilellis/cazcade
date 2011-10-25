package cazcade.fountain.index.model;

import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public class BaseBase implements Serializable {
    protected String id;
    protected String uri;
    protected String lsdType;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseBase)) return false;

        BaseBase that = (BaseBase) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


}
