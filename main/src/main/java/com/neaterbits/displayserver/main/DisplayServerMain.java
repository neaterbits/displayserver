package com.neaterbits.displayserver.main;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.events.xwindows.XWindowsEventSource;
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
import com.neaterbits.displayserver.server.XServer;
import com.neaterbits.displayserver.util.logging.DebugLevel;
import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;
import com.neaterbits.displayserver.windows.config.DisplayConfig;

public class DisplayServerMain {

	public static void main(String [] args) throws Exception {

	    final int display;
	    
	    if (args.length == 1) {
	        display = Integer.parseInt(args[0]);
	    }
	    else {
	        display = 0;
	    }
	    
	    final DisplayDeviceId displayDeviceId = new DisplayDeviceId("XWindows", Alignment.CENTER);
	    
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

	             while (driverConnection.getServerMessage() == null) {
	                 asyncServers.checkForIO();
	             }

			    System.out.println("## done check for IO");
				
				final XWindowsEventSource eventSource = new XWindowsEventSource(driverConnection);
				final XWindowsGraphicsDriver graphicsDriver = new XWindowsGraphicsDriver(driverConnection, displayDeviceId);

				final NonBlockingChannelWriterLog connectionWriteLog = new NonBlockingChannelWriterLogImpl(
				        "Connectionwrite",
				        DebugLevels.CONNECTION_WRITE);
				
				final XWindowsServerProtocolLog protocolLog = new XWindowsServerProtocolLogImpl("XWindowsProtocol", DebugLevels.XWINDOWS_PROTOCOL);

				final DisplayConfig displayConfig = new DisplayConfig(displayDeviceId, Alignment.CENTER);
				
				final DisplayAreaConfig displayAreaConfig = new DisplayAreaConfig(
				        1, Arrays.asList(displayConfig));
				

				
				try (XServer server = new XServer(
				        eventSource,
				        graphicsDriver,
				        displayAreaConfig,
				        protocolLog,
				        connectionWriteLog)) {
				    
			        asyncServers.addServer(
			                ":1",
			                new SocketAddress [] {
			                    new InetSocketAddress("127.0.0.1", 6000 + display + 1)
			                },
			                socketChannel -> server.processConnection(socketChannel));
				}
				
				asyncServers.waitForIO();
			}
		}
	}
}
