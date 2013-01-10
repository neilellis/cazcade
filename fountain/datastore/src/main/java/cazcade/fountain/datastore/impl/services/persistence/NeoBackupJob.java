/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainNeo;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
class NeoBackupJob implements Runnable {
    @Nonnull
    private static final Logger log = Logger.getLogger(NeoBackupJob.class);
    private final FountainNeo fountainNeo;

    public NeoBackupJob(final FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }

    public void run() {
        try {
            fountainNeo.pause();
        } catch (Exception e) {
            log.error(e);
            return;
        }
        try {
            fountainNeo.backup();
        } catch (Exception e) {
            log.error(e);
        } finally {
            try {
                fountainNeo.resume();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}
