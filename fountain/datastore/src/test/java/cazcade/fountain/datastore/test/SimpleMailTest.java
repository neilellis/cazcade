package cazcade.fountain.datastore.test;

import cazcade.fountain.datastore.impl.email.MailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

/**
 * The first in hopefully many unit tests agains the fountain server.
 *
 * @author neilellis@cazcade.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from "/applicationContext.xml" and "/applicationContext-test.xml"
// in the root of the classpath
@ContextConfiguration({"classpath:datastore-spring-config.xml"})
public class SimpleMailTest {
    @Autowired
    private MailService mailService;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() throws InterruptedException {
        mailService.sendMailFromTemplate("welcome.html", "test", new String[]{"neilellis@cazcade.com"}, new String[0],
                                         new String[0], new HashMap<String, Object>(), false
                                        );
    }
}
