package org.newdawn.slick.tiled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class TiledMap$1
implements EntityResolver {
    TiledMap$1() {
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return new InputSource(new ByteArrayInputStream(new byte[0]));
    }
}
