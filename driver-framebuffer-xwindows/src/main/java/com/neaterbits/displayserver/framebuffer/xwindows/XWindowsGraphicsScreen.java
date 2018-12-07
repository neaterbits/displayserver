package com.neaterbits.displayserver.framebuffer.xwindows;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.framebuffer.common.BaseGraphicsScreen;
import com.neaterbits.displayserver.framebuffer.common.FrameBuffer;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.SCREEN;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
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
import com.neaterbits.displayserver.types.Size;

final class XWindowsGraphicsScreen extends BaseGraphicsScreen {
    
    private final XWindowsDriverConnection driverConnection;
    private final Size size;
    private final int depth;

    private final SCREEN screen;
    
    private final XWindowsFrameBuffer frameBuffer;
    
    XWindowsGraphicsScreen(XWindowsDriverConnection driverConnection, Size size, int depth) {

        Objects.requireNonNull(driverConnection);
        Objects.requireNonNull(size);
        
        final ServerMessage serverMessage = driverConnection.getServerMessage();
        
        Objects.requireNonNull(serverMessage);
        
        this.driverConnection = driverConnection;
        this.size = size;
        this.depth = depth;
        
        this.screen = serverMessage.getScreens()[0];
        
        this.frameBuffer = initFrameBuffer();
    }

    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public Size getSizeInMillimeters() {
        
        return new Size(
                dimensionInMillimeters(size.getWidth(), screen.getWidthInPixels().getValue(), screen.getWidthInMillimiters().getValue()),
                dimensionInMillimeters(size.getHeight(), screen.getHeightInPixels().getValue(), screen.getHeightInMillimiters().getValue()));
    }

    private static int dimensionInMillimeters(int windowInPixels, int screenInPixels, int screenInMillimeters) {
        final double ratio = windowInPixels / (double)screenInPixels;
    
        return (int)(screenInMillimeters * ratio);
    }
    
    @Override
    public PixelFormat getPixelFormat() {
        return PixelFormat.RGB24;
    }
    
    private XWindowsFrameBuffer initFrameBuffer() {
        try {
            final WINDOW window = new WINDOW(driverConnection.allocateResourceId());
            final GCONTEXT gc = new GCONTEXT(driverConnection.allocateResourceId());
    
            final CreateWindow createWindow = new CreateWindow(
                    new CARD8((short)depth),
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

            System.out.println("## send creategc");

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
            
            return new XWindowsFrameBuffer(driverConnection, window, gc, size, depth);
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    

    @Override
    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }
}
