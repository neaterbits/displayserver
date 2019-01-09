package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Set;

public abstract class NonBlockingChannelReaderWriter
    extends NonBlockingChannelWriter
    implements Selectable, MessageProcessor {

    private final SocketAddress socketAddress;
    
    private SocketChannel socketChannel;
    
    private SelectionKey selectionKey;

    public NonBlockingChannelReaderWriter(InetSocketAddress socketAddress, NonBlockingChannelWriterLog log) {
        super(log);

        this.socketAddress = socketAddress;
    }

    @Override
    public final Set<SelectionKey> register(SelectorProvider selectorProvider, Selector selector) throws IOException {
        
        final Set<SelectionKey> set = new HashSet<>();
        
        if (socketChannel != null) {
            throw new IllegalStateException();
        }

        this.socketChannel = selectorProvider.openSocketChannel();
                
        if (!socketChannel.connect(socketAddress)) {
            throw new IOException("Failed to connect");
        }

        socketChannel.configureBlocking(false);
        
        this.selectionKey = socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
        
        if (this.selectionKey == null) {
            throw new IllegalStateException();
        }
        
        set.add(selectionKey);
        
        return set;
    }
    
    @Override
    public final int read(SelectionKey selectionKey, ByteBuffer buffer) throws IOException {

        if (this.selectionKey != selectionKey) {
            throw new IllegalArgumentException();
        }
        
        final SocketChannel channel = getChannel();
        final int bytesRead = channel.read(buffer);
        
        return bytesRead;
    }
    
    @Override
    public final void unregister(SelectorProvider selectorProvider, Selector selector) {
        
    }
    
    @Override
    protected final SocketChannel getChannel() {
        return socketChannel;
    }
    
    @Override
    protected final SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void close() throws Exception {
        socketChannel.close();
    }
}
