package com.neaterbits.displayserver.windows;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class WindowsDisplayAreas {

    private final List<WindowsDisplayArea> displayAreas;
    
    public WindowsDisplayAreas(DisplayAreas displayAreas) {

        final List<WindowsDisplayArea> windowsDisplayAreas = displayAreas.getDisplayAreas().stream()
                .map(displayArea -> new WindowsDisplayAreaImpl(displayArea))
                .collect(Collectors.toList());
    
        this.displayAreas = Collections.unmodifiableList(windowsDisplayAreas);
    }

    public boolean contains(WindowsDisplayArea displayArea) {
        
        Objects.requireNonNull(displayArea);
        
        return displayAreas.contains(displayArea);
    }
    
    public List<WindowsDisplayArea> getDisplayAreas() {
        return displayAreas;
    }
}
