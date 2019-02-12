package com.neaterbits.displayserver.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsNetwork;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsNetworkFactory;
import com.neaterbits.displayserver.driver.xwindows.common.messaging.XCBXWindowsNetwork;
import com.neaterbits.displayserver.events.xwindows.XWindowsInputDriver;
import com.neaterbits.displayserver.framebuffer.common.Alignment;
import com.neaterbits.displayserver.framebuffer.common.DisplayDeviceId;
import com.neaterbits.displayserver.framebuffer.xwindows.XWindowsGraphicsDriver;
import com.neaterbits.displayserver.io.common.AsyncServers;
import com.neaterbits.displayserver.io.common.AsyncServersLog;
import com.neaterbits.displayserver.io.common.AsyncServersLogImpl;
import com.neaterbits.displayserver.io.common.MessageProcessor;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLogImpl;
import com.neaterbits.displayserver.io.common.SelectableLog;
import com.neaterbits.displayserver.io.common.SelectableLogImpl;
import com.neaterbits.displayserver.protocol.logging.XWindowsClientProtocolLog;
import com.neaterbits.displayserver.protocol.logging.XWindowsClientProtocolLogImpl;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLogImpl;
import com.neaterbits.displayserver.render.cairo.xcb.XCBConnection;
import com.neaterbits.displayserver.server.XConfig;
import com.neaterbits.displayserver.server.XHardware;
import com.neaterbits.displayserver.server.XRendering;
import com.neaterbits.displayserver.server.XServer;
import com.neaterbits.displayserver.server.render.cairo.CairoFontBufferFactory;
import com.neaterbits.displayserver.server.render.cairo.CairoWrapperFontBufferFactory;
import com.neaterbits.displayserver.server.render.cairo.CairoXLibRendererFactory;
import com.neaterbits.displayserver.util.logging.DebugLevel;
import com.neaterbits.displayserver.windows.DisplayAreas;
import com.neaterbits.displayserver.windows.compositor.Compositor;
import com.neaterbits.displayserver.windows.compositor.SingleViewPortDirectCompositor;
import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;
import com.neaterbits.displayserver.windows.config.DisplayConfig;
import com.neaterbits.displayserver.xwindows.fonts.FontLoaderConfig;
import com.neaterbits.displayserver.xwindows.fonts.model.StoreOrder;
import com.neaterbits.displayserver.xwindows.util.JNIBindings;
import com.neaterbits.displayserver.xwindows.util.XAuth;

public class DisplayServerMain {

	public static void main(String [] args) throws Exception {

	    final int display;
	    
	    if (args.length == 1) {
	        display = Integer.parseInt(args[0]);
	    }
	    else {
	        display = 0;
	    }

	    JNIBindings.load();
	    
	    final AsyncServersLog asyncServersLog = new AsyncServersLogImpl("Asyncservers", DebugLevels.ASYNC_SERVERS);
	    
	    final SelectableLog connectionReadLog = new SelectableLogImpl("Connectionread", DebugLevels.CONNECTION_READ);
	    
	    final SelectorProvider selectorProvider = SelectorProvider.provider();
	    
		try (AsyncServers asyncServers = new AsyncServers(asyncServersLog, connectionReadLog, selectorProvider)) {
		    
	        final XAuth xAuthForTCPConnection = XAuth.getXAuthInfo(display, "MIT-MAGIC-COOKIE-1");

	        final XCBConnection xcbConnection = XCBConnection.connect(
	                ":" + display,
	                xAuthForTCPConnection.getAuthorizationProtocol(),
	                xAuthForTCPConnection.getAuthorizationData());
	        
            /*
            final NonBlockingChannelWriterLog driverWriteLog = new NonBlockingChannelWriterLogImpl(
                    "Driverwrite",
                    DebugLevels.DRIVER_WRITE);

            final InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 6000 + display);

		    final XWindowsNetworkFactory xWindowsNetworkFactory = new XWindowsNetworkFactory() {
                @Override
                public XWindowsNetwork connect(MessageProcessor listener) throws IOException {
                    
                    final NonBlockingXWindowsNetwork network = new NonBlockingXWindowsNetwork(
                            socketAddress,
                            xAuthForTCPConnection,
                            driverWriteLog,
                            listener);
                    
                
                    final String name = "Driverevents";
                    
                    asyncServers.addSelectable(
                               name,
                               network.getSelectable(),
                               network.getMessageProcessor(),
                               new SelectableLogImpl(name, DebugLevels.DRIVER_READ));
                    
                    return network;
                }
            };
            */

            final XWindowsNetworkFactory xcbNetworkFactory = new XWindowsNetworkFactory() {
                
                @Override
                public XWindowsNetwork connect(MessageProcessor listener) throws IOException {
                    return new XCBXWindowsNetwork(xcbConnection);
                }
            };
		    
		    final XWindowsClientProtocolLog driverProtocolLog = new XWindowsClientProtocolLogImpl("driver", DebugLevel.DEBUG);
		    
			try (XWindowsDriverConnection driverConnection = new XWindowsDriverConnection(
			        display,
			        xcbConnection,
			        // xWindowsNetworkFactory,
			        xcbNetworkFactory,
			        driverProtocolLog)) {

	             System.out.println("## start check for IO");

	             final DisplayDeviceId displayDeviceId = new DisplayDeviceId("XWindows", Alignment.CENTER);

	             final XHardware hardware = initDriver(asyncServers, driverConnection, displayDeviceId);
			    
	             initXWindows(display, asyncServers, displayDeviceId, driverConnection, hardware);
			}
		}
	}
	
	
	private static XHardware initDriver(
	        AsyncServers asyncServers,
	        XWindowsDriverConnection driverConnection,
	        DisplayDeviceId displayDeviceId) throws IOException {

        while (driverConnection.getServerMessage() == null) {
            asyncServers.checkForIO(-1L);
        }

        System.out.println("## done check for IO");

        final XWindowsInputDriver inputDriver = new XWindowsInputDriver(driverConnection);
        final XWindowsGraphicsDriver graphicsDriver = new XWindowsGraphicsDriver(driverConnection, displayDeviceId);
	 
        while (!inputDriver.isInitialized() || !graphicsDriver.isInitialized()) {
            asyncServers.checkForIO(-1L);
        }

        final XHardware hardware = new XHardware(inputDriver, graphicsDriver);

        return hardware;
	}
	
