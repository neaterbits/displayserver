package com.neaterbits.displayserver.server.render.cairo;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.xwindows.fonts.FontLoader;
import com.neaterbits.displayserver.xwindows.fonts.NoSuchFontException;
import com.neaterbits.displayserver.xwindows.fonts.model.StoreOrder;
import com.neaterbits.displayserver.xwindows.util.JNIBindings;

public class CairoFontBufferTest {

    private int atomSequence = 1;
    
    @Before
    public void loadJNI() {
        JNIBindings.load();
    }
    
    @Test
    public void testPrintCairoFont() throws IOException, NoSuchFontException, MatchException {
        
        
        final FontLoader fontLoader = new FontLoader(
                Arrays.asList("/usr/share/fonts/X11/misc"),
                name -> new ATOM(atomSequence ++));

        fontLoader.loadFont("7x14", new CairoFontBufferFactory(StoreOrder.getNativeOrder()));
    }
}
