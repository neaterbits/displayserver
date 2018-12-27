package com.neaterbits.displayserver.xwindows.model;

import java.util.Set;
import java.util.function.Supplier;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

public class XDisplayState<W extends XWindow, WINDOWS extends XWindows<W>>
    implements
        XScreensConstAccess,
        XVisualsConstAccess,
        XWindowsConstAccess<W>
{

    private final XScreens screens;
    private final XVisuals visuals;
    private final WINDOWS windows;

    protected XDisplayState(XScreensAndVisuals screensAndVisuals, Supplier<WINDOWS> windowsCtor) {

        this.screens = new XScreens(screensAndVisuals.getScreens());
        this.visuals = new XVisuals(screensAndVisuals.getVisuals());
        
        this.windows = windowsCtor.get();
    }

    @Override
    public final int getNumberOfScreens() {
        return screens.getNumberOfScreens();
    }

    @Override
    public final XScreen getScreen(int screenNo) {
        return screens.getScreen(screenNo);
    }
    
    @Override
    public final XVisual getVisual(VISUALID visual) {
        return visuals.getVisual(visual);
    }

    @Override
    public final Set<PixelFormat> getDistinctPixelFormats() {
        return screens.getDistinctPixelFormats();
    }

    @Override
    public final XWindow getClientOrRootWindow(WINDOW windowResource) {
        return windows.getClientOrRootWindow(windowResource);
    }
    
    @Override
    public final XWindow getClientOrRootWindow(DRAWABLE windowResource) {
        return windows.getClientOrRootWindow(windowResource);
    }

    @Override
    public final W getClientWindow(DRAWABLE windowResource) {
        return windows.getClientWindow(windowResource);
    }

    @Override
    public final W getClientWindow(WINDOW windowResource) {
        return windows.getClientWindow(windowResource);
    }

    @Override
    public final XWindow findRootWindowOf(WINDOW windowResource) {
        return windows.findRootWindowOf(windowResource);
    }

    @Override
    public final W getClientWindow(Window window) {
        return windows.getClientWindow(window);
    }

    protected final WINDOWS getWindows() {
        return windows;
    }
}
