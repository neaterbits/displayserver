package com.neaterbits.displayserver.xwindows.fonts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FontAliasReaderTest extends BaseFontTest {

    @Test
    public void testFontAliases() throws FileNotFoundException, IOException, XFLDException {
        
        FontAliases fontAliases;
        
        try (FileInputStream inputStream = new FileInputStream(getFontAliasesFile())) {
            fontAliases = FontAliasFileReader.read(inputStream);
        }
     
        assertThat(fontAliases).isNotNull();
        
        XLFD xlfd = XLFD.decode("-misc-fixed-medium-r-normal--15-140-75-75-c-90-iso8859-1", true);
        
        List<FontDescriptor> fontNames = new ArrayList<>();
        
        fontAliases.getFontNamesForXLFD(xlfd, fontNames);
        
        assertThat(fontNames.size()).isEqualTo(1);
        assertThat(fontNames.get(0).getFontName()).isEqualTo("9x15");

        fontNames.clear();
        
        assertThat(fontNames.isEmpty()).isTrue();
        
        xlfd = XLFD.decode("-misc-fixed-medium-r-normal--15-120-100-100-c-90-iso8859-1", true);
        fontAliases.getFontNamesForXLFD(xlfd, fontNames);
        
        assertThat(fontNames.size()).isEqualTo(1);
        assertThat(fontNames.get(0).getFontName()).isEqualTo("9x15");
        
    }
}
