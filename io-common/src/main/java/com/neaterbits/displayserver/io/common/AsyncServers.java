package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class AsyncServers implements AutoCloseable {
	
    private final AsyncServersLog log;
    private final SelectableLog connectionReadLog;
    
	private final List<AsyncServer> servers;
	private final Map<SelectionKey, BaseSelectable> selectableBySelectorKey;
	
	private final SelectorProvider selectorProvider;
	private final AbstractSelector selector;
	
	public AsyncServers(AsyncServersLog log, SelectableLog connectionReadLog) throws IOException {
	    
	    this.log = log;
	    this.connectionReadLog = connectionReadLog;
	    
		this.servers = new ArrayList<>();
		this.selectableBySelectorKey = new HashMap<>();
		
		this.selectorProvider = SelectorProvider.provider();
		this.selector = selectorProvider.openSelector();
	}
	

	public void addServer(
			String name,
			SocketAddress [] addresses,
			Function<SocketChannel, Client> onClientConnect) throws IOException {
		
		Objects.requireNonNull(name);
		Objects.requireNonNull(addresses);
		
		final ServerSocketChannel [] socketChannels = new ServerSocketChannel[addresses.length];
		
		for (int i = 0; i < addresses.length; ++ i) {
			
			try {
				final ServerSocketChannel socketChannel = selectorProvider.openServerSocketChannel();

                socketChannel.configureBlocking(false);

				socketChannel.bind(addresses[i]);
				
				socketChannels[i] = socketChannel;
				
				socketChannel.register(selector, SelectionKey.OP_ACCEPT);
			}
			catch (IOException ex) {
				for (int j = 0; j < i; ++ j) {
					try {
						socketChannels[j].close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		final AsyncServer server = new AsyncServer(name, onClientConnect, socketChannels);
	
		servers.add(server);
	}
	
	public void addSelectable(
			String name,
			Selectable selectable,
			MessageProcessor messageProcessor,
			SelectableLog log) throws IOException {
		
		final GenericSelectable genericSelectable = new GenericSelectable(
				name,
				selectable,
				messageProcessor,
				log);
		
		final Set<SelectionKey> selectionKeys = selectable.register(selectorProvider, selector);
		
		for (SelectionKey selectionKey : selectionKeys) {
		    
		    if (selectionKey == null) {
		        throw new IllegalStateException();
		    }
		    
			selectableBySelectorKey.put(selectionKey, genericSelectable);
		}
	}
	
	public void waitForIO() throws IOException {

		for (;;) {
		    checkForIO();
		}
	}
	
	public void checkForIO() throws IOException {
	    
        final int numUpdated = selector.select(1000L);
        
        if (numUpdated > 0) {
        
            if (log != null) {
                log.onSelectUpdated(numUpdated);
            }
            
            final Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            
            while (iter.hasNext()) {
                
                final SelectionKey selectionKey = iter.next();
                
                if (!selectionKey.isValid()) {
                    throw new IllegalStateException();
                }
                
                if (selectionKey.isAcceptable()) {

                    if (log != null) {
                        log.onAccept(selectionKey);
                    }
                    
                    final ServerSocketChannel socketChannel = (ServerSocketChannel)selectionKey.channel();
                    
                    final SocketChannel clientChannel = socketChannel.accept();
                    
                    Objects.requireNonNull(clientChannel);
                    
                    iter.remove();
    
                    final AsyncServer asyncServer = findAsyncServer(socketChannel);
    
                    processInboundConnection(asyncServer, socketChannel, clientChannel);
                }
                else if (selectionKey.isReadable()) {
                    final BaseSelectable selectable = selectableBySelectorKey.get(selectionKey);
                    
                    // System.out.println("## readable: " + selectionKey);
                    
                    if (selectable != null) {
                        selectable.readAndProcess(selectionKey, selector);
                        
                        // selectionKey.cancel();
                    }
                    
                    iter.remove();
                }
                else if (selectionKey.isWritable()) {
                    final BaseSelectable selectable = selectableBySelectorKey.get(selectionKey);
                    
                    // System.out.println("## onWritable");
                    
                    if (selectable != null) {
                        selectable.onWriteable(selectionKey, selector);
                    }
    
                    // iter.remove();
                }
            }
        }
	}
	
	private AsyncServer findAsyncServer(ServerSocketChannel socketChannel) {
        AsyncServer asyncServer = null;
        
        for (AsyncServer server : servers) {
            for (ServerSocketChannel serverSocketChannel : server.socketChannels) {
                if (socketChannel == serverSocketChannel) {
                    asyncServer = server;
                    break;
                }
            }
            
            if (asyncServer != null) {
                break;
            }
        }

        return asyncServer;
	}

	private void addConnectedClient(AsyncServer server, SocketChannel socket) throws IOException {

	    Objects.requireNonNull(server);
	    Objects.requireNonNull(socket);
	    
		socket.configureBlocking(false);
		
		socket.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
		
		final Client client = server.onClientConnect.apply(socket);
		
		final ClientConnection clientConnection = new ClientConnection(socket, client, client, connectionReadLog);

		server.addClient(clientConnection);
		
		selectableBySelectorKey.put(socket.keyFor(selector), clientConnection);
	}
	
	private void processInboundConnection(AsyncServer server, ServerSocketChannel serverSocketChannel, SocketChannel socket) throws IOException {

	    Objects.requireNonNull(server);
		Objects.requireNonNull(socket);
		
		addConnectedClient(server, socket);
	}

	@Override
	public void close() throws Exception {
		
		try {
			for (AsyncServer server : servers) {
				try {
					server.close();
				}
				catch (IOException ex) {
					
				}
			}
		}
		finally {
			selector.close();
		}
	}
}
