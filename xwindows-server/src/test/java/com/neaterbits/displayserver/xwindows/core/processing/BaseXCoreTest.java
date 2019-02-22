package com.neaterbits.displayserver.xwindows.core.processing;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.io.common.DataWriter;
import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.VisualClass;
import com.neaterbits.displayserver.protocol.enums.WindowClass;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLogImpl;
import com.neaterbits.displayserver.protocol.messages.XEncodeable;
import com.neaterbits.displayserver.protocol.messages.XError;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.server.XClientCloseHandler;
import com.neaterbits.displayserver.server.XClientWindows;
import com.neaterbits.displayserver.server.XConfig;
import com.neaterbits.displayserver.server.XEventSubscriptions;
import com.neaterbits.displayserver.server.XFocusState;
import com.neaterbits.displayserver.server.XInputEventHandlerConstAccess;
import com.neaterbits.displayserver.server.XTimestampGenerator;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.util.logging.DebugLevel;
import com.neaterbits.displayserver.windows.DisplayArea;
import com.neaterbits.displayserver.windows.WindowManagement;
import com.neaterbits.displayserver.windows.WindowsDisplayAreaImpl;
import com.neaterbits.displayserver.windows.compositor.Compositor;
import com.neaterbits.displayserver.windows.compositor.Surface;
import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;
import com.neaterbits.displayserver.xwindows.fonts.FontLoaderConfig;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;
import com.neaterbits.displayserver.xwindows.model.XColormaps;
import com.neaterbits.displayserver.xwindows.model.XCursors;
import com.neaterbits.displayserver.xwindows.model.XPixmaps;
import com.neaterbits.displayserver.xwindows.model.XScreen;
import com.neaterbits.displayserver.xwindows.model.XScreenDepth;
import com.neaterbits.displayserver.xwindows.model.XScreens;
import com.neaterbits.displayserver.xwindows.model.XVisual;
import com.neaterbits.displayserver.xwindows.model.XVisuals;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;
import com.neaterbits.displayserver.xwindows.model.render.XLibRendererFactory;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class BaseXCoreTest {

    private final XCoreModule coreModule;
    private final XScreen screen;
    
    private final WindowManagement windowManagement;
    protected final Compositor compositor;
    protected final XLibRendererFactory rendererFactory;
    private final FontBufferFactory fontBufferFactory;
    private final XTimestampGenerator timestampGenerator;
    private final InputDriver inputDriver;
    protected final DisplayArea displayArea;
    protected final Surface rootSurface;
    private final XLibRenderer rootRenderer;
    protected final XClientOps client;
    protected final WINDOW rootWindow;
    
    protected final PixelFormat rootPixelFormat;
    private final Size displaySize;

    private int resourceIds;
    private int sequenceNumber;
    
    protected BaseXCoreTest() {
    
        final XClientWindows windows = new XClientWindows();
        final XPixmaps pixmaps = new XPixmaps();
        final XColormaps colormaps = new XColormaps();
        final XCursors cursors = new XCursors();
        
        this.compositor = mock(Compositor.class);
        this.rendererFactory = mock(XLibRendererFactory.class);
        this.fontBufferFactory = mock(FontBufferFactory.class);

        this.timestampGenerator = mock(XTimestampGenerator.class);
        
        this.inputDriver = mock(InputDriver.class);
        
        final XConfig config = new XConfig(
                new DisplayAreaConfig(0, Collections.emptyList()),
                new FontLoaderConfig(Arrays.asList("/usr/share/fonts/X11/misc")),
                "/usr/share/X11/rgb.txt");
    
        this.displaySize = new Size(1280, 1024);
        
        this.resourceIds = 1;
        this.sequenceNumber = 0;
        
        this.rootWindow = new WINDOW(allocateResourceId());
    
        this.rootSurface = mock(Surface.class);
    
        rootRenderer = mock(XLibRenderer.class);

        final VISUALID rootVisual = new VISUALID(allocateResourceId());

        final XVisual xRootVisual = new XVisual(
                VisualClass.TRUECOLOR,
                8,
                1 << 24,
                0x00FF0000,
                0x0000FF00,
                0x000000FF);
        
        final Map<VISUALID, XVisual> visualsMap = new HashMap<>();
        
        visualsMap.put(rootVisual, xRootVisual);
        
        final Size displaySize = new Size(1280, 1024);

        this.rootPixelFormat = PixelFormat.RGB24;
        
        final int rootDepth = rootPixelFormat.getDepth();
        
        this.displayArea = mock(DisplayArea.class);

        Mockito.when(displayArea.getDepth()).thenReturn(rootDepth);
        Mockito.when(displayArea.getSize()).thenReturn(displaySize);
        Mockito.when(displayArea.getPixelFormat()).thenReturn(rootPixelFormat);

        final WindowsDisplayAreaImpl windowsDisplayArea = new WindowsDisplayAreaImpl(displayArea);
        
        this.windowManagement = windowsDisplayArea;
        
        final XWindowAttributes windowAttributes = new XWindowAttributes(
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
                null);
        
        final XWindow xRootWindow = new XWindow(
                windowsDisplayArea.getRootWindow(),
                rootWindow,
                rootVisual,
                new CARD16(0),
                WindowClass.InputOutput,
                windowAttributes,
                rootRenderer,
                rootSurface);
        
        final List<XScreenDepth> depths = Arrays.asList(
                new XScreenDepth(rootDepth, Arrays.asList(rootVisual))
        );
        
        this.screen = new XScreen(
                0,
                windowsDisplayArea,
                xRootWindow,
                rootVisual,
                depths);
        
        windows.addRootWindow(screen.getScreenNo(), xRootWindow);
        
        this.client = mock(XClientOps.class);
        
        try {
            this.coreModule = new XCoreModule(
                new XWindowsServerProtocolLogImpl("test", DebugLevel.TRACE),
                windowManagement,
                new XScreens(Arrays.asList(screen)),
                new XVisuals(visualsMap),
                windows,
                pixmaps,
                colormaps,
                cursors,
                new XEventSubscriptions(),
                mock(XClientCloseHandler.class),
                mock(XInputEventHandlerConstAccess.class),
                new XFocusState(),
                new HashSet<>(),
                compositor,
                rendererFactory,
                fontBufferFactory,
                timestampGenerator,
                inputDriver,
                config);
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        
        Mockito.verify(displayArea, Mockito.atLeastOnce()).getDepth();
        Mockito.verify(displayArea, Mockito.atLeastOnce()).getSize();
        // Mockito.verify(displayArea, Mockito.atLeastOnce()).getPixelFormat();
        
        Mockito.verifyNoMoreInteractions(Mockito.ignoreStubs(displayArea));
    }

    protected final VISUALID getRootVisual() {
        return screen.getRootVisual();
    }
    
    protected final int allocateResourceId() {
        return resourceIds ++;
    }

    protected final int getRootDepthAsInt() {
        return rootPixelFormat.getDepth();
    }
    
    protected final int getRootWidth() {
        return displaySize.getWidth();
    }
    
    protected final int getRootHeight() {
        return displaySize.getHeight();
    }
    
    protected final CARD8 getRootDepth() {
        return new CARD8((byte)getRootDepthAsInt());
    }
    
    protected final void sendRequest(XRequest request) {
        
        final DataWriter dataWriter = XEncodeable.makeDataWriter(request);
        
        final ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        
        final byte [] encoded = DataWriter.writeToBuf(dataWriter, byteOrder);

        final ByteBuffer byteBuffer = ByteBuffer.wrap(encoded);
        
        byteBuffer.order(byteOrder);
        
        final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
        
        try {
            // read opcode
            final BYTE opcode = stream.readBYTE();

            assertThat((int)opcode.getValue()).isEqualTo(request.getOpCode());
            
            coreModule.processMessage(
                    stream,
                    encoded.length,
                    request.getOpCode(),
                    new CARD16(++ sequenceNumber),
                    client);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    protected final WINDOW createWindow(Position position, Size size, int borderWidth) {
        return createWindow(position, size, borderWidth, null, null);
    }

    protected final WINDOW createWindow(Position position, Size size, int borderWidth, XWindowAttributes windowAttributes, WINDOW w) {

        final WINDOW window = new WINDOW(allocateResourceId());
        
        final CreateWindow createWindow = new CreateWindow(
                new CARD8((short)0),
                window,
                w != null ? w : screen.getRootWINDOW(),
                new INT16((short)position.getLeft()), new INT16((short)position.getTop()),
                new CARD16(size.getWidth()), new CARD16(size.getHeight()),
                new CARD16(borderWidth),
                WindowClass.InputOutput,
                screen.getRootVisual(),
                windowAttributes != null ? windowAttributes : new XWindowAttributes(
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

        sendRequest(createWindow);
        
        return window;
    }

    protected final <T extends XReply> T expectReply(Class<T> replyClass) {
        
        final ArgumentCaptor<T> argumentCaptor = ArgumentCaptor.forClass(replyClass);
        
        Mockito.verify(client).sendReply(argumentCaptor.capture());
    
        return argumentCaptor.getValue();
    }

    protected final <T extends XEvent> void whenEvent(Class<T> eventClass) {
        
        Mockito.when(client.getSequenceNumber()).thenReturn(new CARD16(sequenceNumber));
        
    }

    protected final <T extends XEvent> T expectEvent(Class<T> eventClass) {

        final ArgumentCaptor<T> argumentCaptor = ArgumentCaptor.forClass(eventClass);

        Mockito.verify(client).getSequenceNumber();
        Mockito.verify(client).sendEvent(argumentCaptor.capture());
    
        return argumentCaptor.getValue();
    }

    protected final XError expectError() {
        
        final ArgumentCaptor<XError> argumentCaptor = ArgumentCaptor.forClass(XError.class);
        
        Mockito.verify(client).sendError(argumentCaptor.capture());
    
        return argumentCaptor.getValue();
    }

    protected final void closeWindow(WINDOW window) {
        
        sendRequest(new DestroyWindow(window));
        
    }

    protected final void verifyNoMoreInteractions() {

        Mockito.verifyNoMoreInteractions(
                compositor,
                rendererFactory,
                fontBufferFactory,
                timestampGenerator,
                inputDriver,
                displayArea,
                rootSurface,
                rootRenderer,
                client);
    }
}
