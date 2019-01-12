package com.neaterbits.displayserver.server;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.GetImageListener;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.ImageFormat;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.enums.WindowClass;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.FontException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.exception.WindowException;
import com.neaterbits.displayserver.protocol.messages.replies.GetImageReply;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeGC;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.ClearArea;
import com.neaterbits.displayserver.protocol.messages.requests.ConfigureWindow;
import com.neaterbits.displayserver.protocol.messages.requests.CopyArea;
import com.neaterbits.displayserver.protocol.messages.requests.CreateCursor;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreatePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.FreeGC;
import com.neaterbits.displayserver.protocol.messages.requests.FreePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.GetImage;
import com.neaterbits.displayserver.protocol.messages.requests.MapWindow;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.WindowConfiguration;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.CloseFont;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.ImageText16;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.OpenFont;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyFillRectangle;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyLine;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyPoint;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyRectangle;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolySegment;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.QueryFont;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.CHAR2B;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.RESOURCE;
import com.neaterbits.displayserver.protocol.types.STRING16;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.Display;
import com.neaterbits.displayserver.windows.DisplayArea;
import com.neaterbits.displayserver.windows.WindowsDisplayArea;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.WindowParameters;
import com.neaterbits.displayserver.windows.compositor.Compositor;
import com.neaterbits.displayserver.windows.compositor.OffscreenSurface;
import com.neaterbits.displayserver.windows.compositor.Surface;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.model.XDrawable;
import com.neaterbits.displayserver.xwindows.model.XGC;
import com.neaterbits.displayserver.xwindows.model.XPixmap;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

public class XClient extends XConnection {
    
    private final XServer server;
    private final XRendering rendering;
    private final Set<Integer> utilizedResourceIds;
    private final Map<GCONTEXT, XGC> gcs;

    private final Map<FONT, XFont> openFonts;
    
    public XClient(XServer server, SocketChannel socketChannel, SelectionKey selectionKey, int connectionNo,
            NonBlockingChannelWriterLog log, XRendering rendering) {
        super(socketChannel, selectionKey, connectionNo, log);

        Objects.requireNonNull(server);
        Objects.requireNonNull(rendering);

        this.server = server;
        this.rendering = rendering;

        this.utilizedResourceIds = new HashSet<>();
        
        this.gcs = new HashMap<>();
        
        this.openFonts = new HashMap<>();
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
        
        final Compositor compositor = rendering.getCompositor();
        
        final Surface windowSurface = compositor.allocateSurfaceForClientWindow(window);
        
        final XLibRenderer renderer = rendering.getRendererFactory().createRenderer(windowSurface, window.getPixelFormat());
        
        final XWindow xWindowsWindow = new XClientWindow(
                this,
                window,
                createWindow.getWid(),
                rootWindow.getWINDOW(),
                createWindow.getParent(),
                parentWindow.getVisual(),
                createWindow.getBorderWidth(),
                createWindow.getWindowClass(),
                WindowAttributes.DEFAULT_ATTRIBUTES.applyImmutably(createWindow.getAttributes()),
                renderer,
                windowSurface);
        
        return xWindowsWindow;
    }
    
    final void changeWindowAttributes(ChangeWindowAttributes changeWindowAttributes) throws WindowException {
    
        final XWindow xWindow = findClientOrRootWindow(changeWindowAttributes.getWindow());
     
        final WindowAttributes currentAttributes = xWindow.getCurrentWindowAttributes();

        final WindowAttributes updatedAttributes = currentAttributes.applyImmutably(changeWindowAttributes.getAttributes());
        
        xWindow.setCurrentWindowAttributes(updatedAttributes);
        
        final boolean sameBgMask = BITMASK.isSameMask(
                currentAttributes.getValueMask(),
                updatedAttributes.getValueMask(),
                WindowAttributes.BACKGROUND_PIXEL|WindowAttributes.BACKGROUND_PIXMAP);

        
        if (xWindow.isMapped()) {
        
            if (    !sameBgMask
                 || (    updatedAttributes.isSet(WindowAttributes.BACKGROUND_PIXEL)
                      && !updatedAttributes.getBackgroundPixel().equals(currentAttributes.getBackgroundPixel()))
                 || (    updatedAttributes.isSet(WindowAttributes.BACKGROUND_PIXMAP)
                      && !updatedAttributes.getBackgroundPixmap().equals(currentAttributes.getBackgroundPixmap()))
             ) {
    
                final BufferOperations windowBuffer = xWindow.getBufferOperations();
    
                renderWindowBackground(updatedAttributes, xWindow.getWindow(), xWindow.getRenderer(), windowBuffer);
            }
        }
    }
    
