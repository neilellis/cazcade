package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidUUID;
import com.google.gwt.storage.client.Storage;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @author neilellis@cazcade.com
 */
public class PersistentUUIDService {

    private String uuids;
    private int size = 0;
    @Nonnull
    public static final String LOCAL_STORAGE_KEY = "cazcade.vortex.uuids";

    public PersistentUUIDService() {
        final Storage storage = Storage.getLocalStorageIfSupported();
        uuids = storage.getItem(LOCAL_STORAGE_KEY);
        if (uuids == null) {
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

    public void topUp(@Nonnull ArrayList<LiquidUUID> result) {
        StringBuilder s = new StringBuilder(uuids);
        for (LiquidUUID uuid : result) {
            s.append(uuid.toString()).append(" ");
        }
        uuids = s.toString();
        recount();
    }

    private void persistUUIDs() {

        Storage.getLocalStorageIfSupported().setItem(LOCAL_STORAGE_KEY, uuids);

    }

    @Nonnull
    public LiquidUUID pop() {
        int pos = uuids.indexOf(' ');
        if (pos == 0) {
            uuids = uuids.substring(1);
            return pop();
        }
        LiquidUUID result = new LiquidUUID(uuids.substring(0, pos));
        uuids = uuids.substring(pos);
        recount();
        persistUUIDs();
        return result;
    }
}
