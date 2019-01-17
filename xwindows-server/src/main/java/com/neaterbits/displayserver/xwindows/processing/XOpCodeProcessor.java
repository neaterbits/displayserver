package com.neaterbits.displayserver.xwindows.processing;

import java.util.Objects;
import java.util.function.Function;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.WindowException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.server.XEventSubscriptionsConstAccess;
import com.neaterbits.displayserver.windows.WindowsDisplayArea;
import com.neaterbits.displayserver.xwindows.model.XDrawable;
import com.neaterbits.displayserver.xwindows.model.XPixmapsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;

public abstract class XOpCodeProcessor extends XMessageProcessor {

    protected abstract int [] getOpCodes();

    public XOpCodeProcessor(XWindowsServerProtocolLog protocolLog) {
        super(protocolLog);
    }

    protected final VISUALID getVisual(XWindowsConstAccess<?> xWindows, XPixmapsConstAccess xPixmaps, DRAWABLE drawable) throws DrawableException {
        
        final XDrawable xDrawable = findDrawable(xWindows, xPixmaps, drawable);
        
        return xDrawable.getVisual();
    }

    protected final XWindow findClientOrRootWindow(XWindowsConstAccess<?> xWindows, WINDOW window) throws WindowException {
        
        final XWindow xWindow = xWindows.getClientOrRootWindow(window);
        
        if (xWindow == null) {
            throw new WindowException("No such window", window);
        }
        
        return xWindow;
    }

    protected final XWindow findClientWindow(XWindowsConstAccess<?> xWindows, WINDOW window) throws WindowException {
        
        final XWindow xWindow = xWindows.getClientWindow(window);
        
        if (xWindow == null) {
            throw new WindowException("No such window", window);
        }
        
        return xWindow;
    }

    private XDrawable findXDrawable(XWindowsConstAccess<?> xWindows, XPixmapsConstAccess xPixmaps, DRAWABLE drawable) {
        
        XWindow window = xWindows.getClientOrRootWindow(drawable.toWindow());

        final XDrawable xDrawable;
        
        if (window != null) {
            xDrawable = window;
        }
        else {
            xDrawable = xPixmaps.getPixmap(drawable.toPixmap());
        }
        
        return xDrawable;
    }

    
    protected final XDrawable findDrawable(XWindowsConstAccess<?> xWindows, XPixmapsConstAccess xPixmaps, DRAWABLE drawable) throws DrawableException {
        
        final XDrawable xDrawable = findXDrawable(xWindows, xPixmaps, drawable);

        if (drawable == null) {
            throw new DrawableException("No such drawable", drawable);
        }
        
        return xDrawable;
    }

    protected final XWindow findPixmapWindow(
            XWindowsConstAccess<?> xWindows,
            XPixmapsConstAccess xPixmaps,
            PIXMAP pixmap) {

        final DRAWABLE pixmapOwnerDrawable = xPixmaps.getOwnerDrawable(pixmap);
        
        if (pixmapOwnerDrawable == null) {
            throw new IllegalStateException();
        }

        XWindow xWindow = xWindows.getClientOrRootWindow(pixmapOwnerDrawable.toWindow());
        
        if (xWindow == null) {
            // See if is pixmap instead
            xWindow = findPixmapWindow(xWindows, xPixmaps, pixmapOwnerDrawable.toPixmap());
        }
        
        return xWindow;
    }

    protected final WindowsDisplayArea findDisplayArea(
            XWindowsConstAccess<?> xWindows,
            XPixmapsConstAccess xPixmaps,
            DRAWABLE drawable) {
        
        Objects.requireNonNull(drawable);
        
        XWindow window = xWindows.getClientOrRootWindow(drawable.toWindow());
        
        if (window == null) {
            window = findPixmapWindow(xWindows, xPixmaps, drawable.toPixmap());
        }

        return window.getWindow().getDisplayArea();
    }

    protected final BufferOperations getBufferOperations(
            XWindowsConstAccess<?> xWindows,
            XPixmapsConstAccess xPixmaps,
            DRAWABLE drawable) throws DrawableException {
        
        Objects.requireNonNull(drawable);
        
        final XDrawable xDrawable = findDrawable(xWindows, xPixmaps, drawable);
        
        return xDrawable.getSurface();
    }

    protected final void sendEventToSubscribing(
            XEventSubscriptionsConstAccess xEventSubscriptions,
            XWindow xWindow,
            int eventCode,
            Function<XClientOps, Event> makeEvent) {
        
        sendEventToSubscribing(xEventSubscriptions, xWindow.getWINDOW(), eventCode, makeEvent);
    }
        
    protected final void sendEventToSubscribing(
            XEventSubscriptionsConstAccess xEventSubscriptions,
            WINDOW window,
            int eventCode,
            Function<XClientOps, Event> makeEvent) {
        
        
        for (XClientOps client : xEventSubscriptions.getClientsInterestedInEvent(window, eventCode)) {
            
            final Event event = makeEvent.apply(client);

            sendEvent(client, window, event);
        }
    }


}
