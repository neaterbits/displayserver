package com.neaterbits.displayserver.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.events.xwindows.XWindowsInputDriver;
import com.neaterbits.displayserver.framebuffer.common.Alignment;
import com.neaterbits.displayserver.framebuffer.common.DisplayDeviceId;
import com.neaterbits.displayserver.framebuffer.xwindows.XWindowsGraphicsDriver;
import com.neaterbits.displayserver.io.common.AsyncServers;
import com.neaterbits.displayserver.io.common.AsyncServersLog;
import com.neaterbits.displayserver.io.common.AsyncServersLogImpl;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLogImpl;
import com.neaterbits.displayserver.io.common.SelectableLog;
import com.neaterbits.displayserver.io.common.SelectableLogImpl;
import com.neaterbits.displayserver.protocol.logging.XWindowsClientProtocolLog;
import com.neaterbits.displayserver.protocol.logging.XWindowsClientProtocolLogImpl;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLogImpl;
import com.neaterbits.displayserver.server.XConfig;
import com.neaterbits.displayserver.server.XHardware;
import com.neaterbits.displayserver.server.XRendering;
import com.neaterbits.displayserver.server.XServer;
import com.neaterbits.displayserver.server.render.cairo.CairoXLibRendererFactory;
import com.neaterbits.displayserver.util.logging.DebugLevel;
import com.neaterbits.displayserver.windows.compositor.Compositor;
import com.neaterbits.displayserver.windows.compositor.SingleViewPortCompositor;
import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;
import com.neaterbits.displayserver.windows.config.DisplayConfig;

public class DisplayServerMain {

	public static void main(String [] args) throws Exception {

	    final String rootDir = System.getenv("HOME") + "/projects/displayserver";
	    
	    System.load(rootDir + "/native-xcb/Debug/libxcbjni.so");
	    System.load(rootDir + "/native-cairo/Debug/libcairojni.so");
        
	    final int display;
	    
	    if (args.length == 1) {
	        display = Integer.parseInt(args[0]);
	    }
	    else {
	        display = 0;
	    }
	    
	    final AsyncServersLog asyncServersLog = new AsyncServersLogImpl("Asyncservers", DebugLevels.ASYNC_SERVERS);
	    
	    final SelectableLog connectionReadLog = new SelectableLogImpl("Connectionread", DebugLevels.CONNECTION_READ);
	    
		try (AsyncServers asyncServers = new AsyncServers(asyncServersLog, connectionReadLog)) {
		
		    final NonBlockingChannelWriterLog driverWriteLog = new NonBlockingChannelWriterLogImpl(
		            "Driverwrite",
		            DebugLevels.DRIVER_WRITE);
		    
		    final XWindowsClientProtocolLog driverProtocolLog = new XWindowsClientProtocolLogImpl("driver", DebugLevel.DEBUG);
		    
			try (XWindowsDriverConnection driverConnection = new XWindowsDriverConnection(display, driverWriteLog, driverProtocolLog)) {

			    final String name = "Driverevents";
			    
	             asyncServers.addSelectable(
	                        name,
	                        driverConnection.getSelectable(),
	                        driverConnection.getMessageProcessor(),
	                        new SelectableLogImpl(name, DebugLevels.DRIVER_READ));

	             System.out.println("## start check for IO");

	             final DisplayDeviceId displayDeviceId = new DisplayDeviceId("XWindows", Alignment.CENTER);

	             final XHardware hardware = initDriver(asyncServers, driverConnection, displayDeviceId);
			    
	             initXWindows(display, asyncServers, displayDeviceId, hardware);
			}
		}
	}
	
	private static XHardware initDriver(AsyncServers asyncServers, XWindowsDriverConnection driverConnection, DisplayDeviceId displayDeviceId) throws IOException {

        while (driverConnection.getServerMessage() == null) {
            asyncServers.checkForIO();
        }

        System.out.println("## done check for IO");

        final XWindowsInputDriver inputDriver = new XWindowsInputDriver(driverConnection);
        final XWindowsGraphicsDriver graphicsDriver = new XWindowsGraphicsDriver(driverConnection, displayDeviceId);
	 
        while (!inputDriver.isInitialized() || !graphicsDriver.isInitialized()) {
            asyncServers.checkForIO();
        }

        final XHardware hardware = new XHardware(inputDriver, graphicsDriver);

        return hardware;
	}
	
	private static void initXWindows(int display, AsyncServers asyncServers, DisplayDeviceId displayDeviceId, XHardware hardware) throws Exception {

        final DisplayConfig displayConfig = new DisplayConfig(displayDeviceId, Alignment.CENTER);
        
        final DisplayAreaConfig displayAreaConfig = new DisplayAreaConfig(
                1, Arrays.asList(displayConfig));

        final XConfig config = new XConfig(displayAreaConfig, Arrays.asList("/usr/share/fonts/X11/misc"));
                
        startXServer(display, asyncServers, hardware, config);
    
        asyncServers.waitForIO();
	}
	
	private static void startXServer(int display, AsyncServers asyncServers, XHardware hardware, XConfig config) throws IOException, Exception {
	    
        final NonBlockingChannelWriterLog connectionWriteLog = new NonBlockingChannelWriterLogImpl(
                "Connectionwrite",
                DebugLevels.CONNECTION_WRITE);
        
        final XWindowsServerProtocolLog protocolLog = new XWindowsServerProtocolLogImpl("XWindowsProtocol", DebugLevels.XWINDOWS_PROTOCOL);

        final Compositor compositor = new SingleViewPortCompositor();
        
        final XRendering rendering = new XRendering(compositor, new CairoXLibRendererFactory());
        
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
                    socketChannel -> server.processConnection(socketChannel));
        }
	}
}
