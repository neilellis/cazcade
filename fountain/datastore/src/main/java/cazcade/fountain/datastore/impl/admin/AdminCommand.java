package cazcade.fountain.datastore.impl.admin;

import cazcade.fountain.datastore.impl.FountainNeo;

/**
 * @author neilellis@cazcade.com
 */
public interface AdminCommand {
    void execute(String[] args, FountainNeo fountainNeo) throws InterruptedException;
}
