package com.neaterbits.displayserver.xwindows.fonts.pcf;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.xwindows.fonts.FontLoader;
import com.neaterbits.displayserver.xwindows.fonts.FontLoaderConfig;
import com.neaterbits.displayserver.xwindows.fonts.NoSuchFontException;
import com.neaterbits.displayserver.xwindows.fonts.model.FontBitmapFormat;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBuffer;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;

public class FontPrintTest {

    @Test
    public void testPrintFont() throws IOException, NoSuchFontException, MatchException {
        
        final FontLoader fontLoader = new FontLoader(
                new FontLoaderConfig(Arrays.asList("/usr/share/fonts/X11/misc")));

        final XFont font = fontLoader.loadFont("7x14", new FontBufferFactory() {
            @Override
            public FontBuffer createFontBuffer(int glyphIndex, byte[] bitmap, FontBitmapFormat fontBitmapFormat, int width, int height) {
                return null;
            }
        });
        
        
        font.print(System.out);
        
    }
    
}