    final XWindow destroyWindow(Display display, DestroyWindow destroyWindow) {
        
        checkAndRemoveResourceId(destroyWindow.getWindow());
    
        final XWindow xWindow = server.getWindows().getClientWindow(destroyWindow.getWindow());
        
        if (xWindow != null) {
            
            if (!xWindow.isRootWindow()) {
                rendering.getCompositor().freeSurfaceForClientWindow(xWindow.getWindow());
            }
            
            display.disposeWindow(xWindow.getWindow());
            
            xWindow.dispose();
        }
        
        return xWindow;
    }

    final void mapWindow(MapWindow mapWindow) throws WindowException {
        
        final XWindow xWindow = findClientWindow(mapWindow.getWindow());
        
        if (!xWindow.isMapped()) {
            renderWindowBackground(
                    xWindow.getCurrentWindowAttributes(),
                    xWindow.getWindow(),
                    xWindow.getRenderer(),
                    xWindow.getBufferOperations());
        }
    }
    
    final void configureWindow(ConfigureWindow configureWindow) throws WindowException {
     
        final XWindow xWindow = findClientWindow(configureWindow.getWindow());
        
        Integer updatedX = null;
        Integer updatedY = null;
        
        Integer updatedWidth = null;
        Integer updatedHeight = null;
        
        final WindowConfiguration windowConfiguration = configureWindow.getConfiguration();
        
        if (windowConfiguration.isSet(WindowConfiguration.X)) {
            updatedX = (int)windowConfiguration.getX().getValue();
        }
        
        if (windowConfiguration.isSet(WindowConfiguration.Y)) {
            updatedY = (int)windowConfiguration.getY().getValue();
        }
        
        if (windowConfiguration.isSet(WindowConfiguration.WIDTH)) {
            updatedWidth = (int)windowConfiguration.getWidth().getValue();
        }
        
        if (windowConfiguration.isSet(WindowConfiguration.HEIGHT)) {
            updatedHeight = (int)windowConfiguration.getHeight().getValue();
        }
        
        final Window window = xWindow.getWindow();
        
        if (updatedX != null || updatedY != null) {
        
            final Position position = window.getPosition();
            
            final Position updatedPosition = new Position(
                    updatedX != null ? updatedX : position.getLeft(),
                    updatedY != null ? updatedY : position.getTop());
            
            
            window.setPosition(updatedPosition);
        }
        
        if (updatedWidth != null || updatedHeight != null) {
            
            final Size size = window.getSize();
        
            final Size updatedSize = new Size(
                    updatedWidth != null ? updatedWidth : size.getWidth(),
                    updatedHeight != null ? updatedHeight : size.getHeight());
        
            window.setSize(updatedSize);
        }
        
        if (windowConfiguration.isSet(WindowConfiguration.BORDER_WIDTH)) {
            
            xWindow.setBorderWidth(windowConfiguration.getBorderWidth());
            
            System.out.println("TODO - configure borderwidth");
        }
    }
    
    
    private void renderWindowBackground(WindowAttributes windowAttributes, Window window, XLibRenderer renderer, BufferOperations windowBuffer) {
        
        if (    windowAttributes.isSet(WindowAttributes.BACKGROUND_PIXMAP)
            && !windowAttributes.getBackgroundPixmap().equals(PIXMAP.None)) {

            final PIXMAP pixmapResource = windowAttributes.getBackgroundPixmap();

            final XPixmap xPixmap = server.getPixmaps().getPixmap(pixmapResource);

            if (xPixmap != null) {

                final OffscreenSurface src = xPixmap.getOffscreenSurface();

                System.out.println("## render to window of size " + window.getSize() + " from " + src.getWidth() + "/"
                        + src.getHeight() + " src " + src);

                final Size windowSize = window.getSize();

                for (int dstY = 0; dstY < windowSize.getHeight();) {

                    final int height = Math.min(src.getHeight(), windowSize.getHeight() - dstY);

                    for (int dstX = 0; dstX < windowSize.getWidth();) {

                        final int width = Math.min(src.getWidth(), windowSize.getWidth() - dstX);

                        windowBuffer.copyArea(src, 0, 0, dstX, dstY, width, height);

                        dstX += width;
                    }

                    dstY += height;
                }
            }
        } else if (windowAttributes.isSet(WindowAttributes.BACKGROUND_PIXEL)) {
        
            final int bgPixel = (int)windowAttributes.getBackgroundPixel().getValue();
            
            final PixelFormat pixelFormat = window.getPixelFormat();
            
            renderer.fillRectangle(
                    window.getPosition().getLeft(), window.getPosition().getTop(),
                    window.getSize().getWidth(), window.getSize().getHeight(),
                    pixelFormat.getRed(bgPixel),
                    pixelFormat.getGreen(bgPixel),
                    pixelFormat.getBlue(bgPixel));
            
            renderer.flush();
        }
    }
    
