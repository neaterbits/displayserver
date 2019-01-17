package com.neaterbits.displayserver.windows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;
import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;

public final class DisplayAreas {

    private final List<DisplayArea> displayAreas;

    public static DisplayAreas from(DisplayAreaConfig config, GraphicsDriver driver) {
        
        final DisplayArea displayArea = DisplayAreaFinder.makeDisplayArea(config, driver);
        
        if (displayArea == null) {
            throw new IllegalStateException();
        }
        
        final List<DisplayArea> displayAreas = Arrays.asList(
                displayArea
        );
        
        return new DisplayAreas(displayAreas);
    }

    public List<DisplayArea> getDisplayAreas() {
        return displayAreas;
    }

    public WindowsDisplayAreas toWindowsDisplayAreas(WindowEventListener windowEventListener) {
        
        Objects.requireNonNull(windowEventListener);
        
        return new WindowsDisplayAreas(this, windowEventListener);
    }
    
    private DisplayAreas(List<DisplayArea> displayAreas) {

        Objects.requireNonNull(displayAreas);
        
        this.displayAreas = Collections.unmodifiableList(displayAreas);
    }
}
