package com.neaterbits.displayserver.main;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.events.xwindows.XWindowsEventSource;
import com.neaterbits.displayserver.framebuffer.xwindows.XWindowsGraphicsDriver;
import com.neaterbits.displayserver.io.common.AsyncServers;
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
	    
		try (AsyncServers asyncServers = new AsyncServers()) {
		
			try (XWindowsDriverConnection driverConnection = new XWindowsDriverConnection(display)) {

	             asyncServers.addSelectable(
	                        "XWindows events",
	                        driverConnection.getSelectable(),
	                        driverConnection.getMessageProcessor());

	             System.out.println("## start check for IO");

	             while (driverConnection.getServerMessage() == null) {
	                 asyncServers.checkForIO();
	             }

			    System.out.println("## done check for IO");
				
				final XWindowsEventSource eventSource = new XWindowsEventSource(driverConnection);
				final XWindowsGraphicsDriver graphicsDriver = new XWindowsGraphicsDriver(driverConnection);

				try (XWindowsProtocolServer server = new XWindowsProtocolServer(eventSource, graphicsDriver)) {
				    
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
