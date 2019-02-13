package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import com.neaterbits.displayserver.xwindows.fonts.model.XFontProperty;

public class XLFDTest {

    @Test
    public void testFromFontProperties() throws IOException, XFLDException {
        
        final File path = new File("/usr/share/fonts/X11/misc");
        
        final XLFD matches = XLFD.decode("-*-*-*-R-*-*-*-120-*-*-*-*-ISO8859-*", true);

        for (File file : path.listFiles((File file, String name) -> name.endsWith(".pcf.gz"))) {

            final List<XFontProperty> fontProperties = FontUtil.readProperties(file);
            
            final XLFD xlfd = XLFD.fromFontProperties(fontProperties);
            
            if (matches.matchesFont(xlfd)) {
                System.out.println("XLFD: " + xlfd.asString());
            }
            
            assertThat(xlfd).isNotNull();
        }
    }
}
