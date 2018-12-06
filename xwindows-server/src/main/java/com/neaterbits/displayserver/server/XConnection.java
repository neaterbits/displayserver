package com.neaterbits.displayserver.server;

import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.io.common.NonBlockingChannelWriter;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.NonBlockingWritable;
import com.neaterbits.displayserver.protocol.DataOutputXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.types.CARD16;

public class XConnection
    extends NonBlockingChannelWriter
    implements NonBlockingWritable, AutoCloseable {

	enum State {
		
		CREATED,
		INITIAL_RECEIVED,
        INITIAL_ERROR,
		CONNECTED;
	}
	
	private final SocketChannel socketChannel;
	private final int connectionNo;
	
	private State state;
	private ByteOrder byteOrder;
	
	private final List<Event> events;
	
	private int sequenceNumber;
	
	XConnection(
	        SocketChannel socketChannel,
	        int connectionNo,
	        NonBlockingChannelWriterLog log) {
	    
	    super(log);
	    
		Objects.requireNonNull(socketChannel);
		
		this.socketChannel = socketChannel;
		this.connectionNo = connectionNo;
		
		this.state = State.CREATED;
		
		this.events = new ArrayList<>();
		
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
	    
	    write(byteOrder, dataOutputStream -> {
            final XWindowsProtocolOutputStream protocolOutputStream = new DataOutputXWindowsProtocolOutputStream(dataOutputStream);
            
            message.encode(protocolOutputStream);
	    });
	}
	
    @Override
    protected final SocketChannel getChannel(SelectionKey selectionKey, Selector selector) {
        return socketChannel;
    }

	void addEvent(Event event) {
		Objects.requireNonNull(event);
	
		events.add(event);
	}

    @Override
    public final void close() throws Exception {
        socketChannel.close();
    }
}
