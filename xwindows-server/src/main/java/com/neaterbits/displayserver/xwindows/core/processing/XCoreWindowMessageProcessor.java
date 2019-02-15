package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.neaterbits.displayserver.layers.LayerRectangle;
import com.neaterbits.displayserver.layers.LayerRegion;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.BackingStore;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.MapState;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.enums.WindowClass;
import com.neaterbits.displayserver.protocol.exception.AccessException;
import com.neaterbits.displayserver.protocol.exception.ColormapException;
import com.neaterbits.displayserver.protocol.exception.CursorException;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.exception.PixmapException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.exception.WindowException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.events.CreateNotify;
import com.neaterbits.displayserver.protocol.messages.events.Expose;
import com.neaterbits.displayserver.protocol.messages.events.MapNotify;
import com.neaterbits.displayserver.protocol.messages.events.MapRequest;
import com.neaterbits.displayserver.protocol.messages.replies.GetGeometryReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetWindowAttributesReply;
import com.neaterbits.displayserver.protocol.messages.replies.QueryTreeReply;
import com.neaterbits.displayserver.protocol.messages.replies.TranslateCoordinatesReply;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.ConfigureWindow;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.GetGeometry;
import com.neaterbits.displayserver.protocol.messages.requests.GetWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.MapSubwindows;
import com.neaterbits.displayserver.protocol.messages.requests.MapWindow;
import com.neaterbits.displayserver.protocol.messages.requests.QueryTree;
import com.neaterbits.displayserver.protocol.messages.requests.TranslateCoordinates;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowConfiguration;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.server.XClientWindow;
import com.neaterbits.displayserver.server.XClientWindows;
import com.neaterbits.displayserver.server.XEventSubscriptions;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.TranslatedCoordinates;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.WindowContentStorage;
import com.neaterbits.displayserver.windows.WindowManagement;
import com.neaterbits.displayserver.windows.WindowParameters;
import com.neaterbits.displayserver.windows.compositor.Compositor;
import com.neaterbits.displayserver.windows.compositor.Surface;
import com.neaterbits.displayserver.xwindows.model.XColormapsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XCursorsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XPixmap;
import com.neaterbits.displayserver.xwindows.model.XPixmaps;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;
import com.neaterbits.displayserver.xwindows.model.render.XLibRendererFactory;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

public class XCoreWindowMessageProcessor extends BaseXCorePixmapRenderProcessor {

    private final WindowManagement windowManagement;
    private final XClientWindows xWindows;
    private final XPixmaps xPixmaps;
    private final XColormapsConstAccess xColormaps;
    private final XCursorsConstAccess xCursors;
    private final XEventSubscriptions eventSubscriptions;
    private final Compositor compositor;
    private final XLibRendererFactory rendererFactory;
    
    public XCoreWindowMessageProcessor(
            XWindowsServerProtocolLog protocolLog,
            WindowManagement windowManagement,
            XClientWindows xWindows,
            XPixmaps xPixmaps,
            XColormapsConstAccess xColormaps,
            XCursorsConstAccess xCursors,
            XEventSubscriptions eventSubscriptions,
            Compositor compositor,
            XLibRendererFactory rendererFactory) {
        
        super(protocolLog, xPixmaps);
        
        this.windowManagement = windowManagement;
        this.xWindows = xWindows;
        this.xPixmaps = xPixmaps;
        this.xColormaps = xColormaps;
        this.xCursors = xCursors;
        this.eventSubscriptions = eventSubscriptions;
        this.compositor = compositor;
        this.rendererFactory = rendererFactory;
    }

    @Override
    protected int[] getOpCodes() {
        
        return new int [] {
                OpCodes.CREATE_WINDOW,
                OpCodes.CHANGE_WINDOW_ATTRIBUTES,
                OpCodes.GET_WINDOW_ATTRIBUTES,
                OpCodes.DESTROY_WINDOW,
                OpCodes.MAP_WINDOW,
                OpCodes.MAP_SUBWINDOWS,
                OpCodes.UNMAP_WINDOW,
                OpCodes.CONFIGURE_WINDOW,
                OpCodes.GET_GEOMETRY,
                OpCodes.TRANSLATE_COORDINATES
        };
    }