	private static void initXWindows(
	        int display,
	        AsyncServers asyncServers,
	        DisplayDeviceId displayDeviceId,
	        XWindowsDriverConnection driverConnection,
	        XHardware hardware) throws Exception {

        final DisplayConfig displayConfig = new DisplayConfig(displayDeviceId, Alignment.CENTER);
        final DisplayAreaConfig displayAreaConfig = new DisplayAreaConfig(
                1, Arrays.asList(displayConfig));

        final DisplayAreas displayAreas = DisplayAreas.from(displayAreaConfig, hardware.getGraphicsDriver());
        
        if (displayAreas.getDisplayAreas().size() != 1) {
            throw new IllegalStateException();
        }
        
        final Compositor compositor = new SingleViewPortDirectCompositor(displayAreas.getDisplayAreas().get(0));

        final XConfig config = new XConfig(
                displayAreaConfig,
                new FontLoaderConfig(Arrays.asList("/usr/share/fonts/X11/misc")),
                "/usr/share/X11/rgb.txt");
                
        startXServer(display, asyncServers, hardware, config, displayAreas, compositor);
    
        if (driverConnection.isPolling()) {
            for (;;) {
                asyncServers.waitForIO(100L);
                
                hardware.getInputDriver().pollForEvents();
            }
        }
        else {
            asyncServers.waitForIO(-1L);
        }
	}
	
	private static void startXServer(
	        int display,
	        AsyncServers asyncServers,
	        XHardware hardware,
	        XConfig config,
	        DisplayAreas displayAreas,
	        Compositor compositor) throws IOException, Exception {
	    
        final NonBlockingChannelWriterLog connectionWriteLog = new NonBlockingChannelWriterLogImpl(
                "Connectionwrite",
                DebugLevels.CONNECTION_WRITE);
        
        final XWindowsServerProtocolLog protocolLog = new XWindowsServerProtocolLogImpl("XWindowsProtocol", DebugLevels.XWINDOWS_PROTOCOL);

        
        final StoreOrder nativeOrder = StoreOrder.getNativeOrder();
        
        final XRendering rendering = new XRendering(
                displayAreas,
                compositor,
                new CairoXLibRendererFactory(),
                new CairoWrapperFontBufferFactory(new CairoFontBufferFactory(nativeOrder)));
        
        try (XServer server = new XServer(
                hardware,
                config,
                rendering,
                protocolLog,
                connectionWriteLog)) {
            
            asyncServers.addServer(
                    ":1",
                    new SocketAddress [] {
                        new InetSocketAddress("127.0.0.1", 6000 + display + 1)
                    },
                    (socketChannel, selectionKey) -> server.processConnection(socketChannel, selectionKey));
        }
	}
}
