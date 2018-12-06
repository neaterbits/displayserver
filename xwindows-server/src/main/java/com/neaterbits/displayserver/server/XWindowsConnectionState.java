package com.neaterbits.displayserver.server;

import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.neaterbits.displayserver.buffers.ImageBuffer;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.framebuffer.common.GraphicsScreen;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriter;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.NonBlockingWritable;
import com.neaterbits.displayserver.layers.Rectangle;
import com.neaterbits.displayserver.layers.Region;
import com.neaterbits.displayserver.protocol.DataOutputXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.OpCodes;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.messages.events.GraphicsExposure;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreatePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.FreePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.RESOURCE;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Display;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.WindowClass;
import com.neaterbits.displayserver.windows.WindowEventListener;
import com.neaterbits.displayserver.windows.WindowParameters;

public class XWindowsConnectionState
    extends NonBlockingChannelWriter
    implements WindowEventListener, NonBlockingWritable, AutoCloseable {

	enum State {
		
		CREATED,
		INITIAL_RECEIVED,
        INITIAL_ERROR,
		CONNECTED;
	}
	
	private final XWindowsProtocolServer server;
	private final SocketChannel socketChannel;
	private final int connectionNo;
	
	private State state;
	private ByteOrder byteOrder;
	
	private final Set<Integer> utilizedResourceIds;
	
	private final Map<DRAWABLE, XWindowsWindow> drawableToWindow;
	private final Map<XWindowsWindow, DRAWABLE> windowToDrawable;
	
	private final Map<DRAWABLE, ImageBuffer> drawableToImageBuffer;

	private final Map<DRAWABLE, DRAWABLE> pixmapToDrawable;
	
	private final List<Event> events;
	
	private int sequenceNumber;
	
	XWindowsConnectionState(XWindowsProtocolServer server, SocketChannel socketChannel, int connectionNo, NonBlockingChannelWriterLog log) {
	    super(log);
	    
		Objects.requireNonNull(server);
		Objects.requireNonNull(socketChannel);
		
		this.server = server;
		this.socketChannel = socketChannel;
		this.connectionNo = connectionNo;
		
		this.state = State.CREATED;
	
		this.utilizedResourceIds = new HashSet<>();
		
		this.drawableToWindow = new HashMap<>();
		this.windowToDrawable = new HashMap<>();
	
		this.drawableToImageBuffer = new HashMap<>();
		
		this.pixmapToDrawable = new HashMap<>();
		
		this.events = new ArrayList<>();
		
		this.sequenceNumber = 0;
	}
	
	CARD16 increaseSequenceNumber() {
	    if (sequenceNumber == 65535) {
	        sequenceNumber = 0;
	    }
	    else {
	        ++ sequenceNumber;
	    }
	
	    return new CARD16(sequenceNumber);
	}
	
	int getConnectionNo() {
        return connectionNo;
    }
	
    State getState() {
        return state;
    }
    

    void setByteOrder(ByteOrder byteOrder) {
    
        Objects.requireNonNull(byteOrder);
        
        if (this.byteOrder != null) {
            throw new IllegalStateException();
        }
        
        this.byteOrder = byteOrder;
    }

    void setState(State state) {
        
        Objects.requireNonNull(state);
        
        this.state = state;
    }

	final XWindowsWindow getWindow(WINDOW window) {

		final XWindowsWindow result;
		
		if (server.isRootWindow(window)) {
			result = server.getRootWindow(window);
		}
		else {
			result = drawableToWindow.get(window.toDrawable());
		}
		
		return result;
	}
	
	private void checkAndAddResourceId(RESOURCE resource) throws IDChoiceException {
		if (utilizedResourceIds.contains(resource.getValue())) {
			throw new IDChoiceException("Already utilized");
		}
		
		utilizedResourceIds.add(resource.getValue());
	}
	
	private void checkAndRemoveResourceId(RESOURCE resource) {
		utilizedResourceIds.remove(resource.getValue());
	}
	
	final XWindowsWindow createWindow(Display display, CreateWindow createWindow) throws ValueException, IDChoiceException {
		
		final DRAWABLE drawable = createWindow.getWid().toDrawable();
		
		checkAndAddResourceId(drawable);
		
		final WindowClass windowClass;

		final XWindowsWindow parentWindow = getWindow(createWindow.getParent());
		
		if (parentWindow == null) {
			throw new ValueException("Unknown parent window");
		}
		
		switch (createWindow.getWindowClass().getValue()) {
		case 0:
			windowClass = parentWindow.getWindow().getParameters().getWindowClass();
			break;
			
		case 1:
			windowClass = WindowClass.INPUT_OUTPUT;
			break;
			
		case 2:
			windowClass = WindowClass.INPUT_ONLY;
			break;
			
		default:
			throw new ValueException("Unknown window class");
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
		
		final XWindowsWindow xWindowsWindow = new XWindowsWindow(window, createWindow.getWindowClass(), createWindow.getAttributes());
		
		drawableToWindow.put(drawable, xWindowsWindow);
		windowToDrawable.put(xWindowsWindow, drawable);
		
		return xWindowsWindow;
	}
	
	final Window destroyWindow(Display display, DestroyWindow destroyWindow) {
		checkAndRemoveResourceId(destroyWindow.getWindow());
	
		final XWindowsWindow window = drawableToWindow.remove(destroyWindow.getWindow().toDrawable());
		
		if (window != null) {
			windowToDrawable.remove(window);

			display.disposeWindow(window.getWindow());
		}
		
		return window.getWindow();
	}
	
	private GraphicsScreen findGraphicsScreen(DRAWABLE drawable) {
	    
	    XWindowsWindow window = drawableToWindow.get(drawable);
	    
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
	    
	    final XWindowsWindow window = drawableToWindow.get(putImage.getDrawable());
	    
	    if (window != null) {
	        
	    }
	    else {
	        final ImageBuffer imageBuffer = drawableToImageBuffer.get(putImage.getDrawable());
	        
	        if (imageBuffer != null) {
	            
	        }
	    }
	}

	final void send(Encodeable message) {
	    
	    write(byteOrder, dataOutputStream -> {
            final XWindowsProtocolOutputStream protocolOutputStream = new DataOutputXWindowsProtocolOutputStream(dataOutputStream);
            
            message.encode(protocolOutputStream);
	    });
	}
	
    @Override
    protected final SocketChannel getChannel(SelectionKey selectionKey, Selector selector) {
        return socketChannel;
    }

    @Override
	public final void onUpdate(Window window, Region region) {
		
		Objects.requireNonNull(window);
		Objects.requireNonNull(region);
		
		final DRAWABLE drawable = windowToDrawable.get(window);
		
		if (drawable == null) {
			throw new IllegalStateException();
		}
		
		int count = region.getRectangles().size();
		
		for (Rectangle rectangle : region.getRectangles()) {
			
			-- count;
			
			final GraphicsExposure graphicsExposure = new GraphicsExposure(
					drawable,
					new CARD16(rectangle.getLeft()), new CARD16(rectangle.getTop()),
					new CARD16(rectangle.getWidth()), new CARD16(rectangle.getHeight()),
					new CARD16(count),
					new CARD8((byte)OpCodes.COPY_AREA), new CARD16(0));
			
			addEvent(graphicsExposure);
		}
	}
	
	private void addEvent(Event event) {
		Objects.requireNonNull(event);
	
		events.add(event);
	}

    @Override
    public final void close() throws Exception {
        socketChannel.close();
    }
}
