package com.neaterbits.displayserver.framebuffer.xwindows;

import java.io.IOException;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.SCREEN;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.MapWindow;
import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

class XWindowsClientHelper {

    static WINDOW createWindow(
            XWindowsDriverConnection driverConnection,
            Position position,
            Size size,
            SCREEN screen) {
    
        final WINDOW window = new WINDOW(driverConnection.allocateResourceId());
    
        final CreateWindow createWindow = new CreateWindow(
                new CARD8((short)0),
                window,
                screen.getRoot(),
                new INT16((short)250), new INT16((short)250),
                new CARD16(size.getWidth()), new CARD16(size.getHeight()),
                new CARD16(0),
                new CARD16(1),
                screen.getRootVisual(),
                new WindowAttributes(
                        new BITMASK(0),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));

        System.out.println("## send createwindow");

        driverConnection.sendRequest(createWindow);

        final MapWindow mapWindow = new MapWindow(window);
        
        System.out.println("## send mapwindow");
        
        driverConnection.sendRequest(mapWindow);
        
        return window;
    }
    
    static GCONTEXT createGC(XWindowsDriverConnection driverConnection, WINDOW window) throws IOException {
        
        final GCONTEXT gc = new GCONTEXT(driverConnection.allocateResourceId());

        final CreateGC createGC = new CreateGC(
                gc,
                window.toDrawable(),
                new GCAttributes(
                        new BITMASK(0),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        
        driverConnection.sendRequest(createGC);
        
        return gc;
    }
}