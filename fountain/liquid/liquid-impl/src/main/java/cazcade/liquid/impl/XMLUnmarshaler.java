package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntityFactory;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;

/**
 * @author Neil Ellis
 */

public class XMLUnmarshaler implements LSDUnmarshaler {
    private XMLInputFactory xmlInputFactory;
    private LSDSimpleEntityFactory lsdFactory;


    public void unmarshal(LSDEntity lsdEntity, InputStream input) {
        xmlInputFactory = XMLInputFactory.newInstance();
        LiquidXStreamFactory.getXstream().fromXML(input, lsdEntity);
    }

    @Nonnull
    public LSDEntity unmarshal(InputStream input) {
        xmlInputFactory = XMLInputFactory.newInstance();
        return (LSDEntity) LiquidXStreamFactory.getXstream().fromXML(input);
    }

    public void setLsdFactory(LSDSimpleEntityFactory lsdFactory) {
        this.lsdFactory = lsdFactory;
    }

    public LSDSimpleEntityFactory getLsdFactory() {
        return lsdFactory;
    }
}