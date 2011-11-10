package cazcade.common;

import junit.framework.TestCase;

/**
 * @author neilellis@cazcade.com
 */
public class LoggerTest extends TestCase {

    public void test() {
        RuntimeException e = new RuntimeException("Boo!");
        e.printStackTrace();
        Logger logger = Logger.getLogger("test");
        logger.addContext(this);
        logger.notifyOfError(e, "Testing Jira");
    }
}
