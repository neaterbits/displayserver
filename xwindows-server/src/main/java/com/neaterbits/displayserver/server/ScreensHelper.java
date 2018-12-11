package com.neaterbits.displayserver.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;
import com.neaterbits.displayserver.protocol.enums.WindowClass;
import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.BITGRAVITY;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.SETofDEVICEEVENT;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;
import com.neaterbits.displayserver.windows.DisplayArea;
import com.neaterbits.displayserver.windows.DisplayAreaWindows;

class ScreensHelper {

    private static WindowAttributes getRootWindowAttributes(DisplayArea displayArea) {
        
        return new WindowAttributes(
                WindowAttributes.ALL,
                PIXMAP.None,
                new CARD32(0),
                PIXMAP.None, new CARD32(0),
                BITGRAVITY.Forget, WINGRAVITY.NorthWest,
                new BYTE((byte)0),
                new CARD32(0xFFFFFFFFL), new CARD32(0),
                new BOOL(false),
                new BOOL(false),
                new SETofEVENT(0),
                new SETofDEVICEEVENT(0),
                COLORMAP.None,
                CURSOR.None);
        
    }

    static List<XScreen> getScreens(
            GraphicsDriver graphicsDriver,
            List<DisplayAreaWindows> displayAreas,
            ServerResourceIdAllocator resourceIdAllocator,
            Consumer<XWindow> addRootWindow) {
        
        final List<XScreen> screens = new ArrayList<>(displayAreas.size());

        for (DisplayAreaWindows displayArea : displayAreas) {
            
            final int rootWindow = resourceIdAllocator.allocateRootWindowId();
            
            final WINDOW rootWindowResource = new WINDOW(rootWindow);
            
            final XWindow xWindow = new XWindow(
                    displayArea.getRootWindow(),
                    rootWindowResource,
                    new CARD16(0),
                    WindowClass.InputOnly,
                    getRootWindowAttributes(displayArea));
            
            addRootWindow.accept(xWindow);
            
            screens.add(new XScreen(displayArea, xWindow));
        }
        
        return screens;
    }
}
