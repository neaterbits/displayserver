package com.neaterbits.displayserver.windows.compositor;


import com.neaterbits.displayserver.windows.DisplayArea;
import com.neaterbits.displayserver.windows.Window;

// Renders client windows directly to framebuffer
// for example for xwindows driver

public final class SingleViewPortDirectCompositor extends SingleViewPortCompositor {

    public SingleViewPortDirectCompositor(DisplayArea displayArea) {
        super(displayArea);
    }

    @Override
    public Surface allocateSurfaceForClientWindow(Window window) {
        return new SurfaceWrapper(
                getRootFramebuffer(),
                new CoordinateTranslator() {
                    
                    @Override
                    public int translateX(int x) {
                        return x + window.getPosition().getLeft();
                    }
                    
                    @Override
                    public int translateY(int y) {
                        return y + window.getPosition().getTop();
                    }

                    @Override
                    public double translateX(double x) {
                        return x + window.getPosition().getLeft();
                    }
                    
                    @Override
                    public double translateY(double y) {
                        return y + window.getPosition().getTop();
                    }
                },
                window.getSize(),
                window.getDepth());
    }

    @Override
    public void freeSurfaceForClientWindow(Window window, Surface surface) {
        
    }
}
