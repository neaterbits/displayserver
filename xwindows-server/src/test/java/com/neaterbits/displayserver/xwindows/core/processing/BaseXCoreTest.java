package com.neaterbits.displayserver.xwindows.core.processing;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.io.common.DataWriter;
import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.BackingStore;
import com.neaterbits.displayserver.protocol.enums.WindowClass;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLogImpl;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.server.XClientWindows;
import com.neaterbits.displayserver.server.XConfig;
import com.neaterbits.displayserver.server.XTimestampGenerator;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.util.logging.DebugLevel;
import com.neaterbits.displayserver.windows.DisplayArea;
import com.neaterbits.displayserver.windows.WindowEventListener;
import com.neaterbits.displayserver.windows.WindowManagement;
import com.neaterbits.displayserver.windows.WindowsDisplayAreaImpl;
import com.neaterbits.displayserver.windows.compositor.Compositor;
import com.neaterbits.displayserver.windows.compositor.Surface;
import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;
import com.neaterbits.displayserver.xwindows.fonts.FontLoaderConfig;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;
import com.neaterbits.displayserver.xwindows.model.XPixmaps;
import com.neaterbits.displayserver.xwindows.model.XScreen;
import com.neaterbits.displayserver.xwindows.model.XScreenDepth;
import com.neaterbits.displayserver.xwindows.model.XScreens;
import com.neaterbits.displayserver.xwindows.model.XVisuals;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;
import com.neaterbits.displayserver.xwindows.model.render.XLibRendererFactory;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private final WindowEventListener windowEventListener;
    protected final Surface rootSurface;
    private final XLibRenderer rootRenderer;
    protected final XClientOps client;
    
    protected final PixelFormat rootPixelFormat;
    
    private int resourceIds;
    private int sequenceNumber;
    
    protected BaseXCoreTest() {
    
        final XClientWindows windows = new XClientWindows();
        final XPixmaps pixmaps = new XPixmaps();
        
        this.compositor = mock(Compositor.class);
        this.rendererFactory = mock(XLibRendererFactory.class);
        this.fontBufferFactory = mock(FontBufferFactory.class);

        this.timestampGenerator = mock(XTimestampGenerator.class);
        
        this.inputDriver = mock(InputDriver.class);
        
        final XConfig config = new XConfig(
                new DisplayAreaConfig(0, Collections.emptyList()),
                new FontLoaderConfig(Collections.emptyList()),
                "/usr/share/X11/rgb.txt");
    
        this.resourceIds = 1;
        this.sequenceNumber = 1;
        
        final WINDOW rootWindowResource = new WINDOW(allocateResourceId());
    
        this.rootSurface = mock(Surface.class);
    
        rootRenderer = mock(XLibRenderer.class);

        final VISUALID rootVisual = new VISUALID(allocateResourceId());

        final Size displaySize = new Size(1280, 1024);

        this.rootPixelFormat = PixelFormat.RGB24;
        
        final int rootDepth = rootPixelFormat.getDepth();
        
        this.displayArea = mock(DisplayArea.class);
        this.windowEventListener = mock(WindowEventListener.class);

        Mockito.when(displayArea.getDepth()).thenReturn(rootDepth);
        Mockito.when(displayArea.getSize()).thenReturn(displaySize);
        Mockito.when(displayArea.getPixelFormat()).thenReturn(rootPixelFormat);

        final WindowsDisplayAreaImpl windowsDisplayArea = new WindowsDisplayAreaImpl(displayArea, windowEventListener);
        
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
                rootWindowResource,
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
                new XScreens(Collections.emptyList()),
                new XVisuals(Collections.emptyMap()),
                windows,
                pixmaps,
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

    private int allocateResourceId() {
        return resourceIds ++;
    }
    
    private void sendMessage(Request message) {
        
        final DataWriter dataWriter = Encodeable.makeDataWriter(message);
        
        final ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        
        final byte [] encoded = DataWriter.writeToBuf(dataWriter, byteOrder);

        final ByteBuffer byteBuffer = ByteBuffer.wrap(encoded);
        
        byteBuffer.order(byteOrder);
        
        final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
        
        try {
            // read opcode
            final BYTE opcode = stream.readBYTE();

            assertThat((int)opcode.getValue()).isEqualTo(message.getOpCode());
            
            coreModule.processMessage(
                    stream,
                    encoded.length,
                    message.getOpCode(),
                    new CARD16(sequenceNumber ++),
                    client);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    protected final WINDOW createWindow(Position position, Size size) {
        
        return createWindow(position.getLeft(), position.getTop(), size.getWidth(), size.getHeight());
    }

    protected final WINDOW createWindow(int x, int y, int width, int height) {

        final WINDOW window = new WINDOW(allocateResourceId());
        
        final CreateWindow createWindow = new CreateWindow(
                new CARD8((short)0),
                window,
                screen.getRootWINDOW(),
                new INT16((short)x), new INT16((short)y),
                new CARD16(width), new CARD16(height),
                new CARD16(0),
                new CARD16(1),
                screen.getRootVisual(),
                new XWindowAttributes(
                        new BITMASK(
                                  XWindowAttributes.BACKING_STORE
                        //        | WindowAttributes.EVENT_MASK
                        ),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        BackingStore.WhenMapped,
                        null,
                        null,
                        null,
                        null,
                        new SETofEVENT(
                                  SETofEVENT.KEY_PRESS
                                | SETofEVENT.KEY_RELEASE
                                | SETofEVENT.BUTTON_PRESS
                                | SETofEVENT.BUTTON_RELEASE
                                | SETofEVENT.POINTER_MOTION
                                | SETofEVENT.STRUCTURE_NOTIFY),
                        null,
                        null,
                        null));

        sendMessage(createWindow);
        
        return window;
    }

    protected final void closeWindow(WINDOW window) {
        
        sendMessage(new DestroyWindow(window));
        
    }

    protected final void verifyNoMoreInteractions() {

        Mockito.verifyNoMoreInteractions(
                compositor,
                rendererFactory,
                fontBufferFactory,
                timestampGenerator,
                inputDriver,
                displayArea,
                windowEventListener,
                rootSurface,
                rootRenderer,
                client);
    }
}
