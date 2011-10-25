package cazcade.fountain.datastore.impl;

import cazcade.common.Logger;

/**
 * @author neilellis@cazcade.com
 */
class NeoBackupJob implements Runnable {
    private final static Logger log = Logger.getLogger(NeoBackupJob.class);
    private FountainNeo fountainNeo;

    public NeoBackupJob(FountainNeo fountainNeo) {
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
