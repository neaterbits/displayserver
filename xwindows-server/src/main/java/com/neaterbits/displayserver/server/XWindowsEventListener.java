package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.layers.LayerRectangle;
import com.neaterbits.displayserver.layers.LayerRegion;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.WindowEventListener;
import com.neaterbits.displayserver.xwindows.model.XWindow;

final class XWindowsEventListener implements WindowEventListener {

    private final XServer server;
    
    XWindowsEventListener(XServer server) {
        
        Objects.requireNonNull(server);
        
        this.server = server;
    }
    
    @Override
    public final void onUpdate(Window window, LayerRegion region) {
        
        Objects.requireNonNull(window);
        Objects.requireNonNull(region);
        
        // TODO might be root window too?
        final XWindow xWindow = server.getWindows().getClientWindow(window);
        
        if (xWindow == null) {
            throw new IllegalStateException();
        }
        
        int count = region.getRectangles().size();
        
        for (LayerRectangle rectangle : region.getRectangles()) {
            
            -- count;
            
            final int c = count;
            
            /*
            sendEventToSubscribing(
                    server.getEventSubscriptions(),
                    xWindow,
                    Events.GRAPHICS_EXPOSURE,
                    client ->
            
                new GraphicsExposure(
                    client.getSequenceNumber(),
                    xWindow.getWINDOW().toDrawable(),
                    new CARD16(rectangle.getLeft()), new CARD16(rectangle.getTop()),
                    new CARD16(rectangle.getWidth()), new CARD16(rectangle.getHeight()),
                    new CARD16(c),
                    new CARD8((byte)OpCodes.COPY_AREA), new CARD16(0))
                );

            */
        }
    }
}
