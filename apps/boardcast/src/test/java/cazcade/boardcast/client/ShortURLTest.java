/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client;

import cazcade.liquid.api.LiquidBoardURL;
import junit.framework.TestCase;

/**
 * @author neilellis@cazcade.com
 */
public class ShortURLTest extends TestCase {

    public void testBasic() {
        final LiquidBoardURL boardURL = new LiquidBoardURL("welcome");
        assertEquals("pool:///boards/public/welcome", boardURL.asURI().toString());
        assertEquals("welcome", boardURL.toString());
    }


    public void testSub() {
        final LiquidBoardURL boardURL = new LiquidBoardURL("welcome/stuff");
        assertEquals("pool:///boards/public/welcome/stuff", boardURL.asURI().toString());
        assertEquals("welcome/stuff", boardURL.toString());
    }

    public void testBoardcastOnly() {
        final LiquidBoardURL boardURL = new LiquidBoardURL("$welcome");
        assertEquals("pool:///boards/public/$welcome", boardURL.asURI().toString());
        assertEquals("$welcome", boardURL.toString());
    }

    public void testPublic() {
        final LiquidBoardURL boardURL = new LiquidBoardURL("welcome@neilellis");
        assertEquals("pool:///people/neilellis/public/welcome", boardURL.asURI().toString());
        assertEquals("welcome@neilellis", boardURL.toString());
    }

    public void testPersonal() {
        final LiquidBoardURL boardURL = new LiquidBoardURL("@neilellis");
        assertEquals("pool:///people/neilellis/profile", boardURL.asURI().toString());
        assertEquals("@neilellis", boardURL.toString());
    }

    public void testPersonalSub() {
        final LiquidBoardURL boardURL = new LiquidBoardURL("public/stuff@neilellis");
        assertEquals("pool:///people/neilellis/public/public/stuff", boardURL.asURI().toString());
        assertEquals("public/stuff@neilellis", boardURL.toString());
    }


    public void testPersonalLogicalSub() {
        final LiquidBoardURL boardURL = new LiquidBoardURL("public.stuff@neilellis");
        assertEquals("pool:///people/neilellis/public/public.stuff", boardURL.asURI().toString());
        assertEquals("public.stuff@neilellis", boardURL.toString());
    }

}
