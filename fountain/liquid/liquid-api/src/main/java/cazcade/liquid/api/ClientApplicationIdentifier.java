/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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

    @Nonnull
    public static ClientApplicationIdentifier valueOf(@Nonnull final String s) {
        final String[] strings = s.split(":");
        return new ClientApplicationIdentifier(strings[0], strings[1], strings[2]);
    }

    public ClientApplicationIdentifier(final String name, final String key, final String hostinfo) {
        this.name = name;
        this.key = key;
        this.hostinfo = hostinfo;
    }

    public ClientApplicationIdentifier() {
    }

    @Nonnull @Override
    public String toString() {
        return name + ":" + hostinfo + ":" + key;
    }

    public String getHostinfo() {
        return hostinfo;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
