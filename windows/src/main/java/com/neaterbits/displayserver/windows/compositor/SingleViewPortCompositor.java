package com.neaterbits.displayserver.windows.compositor;

import java.util.List;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.windows.ViewPort;
import com.neaterbits.displayserver.windows.Window;

public final class SingleViewPortCompositor implements Compositor {

    @Override
    public BufferOperations getBufferForWindow(Window window) {

        final List<ViewPort> viewPorts = window.getDisplayArea().getViewPorts();
        
        if (viewPorts.size() != 1) {
            throw new IllegalStateException();
        }
        
        return viewPorts.get(0).getFrameBuffer();
    }
}
