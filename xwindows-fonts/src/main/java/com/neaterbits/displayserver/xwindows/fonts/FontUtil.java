package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.neaterbits.displayserver.xwindows.fonts.model.XFontProperty;
import com.neaterbits.displayserver.xwindows.fonts.pcf.PCFReader;

class FontUtil {

    static List<XFontProperty> readProperties(File file) throws IOException {
        
        final List<XFontProperty> properties;
        
        final FontFileType type = FontFileType.getFontFileType(file);
        
        if (type == null) {
            properties = null;
        }
        else {
        
            switch (type) {
            case PCF:
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    properties = PCFReader.readProperties(inputStream);
                }
                break;
                
            case PCF_COMPRESSED:
                try (GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file))) {
                    properties = PCFReader.readProperties(inputStream);
                }
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown font file type " + type);
            }
        }
        
        return properties;
    }
}
