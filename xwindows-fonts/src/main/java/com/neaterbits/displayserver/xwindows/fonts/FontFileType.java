package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;

enum FontFileType {

    PCF("pcf"),
    PCF_COMPRESSED("pcf.gz");
    
    private final String extension;

    private FontFileType(String extension) {
        this.extension = "." + extension;
    }

    String getExtension() {
        return extension;
    }

    static FontFileType getFontFileType(File file) {
    
        for (FontFileType type : values()) {
            if (file.getName().endsWith(type.extension)) {
                return type;
            }
        }
        
        return null;
    }
}
