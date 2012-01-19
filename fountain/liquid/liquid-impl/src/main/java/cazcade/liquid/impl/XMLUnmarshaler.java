package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntityFactory;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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


    public void unmarshal(final LSDBaseEntity lsdEntity, final InputStream input) {
        xmlInputFactory = XMLInputFactory.newInstance();
        LiquidXStreamFactory.getXstream().fromXML(input, lsdEntity);
    }

    @Nonnull
    public LSDTransferEntity unmarshal(final InputStream input) {
        xmlInputFactory = XMLInputFactory.newInstance();
        return (LSDTransferEntity) LiquidXStreamFactory.getXstream().fromXML(input);
    }

    public LSDSimpleEntityFactory getLsdFactory() {
        return lsdFactory;
    }

    public void setLsdFactory(final LSDSimpleEntityFactory lsdFactory) {
        this.lsdFactory = lsdFactory;
    }
}