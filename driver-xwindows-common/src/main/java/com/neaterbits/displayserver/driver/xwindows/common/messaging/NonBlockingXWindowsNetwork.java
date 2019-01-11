package com.neaterbits.displayserver.driver.xwindows.common.messaging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import com.neaterbits.displayserver.driver.xwindows.common.ClientResourceIdAllocator;
import com.neaterbits.displayserver.driver.xwindows.common.SentRequest;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsNetwork;
import com.neaterbits.displayserver.io.common.NonBlockingChannelReaderWriter;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.Selectable;
import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.xwindows.util.XAuth;
import com.neaterbits.displayserver.io.common.DataWriter;
import com.neaterbits.displayserver.io.common.MessageProcessor;

public class NonBlockingXWindowsNetwork implements XWindowsNetwork {

    private final NonBlockingChannelReaderWriter readerWriter;
    
    private int sequenceNumber;
    private ClientResourceIdAllocator clientResourceIdAllocator;
    
    private ServerMessage initialMessage;

    public NonBlockingXWindowsNetwork(
            InetSocketAddress socketAddress,
            XAuth xAuth,
            NonBlockingChannelWriterLog log,
            MessageProcessor listener) {

        Objects.requireNonNull(listener);
        
        this.readerWriter = new NonBlockingChannelReaderWriter(socketAddress, log) {
            @Override
            public Integer getLengthOfMessage(ByteBuffer byteBuffer) {
                return listener.getLengthOfMessage(byteBuffer);
            }

            @Override
            public void onMessage(ByteBuffer byteBuffer, int messageLength) {
                
                if (initialMessage == null) {
                    
                    final int numBytes = byteBuffer.remaining();
                    
                    final byte [] bytes = new byte[numBytes];
                    
                    for (int i = 0; i < numBytes; ++ i) {
                        bytes[i] = byteBuffer.get(byteBuffer.position() + i);
                    }
                    
                    final ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    
                    buffer.order(getByteOrder());
                    
                    final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(buffer);
                    
                    try {
                        initialMessage = ServerMessage.decode(stream);
                    } catch (IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                    
                    clientResourceIdAllocator = new ClientResourceIdAllocator(
                            initialMessage.getResourceIdBase().getValue(),
                            initialMessage.getResourceIdMask().getValue());
                }
                
                listener.onMessage(byteBuffer, messageLength);
            }
        };

        this.sequenceNumber = 1;

        final ClientMessage clientMessage = new ClientMessage(
                new CARD8((short)'B'),
                new CARD16(11),
                new CARD16(0),
                xAuth != null ? xAuth.getAuthorizationProtocol() : "",
                xAuth != null ? xAuth.getAuthorizationData() : "".getBytes());

        send(clientMessage, ByteOrder.BIG_ENDIAN);
    }

    
    public Selectable getSelectable() {
        return readerWriter;
    }
    
    public MessageProcessor getMessageProcessor() {
        return readerWriter;
    }

    @Override
    public ByteOrder getByteOrder() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public int generateResourceId() {
        return clientResourceIdAllocator.allocateResourceId();
    }

    @Override
    public void freeResourceId(int resourceId) {
        clientResourceIdAllocator.freeResourceId(resourceId);
    }
    
    @Override
    public ServerMessage getInitialMessage() {
        return null;
    }

    @Override
    public SentRequest sendRequest(Request request, ByteOrder byteOrder) {

        final DataWriter dataWriter = Encodeable.makeDataWriter(request);

        final SentRequest sentRequest;
        
        try {
            final int messageLength = readerWriter.writeToOutputBuffer(byteOrder, dataWriter);
            
            sentRequest = new SentRequest(sequenceNumber, messageLength, null);
        }
        finally {
            ++ sequenceNumber;
        }
        
        return sentRequest;
    }

    @Override
    public int send(Encodeable message, ByteOrder byteOrder) {
        
        final DataWriter dataWriter = Encodeable.makeDataWriter(message);

        return readerWriter.writeToOutputBuffer(byteOrder, dataWriter);
    }

    @Override
    public void close() throws Exception {
        readerWriter.close();
    }
}
