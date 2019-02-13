package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class FontSource {

    abstract <T> void iterateFonts(FontDescriptorIterator iterator);

    abstract <T> void iterateFontsByName(Pattern pattern, FontDescriptorIterator iterator);

    abstract <T> void iterateFontsByXLFD(XLFD xlfd, FontDescriptorIterator iterator);
    
    abstract File findFontFile(FontDescriptor fontDescriptor);

    final boolean matchesFontName(Pattern pattern, String name) {

        final String lowercaseName = name.toLowerCase();

        final Matcher matcher = pattern.matcher(lowercaseName);

        return matcher.matches();
    }
}
