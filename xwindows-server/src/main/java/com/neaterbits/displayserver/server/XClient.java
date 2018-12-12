package com.neaterbits.displayserver.server;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.neaterbits.displayserver.buffers.GetImageListener;
import com.neaterbits.displayserver.buffers.OffscreenBuffer;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.ImageFormat;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.enums.WindowClass;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.messages.replies.GetImageReply;
import com.neaterbits.displayserver.protocol.messages.requests.CreateCursor;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreatePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.FreeGC;
import com.neaterbits.displayserver.protocol.messages.requests.FreePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.GetImage;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.RESOURCE;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.Display;
import com.neaterbits.displayserver.windows.DisplayArea;
import com.neaterbits.displayserver.windows.DisplayAreaWindows;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.WindowParameters;

public class XClient extends XConnection {
    
    private final XServer server;
    private final Set<Integer> utilizedResourceIds;
    private final Map<DRAWABLE, XPixmap> drawableToXPixmap;
    private final Map<DRAWABLE, DRAWABLE> pixmapToOwnerDrawable;
    private final Map<GCONTEXT, XDrawable> gcToDrawable;
    
    public XClient(XServer server, SocketChannel socketChannel, int connectionNo,
            NonBlockingChannelWriterLog log) {
        super(socketChannel, connectionNo, log);

        Objects.requireNonNull(server);

        this.server = server;

        this.utilizedResourceIds = new HashSet<>();
        
        this.drawableToXPixmap = new HashMap<>();
        this.pixmapToOwnerDrawable = new HashMap<>();
        this.gcToDrawable = new HashMap<>();
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
                parentWindow.getVisual(),
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
    
    private DisplayAreaWindows findDisplayArea(DRAWABLE drawable) {
        
        XWindow window = server.getWindows().getClientOrRootWindow(drawable);
        
        DisplayAreaWindows displayArea = null;
        
        if (window != null) {
            displayArea = window.getWindow().getDisplayArea();
        }
        else {
            DRAWABLE pixmapDrawable = pixmapToOwnerDrawable.get(drawable);
            
            if (pixmapDrawable == null) {
                throw new IllegalStateException();
            }
            
            displayArea = findDisplayArea(pixmapDrawable);
        }
        
        return displayArea;
    }

    private XDrawable findDrawble(DRAWABLE drawable) {
        
        XWindow window = server.getWindows().getClientOrRootWindow(drawable);

        final XDrawable xDrawable;
        
        if (window != null) {
            xDrawable = window;
        }
        else {
            XPixmap pixmapDrawable = drawableToXPixmap.get(drawable);
            
            if (pixmapDrawable == null) {
                throw new IllegalStateException();
            }

            xDrawable = pixmapDrawable;
        }
        
        return xDrawable;
    }

    private VISUALID getVisual(DRAWABLE drawable) {
        
        final XDrawable xDrawable = findDrawble(drawable);
        
        return xDrawable.getVisual();
    }
    
    final XPixmap createPixmap(CreatePixmap createPixmap) throws IDChoiceException {
        
        final DisplayAreaWindows displayArea = findDisplayArea(createPixmap.getDrawable());
        
        checkAndAddResourceId(createPixmap.getPid());
        
        final Size size = new Size(
                createPixmap.getWidth().getValue(),
                createPixmap.getHeight().getValue());
        
        final OffscreenBuffer imageBuffer = displayArea.getOffscreenBufferProvider().allocateOffscreenBuffer(
                size,
                PixelFormat.RGB24);
        
        final DRAWABLE pixmapDrawable = createPixmap.getPid().toDrawable();
        
        final XPixmap xPixmap = new XPixmap(getVisual(createPixmap.getDrawable()), imageBuffer);
        
        drawableToXPixmap.put(pixmapDrawable, xPixmap);
        
        pixmapToOwnerDrawable.put(pixmapDrawable, createPixmap.getDrawable());
        
        return xPixmap;
    }
    
    final void freePixmap(FreePixmap freePixmap) {
        checkAndRemoveResourceId(freePixmap.getPixmap());
        
        final DRAWABLE pixmapDrawable = freePixmap.getPixmap().toDrawable();
        
        final DisplayArea graphicsScreen = findDisplayArea(pixmapDrawable);

        final XPixmap xPixmap = drawableToXPixmap.remove(freePixmap.getPixmap().toDrawable());
        
        if (xPixmap != null) {
            if (xPixmap.getOffscreenBuffer() != null) {
                graphicsScreen.getOffscreenBufferProvider().freeOffscreenBuffer(xPixmap.getOffscreenBuffer());
            }
        }
        
        pixmapToOwnerDrawable.remove(pixmapDrawable);
    }
    
    final void createGC(CreateGC createGC) throws DrawableException, IDChoiceException {
        
        if (gcToDrawable.containsKey(createGC.getCid())) {
            throw new IDChoiceException("ID already added", createGC.getCid());
        }
        
        final GCAttributes attributes = GCAttributes.DEFAULT_ATTRIBUTES.applyImmutably(createGC.getAttributes());
        
        final DRAWABLE drawable = createGC.getDrawable();
        
        final XPixmap xPixmap = drawableToXPixmap.get(drawable);
        final XDrawable xDrawable;
        
        if (xPixmap != null) {
            xDrawable = xPixmap;
        }
        else {
            xDrawable = server.getWindows().getClientOrRootWindow(drawable);
        }

        if (xDrawable == null) {
            throw new DrawableException("No such drawable", drawable);
        }

        xDrawable.addGC(createGC.getCid(), attributes);
        
        gcToDrawable.put(createGC.getCid(), xDrawable);
    }
    
    final void freeGC(FreeGC freeGC) throws GContextException {
        
        final XDrawable xDrawable = gcToDrawable.remove(freeGC.getGContext());
        
        if (xDrawable == null) {
            throw new GContextException("No such GContext", freeGC.getGContext());
        }
        
        xDrawable.removeGC(freeGC.getGContext());
    }
    
    final void putImage(PutImage putImage) {
        
        final XWindow window = server.getWindows().getClientWindow(putImage.getDrawable());
        
        if (window != null) {
            
        }
        else {
            final XPixmap xPixmap = drawableToXPixmap.get(putImage.getDrawable());
            
            if (xPixmap != null) {
                
            }
        }
    }
    
    final void getImage(GetImage getImage, CARD16 sequenceNumber, ServerToClient serverToClient) throws MatchException {

        final XWindow window = server.getWindows().getClientWindow(getImage.getDrawable());
        
        if (window != null) {
            
        }
        else {
            final XPixmap xPixmap = drawableToXPixmap.get(getImage.getDrawable());
            
            if (xPixmap != null) {
                getImage(
                        getImage,
                        sequenceNumber,
                        xPixmap.getOffscreenBuffer(),
                        xPixmap.getVisual(),
                        serverToClient);
            }
        }
    }
    
    private void getImage(
            GetImage getImage,
            CARD16 sequenceNumber,
            OffscreenBuffer offscreenBuffer,
            VISUALID visual,
            ServerToClient serverToClient)
    
        throws MatchException{
        
        if (getImage.getX().getValue() + getImage.getWidth().getValue() > offscreenBuffer.getWidth()) {
            throw new MatchException("Width outside of bounds");
        }
        
        if (getImage.getY().getValue() + getImage.getHeight().getValue() > offscreenBuffer.getHeight()) {
            throw new MatchException("Height outside of bounds");
        }

        switch (getImage.getFormat().getValue()) {

        case ImageFormat.ZPIXMAP:
            offscreenBuffer.getImage(
                    getImage.getX().getValue(), getImage.getY().getValue(),
                    getImage.getWidth().getValue(), getImage.getHeight().getValue(),
                    PixelFormat.RGB24,
                    
                    new GetImageListener() {
                        @Override
                        public void onResult(byte[] data) {
                            sendGetImageReply(sequenceNumber, offscreenBuffer, visual, serverToClient, data);
                        }
                        
                        @Override
                        public void onError() {
                            serverToClient.sendError(
                                    XClient.this,
                                    Errors.Implementation,
                                    sequenceNumber,
                                    0L,
                                    OpCodes.GET_IMAGE);
                        }
                    }); 
            break;
            
        case ImageFormat.BITMAP:
        case ImageFormat.XYPIXMAP:
            throw new UnsupportedOperationException("TODO");

        default:
            throw new UnsupportedOperationException();
        }

    }
    
    private void sendGetImageReply(
            CARD16 sequenceNumber,
            OffscreenBuffer offscreenBuffer,
            VISUALID visual,
            ServerToClient serverToClient,
            byte [] data) {
        
        final GetImageReply getImageReply = new GetImageReply(
                sequenceNumber,
                new CARD8((byte)offscreenBuffer.getDepth()),
                visual,
                data);
        
        serverToClient.sendReply(this, getImageReply);
    }
    
    final void createCursor(CreateCursor createCursor) throws IDChoiceException {
        
        checkAndAddResourceId(createCursor.getCID());
        
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
