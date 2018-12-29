package com.neaterbits.displayserver.xwindows.fonts;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.xwindows.fonts.model.XFont;

public final class FontCache {

    private final Map<String, XFont> map; 
    
    public FontCache() {
        this.map = new HashMap<>();
    }
    
    public void add(String name, XFont font) {
        
        Objects.requireNonNull(name);
        Objects.requireNonNull(font);
        
        map.put(name, font);
    }
    
    public XFont getFont(String name) {
        
        Objects.requireNonNull(name);
        
        return map.get(name);
    }
}