    private WindowsDisplayArea findDisplayArea(DRAWABLE drawable) {
        return server.findDisplayArea(drawable);
    }

    private XDrawable findDrawable(DRAWABLE drawable) throws DrawableException {
        
        final XDrawable xDrawable = server.getDrawables().findDrawable(drawable);

        if (drawable == null) {
            throw new DrawableException("No such drawable", drawable);
        }
        
        return xDrawable;
    }

    private XWindow findClientOrRootWindow(WINDOW window) throws WindowException {
        
        final XWindow xWindow = server.getWindows().getClientOrRootWindow(window);
        
        if (xWindow == null) {
            throw new WindowException("No such window", window);
        }
        
        return xWindow;
    }

    private XWindow findClientWindow(WINDOW window) throws WindowException {
        
        final XWindow xWindow = server.getWindows().getClientWindow(window);
        
        if (xWindow == null) {
            throw new WindowException("No such window", window);
        }
        
        return xWindow;
    }

    private VISUALID getVisual(DRAWABLE drawable) throws DrawableException {
        
        final XDrawable xDrawable = findDrawable(drawable);
        
        return xDrawable.getVisual();
    }
    
    private XFont getFont(FONT fontResource) throws FontException {
        
        Objects.requireNonNull(fontResource);
        
        final XFont font = openFonts.get(fontResource);
        
        if (font == null) {
            throw new FontException("No such font", fontResource);
        }
        
        return font;
    }

    
    void openFont(OpenFont openFont, XFont font) throws IDChoiceException {
        
        Objects.requireNonNull(openFont);
        Objects.requireNonNull(font);
        
        final FONT fontResource = openFont.getFid();
        
        checkAndAddResourceId(fontResource);
        
        if (openFonts.containsKey(fontResource)) {
            throw new IDChoiceException("Already open", fontResource);
        }
        
        openFonts.put(fontResource, font);
    }
    
    XFont closeFont(CloseFont closeFont) throws FontException {
        
        Objects.requireNonNull(closeFont);
        
        final FONT fontResource = closeFont.getFont();
        
        if (!openFonts.containsKey(fontResource)) {
            throw new FontException("Font not open", fontResource);
        }
        
        final XFont font = openFonts.remove(fontResource);

        checkAndRemoveResourceId(fontResource);
        
        return font;
    }
    
    void queryFont(QueryFont queryFont, CARD16 sequenceNumber, ServerToClient serverToClient) throws FontException {
        
        final FONT fontResource = queryFont.getFont().toFontResource();
        
        XFont font = openFonts.get(fontResource);
        
        if (font == null) {
            final GCONTEXT gcResource = queryFont.getFont().toGCResource();
            
            final XGC gc;
            try {
                gc = getGC(gcResource);

                font = openFonts.get(gc.getAttributes().getFont());
                
            } catch (GContextException ex) {
                throw new FontException("No such font", queryFont.getFont());
            }
        }
        
        if (font == null) {
            throw new FontException("No such font", fontResource);
        }
        
        MessageProcessorFonts.queryFont(queryFont, sequenceNumber, this, font, serverToClient);
    }
    
    private XGC getGC(GCONTEXT gcResource) throws GContextException {
        
        Objects.requireNonNull(gcResource);

        final XGC gc = gcs.get(gcResource);
        
        if (gc == null) {
            throw new GContextException("No such GC", gcResource);
        }
        
        return gc;
    }
    
    final XPixmap createPixmap(CreatePixmap createPixmap) throws IDChoiceException, DrawableException {
        
        final WindowsDisplayArea displayArea = findDisplayArea(createPixmap.getDrawable());
        
        checkAndAddResourceId(createPixmap.getPid());
        
        final Size size = new Size(
                createPixmap.getWidth().getValue(),
                createPixmap.getHeight().getValue());
        
        final PixelFormat pixelFormat = PixelFormat.RGB24;
        
        final OffscreenSurface surface = displayArea.allocateOffscreenSurface(
                size,
                pixelFormat);
        
        final XPixmap xPixmap = new XPixmap(
                getVisual(createPixmap.getDrawable()),
                surface,
                rendering.getRendererFactory().createRenderer(surface, pixelFormat));
        
        return xPixmap;
    }
    

