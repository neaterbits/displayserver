package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.events.types.EventState;
import com.neaterbits.displayserver.protocol.types.BUTTON;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class ButtonRelease extends BaseWindowEvent {

    private final BUTTON detail;
    
    public static ButtonRelease decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BUTTON detail = stream.readBUTTON();
        
        final CARD16 sequenceNumber = readSequenceNumber(stream);
        
        final EventState eventState = EventState.decode(stream);
        
        readUnusedByte(stream);
        
        return new ButtonRelease(sequenceNumber, detail, eventState);
    }
    
    public ButtonRelease(CARD16 sequenceNumber, BUTTON detail, EventState eventState) {
        super(sequenceNumber, eventState);
    
        Objects.requireNonNull(detail);
        
        this.detail = detail;
    }

    public BUTTON getDetail() {
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
        
        stream.writeBUTTON(detail);
        
        writeSequenceNumber(stream);
        
        getEventState().encode(stream);
        
        writeUnusedByte(stream);
    }

    @Override
    public int getEventCode() {
        return Events.BUTTON_RELEASE;
    }
}
