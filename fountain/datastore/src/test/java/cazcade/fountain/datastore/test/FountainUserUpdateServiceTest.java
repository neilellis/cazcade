package cazcade.fountain.datastore.test;

import cazcade.fountain.datastore.impl.services.persistence.FountainUserUpdateService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * The first in hopefully many unit tests agains the fountain server.
 *
 * @author neilellis@cazcade.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from "/applicationContext.xml" and "/applicationContext-test.xml"
// in the root of the classpath
@ContextConfiguration({"classpath:datastore-spring-config.xml"})
public class FountainUserUpdateServiceTest {
    @Autowired
    private FountainUserUpdateService userUpdateService;

    @Before
    public void setUp() throws Exception {
        userUpdateService.setTest(true);
    }

    @Test
    public void testUserUpdateService() throws InterruptedException {
        userUpdateService.trivialUpdateLoop();
    }
}
