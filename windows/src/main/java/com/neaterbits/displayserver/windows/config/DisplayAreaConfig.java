package com.neaterbits.displayserver.windows.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DisplayAreaConfig {

    private final int columnCount;
    private final List<DisplayConfig> displays;
    
    public DisplayAreaConfig(int columnCount, List<DisplayConfig> displays) {
        
        this.columnCount = columnCount;
        
        this.displays = new ArrayList<>(displays);
    }

    public int getColumnCount() {
        return columnCount;
    }

    public List<DisplayConfig> getDisplays() {
        return Collections.unmodifiableList(displays);
    }
}

