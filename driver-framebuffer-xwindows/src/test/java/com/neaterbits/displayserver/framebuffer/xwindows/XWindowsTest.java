package com.neaterbits.displayserver.framebuffer.xwindows;

import java.nio.ByteOrder;

import org.junit.Test;

import com.neaterbits.displayserver.io.common.DataWriter;
import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.enums.BackingStore;
import com.neaterbits.displayserver.protocol.enums.WindowClass;
import com.neaterbits.displayserver.protocol.enums.gc.FillStyle;
import com.neaterbits.displayserver.protocol.enums.gc.Function;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.MapWindow;
import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyFillRectangle;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.render.cairo.xcb.XCBConnection;
import com.neaterbits.displayserver.render.cairo.xcb.XCBScreen;
import com.neaterbits.displayserver.xwindows.util.JNIBindings;

public class XWindowsTest {

    @Test
    public void testOpenWindow() throws Exception {
        
        JNIBindings.load();
        
        final int display = 0;
        
        // final XAuth xAuth = XAuth.getXAuthInfo(display, "MIT-MAGIC-COOKIE-1");

        // final XCBConnection connection = XCBConnection.connect(":" + display, xAuth.getAuthorizationProtocol(), xAuth.getAuthorizationData());
        final XCBConnection connection = XCBConnection.connect(":" + display);

        try {

            System.out.println("## create window");
            
            final XCBScreen screen = connection.getSetup().getScreens().get(0);
            
            final WINDOW wid = new WINDOW(connection.generateId());
            
            final CreateWindow createWindow = new CreateWindow(
                    new CARD8((byte)screen.getRootDepth()),
                    wid,
                    new WINDOW(screen.getRoot()),
                    new INT16((short)150), new INT16((short)150),
                    new CARD16(1024), new CARD16(768),
                    new CARD16(0),
                    WindowClass.InputOutput,
                    new VISUALID(screen.getRootVisual()),
                    new WindowAttributes(
                            new BITMASK(
                                    // WindowAttributes.BACKGROUND_PIXEL
                                    WindowAttributes.BACKING_STORE
                                    |WindowAttributes.EVENT_MASK
                                    ),
                            null,
                            new CARD32(0xAAAAAA),
                            null,
                            null,
                            null, // BITGRAVITY.Center,
                            null, // WINGRAVITY.Center,
                            BackingStore.WhenMapped,
                            null,
                            null, // new CARD32(0xAAAAAA),
                            null,
                            null,
                            new SETofEVENT(SETofEVENT.EXPOSURE|SETofEVENT.STRUCTURE_NOTIFY),
                            null,
                            null,
                            null));

            sendRequest(connection, createWindow);
            
            sendRequest(connection, new MapWindow(wid));

            final GCONTEXT cid = new GCONTEXT(connection.generateId());
            
            final CreateGC createGC = new CreateGC(cid, wid.toDrawable(), new GCAttributes(
                    new BITMASK(GCAttributes.FUNCTION|GCAttributes.PLANE_MASK|GCAttributes.FOREGROUND),
                    Function.Copy,
                    new CARD32(0xFFFFFFFFL),
                    new CARD32(0xAAAAAA),
                    null, // new CARD32(0xAAAAAA),
                    null,
                    null,
                    null,
                    null,
                    FillStyle.Solid,
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
            
            sendRequest(connection, createGC);

            connection.flush();
            
            final RECTANGLE rectangle = new RECTANGLE(
                    new INT16((short)250), new INT16((short)250),
                    new CARD16(300), new CARD16(300)
            );

            final PolyFillRectangle polyFillRectangle = new PolyFillRectangle(
                    wid.toDrawable(),
                    cid,
                    new RECTANGLE [] { rectangle });
            
            sendRequest(connection, polyFillRectangle);

            connection.flush();

            System.out.println("## events\n");

            int eventCode;
            
            // boolean rendered = false;
            
            while (0 != (eventCode = connection.waitForEvent())) {
                
                System.out.println("## response code " + eventCode);

                switch (eventCode & ~0x80) {
                case Events.EXPOSE:

                    System.out.println("## render rectangle\n");
                    
                    // if (!rendered) {
                    
                        sendRequest(connection, polyFillRectangle);
    
                        connection.flush();
                        
                        // rendered = true;
                    // }
                    break;

                case Events.DESTROY_NOTIFY:
                    return;
                }
            }
            
        }
        finally {
            connection.close();
        }
    }
    
    private void sendRequest(XCBConnection connection, Request request) {
        
        final DataWriter dataWriter = Encodeable.makeDataWriter(request);
        
        final byte [] buf = DataWriter.writeToBuf(dataWriter, ByteOrder.LITTLE_ENDIAN);
        
        connection.sendRequest(buf, request.getOpCode(), request.getReplyClass() == null);
    }
}
