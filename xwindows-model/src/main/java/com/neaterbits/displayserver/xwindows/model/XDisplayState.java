package com.neaterbits.displayserver.xwindows.model;

import java.util.Set;
import java.util.function.Supplier;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

public class XDisplayState<W extends XWindow, WINDOWS extends XWindows<W>>
    implements
        XScreensConstAccess,
        XVisualsConstAccess,
        XWindowsConstAccess<W>,
        XPixmapsConstAccess,
        XColormapsConstAccess,
        XCursorsConstAccess
{

    private final XScreens screens;
    private final XVisuals visuals;
    private final WINDOWS windows;
    private final XPixmaps pixmaps;
    private final XColormaps colormaps;
    private final XCursors cursors;

    protected XDisplayState(XScreensAndVisuals screensAndVisuals, Supplier<WINDOWS> windowsCtor) {

        this.screens = new XScreens(screensAndVisuals.getScreens());
        this.visuals = new XVisuals(screensAndVisuals.getVisuals());
        
        this.windows = windowsCtor.get();
        this.pixmaps = new XPixmaps();
        this.colormaps = new XColormaps();
        this.cursors = new XCursors();
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

    @Override
    public DRAWABLE getOwnerDrawable(PIXMAP pixmapDrawable) {
        return pixmaps.getOwnerDrawable(pixmapDrawable);
    }

    public final XScreens getScreens() {
        return screens;
    }

    public final XVisuals getVisuals() {
        return visuals;
    }

    public final WINDOWS getWindows() {
        return windows;
    }

    public final XPixmaps getPixmaps() {
        return pixmaps;
    }

    public final XColormaps getColormaps() {
        return colormaps;
    }

    public final XCursors getCursors() {
        return cursors;
    }

    public final void addPixmap(PIXMAP resource, DRAWABLE drawable, XPixmap pixmap) {
        pixmaps.addPixmap(resource, drawable, pixmap);
    }

    public final XPixmap removePixmap(PIXMAP resource) {
        return pixmaps.removePixmap(resource);
    }
    
    @Override
    public final boolean hasPixmap(PIXMAP pixmap) {
        return pixmaps.hasPixmap(pixmap);
    }

    @Override
    public final XPixmap getPixmap(PIXMAP pixmap) {
        return pixmaps.getPixmap(pixmap);
    }

    @Override
    public final boolean hasColormap(COLORMAP resource) {
        return colormaps.hasColormap(resource);
    }

    @Override
    public final XColormap getColormap(COLORMAP resource) {
        return colormaps.getColormap(resource);
    }

    @Override
    public final boolean hasCursor(CURSOR resource) {
        return cursors.hasCursor(resource);
    }

    @Override
    public final XCursor getCursor(CURSOR resource) {
        return cursors.getCursor(resource);
    }

    @Override
    public Integer getScreenForWindow(WINDOW window) {
        return windows.getScreenForWindow(window);
    }
}
