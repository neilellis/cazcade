/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidUUID;
import cazcade.vortex.gwt.util.client.Config;
import com.google.gwt.storage.client.Storage;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @author neilellis@cazcade.com
 */
public class PersistentUUIDService {

    private String uuids;
    private int    size;
    @Nonnull
    public static final String LOCAL_STORAGE_KEY = "cazcade.vortex.uuids";

    public PersistentUUIDService() {
        if (Config.isLocalStorageSupported()) {
            final Storage storage = Storage.getLocalStorageIfSupported();
            uuids = storage.getItem(LOCAL_STORAGE_KEY);
            if (uuids == null) {
                uuids = "";
                size = 0;
            }
        } else {
            uuids = "";
            size = 0;
        }
        recount();
    }

    private void recount() {
        size = uuids.split(" ").length;
    }

    public int size() {
        return size;
    }

    public synchronized void topUp(@Nonnull final ArrayList<LiquidUUID> result) {
        final StringBuilder s = new StringBuilder(uuids);
        for (final LiquidUUID uuid : result) {
            s.append(uuid.toString()).append(" ");
        }
        uuids = s.toString();
        recount();
    }

    private void persistUUIDs() {
        if (Config.isLocalStorageSupported()) {
            Storage.getLocalStorageIfSupported().setItem(LOCAL_STORAGE_KEY, uuids);
        }

    }

    @Nonnull
    public LiquidUUID pop() {
        final int pos = uuids.indexOf(' ');
        if (pos == 0) {
            uuids = uuids.substring(1);
            return pop();
        }
        if (pos == -1) {
            throw new IllegalStateException("No UUIDs available.");
        }
        final LiquidUUID result = new LiquidUUID(uuids.substring(0, pos));
        uuids = uuids.substring(pos);
        recount();
        persistUUIDs();
        return result;
    }
}
