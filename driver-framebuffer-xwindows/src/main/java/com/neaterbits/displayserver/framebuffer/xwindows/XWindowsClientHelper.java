package com.neaterbits.displayserver.framebuffer.xwindows;

import java.io.IOException;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.protocol.enums.BackingStore;
import com.neaterbits.displayserver.protocol.enums.gc.Function;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.SCREEN;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.XGCAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.MapWindow;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
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
                new XWindowAttributes(
                        new BITMASK(
                                  XWindowAttributes.BACKING_STORE
                                | XWindowAttributes.EVENT_MASK
                        ),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        BackingStore.WhenMapped,
                        null,
                        null,
                        null,
                        null,
                        new SETofEVENT(
                                  SETofEVENT.KEY_PRESS
                                | SETofEVENT.KEY_RELEASE
                                | SETofEVENT.BUTTON_PRESS
                                | SETofEVENT.BUTTON_RELEASE
                                | SETofEVENT.POINTER_MOTION
                                | SETofEVENT.STRUCTURE_NOTIFY),
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
                new XGCAttributes(
                        new BITMASK(XGCAttributes.FUNCTION|XGCAttributes.PLANE_MASK|XGCAttributes.GRAPHICS_EXPOSURES),
                        Function.Copy,
                        new CARD32(0xFFFFFFFFL),
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
                        new BOOL(false),
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
