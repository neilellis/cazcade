/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.test;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainUserDAO;
import cazcade.fountain.datastore.impl.services.persistence.FountainEmailService;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

/**
 * The first in hopefully many unit tests agains the fountain server.
 *
 * @author neilellis@cazcade.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from "/applicationContext.xml" and "/applicationContext-test.xml"
// in the root of the classpath
@ContextConfiguration({"classpath:datastore-spring-config.xml"})
public class FountainEmailIntegrationTest {
    @Autowired
    private FountainEmailService mailService;

    @Autowired
    private FountainUserDAO userDAO;

    @Autowired
    private FountainNeo fountainNeo;

    @Before
    public void setUp() throws Exception {

    }

    @Test @Transactional
    public void test() throws InterruptedException, UnsupportedEncodingException {
        final LSDTransferEntity aliasFromNode = userDAO.getAliasFromNode(fountainNeo.findByURI(new LiquidURI("alias:cazcade:admin"), true), true, LiquidRequestDetailLevel.COMPLETE);
        final LSDTransferEntity userFromNode = fountainNeo.findByURI(new LiquidURI("user:admin"), true)
                                                          .toLSD(LiquidRequestDetailLevel.COMPLETE, true);
        mailService.send(userFromNode, aliasFromNode, "test-email.html", "Welcome", "", false);
    }
}
