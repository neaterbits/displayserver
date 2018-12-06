package com.neaterbits.displayserver.main;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.events.xwindows.XWindowsEventSource;
import com.neaterbits.displayserver.framebuffer.xwindows.XWindowsGraphicsDriver;
import com.neaterbits.displayserver.io.common.AsyncServers;
import com.neaterbits.displayserver.io.common.AsyncServersLog;
import com.neaterbits.displayserver.io.common.AsyncServersLogImpl;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLogImpl;
import com.neaterbits.displayserver.io.common.SelectableLog;
import com.neaterbits.displayserver.io.common.SelectableLogImpl;
import com.neaterbits.displayserver.protocol.logging.XWindowsProtocolLog;
import com.neaterbits.displayserver.protocol.logging.XWindowsProtocolLogImpl;
import com.neaterbits.displayserver.server.XWindowsProtocolServer;

public class DisplayServerMain {

	public static void main(String [] args) throws Exception {

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
		    
			try (XWindowsDriverConnection driverConnection = new XWindowsDriverConnection(display, driverWriteLog)) {

			    final String name = "Driverevents";
			    
	             asyncServers.addSelectable(
	                        name,
	                        driverConnection.getSelectable(),
	                        driverConnection.getMessageProcessor(),
	                        new SelectableLogImpl(name, DebugLevels.CONNECTION_READ));

	             System.out.println("## start check for IO");

	             while (driverConnection.getServerMessage() == null) {
	                 asyncServers.checkForIO();
	             }

			    System.out.println("## done check for IO");
				
				final XWindowsEventSource eventSource = new XWindowsEventSource(driverConnection);
				final XWindowsGraphicsDriver graphicsDriver = new XWindowsGraphicsDriver(driverConnection);

				final NonBlockingChannelWriterLog connectionWriteLog = new NonBlockingChannelWriterLogImpl(
				        "Connectionwrite",
				        DebugLevels.CONNECTION_WRITE);
				
				final XWindowsProtocolLog protocolLog = new XWindowsProtocolLogImpl("XWindowsProtocol", DebugLevels.XWINDOWS_PROTOCOL);
				
				try (XWindowsProtocolServer server = new XWindowsProtocolServer(
				        eventSource,
				        graphicsDriver,
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
