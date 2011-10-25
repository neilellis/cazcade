package cazcade.liquid.api;

import java.io.Serializable;

/**
 * @author neilelliz@cazcade.com
 */
public class ClientApplicationIdentifier implements Serializable {

    private String name;
    private String key;
    private String hostinfo;

    public ClientApplicationIdentifier() {
    }

    public ClientApplicationIdentifier(String name, String key, String hostinfo) {
        this.name = name;
        this.key = key;
        this.hostinfo = hostinfo;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getHostinfo() {
        return hostinfo;
    }
}
