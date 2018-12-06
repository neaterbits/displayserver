package com.neaterbits.displayserver.server;

import java.util.ArrayList;
import java.util.List;

import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;
import com.neaterbits.displayserver.framebuffer.common.GraphicsScreen;
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
import com.neaterbits.displayserver.windows.Screen;
import com.neaterbits.displayserver.windows.WindowEventListener;

class ScreensHelper {

    private static WindowAttributes getRootWindowAttributes(Screen screen) {
        
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

    static List<XWindowsScreen> getScreens(GraphicsDriver graphicsDriver, WindowEventListener windowEventListener, ServerResourceIdAllocator resourceIdAllocator) {
        
        final List<GraphicsScreen> driverScreens = graphicsDriver.getScreens();
        final List<XWindowsScreen> screens = new ArrayList<>(driverScreens.size());

        for (GraphicsScreen driverScreen : driverScreens) {
            
            final Screen screen = new Screen(driverScreen, windowEventListener);
            
            final int rootWindow = resourceIdAllocator.allocateRootWindowId();
            
            final WINDOW rootWindowResource = new WINDOW(rootWindow);
            
            final XWindow window = new XWindow(
                    screen.getRootWindow(),
                    rootWindowResource,
                    new CARD16(0),
                    WindowClass.InputOnly,
                    getRootWindowAttributes(screen));
            
            screens.add(new XWindowsScreen(screen, rootWindowResource, window));
        }
        
        return screens;
    }
}
