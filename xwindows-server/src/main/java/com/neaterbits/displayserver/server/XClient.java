package com.neaterbits.displayserver.server;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.neaterbits.displayserver.buffers.ImageBuffer;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.framebuffer.common.GraphicsScreen;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.protocol.enums.WindowClass;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreatePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.FreePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.RESOURCE;
import com.neaterbits.displayserver.windows.Display;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.WindowParameters;

public class XClient extends XConnection {
    
    private final XServer server;
    private final Set<Integer> utilizedResourceIds;
    private final Map<DRAWABLE, ImageBuffer> drawableToImageBuffer;
    private final Map<DRAWABLE, DRAWABLE> pixmapToDrawable;
    
    public XClient(XServer server, SocketChannel socketChannel, int connectionNo,
            NonBlockingChannelWriterLog log) {
        super(socketChannel, connectionNo, log);

        Objects.requireNonNull(server);

        this.server = server;

        this.utilizedResourceIds = new HashSet<>();
        
        this.drawableToImageBuffer = new HashMap<>();
        
        this.pixmapToDrawable = new HashMap<>();
    }


    final XWindow createWindow(Display display, CreateWindow createWindow, XWindow parentWindow) throws ValueException, IDChoiceException {

        Objects.requireNonNull(display);
        Objects.requireNonNull(createWindow);
        Objects.requireNonNull(parentWindow);
        
        final DRAWABLE drawable = createWindow.getWid().toDrawable();
        
        checkAndAddResourceId(drawable);
        
        final com.neaterbits.displayserver.windows.WindowClass windowClass;

        switch (createWindow.getWindowClass().getValue()) {
        
        case WindowClass.COPY_FROM_PARENT:
            windowClass = parentWindow.getWindow().getParameters().getWindowClass();
            break;
            
        case WindowClass.INPUT_OUTPUT:
            windowClass = com.neaterbits.displayserver.windows.WindowClass.INPUT_OUTPUT;
            break;
            
        case WindowClass.INPUT_ONLY:
            windowClass = com.neaterbits.displayserver.windows.WindowClass.INPUT_ONLY;
            break;
            
        default:
            throw new ValueException("Unknown window class", createWindow.getWindowClass().getValue());
        }
        
        final WindowParameters windowParameters = new WindowParameters(
                windowClass,
                createWindow.getDepth().getValue(),
                null,
                createWindow.getX().getValue(),
                createWindow.getY().getValue(),
                createWindow.getWidth().getValue(),
                createWindow.getHeight().getValue(),
                createWindow.getBorderWidth().getValue());
        

        final Window window = display.createWindow(parentWindow.getWindow(), windowParameters, null);
        
        if (window == null) {
            throw new IllegalStateException();
        }
        
        final XWindow rootWindow = server.getWindows().findRootWindowOf(createWindow.getParent());
        
        final XWindow xWindowsWindow = new XWindow(
                this,
                window,
                createWindow.getWid(),
                rootWindow.getWINDOW(),
                createWindow.getParent(),
                createWindow.getBorderWidth(),
                createWindow.getWindowClass(),
                WindowAttributes.DEFAULT_ATTRIBUTES.applyImmutably(createWindow.getAttributes()));
        
        return xWindowsWindow;
    }
    
    final XWindow destroyWindow(Display display, DestroyWindow destroyWindow) {
        checkAndRemoveResourceId(destroyWindow.getWindow());
    
        final XWindow window = server.getWindows().getClientWindow(destroyWindow.getWindow());
        
        if (window != null) {
            display.disposeWindow(window.getWindow());
        }
        
        return window;
    }
    
    private GraphicsScreen findGraphicsScreen(DRAWABLE drawable) {
        
        XWindow window = server.getWindows().getClientWindow(drawable);
        
        GraphicsScreen screen = null;
        
        if (window != null) {
            screen = window.getWindow().getScreen().getDriverScreen();
        }
        else {
            DRAWABLE pixmapDrawable = pixmapToDrawable.get(drawable);
            
            if (pixmapDrawable == null) {
                throw new IllegalStateException();
            }
            
            screen = findGraphicsScreen(pixmapDrawable);
        }
        
        return screen;
    }
    

    final ImageBuffer createPixmap(CreatePixmap createPixmap) throws IDChoiceException {
        
        final GraphicsScreen graphicsScreen = findGraphicsScreen(createPixmap.getDrawable());
        
        checkAndAddResourceId(createPixmap.getPid());
        
        final ImageBuffer imageBuffer = graphicsScreen.allocateBuffer(
                createPixmap.getWidth().getValue(),
                createPixmap.getHeight().getValue(),
                PixelFormat.RGB24);
        
        final DRAWABLE pixmapDrawable = createPixmap.getPid().toDrawable();
        
        drawableToImageBuffer.put(pixmapDrawable, imageBuffer);
        
        pixmapToDrawable.put(pixmapDrawable, createPixmap.getDrawable());
        
        return imageBuffer;
    }
    
    final void freePixmap(FreePixmap freePixmap) {
        checkAndRemoveResourceId(freePixmap.getPixmap());
        
        final DRAWABLE pixmapDrawable = freePixmap.getPixmap().toDrawable();
        
        final GraphicsScreen graphicsScreen = findGraphicsScreen(pixmapDrawable);

        final ImageBuffer imageBuffer = drawableToImageBuffer.remove(freePixmap.getPixmap().toDrawable());
        
        if (imageBuffer != null) {
            graphicsScreen.freeBuffer(imageBuffer);
        }
        
        pixmapToDrawable.remove(pixmapDrawable);
    }
    
    final void createGC(CreateGC createGC) {
        
    }
    
    final void putImage(PutImage putImage) {
        
        final XWindow window = server.getWindows().getClientWindow(putImage.getDrawable());
        
        if (window != null) {
            
        }
        else {
            final ImageBuffer imageBuffer = drawableToImageBuffer.get(putImage.getDrawable());
            
            if (imageBuffer != null) {
                
            }
        }
    }
    
    private void checkAndAddResourceId(RESOURCE resource) throws IDChoiceException {
        if (utilizedResourceIds.contains(resource.getValue())) {
            throw new IDChoiceException("Already utilized", resource);
        }
        
        utilizedResourceIds.add(resource.getValue());
    }
    
    private void checkAndRemoveResourceId(RESOURCE resource) {
        utilizedResourceIds.remove(resource.getValue());
    }
}
