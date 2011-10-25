package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDType;
import cazcade.liquid.api.lsd.LSDTypeDefImpl;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDTypeDefTest extends TestCase {

    public void test() {
        LSDTypeDefImpl typeDef = new LSDTypeDefImpl("Image.Bitmap.2DBitmap.JPEG(Image.FairyDust.MagicPicture,MovingPicture.Video.2DVideo.HTML5Compatible.H264)");
        LSDType primaryType = typeDef.getPrimaryType();
        assertEquals("Image.Bitmap.2DBitmap.JPEG", primaryType.asString());
        assertEquals("Image", primaryType.getGenus());
        assertEquals("Bitmap", primaryType.getFamily());
        assertEquals("2DBitmap", primaryType.getTypeClass());
        assertEquals("JPEG", primaryType.getFlavors().get(0));
        assertEquals(1, primaryType.getFlavors().size());

        LSDType primaryParentType = primaryType.getParentType();
        assertEquals("Image.Bitmap.2DBitmap", primaryParentType.asString());

        List<LSDType> secondaryTypes = typeDef.getSecondaryTypes();
        LSDType secondSecondaryType = secondaryTypes.get(1);
        assertEquals("MovingPicture.Video.2DVideo.HTML5Compatible.H264", secondSecondaryType.asString());
        assertEquals(2, secondSecondaryType.getFlavors().size());
        assertEquals("MovingPicture.Video.2DVideo", secondSecondaryType.getParentType().getParentType().asString());


    }
}
