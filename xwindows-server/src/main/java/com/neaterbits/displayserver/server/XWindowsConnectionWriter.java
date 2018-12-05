package com.neaterbits.displayserver.server;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Objects;

import com.neaterbits.displayserver.io.common.NonBlockingChannelWriter;

final class XWindowsConnectionWriter extends NonBlockingChannelWriter {

    private final SocketChannel socketChannel;

    XWindowsConnectionWriter(SocketChannel socketChannel) {
        
        Objects.requireNonNull(socketChannel);
        
        this.socketChannel = socketChannel;
    }

    @Override
    protected SocketChannel getChannel(SelectionKey selectionKey, Selector selector) {
        return socketChannel;
    }
}
