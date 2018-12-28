package com.neaterbits.displayserver.server;

import java.util.Set;
import java.util.function.Supplier;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.VisualClass;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.DEPTH;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.FORMAT;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.SCREEN;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.VISUALTYPE;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.KEYCODE;
import com.neaterbits.displayserver.protocol.types.SET32;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.DisplayArea;
import com.neaterbits.displayserver.xwindows.model.XScreen;
import com.neaterbits.displayserver.xwindows.model.XScreensConstAccess;

class InitialServerMessageHelper {

    static ServerMessage constructServerMessage(
            int connectionNo,
            XScreensConstAccess screensAccess,
            long resourceBase,
            long resourceMask,
            Supplier<Integer> allocateVisualId) {
        final String vendor = "Test";
        
        final Set<PixelFormat> distinctPixelFormats = screensAccess.getDistinctPixelFormats();
        
        final FORMAT [] formats = new FORMAT[distinctPixelFormats.size()];
        
        int dstIdx = 0;
        
        for (PixelFormat pixelFormat : distinctPixelFormats) {
            final FORMAT format = new FORMAT(
                    new CARD8((short)pixelFormat.getDepth()),
                    new CARD8((short)pixelFormat.getBitsPerPixel()),
                    new CARD8((short)0));
            
            formats[dstIdx ++] = format;
        }
        
        final int numScreens = screensAccess.getNumberOfScreens();
        
        final SCREEN [] screens = new SCREEN[numScreens];
        
        for (int i = 0; i < numScreens; ++ i) {
            final XScreen xWindowsScreen = screensAccess.getScreen(i);
            final DisplayArea displayArea = xWindowsScreen.getDisplayArea();
            
            final Size size = displayArea.getSize();
            final Size sizeInMillimeters = displayArea.getSizeInMillimeters();
            
            final PixelFormat pixelFormat = displayArea.getPixelFormat();
            
            final VISUALID visualId = new VISUALID(allocateVisualId.get());
            
            final VISUALTYPE visual = new VISUALTYPE(
                    visualId,
                    VisualClass.TrueColor,
                    new CARD8((short)pixelFormat.getBitsPerColorComponent()),
                    new CARD16(pixelFormat.getNumberOfDistinctColors()),
                    new CARD32(pixelFormat.getRedMask()),
                    new CARD32(pixelFormat.getGreenMask()),
                    new CARD32(pixelFormat.getBlueMask()));
            
            final DEPTH depth = new DEPTH(
                    new CARD8((short)pixelFormat.getDepth()),
                    new CARD16(1),
                    new VISUALTYPE[] { visual });
            
            final SCREEN screen = new SCREEN(
                    xWindowsScreen.getRootWINDOW(),
                    new COLORMAP(0),
                    new CARD32(0xFFFFFFF), new CARD32(0x000000),
                    new SET32(0),
                    new CARD16(size.getWidth()), new CARD16(size.getHeight()),
                    new CARD16(sizeInMillimeters.getWidth()), new CARD16(sizeInMillimeters.getHeight()),
                    new CARD16(0), new CARD16(0),
                    visualId,
                    new BYTE((byte)0), new BOOL((byte)0),
                    new CARD8((short)24), new CARD8((short)1), new DEPTH [] { depth });
         
            screens[i] = screen;
        }
        
        final int vendorAndScreenBytes = 
                vendor.length()
              + XWindowsProtocolUtil.getPadding(vendor.length())
              + length(screens);
        
        final int length = 
                  8 
                + 2 * formats.length
                + (vendorAndScreenBytes / 4);
        
        final ServerMessage serverMessage = new ServerMessage(
                new BYTE((byte)1),
                new CARD16((short)11), new CARD16((short)0),
                new CARD16(length),
                new CARD32(1),
                new CARD32(resourceBase),
                new CARD32(resourceMask),
                new CARD32(0),
                new CARD16(vendor.length()), new CARD16((1 << 15) - 1),
                new CARD8((short)screens.length), new CARD8((short)formats.length),
                new BYTE((byte)0), new BYTE((byte)0), new CARD8((byte)32), new CARD8((byte)32),
                new KEYCODE((short)8), new KEYCODE((short)105),
                vendor,
                formats,
                screens);
        

        return serverMessage;
    }
    
    private static int length(SCREEN [] screens) {
        
        int length = 0;
        
        for (SCREEN screen : screens) {
            length += 40 + length(screen.getAllowedDepths());
        }
        
        return length;
    }
    
    private static int length(DEPTH [] depths) {

        int length = 0;

        for (DEPTH depth : depths) {
            length += 8 + depth.getVisuals().length * 24;
        }
    
        return length;
    }
}
