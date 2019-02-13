package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.neaterbits.displayserver.xwindows.fonts.model.XFontProperty;

final class CacheFontSource extends FontSource {

    private final FontSource delegate;

    private final List<FontDescriptor> cachedDescriptors;
    
    public CacheFontSource(FontSource delegate) {

        Objects.requireNonNull(delegate);
        
        this.delegate = delegate;
        this.cachedDescriptors = new ArrayList<>();
    
        reload();
    }

    private void reload() {
        
        cachedDescriptors.clear();
        
        delegate.iterateFonts(fontDescriptor -> {
            
            final FontDescriptor toAdd;
            
            if (fontDescriptor.getXlfd() != null) {
                toAdd = fontDescriptor;
            }
            else {
                final File fontFile = delegate.findFontFile(fontDescriptor);
                
                if (fontFile != null) {
                    
                    List<XFontProperty> properties = null;

                    try {
                        properties = FontUtil.readProperties(fontFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    
                    if (properties != null) {
                        final XLFD fontXlfd = XLFD.fromFontProperties(properties);

                        toAdd = new FontDescriptor(fontDescriptor.getFontName(), fontXlfd);
                    }
                    else {
                        toAdd = null;
                    }
                }
                else {
                    toAdd = null;
                }
            }
            
            if (toAdd != null) {
                cachedDescriptors.add(toAdd);
            }
        });
    }
    
    @Override
    <T> void iterateFonts(FontDescriptorIterator iterator) {

        cachedDescriptors.forEach(iterator::onFontDescriptor);

    }

    @Override
    <T> void iterateFontsByName(Pattern pattern, FontDescriptorIterator iterator) {
    
        cachedDescriptors.stream()
            .filter(fd -> matchesFontName(pattern, fd.getFontName()))
            .forEach(iterator::onFontDescriptor);
        
    }

    @Override
    <T> void iterateFontsByXLFD(XLFD xlfd, FontDescriptorIterator iterator) {

        cachedDescriptors.stream()
            .filter(fd -> xlfd.matchesFont(fd.getXlfd()))
            .forEach(iterator::onFontDescriptor);
        
    }

    @Override
    File findFontFile(FontDescriptor fontDescriptor) {
        return delegate.findFontFile(fontDescriptor);
    }
}
