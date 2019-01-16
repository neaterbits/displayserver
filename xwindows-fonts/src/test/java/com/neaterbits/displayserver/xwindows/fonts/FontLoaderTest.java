package com.neaterbits.displayserver.xwindows.fonts;

import org.junit.Test;

import com.neaterbits.displayserver.protocol.exception.ValueException;

import static org.assertj.core.api.Assertions.assertThat;

public class FontLoaderTest extends BaseFontTest {

    @Test
    public void testListFonts() throws ValueException {
        
        final String xlfd = "-misc-fixed-medium-r-normal--15-120-100-100-c-90-iso8859-1";

        final FontLoader fontLoader = getFontLoader(true);
        
        final String [] fonts = fontLoader.listFonts(xlfd);
        
        assertThat(fonts).isNotNull();
        assertThat(fonts.length).isEqualTo(1);
        assertThat(fonts[0]).isEqualTo("9x15");
    }
}
