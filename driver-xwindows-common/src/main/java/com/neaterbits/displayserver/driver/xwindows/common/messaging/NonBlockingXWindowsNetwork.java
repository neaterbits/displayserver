package com.neaterbits.displayserver.driver.xwindows.common.messaging;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsNetwork;
import com.neaterbits.displayserver.io.common.NonBlockingChannelReaderWriter;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.Selectable;
import com.neaterbits.displayserver.io.common.DataWriter;
import com.neaterbits.displayserver.io.common.MessageProcessor;

public class NonBlockingXWindowsNetwork implements XWindowsNetwork {

    private final NonBlockingChannelReaderWriter readerWriter;
    
    public NonBlockingXWindowsNetwork(InetSocketAddress socketAddress, NonBlockingChannelWriterLog log, MessageProcessor listener) {

        Objects.requireNonNull(listener);
        
        this.readerWriter = new NonBlockingChannelReaderWriter(socketAddress, log) {
            @Override
            public Integer getLengthOfMessage(ByteBuffer byteBuffer) {
                return listener.getLengthOfMessage(byteBuffer);
            }

            @Override
            public void onMessage(ByteBuffer byteBuffer, int messageLength) {
                listener.onMessage(byteBuffer, messageLength);
            }
        };
    }

    public Selectable getSelectable() {
        return readerWriter;
    }
    
    public MessageProcessor getMessageProcessor() {
        return readerWriter;
    }
    
    @Override
    public int sendRequest(DataWriter request, ByteOrder byteOrder) {
        return readerWriter.writeToOutputBuffer(byteOrder, request);
    }

    @Override
    public void close() throws Exception {
        readerWriter.close();
    }
}