    final void freePixmap(FreePixmap freePixmap, XPixmap xPixmap, DisplayArea graphicsScreen) {

        checkAndRemoveResourceId(freePixmap.getPixmap());
        
        if (xPixmap.getOffscreenSurface() != null) {
            graphicsScreen.freeOffscreenSurface(xPixmap.getOffscreenSurface());
        }
        
        xPixmap.dispose();
    }
    
    final void createGC(CreateGC createGC) throws DrawableException, IDChoiceException {
        
        if (gcs.containsKey(createGC.getCid())) {
            throw new IDChoiceException("ID already added", createGC.getCid());
        }
        
        final GCAttributes attributes = GCAttributes.DEFAULT_ATTRIBUTES.applyImmutably(createGC.getAttributes());
        
        final DRAWABLE drawable = createGC.getDrawable();
        
        final XDrawable xDrawable = findDrawable(drawable);
        
        if (xDrawable == null) {
            throw new DrawableException("No such drawable", drawable);
        }

        addGC(createGC.getCid(), attributes);
    }
    
    private void addGC(GCONTEXT context, GCAttributes attributes) {
        
        Objects.requireNonNull(context);
        Objects.requireNonNull(attributes);

        if (gcs.containsKey(context)) {
            throw new IllegalStateException();
        }
        
        final XGC xgc = new XGC(attributes);
        
        gcs.put(context, xgc);
    }
    
    private void changeGC(GCONTEXT context, GCAttributes attributes) {
        
        Objects.requireNonNull(context);
        Objects.requireNonNull(attributes);
        
        final XGC existing = gcs.get(context);
        
        if (existing == null) {
            throw new IllegalStateException();
        }

        gcs.put(context, new XGC(existing.getAttributes().applyImmutably(attributes)));
    }

    private void removeGC(GCONTEXT context) {
        
        Objects.requireNonNull(context);
        
        gcs.remove(context);
    }

    final void changeGC(ChangeGC changeGC) throws GContextException {
        
        final GCONTEXT gc = changeGC.getGc();
        
        changeGC(gc, changeGC.getAttributes());
    }
    
    final void freeGC(FreeGC freeGC) throws GContextException {
        removeGC(freeGC.getGContext());
    }
    
    private BufferOperations getBufferOperations(DRAWABLE drawable) throws DrawableException {
    
        Objects.requireNonNull(drawable);
        
        final XDrawable xDrawable = findDrawable(drawable);
        
        return xDrawable.getBufferOperations();
    }

    final void clearArea(ClearArea clearArea) {

    }

    final void copyArea(CopyArea copyArea) throws GContextException, DrawableException {

        final BufferOperations src = getBufferOperations(copyArea.getSrcDrawable());
        final BufferOperations dst = getBufferOperations(copyArea.getDstDrawable());
        
        dst.copyArea(
                src,
                copyArea.getSrcX().getValue(), copyArea.getSrcY().getValue(),
                copyArea.getDstX().getValue(), copyArea.getDstY().getValue(),
                copyArea.getWidth().getValue(), copyArea.getHeight().getValue());
    }

    final void polyPoint(PolyPoint polyPoint) throws DrawableException, GContextException {
        
        final XDrawable xDrawable = findDrawable(polyPoint.getDrawable());
        final XGC gc = getGC(polyPoint.getGC());
        
        final XLibRenderer renderer = xDrawable.getRenderer();

        renderer.polyPoint(gc, polyPoint.getCoordinateMode(), polyPoint.getPoints());
        
        renderer.flush();
    }

    final void polyLine(PolyLine polyLine) throws DrawableException, GContextException {
        
        final XDrawable xDrawable = findDrawable(polyLine.getDrawable());
        final XGC gc = getGC(polyLine.getGC());
        
        final XLibRenderer renderer = xDrawable.getRenderer();

        renderer.polyLine(gc, polyLine.getCoordinateMode(), polyLine.getPoints());
        
        renderer.flush();
    }

    final void polySegment(PolySegment polySegment) throws DrawableException, GContextException {
        
        final XDrawable xDrawable = findDrawable(polySegment.getDrawable());
        final XGC gc = getGC(polySegment.getGC());
        
        final XLibRenderer renderer = xDrawable.getRenderer();

        renderer.polySegment(gc, polySegment.getSegments());
        
        renderer.flush();
    }

