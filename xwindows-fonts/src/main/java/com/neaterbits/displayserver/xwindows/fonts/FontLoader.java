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
import com.neaterbits.displayserver.xwindows.fonts.model.XFontCharacter;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontModel;
import com.neaterbits.displayserver.xwindows.fonts.pcf.PCFReader;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBuffer;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;

public final class FontLoader {

    private final Function<String, ATOM> getAtom;
    private final List<File> fontPaths;
    
    public FontLoader(List<String> fontPaths, Function<String, ATOM> getAtom) {
        
        this.getAtom = getAtom;
        
        this.fontPaths = fontPaths.stream()
                .map(string -> new File(string))
                .collect(Collectors.toList());
    }
    
    public XFont loadFont(String fontName, FontBufferFactory fontBufferFactory) throws IOException, NoSuchFontException {

        final File file = findFontFile(fontName);
        
        final XFontModel fontModel;
        
        if (file != null) {
            
            try (GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file))) {
            
                fontModel = PCFReader.read(inputStream, getAtom);
            }
        }
        else {
            throw new NoSuchFontException("Unknown font " + fontName);
        }
        
        return new XFont(fontName, fontModel, createRenderBitmaps(fontModel, fontBufferFactory));
    }
    
    private static FontBuffer [] createRenderBitmaps(XFontModel fontModel, FontBufferFactory fontBufferFactory) {
        
        final List<byte []> bitmaps = fontModel.getBitmaps().getBitmaps();
        
        final FontBuffer [] fontBuffers = new FontBuffer[bitmaps.size()];
        
        for (int i = 0; i < bitmaps.size(); ++ i) {
            
            final XFontCharacter metrics = fontModel.getMetrics().get(i);
            
            fontBuffers[i] = fontBufferFactory.createFontBuffer(
                    i,
                    bitmaps.get(i),
                    fontModel.getBitmaps().getBitmapFormat(),
                    metrics.getCharacterWidth(),
                    metrics.getAscent() + metrics.getDescent());
            
        }
        
        
        return fontBuffers;
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
