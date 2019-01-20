package com.neaterbits.displayserver.windows.compositor;

import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.windows.DisplayArea;
import com.neaterbits.displayserver.windows.ViewPort;
import com.neaterbits.displayserver.windows.Window;

public abstract class SingleViewPortCompositor implements Compositor {

    private final DisplayArea displayArea;
    private final ViewPort viewPort;
    private final SurfaceWrapper rootSurface;
    
    SingleViewPortCompositor(DisplayArea displayArea) {
        
        Objects.requireNonNull(displayArea);
        
        this.displayArea = displayArea;
    
        this.viewPort = getSingleViewPort(displayArea);

        final CoordinateTranslator coordinateTranslator = new NoopCoordinateTranslator();
        
        this.rootSurface = new SurfaceWrapper(viewPort.getFrameBuffer(), coordinateTranslator);
    }

    final BufferOperations getRootFramebuffer() {
        return viewPort.getFrameBuffer();
    }

    private static ViewPort getSingleViewPort(DisplayArea displayArea) {
        final List<ViewPort> viewPorts = displayArea.getViewPorts();
        
        if (viewPorts.size() != 1) {
            throw new IllegalStateException();
        }
        
        final ViewPort viewPort = viewPorts.get(0);

        return viewPort;
    }
    
    @Override
    public final Surface getSurfaceForRootWindow(Window window) {

        if (!window.getDisplayArea().sameAs(displayArea)) {
            throw new IllegalStateException();
        }
        
        return rootSurface;
    }
}
