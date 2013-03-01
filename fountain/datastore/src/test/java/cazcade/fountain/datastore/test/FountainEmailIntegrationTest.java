/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.test;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainUserDAO;
import cazcade.fountain.datastore.impl.services.persistence.FountainEmailService;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.lsd.TransferEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
    public void test() throws Exception {
        final TransferEntity aliasFromNode = userDAO.getAliasFromNode(fountainNeo.findByURI(new LURI("alias:cazcade:admin"), true), true, RequestDetailLevel.COMPLETE);
        final TransferEntity userFromNode = fountainNeo.findByURI(new LURI("user:admin"), true)
                                                       .toTransfer(RequestDetailLevel.COMPLETE, true);
        mailService.send(userFromNode, aliasFromNode, "test-email.html", "Welcome", "", false);
    }
}
