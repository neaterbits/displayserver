package com.neaterbits.displayserver.server;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;

import com.neaterbits.displayserver.io.common.NonBlockingChannelWriter;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;

final class XConnectionWriter extends NonBlockingChannelWriter {

    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;

    XConnectionWriter(SocketChannel socketChannel, SelectionKey selectionKey, NonBlockingChannelWriterLog log) {
        
        super(log);
        
        Objects.requireNonNull(socketChannel);
        Objects.requireNonNull(selectionKey);
        
        this.socketChannel = socketChannel;
        this.selectionKey = selectionKey;
    }

    @Override
    protected SocketChannel getChannel() {
        return socketChannel;
    }
    
    @Override
    protected SelectionKey getSelectionKey() {
        return selectionKey;
    }
}
