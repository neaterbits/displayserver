package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

public final class XClientWindow extends XWindow {

    private final XClient createdBy;

    XClientWindow(
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            VISUALID visual,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes,
            XLibRenderer renderer) {
        
        this(null, window, windowResource, rootWindow, parentWindow, visual, borderWidth, windowClass, currentWindowAttributes, renderer, 0);
        
    }

    XClientWindow(
            XClient createdBy,
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            VISUALID visual,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes,
            XLibRenderer renderer) {
        
        this(createdBy, window, windowResource, rootWindow, parentWindow, visual, borderWidth, windowClass, currentWindowAttributes, renderer, 0);
        
        Objects.requireNonNull(createdBy);
        
    }

    
    private XClientWindow(
            XClient createdBy,
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            VISUALID visual,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes,
            XLibRenderer renderer,
            int disambiguate) {

        super(window, windowResource, rootWindow, parentWindow, visual, borderWidth, windowClass, currentWindowAttributes, renderer);
        
        this.createdBy = createdBy;
        
    }

    
    boolean isCreatedBy(XClient client) {

        Objects.requireNonNull(client);

        return createdBy == client;
    }
}
