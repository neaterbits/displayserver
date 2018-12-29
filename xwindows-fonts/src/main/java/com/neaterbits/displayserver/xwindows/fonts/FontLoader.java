package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.fonts.pcf.PCFReader;

public final class FontLoader {

    private final Function<String, ATOM> getAtom;
    private final List<File> fontPaths;
    
    public FontLoader(List<String> fontPaths, Function<String, ATOM> getAtom) {
        
        this.getAtom = getAtom;
        
        this.fontPaths = fontPaths.stream()
                .map(string -> new File(string))
                .collect(Collectors.toList());
    }
    
    public XFont loadFont(String fontName) throws IOException, NoSuchFontException {

        final File file = findFontFile(fontName);
        
        final XFont font;
        
        if (file != null) {
            
            try (GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file))) {
            
                font = PCFReader.read(fontName, inputStream, getAtom);
            }
        }
        else {
            throw new NoSuchFontException("Unknown font " + fontName);
        }
        
        return font;
    }
    
    private File findFontFile(String fontName) {
        
        File found = null;
        
        for (File directory : fontPaths) {
            final File file = new File(directory, fontName + ".pcf.gz");
            
            if (file.exists() && file.canRead()) {
                found = file;
                break;
            }
        }
        
        return found;
    }
}
