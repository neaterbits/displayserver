package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.events.types.EventState;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.KEYCODE;

public final class KeyPress extends BaseWindowEvent {

    private final KEYCODE detail;
    
    public static KeyPress decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final KEYCODE detail = stream.readKEYCODE();
        
        final CARD16 sequenceNumber = readSequenceNumber(stream);
        
        final EventState eventState = EventState.decode(stream);
        
        readUnusedByte(stream);
        
        return new KeyPress(sequenceNumber, detail, eventState);
    }
    
    public KeyPress(CARD16 sequenceNumber, KEYCODE detail, EventState eventState) {
        super(sequenceNumber, eventState);
    
        Objects.requireNonNull(detail);
        
        this.detail = detail;
    }

    public KEYCODE getDetail() {
        return detail;
    }

    @Override
    protected Object[] getServerToClientDebugParams() {
        return merge(
                new Object [] { "detail", detail },
                getEventState().getDebugParams());
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeEventCode(stream);
        
        stream.writeKEYCODE(detail);
        
        writeSequenceNumber(stream);
        
        getEventState().encode(stream);
        
        writeUnusedByte(stream);
    }

    @Override
    public int getEventCode() {
        return Events.KEY_PRESS;
    }
}
