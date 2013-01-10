/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.admin;

import cazcade.fountain.datastore.impl.FountainNeo;

/**
 * @author neilellis@cazcade.com
 */
public interface AdminCommand {
    void execute(String[] args, FountainNeo fountainNeo) throws InterruptedException;
}
