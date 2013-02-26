/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client;

import cazcade.liquid.api.BoardURL;
import junit.framework.TestCase;

/**
 * @author neilellis@cazcade.com
 */
public class ShortURLTest extends TestCase {

    public void testBasic() {
        final BoardURL boardURL = new BoardURL("welcome");
        assertEquals("pool:///boards/public/welcome", boardURL.asURI().toString());
        assertEquals("welcome", boardURL.toString());
    }


    public void testSub() {
        final BoardURL boardURL = new BoardURL("welcome/stuff");
        assertEquals("pool:///boards/public/welcome/stuff", boardURL.asURI().toString());
        assertEquals("welcome/stuff", boardURL.toString());
    }

    public void testBoardcastOnly() {
        final BoardURL boardURL = new BoardURL("$welcome");
        assertEquals("pool:///boards/public/$welcome", boardURL.asURI().toString());
        assertEquals("$welcome", boardURL.toString());
    }

    public void testPublic() {
        final BoardURL boardURL = new BoardURL("welcome@neilellis");
        assertEquals("pool:///people/neilellis/public/welcome", boardURL.asURI().toString());
        assertEquals("welcome@neilellis", boardURL.toString());
    }

    public void testPersonal() {
        final BoardURL boardURL = new BoardURL("@neilellis");
        assertEquals("pool:///people/neilellis/profile", boardURL.asURI().toString());
        assertEquals("@neilellis", boardURL.toString());
    }

    public void testPersonalSub() {
        final BoardURL boardURL = new BoardURL("public/stuff@neilellis");
        assertEquals("pool:///people/neilellis/public/public/stuff", boardURL.asURI().toString());
        assertEquals("public/stuff@neilellis", boardURL.toString());
    }


    public void testPersonalLogicalSub() {
        final BoardURL boardURL = new BoardURL("public.stuff@neilellis");
        assertEquals("pool:///people/neilellis/public/public.stuff", boardURL.asURI().toString());
        assertEquals("public.stuff@neilellis", boardURL.toString());
    }

}
