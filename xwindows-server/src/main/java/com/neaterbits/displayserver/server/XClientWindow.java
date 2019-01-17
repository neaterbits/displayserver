package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.compositor.Surface;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

public final class XClientWindow extends XWindow {

    private final XClientOps createdBy;

    public XClientWindow(
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            VISUALID visual,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes,
            XLibRenderer renderer,
            Surface surface) {
        
        this(null, window, windowResource, rootWindow, parentWindow, visual, borderWidth, windowClass, currentWindowAttributes, renderer, surface, 0);
        
    }

    public XClientWindow(
            XClientOps createdBy,
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            VISUALID visual,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes,
            XLibRenderer renderer,
            Surface surface) {
        
        this(createdBy, window, windowResource, rootWindow, parentWindow, visual, borderWidth, windowClass, currentWindowAttributes, renderer, surface, 0);
        
        Objects.requireNonNull(createdBy);
        
    }

    
    private XClientWindow(
            XClientOps createdBy,
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            VISUALID visual,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes,
            XLibRenderer renderer,
            Surface surface,
            int disambiguate) {

        super(window, windowResource, rootWindow, parentWindow, visual, borderWidth, windowClass, currentWindowAttributes, renderer, surface);
        
        this.createdBy = createdBy;
        
    }

    
    public boolean isCreatedBy(XClientOps client) {

        Objects.requireNonNull(client);

        return createdBy == client;
    }
}
