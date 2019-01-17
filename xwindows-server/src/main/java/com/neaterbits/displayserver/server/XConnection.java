package com.neaterbits.displayserver.server;

import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;

import com.neaterbits.displayserver.io.common.NonBlockingChannelWriter;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.NonBlockingWritable;
import com.neaterbits.displayserver.protocol.DataOutputXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Error;
import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.xwindows.processing.XConnectionOps;

public class XConnection
    extends NonBlockingChannelWriter
    implements NonBlockingWritable, AutoCloseable, XConnectionOps {

	enum State {
		
		CREATED,
		INITIAL_RECEIVED,
        INITIAL_ERROR,
		CONNECTED;
	}
	
	private final SocketChannel socketChannel;
	private final SelectionKey selectionKey;
	private final int connectionNo;
	
	private State state;
	private ByteOrder byteOrder;
	
	private int sequenceNumber;
	
	XConnection(
	        SocketChannel socketChannel,
	        SelectionKey selectionKey,
	        int connectionNo,
	        NonBlockingChannelWriterLog log) {
	    
	    super(log);
	    
		Objects.requireNonNull(socketChannel);
		Objects.requireNonNull(selectionKey);
		
		this.socketChannel = socketChannel;
		this.selectionKey = selectionKey;
		this.connectionNo = connectionNo;
		
		this.state = State.CREATED;
		
		this.sequenceNumber = 0;
	}
	
	final CARD16 increaseSequenceNumber() {
	    if (sequenceNumber == 65535) {
	        sequenceNumber = 0;
	    }
	    else {
	        ++ sequenceNumber;
	    }
	
	    return new CARD16(sequenceNumber);
	}
	
	@Override
	public final CARD16 getSequenceNumber() {
	    return new CARD16(sequenceNumber);
	}
	
	final int getConnectionNo() {
        return connectionNo;
    }
	
    final State getState() {
        return state;
    }

    final void setByteOrder(ByteOrder byteOrder) {
    
        Objects.requireNonNull(byteOrder);
        
        if (this.byteOrder != null) {
            throw new IllegalStateException();
        }
        
        this.byteOrder = byteOrder;
    }

    final void setState(State state) {
        
        Objects.requireNonNull(state);
        
        this.state = state;
    }

	final void send(Encodeable message) {
	    
	    writeToOutputBuffer(byteOrder, dataOutputStream -> {
            final XWindowsProtocolOutputStream protocolOutputStream = new DataOutputXWindowsProtocolOutputStream(dataOutputStream);
            
            message.encode(protocolOutputStream);
	    });
	}
	
    @Override
    public final void sendReply(Reply reply) {
        send(reply);
    }
    
    @Override
    public final void sendError(Error error) {
        send(error);
    }

    @Override
    public final void sendEvent(Event event) {
        send(event);
    }

    @Override
    protected final SocketChannel getChannel() {
        return socketChannel;
    }
    
	@Override
    protected final SelectionKey getSelectionKey() {
	    return selectionKey;
	}

    @Override
    public final void close() throws Exception {
        socketChannel.close();
    }
}
