package cazcade.liquid.api;

import javax.annotation.Nonnull;
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

    @Nonnull
    @Override
    public String toString() {
        return name + ":" + hostinfo + ":" + key;
    }

    @Nonnull
    public static ClientApplicationIdentifier valueOf(@Nonnull String s) {
        final String[] strings = s.split(":");
        return new ClientApplicationIdentifier(strings[0], strings[1], strings[2]);
    }
}
