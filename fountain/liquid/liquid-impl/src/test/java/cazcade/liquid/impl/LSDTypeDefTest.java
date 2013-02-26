/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.Type;
import cazcade.liquid.api.lsd.TypeDefImpl;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDTypeDefTest extends TestCase {
    public void test() {
        final TypeDefImpl typeDef = new TypeDefImpl("Image.Bitmap.2DBitmap.JPEG(Image.FairyDust.MagicPicture,MovingPicture.Video.2DVideo.HTML5Compatible.H264)");
        final Type primaryType = typeDef.getPrimaryType();
        assertEquals("Image.Bitmap.2DBitmap.JPEG", primaryType.asString());
        assertEquals("Image", primaryType.getGenus());
        assertEquals("Bitmap", primaryType.getFamily());
        assertEquals("2DBitmap", primaryType.getTypeClass());
        assertEquals("JPEG", primaryType.getFlavors().get(0));
        assertEquals(1, primaryType.getFlavors().size());

        final Type primaryParentType = primaryType.getParentType();
        assertEquals("Image.Bitmap.2DBitmap", primaryParentType.asString());

        final List<Type> secondaryTypes = typeDef.getSecondaryTypes();
        final Type secondSecondaryType = secondaryTypes.get(1);
        assertEquals("MovingPicture.Video.2DVideo.HTML5Compatible.H264", secondSecondaryType.asString());
        assertEquals(2, secondSecondaryType.getFlavors().size());
        assertEquals("MovingPicture.Video.2DVideo", secondSecondaryType.getParentType().getParentType().asString());
    }
}
