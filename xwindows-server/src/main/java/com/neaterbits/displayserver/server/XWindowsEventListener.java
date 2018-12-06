package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.layers.Rectangle;
import com.neaterbits.displayserver.layers.Region;
import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.messages.events.GraphicsExposure;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.WindowEventListener;

final class XWindowsEventListener implements WindowEventListener {

    private final XServer server;
    
    XWindowsEventListener(XServer server) {
        
        Objects.requireNonNull(server);
        
        this.server = server;
    }

    
    
    @Override
    public final void onUpdate(Window window, Region region) {
        
        Objects.requireNonNull(window);
        Objects.requireNonNull(region);
        
        // TODO might be root window too?
        final XWindow xWindow = server.getWindows().getClientWindow(window);
        
        if (xWindow == null) {
            throw new IllegalStateException();
        }
        
        int count = region.getRectangles().size();
        
        for (Rectangle rectangle : region.getRectangles()) {
            
            -- count;
            
            final GraphicsExposure graphicsExposure = new GraphicsExposure(
                    xWindow.getWINDOW().toDrawable(),
                    new CARD16(rectangle.getLeft()), new CARD16(rectangle.getTop()),
                    new CARD16(rectangle.getWidth()), new CARD16(rectangle.getHeight()),
                    new CARD16(count),
                    new CARD8((byte)OpCodes.COPY_AREA), new CARD16(0));

            
            sendEventToSubscribing(xWindow, graphicsExposure, Events.GRAPHICS_EXPOSURE);
        }
    }
    
    private void sendEventToSubscribing(XWindow xWindow, Event event, int eventCode) {
        for (XConnection connection : server.getEventSubscriptions().getClientsInterestedInEvent(xWindow, eventCode)) {
            connection.addEvent(event);
        }
    }
}