    @Override
    protected void onMessage(XWindowsProtocolInputStream stream, int messageLength, int opcode, CARD16 sequenceNumber, XClientOps client) throws IOException {

        switch (opcode) {
        
        case OpCodes.CREATE_WINDOW: {
            final CreateWindow createWindow = log(messageLength, opcode, sequenceNumber, CreateWindow.decode(stream));

            final XWindow parentWindow = xWindows.getClientOrRootWindow(createWindow.getParent());
            
            if (parentWindow == null) {
                sendError(client, Errors.Window, sequenceNumber, createWindow.getParent().getValue(), opcode);
            }
            else {
                
                try {
                    final XWindow window = createWindow(createWindow, parentWindow, client);

                    if (window != null) {
                        xWindows.addClientWindow(window, client);
                    }
                } catch (ValueException ex) {
                    sendError(client, Errors.Value, sequenceNumber, ex.getValue(), opcode);
                } catch (IDChoiceException ex) {
                    sendError(client, Errors.IDChoice, sequenceNumber, ex.getResource().getValue(), opcode);
                } catch (PixmapException ex) {
                    sendError(client, Errors.Pixmap, sequenceNumber, ex.getPixmap().getValue(), opcode);
                } catch (AccessException ex) {
                    sendError(client, Errors.Acess, sequenceNumber, 0L, opcode);
                } catch (ColormapException ex) {
                    sendError(client, Errors.Colormap, sequenceNumber, ex.getColormap().getValue(), opcode);
                } catch (CursorException ex) {
                    sendError(client, Errors.Cursor, sequenceNumber, ex.getCursor().getValue(), opcode);
                }
            }
            break;
        }
        
        case OpCodes.CHANGE_WINDOW_ATTRIBUTES: {
            final ChangeWindowAttributes changeWindowAttributes = log(messageLength, opcode, sequenceNumber, ChangeWindowAttributes.decode(stream));
            
            try {
                changeWindowAttributes(changeWindowAttributes, client);
            } catch (WindowException ex) {
                sendError(client, Errors.Window, sequenceNumber, ex.getWindow().getValue(), opcode);
            } catch (AccessException ex) {
                sendError(client, Errors.Acess, sequenceNumber, 0L, opcode);
            } catch (PixmapException ex) {
                sendError(client, Errors.Pixmap, sequenceNumber, ex.getPixmap().getValue(), opcode);
            } catch (ColormapException ex) {
                sendError(client, Errors.Colormap, sequenceNumber, ex.getColormap().getValue(), opcode);
            } catch (CursorException ex) {
                sendError(client, Errors.Cursor, sequenceNumber, ex.getCursor().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.GET_WINDOW_ATTRIBUTES: {
            
            final GetWindowAttributes getWindowAttributes = log(messageLength, opcode, sequenceNumber, GetWindowAttributes.decode(stream));

            getWindowAttributes(getWindowAttributes, opcode, sequenceNumber, client);
            break;
        }
        
        case OpCodes.DESTROY_WINDOW: {
            final DestroyWindow destroyWindow = log(messageLength, opcode, sequenceNumber, DestroyWindow.decode(stream));
            
            final XWindow window = destroyWindow(destroyWindow, client);
            
            if (window != null) {
                xWindows.removeClientWindow(window);
            }
            break;
        }
        
        case OpCodes.MAP_WINDOW: {
            
            final MapWindow mapWindow = log(messageLength, opcode, sequenceNumber, MapWindow.decode(stream));
            
            try {
                mapWindow(mapWindow);
            } catch (WindowException ex) {
                sendError(client, Errors.Window, sequenceNumber, ex.getWindow().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.MAP_SUBWINDOWS: {
            
            final MapSubwindows mapSubwindows = log(messageLength, opcode, sequenceNumber, MapSubwindows.decode(stream));

         
            try {
                final Collection<XWindow> subWindows = xWindows.getAllSubWindows(mapSubwindows.getWindow());
                
                for (XWindow xWindow : subWindows) {
                    mapWindow(xWindow);
                }
            }
            catch (WindowException ex) {
                sendError(client, Errors.Window, sequenceNumber, ex.getWindow().getValue(), opcode);
            }
            break;
        }

        case OpCodes.UNMAP_WINDOW: {
            
            log(messageLength, opcode, sequenceNumber, MapWindow.decode(stream));
            
            break;
        }

        case OpCodes.CONFIGURE_WINDOW: {
            
            final ConfigureWindow configureWindow = log(messageLength, opcode, sequenceNumber, ConfigureWindow.decode(stream));
            
            try {
                configureWindow(configureWindow);
            } catch (WindowException ex) {
                sendError(client, Errors.Window, sequenceNumber, ex.getWindow().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.GET_GEOMETRY: {
            
            final GetGeometry getGeometry = log(messageLength, opcode, sequenceNumber, GetGeometry.decode(stream));
            
            final DRAWABLE drawable = getGeometry.getDrawable();
            
            final WINDOW windowResource = drawable.toWindow();
            
            final WINDOW root;
            final int depth;
            
            final int x, y;
            final int width, height;
            final int borderWidth;

            final XWindow xWindow;
            final XPixmap xPixmap;
            
            try {
                if (null != (xWindow = xWindows.getClientOrRootWindow(windowResource))) {
                    root = xWindows.findRootWindowOf(xWindow.getWINDOW()).getWINDOW();
                    depth = xWindow.getDepth();
                    x = xWindow.getX();
                    y = xWindow.getY();
                    width = xWindow.getWidth();
                    height = xWindow.getHeight();
                    borderWidth = xWindow.getBorderWidth().getValue();
                }
                else if (null != (xPixmap = xPixmaps.getPixmap(drawable.toPixmap()))) {
                    
                    final XWindow pixmapXWindow = findPixmapWindow(xWindows, xPixmaps, drawable.toPixmap());
                    
                    root = xWindows.findRootWindowOf(pixmapXWindow.getWINDOW()).getWINDOW();
                    depth = xPixmap.getOffscreenSurface().getDepth();
                    x = 0;
                    y = 0;

                    final Size size = xPixmap.getOffscreenSurface().getSize();
                    
                    width = size.getWidth();
                    height = size.getHeight();
                    borderWidth = 0;
                }
                else {
                    throw new DrawableException("No such drawable", drawable);
                }
                
                final GetGeometryReply reply = new GetGeometryReply(
                        sequenceNumber,
                        new CARD8((byte)depth),
                        root,
                        new INT16((short)x), new INT16((short)y),
                        new CARD16(width), new CARD16(height),
                        new CARD16(borderWidth));
                
                sendReply(client, reply);
            }
            catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.QUERY_TREE: {
            final QueryTree queryTree = log(messageLength, opcode, sequenceNumber, QueryTree.decode(stream));
            
            final XWindow xWindow = xWindows.getClientOrRootWindow(queryTree.getWindow());
            
            if (xWindow == null) {
                sendError(client, Errors.Window, sequenceNumber, queryTree.getWindow().getValue(), opcode);
            }
            else {

                final List<Window> children = windowManagement.getSubWindowsInOrder(xWindow.getWindow());
                
                final QueryTreeReply reply = new QueryTreeReply(
                        sequenceNumber,
                        xWindow.isRootWindow() ? xWindow.getWINDOW() : xWindow.getRootWINDOW(),
                        xWindow.isRootWindow() ? WINDOW.None : xWindow.getParentWINDOW(),
                        children.stream()
                            .map(w -> xWindows.getClientWindow(w))
                            .map(xw -> xw.getWINDOW())
                            .collect(Collectors.toList()));
        
                sendReply(client, reply);
            }
            
            break;
        }

        case OpCodes.TRANSLATE_COORDINATES: {
            
            final TranslateCoordinates translateCoordinates = log(messageLength, opcode, sequenceNumber, TranslateCoordinates.decode(stream));
            
            try {
                
                final XWindow srcXWindow = xWindows.getClientOrRootWindow(translateCoordinates.getSrcWindow());
            
                if (srcXWindow == null) {
                    throw new WindowException("No src window", translateCoordinates.getSrcWindow());
                }
                
                final XWindow dstXWindow = xWindows.getClientOrRootWindow(translateCoordinates.getDstWindow());
                
                if (dstXWindow == null) {
                    throw new WindowException("No dst window", translateCoordinates.getDstWindow());
                }
                
                final int srcScreen = xWindows.getScreenForWindow(srcXWindow.getWINDOW());
                final int dstScreen = xWindows.getScreenForWindow(dstXWindow.getWINDOW());
            
                final TranslateCoordinatesReply reply;
                
                if (srcScreen == dstScreen) {
                    
                    final TranslatedCoordinates translatedCoordinates = windowManagement.translateCoordinates(
                            srcXWindow.getWindow(),
                            translateCoordinates.getSrcX().getValue(),
                            translateCoordinates.getSrcY().getValue());
                    
                    final Window foundWindow = translatedCoordinates.getWindow();

                    WINDOW subWindow = WINDOW.None;
                    
                    if (foundWindow != null) {
                        
                        final XWindow foundXWindow = xWindows.getClientWindow(foundWindow);
                        
                        if (foundXWindow != null) {
                            
                            for (XWindow parentXWindow = xWindows.getClientOrRootWindow(foundXWindow.getParentWINDOW());
                                    parentXWindow != null;
                                    parentXWindow = xWindows.getClientOrRootWindow(foundXWindow.getParentWINDOW())) {
                                
                                if (translateCoordinates.getDstWindow().equals(foundXWindow.getWINDOW())) {

                                    subWindow = foundXWindow.getWINDOW();
                                    break;
                                }
                            }
                        }
                    }
                    
                    reply = new TranslateCoordinatesReply(
                            sequenceNumber,
                            BOOL.True,
                            subWindow,
                            new INT16((short)translatedCoordinates.getX()),
                            new INT16((short)translatedCoordinates.getY()));
                }
                else {
                    reply = new TranslateCoordinatesReply(
                            sequenceNumber,
                            BOOL.False,
                            WINDOW.None,
                            new INT16((short)0),
                            new INT16((short)0));
                }
                
                sendReply(client, reply);
            }
            catch (WindowException ex) {
                sendError(client, Errors.Window, sequenceNumber, ex.getWindow().getValue(), opcode);
            }
            break;
        }
        
        }
    }
    
    private XWindow createWindow(CreateWindow createWindow, XWindow parentWindow, XClientOps client) throws ValueException, IDChoiceException, PixmapException, ColormapException, CursorException, AccessException {

        Objects.requireNonNull(createWindow);
        Objects.requireNonNull(parentWindow);
        
        final DRAWABLE drawable = createWindow.getWid().toDrawable();
        
        client.checkAndAddResourceId(drawable);
        
        checkWindowAttributes(createWindow.getAttributes());
        
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
        
        final XWindowAttributes xWindowAttributes = XWindowAttributes.DEFAULT_ATTRIBUTES.applyImmutably(createWindow.getAttributes());
        
        final WindowContentStorage windowContentStorage;
        
        switch (xWindowAttributes.getBackingStore().getValue()) {
        case BackingStore.NOT_USEFUL:
            windowContentStorage = WindowContentStorage.NONE;
            break;
            
        case BackingStore.WHEN_MAPPED:
            windowContentStorage = WindowContentStorage.WHEN_VISIBLE;
            break;
            
        case BackingStore.ALWAYS:
            windowContentStorage = WindowContentStorage.ALWAYS;
            break;
            
        default:
            throw new IllegalArgumentException();
        }
        
        final WindowParameters windowParameters = new WindowParameters(
                windowClass,
                createWindow.getDepth().getValue(),
                null,
                createWindow.getX().getValue(),
                createWindow.getY().getValue(),
                createWindow.getWidth().getValue(),
                createWindow.getHeight().getValue(),
                createWindow.getBorderWidth().getValue(),
                windowContentStorage);
        

        final Window window = windowManagement.createWindow(parentWindow.getWindow(), windowParameters, null);
        
        if (window == null) {
            throw new IllegalStateException();
        }
        
        final XWindow rootWindow = xWindows.findRootWindowOf(createWindow.getParent());
        
        final Surface windowSurface = compositor.allocateSurfaceForClientWindow(window);
        
        final XLibRenderer renderer = rendererFactory.createRenderer(windowSurface, window.getPixelFormat());
        
        final XWindow xWindow = new XClientWindow(
                client,
                window,
                createWindow.getWid(),
                rootWindow.getWINDOW(),
                createWindow.getParent(),
                parentWindow.getVisual(),
                createWindow.getBorderWidth(),
                createWindow.getWindowClass(),
                xWindowAttributes,
                renderer,
                windowSurface);
        
        subscribeToEvents(xWindow, xWindowAttributes, client);

        sendEventToSubscribing(
                eventSubscriptions,
                createWindow.getParent(),
                SETofEVENT.SUBSTRUCTURE_NOTIFY,
                clientOps -> new CreateNotify(
                        clientOps.getSequenceNumber(),
                        createWindow.getParent(),
                        createWindow.getWid(),
                        createWindow.getX(),
                        createWindow.getY(),
                        createWindow.getWidth(),
                        createWindow.getHeight(),
                        createWindow.getBorderWidth(),
                        getOverrideRedirect(createWindow.getAttributes())));
        
        return xWindow;
    }

    private void checkPixmap(PIXMAP pixmap) throws PixmapException {
        
        if (!pixmap.equals(PIXMAP.None) && !xPixmaps.hasPixmap(pixmap)) {
            throw new PixmapException("No such pixmap", pixmap);
        }
    }

    private void checkColormap(COLORMAP colormap) throws ColormapException {
        
        if (!colormap.equals(COLORMAP.None) && !xColormaps.hasColormap(colormap)) {
            throw new ColormapException("No such colormap", colormap);
        }
    }

    private void checkCursor(CURSOR cursor) throws CursorException {
        
        if (!cursor.equals(CURSOR.None) && !xCursors.hasCursor(cursor)) {
            throw new CursorException("No such cursor", cursor);
        }
    }

    private void checkWindowAttributes(XWindowAttributes requestAttributes) throws PixmapException, ColormapException, CursorException {

        if (requestAttributes.isSet(XWindowAttributes.BACKGROUND_PIXMAP)) {
            checkPixmap(requestAttributes.getBackgroundPixmap());
        }

        if (requestAttributes.isSet(XWindowAttributes.BORDER_PIXMAP)) {
            checkPixmap(requestAttributes.getBorderPixmap());
        }
        
        if (requestAttributes.isSet(XWindowAttributes.COLOR_MAP)) {
            checkColormap(requestAttributes.getColormap());
        }

        if (requestAttributes.isSet(XWindowAttributes.CURSOR)) {
            checkCursor(requestAttributes.getCursor());
        }
    }
    
    private void subscribeToEvents(XWindow xWindow, XWindowAttributes requestAttributes, XClientOps client) throws AccessException {

        if (requestAttributes.isSet(XWindowAttributes.EVENT_MASK)) {
            eventSubscriptions.setEventMapping(xWindow, requestAttributes.getEventMask(), client);
        }
    }
    
    private void changeWindowAttributes(ChangeWindowAttributes changeWindowAttributes, XClientOps client) throws WindowException, AccessException, PixmapException, ColormapException, CursorException {
        
        final XWindow xWindow = findClientOrRootWindow(xWindows, changeWindowAttributes.getWindow());
     
        final XWindowAttributes currentAttributes = xWindow.getCurrentWindowAttributes();

        final XWindowAttributes requestAttributes = changeWindowAttributes.getAttributes();
        
        final XWindowAttributes updatedAttributes = currentAttributes.applyImmutably(requestAttributes);

        checkWindowAttributes(requestAttributes);

        xWindow.setCurrentWindowAttributes(updatedAttributes);

        subscribeToEvents(xWindow, requestAttributes, client);
        
        /*

        if (xWindow.isMapped()) {

            final boolean sameBgMask = BITMASK.isSameMask(
                currentAttributes.getValueMask(),
                updatedAttributes.getValueMask(),
                XWindowAttributes.BACKGROUND_PIXEL|XWindowAttributes.BACKGROUND_PIXMAP);

            if (    !sameBgMask
                 || (    updatedAttributes.isSet(XWindowAttributes.BACKGROUND_PIXEL)
                      && !updatedAttributes.getBackgroundPixel().equals(currentAttributes.getBackgroundPixel()))
                 || (    updatedAttributes.isSet(XWindowAttributes.BACKGROUND_PIXMAP)
                      && !updatedAttributes.getBackgroundPixmap().equals(currentAttributes.getBackgroundPixmap()))
             ) {
    
                final BufferOperations windowBuffer = xWindow.getSurface();
    
                renderWindowBackground(updatedAttributes, xWindow.getWindow(), xWindow.getRenderer(), windowBuffer);
            }
        }
        */
    }

    void getWindowAttributes(GetWindowAttributes getWindowAttributes, int opcode, CARD16 sequenceNumber, XClientOps client) {

        final WINDOW window = getWindowAttributes.getWindow();
        
        final XWindow xWindow = xWindows.getClientOrRootWindow(window);
        
        if (xWindow == null) {
            sendError(client, Errors.Window, sequenceNumber, window.getValue(), opcode);
        }
        else {
            final XWindowAttributes curAttributes = xWindow.getCurrentWindowAttributes();

            final GetWindowAttributesReply reply = new GetWindowAttributesReply(
                    sequenceNumber,
                    curAttributes.getBackingStore(),
                    xWindow.getVisual(),
                    xWindow.getWindowClass(),
                    curAttributes.getBitGravity(), curAttributes.getWinGravity(),
                    curAttributes.getBackingPlanes(), curAttributes.getBackingPixel(),
                    curAttributes.getSaveUnder(),
                    new BOOL(true),
                    MapState.Viewable,
                    curAttributes.getOverrideRedirect(),
                    curAttributes.getColormap(),
                    new SETofEVENT(eventSubscriptions.getAllEventMasks(window)),
                    new SETofEVENT(eventSubscriptions.getYourEventMask(window, client)),
                    curAttributes.getDoNotPropagateMask());
            
            sendReply(client, reply);
        }
    }

    private XWindow destroyWindow(DestroyWindow destroyWindow, XClientOps client) {
        
        client.checkAndRemoveResourceId(destroyWindow.getWindow());
    
        final XWindow xWindow = xWindows.getClientWindow(destroyWindow.getWindow());
        
        if (xWindow != null) {
            
            if (!xWindow.isRootWindow()) {
                compositor.freeSurfaceForClientWindow(xWindow.getWindow(), xWindow.getSurface());
            }
            
            windowManagement.disposeWindow(xWindow.getWindow());
            
            xWindow.dispose();
        }
        
        return xWindow;
    }

    private static BOOL getOverrideRedirect(XWindowAttributes windowAttributes) {
        
        final BOOL overrideRedirect;
        
        if (windowAttributes.isSet(XWindowAttributes.OVERRIDE_REDIRECT)) {
            overrideRedirect = windowAttributes.getOverrideRedirect();
        }
        else {
            overrideRedirect = BOOL.False;
        }

        return overrideRedirect;
    }
    
    private void mapWindow(MapWindow mapWindow) throws WindowException {
        
        final XWindow xWindow = findClientWindow(xWindows, mapWindow.getWindow());
        
        mapWindow(xWindow);
    }

    private void mapWindow(XWindow xWindow) {
        
        if (!xWindow.isMapped()) {
        
            final XWindowAttributes windowAttributes = xWindow.getCurrentWindowAttributes();
            
            final BOOL overrideRedirect = getOverrideRedirect(windowAttributes);
            
            boolean sentMapRequest = false;
            
            if (!xWindow.isRootWindow()) {
            
                final XClientOps client = eventSubscriptions.getSingleClientInterestedInEvent(
                        xWindow.getParentWINDOW(),
                        SETofEVENT.SUBSTRUCTURE_REDIRECT);
            
                if (client != null && !overrideRedirect.isSet()) {
                    
                    final MapRequest mapRequest = new MapRequest(
                            client.getSequenceNumber(),
                            xWindow.getParentWINDOW(),
                            xWindow.getWINDOW());
                    
                    client.sendEvent(mapRequest);
                    
                    sentMapRequest = true;
                }
            }
            
            if (!sentMapRequest) {

                renderWindowBackground(xWindow);

                sendEventToSubscribing(
                        eventSubscriptions,
                        xWindow,
                        SETofEVENT.STRUCTURE_NOTIFY,
                        clientOps -> new MapNotify(
                                clientOps.getSequenceNumber(),
                                xWindow.getWINDOW(),
                                xWindow.getWINDOW(),
                                overrideRedirect));
                
                
                
                if (!xWindow.isRootWindow()) {
                    
                    sendEventToSubscribing(
                            eventSubscriptions,
                            xWindow.getParentWINDOW(), 
                            SETofEVENT.SUBSTRUCTURE_NOTIFY,
                            clientOps -> new MapNotify(
                                    clientOps.getSequenceNumber(),
                                    xWindow.getParentWINDOW(),
                                    xWindow.getWINDOW(),
                                    overrideRedirect));
                }

                final LayerRegion exposedRectangles = windowManagement.showWindow(xWindow.getWindow());
                
                if (exposedRectangles != null) {
                    
                    for (LayerRectangle rectangle : exposedRectangles.getRectangles()) {
                        sendEventToSubscribing(
                                eventSubscriptions,
                                xWindow,
                                SETofEVENT.EXPOSURE,
                                
                                clientOps -> new Expose(
                                        clientOps.getSequenceNumber(),
                                        xWindow.getWINDOW(),
                                        
                                        new CARD16(rectangle.getLeft()),
                                        new CARD16(rectangle.getTop()),
                                        new CARD16(rectangle.getWidth()),
                                        new CARD16(rectangle.getHeight()),
                                        
                                        new CARD16(exposedRectangles.getRectangles().size())
                                        ));
                    }
                }

                xWindow.setMapped(true);
            }
        }
    }
    
    private void configureWindow(ConfigureWindow configureWindow) throws WindowException {
     
        final XWindow xWindow = findClientWindow(xWindows, configureWindow.getWindow());
        
        Integer updatedX = null;
        Integer updatedY = null;
        
        Integer updatedWidth = null;
        Integer updatedHeight = null;
        
        final XWindowConfiguration windowConfiguration = configureWindow.getConfiguration();
        
        if (windowConfiguration.isSet(XWindowConfiguration.X)) {
            updatedX = (int)windowConfiguration.getX().getValue();
        }
        
        if (windowConfiguration.isSet(XWindowConfiguration.Y)) {
            updatedY = (int)windowConfiguration.getY().getValue();
        }
        
        if (windowConfiguration.isSet(XWindowConfiguration.WIDTH)) {
            updatedWidth = (int)windowConfiguration.getWidth().getValue();
        }
        
        if (windowConfiguration.isSet(XWindowConfiguration.HEIGHT)) {
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
        
        if (windowConfiguration.isSet(XWindowConfiguration.BORDER_WIDTH)) {
            
            xWindow.setBorderWidth(windowConfiguration.getBorderWidth());
            
            System.out.println("TODO - configure borderwidth");
        }
    }
}
