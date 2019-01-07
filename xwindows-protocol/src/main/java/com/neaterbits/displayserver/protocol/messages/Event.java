package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.events.ClientMessage;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;

public abstract class Event extends ServerToClientMessage {

    public Event(CARD16 sequenceNumber) {
        super(sequenceNumber);
    }

    protected final void writeEventCode(XWindowsProtocolOutputStream stream, int code) throws IOException {
        stream.writeBYTE(new BYTE((byte)code));
    }
    
    public static Event decode(XWindowsProtocolInputStream stream, int code) throws IOException {
        
        final Event event;
        
        switch (code) {
        
        case Events.CLIENT_MESSAGE:
            event = ClientMessage.decode(stream);
            break;
        
        default:
            throw new UnsupportedOperationException("Unknown event code " + code);
        }
        
        return event;
    }
}