    final void polyFillRectangle(PolyFillRectangle polyFillRectangle) throws DrawableException, GContextException {
        
        final XDrawable xDrawable = findDrawable(polyFillRectangle.getDrawable());
        final XGC gc = getGC(polyFillRectangle.getGC());
        
        final XLibRenderer renderer = xDrawable.getRenderer();

        renderer.polyFillRectangle(gc, polyFillRectangle.getRectangles());
        
        renderer.flush();
    }

    final void polyRectangle(PolyRectangle polyRectangle) throws DrawableException, GContextException {
        
        final XDrawable xDrawable = findDrawable(polyRectangle.getDrawable());
        final XGC gc = getGC(polyRectangle.getGC());
        
        final XLibRenderer renderer = xDrawable.getRenderer();

        renderer.polyRectangle(gc, polyRectangle.getRectangles());
        
        renderer.flush();
    }

    final void putImage(PutImage putImage) throws DrawableException, GContextException {
        
        final XDrawable xDrawable = findDrawable(putImage.getDrawable());
        final XGC gc = getGC(putImage.getGC());
        
        if (putImage.getDataOffset() != 0) {
            throw new IllegalArgumentException();
        }
        
        if (putImage.getDataLength() != putImage.getData().length) {
            throw new IllegalArgumentException();
        }
        
        final XLibRenderer renderer = xDrawable.getRenderer();
        
        renderer.putImage(
                gc,
                putImage.getFormat().getValue(),
                putImage.getWidth().getValue(),
                putImage.getHeight().getValue(),
                putImage.getDstX().getValue(),
                putImage.getDstY().getValue(),
                putImage.getLeftPad().getValue(),
                putImage.getDepth().getValue(),
                putImage.getData()
        );
        
        renderer.flush();
    }
    
    final void getImage(GetImage getImage, CARD16 sequenceNumber, ServerToClient serverToClient) throws MatchException, DrawableException {

        final XDrawable xDrawable = server.getDrawables().findDrawable(getImage.getDrawable());
        
        getImage(
                getImage,
                sequenceNumber,
                xDrawable.getBufferOperations(),
                xDrawable.getVisual(),
                serverToClient);
    }
    
    private void getImage(
            GetImage getImage,
            CARD16 sequenceNumber,
            BufferOperations bufferOperations,
            VISUALID visual,
            ServerToClient serverToClient)
    
        throws MatchException{
        
        if (getImage.getX().getValue() + getImage.getWidth().getValue() > bufferOperations.getWidth()) {
            throw new MatchException("Width outside of bounds");
        }
        
        if (getImage.getY().getValue() + getImage.getHeight().getValue() > bufferOperations.getHeight()) {
            throw new MatchException("Height outside of bounds");
        }

        switch (getImage.getFormat().getValue()) {

        case ImageFormat.ZPIXMAP:
            bufferOperations.getImage(
                    getImage.getX().getValue(), getImage.getY().getValue(),
                    getImage.getWidth().getValue(), getImage.getHeight().getValue(),
                    PixelFormat.RGB24,
                    
                    new GetImageListener() {
                        @Override
                        public void onResult(byte[] data) {
                            sendGetImageReply(sequenceNumber, bufferOperations, VISUALID.None, serverToClient, data);
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
            BufferOperations bufferOperations,
            VISUALID visual,
            ServerToClient serverToClient,
            byte [] data) {
        
        final GetImageReply getImageReply = new GetImageReply(
                sequenceNumber,
                new CARD8((byte)bufferOperations.getDepth()),
                visual,
                data);
        
        serverToClient.sendReply(this, getImageReply);
    }
    
    final void imageText16(ImageText16 imageText) throws DrawableException, GContextException, MatchException {

        final XDrawable xDrawable = findDrawable(imageText.getDrawable());
        final XGC gc = getGC(imageText.getGC());

        final FONT fontResource = gc.getAttributes().getFont();
        
        final XFont font;
        try {
            font = getFont(fontResource);
        } catch (FontException ex) {
            throw new GContextException("No such font", imageText.getGC());
        }
        
        final STRING16 string = imageText.getString();
        
        int x = imageText.getX().getValue();
        final int y = imageText.getY().getValue();
        
        final XLibRenderer renderer = xDrawable.getRenderer();

        for (int i = 0; i < string.length(); ++ i) {
            
            final CHAR2B character = string.getCharacter(i);
            
            final int glyphIndex = font.getGlyphIndex(character);
            
            renderer.renderBitmap(gc, font.getRenderBitmap(glyphIndex), x, y);
            // drawable.getRenderer().fillRectangle(x, y, 15, 15, 0, 0, 0);
            
            x += font.getGlyphRenderWidth(glyphIndex);
        }
        
        renderer.flush();
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
