package com.neaterbits.displayserver.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;
import com.neaterbits.displayserver.protocol.enums.VisualClass;
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
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;
import com.neaterbits.displayserver.windows.DisplayArea;
import com.neaterbits.displayserver.windows.WindowsDisplayArea;
import com.neaterbits.displayserver.windows.WindowsDisplayAreas;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.xwindows.model.XScreen;
import com.neaterbits.displayserver.xwindows.model.XScreenDepth;
import com.neaterbits.displayserver.xwindows.model.XScreensAndVisuals;
import com.neaterbits.displayserver.xwindows.model.XVisual;
import com.neaterbits.displayserver.xwindows.model.XWindow;

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

    static XScreensAndVisuals getScreens(
            GraphicsDriver graphicsDriver,
            WindowsDisplayAreas displayAreas,
            ServerResourceIdAllocator resourceIdAllocator,
            XRendering rendering,
            BiConsumer<Integer, XWindow> addRootWindow) {
        
        
        final List<XScreen> screens = new ArrayList<>(displayAreas.getDisplayAreas().size());
        
        final Map<VISUALID, XVisual> visuals = new HashMap<>();

        int screenNo = 0;
        
        for (WindowsDisplayArea displayArea : displayAreas.getDisplayAreas()) {
            
            final int rootWindow = resourceIdAllocator.allocateRootWindowId();
            
            final WINDOW rootWindowResource = new WINDOW(rootWindow);
            
            final VISUALID visualId = new VISUALID(resourceIdAllocator.allocateVisualId());
            
            final PixelFormat pixelFormat = displayArea.getPixelFormat();
            
            final XVisual xVisual = new XVisual(
                    VisualClass.TRUECOLOR,
                    pixelFormat.getBitsPerColorComponent(),
                    pixelFormat.getNumberOfDistinctColors(),
                    pixelFormat.getRedMask(),
                    pixelFormat.getGreenMask(),
                    pixelFormat.getBlueMask());

            visuals.put(visualId, xVisual);
            
            final BufferOperations bufferOperations = rendering.getCompositor().getSurfaceForRootWindow(displayArea.getRootWindow());
            
            final Window window = displayArea.getRootWindow();
            
            final XWindow xRootWindow = new XWindow(
                    window,
                    rootWindowResource,
                    visualId,
                    new CARD16(0),
                    WindowClass.InputOnly,
                    getRootWindowAttributes(displayArea),
                    rendering.getRendererFactory().createRenderer(bufferOperations, pixelFormat),
                    rendering.getCompositor().getSurfaceForRootWindow(window));
            
            addRootWindow.accept(screenNo, xRootWindow);
            
            final int rootDepth = pixelFormat.getDepth();
            
            final XScreenDepth xScreenDepth = new XScreenDepth(rootDepth, Arrays.asList(visualId));
            
            final XScreen xScreen = new XScreen(screenNo, displayArea, xRootWindow, visualId, Arrays.asList(xScreenDepth));
            
            screens.add(xScreen);
        }
        
        return new XScreensAndVisuals(screens, visuals);
    }
}
