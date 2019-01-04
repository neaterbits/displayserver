package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.DisplayAreaWindows;
import com.neaterbits.displayserver.windows.Window;

public class XDisplayState<W extends XWindow, WINDOWS extends XWindows<W>>
    implements
        XScreensConstAccess,
        XVisualsConstAccess,
        XWindowsConstAccess<W>,
        XPixmapsConstAccess,
        XDrawablesConstAccess
{

    private final XScreens screens;
    private final XVisuals visuals;
    private final WINDOWS windows;
    private final XPixmaps pixmaps;

    protected XDisplayState(XScreensAndVisuals screensAndVisuals, Supplier<WINDOWS> windowsCtor) {

        this.screens = new XScreens(screensAndVisuals.getScreens());
        this.visuals = new XVisuals(screensAndVisuals.getVisuals());
        
        this.windows = windowsCtor.get();
        this.pixmaps = new XPixmaps();
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

    public final void addPixmap(PIXMAP resource, DRAWABLE drawable, XPixmap pixmap) {
        pixmaps.addPixmap(resource, drawable, pixmap);
    }

    public final XPixmap removePixmap(PIXMAP resource) {
        return pixmaps.removePixmap(resource);
    }
    
    @Override
    public XPixmap getPixmap(PIXMAP pixmap) {
        return pixmaps.getPixmap(pixmap);
    }

    @Override
    public XDrawable findDrawable(DRAWABLE drawable) {
        
        XWindow window = getClientOrRootWindow(drawable.toWindow());

        final XDrawable xDrawable;
        
        if (window != null) {
            xDrawable = window;
        }
        else {
            xDrawable = pixmaps.getPixmap(drawable.toPixmap());
        }
        
        return xDrawable;
    }
    
    public final DisplayAreaWindows findDisplayArea(DRAWABLE drawable) {
        
        Objects.requireNonNull(drawable);
        
        XWindow window = getClientOrRootWindow(drawable.toWindow());
        
        DisplayAreaWindows displayArea = null;
        
        if (window != null) {
            displayArea = window.getWindow().getDisplayArea();
        }
        else {
            final DRAWABLE pixmapOwnerDrawable = pixmaps.getOwnerDrawable(drawable.toPixmap());
            
            if (pixmapOwnerDrawable == null) {
                throw new IllegalStateException();
            }
            
            displayArea = findDisplayArea(pixmapOwnerDrawable);
        }
        
        return displayArea;
    }
}
