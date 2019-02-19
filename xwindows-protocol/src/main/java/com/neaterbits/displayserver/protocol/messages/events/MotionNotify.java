package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.events.types.EventState;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class MotionNotify extends BaseWindowEvent {

    private final BYTE detail;
    
    public static MotionNotify decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE detail = stream.readBYTE();
        
        final CARD16 sequenceNumber = readSequenceNumber(stream);
        
        final EventState eventState = EventState.decode(stream);
        
        readUnusedByte(stream);
        
        return new MotionNotify(sequenceNumber, detail, eventState);
    }
    
    public MotionNotify(CARD16 sequenceNumber, BYTE detail, EventState eventState) {
        super(sequenceNumber, eventState);
    
        Objects.requireNonNull(detail);
        
        this.detail = detail;
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
        
        stream.writeBYTE(detail);
        
        writeSequenceNumber(stream);
        
        getEventState().encode(stream);
        
        writeUnusedByte(stream);
    }

    @Override
    public int getEventCode() {
        return Events.MOTION_NOTIFY;
    }
}
