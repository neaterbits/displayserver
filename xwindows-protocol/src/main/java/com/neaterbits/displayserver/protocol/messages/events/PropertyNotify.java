package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class PropertyNotify extends Event {

    public static final BYTE NewValue = new BYTE((byte)0);
    public static final BYTE Deleted = new BYTE((byte)1);
    
    private final WINDOW window;
    private final ATOM atom;
    private final TIMESTAMP time;
    private final BYTE state;
    
    public PropertyNotify(CARD16 sequenceNumber, WINDOW window, ATOM atom, TIMESTAMP time, BYTE state) {
        super(sequenceNumber);
        
        Objects.requireNonNull(window);
        Objects.requireNonNull(atom);
        Objects.requireNonNull(time);
        Objects.requireNonNull(state);
        
        this.window = window;
        this.atom = atom;
        this.time = time;
        this.state = state;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeEventCode(stream, Events.GRAPHICS_EXPOSURE);
        writeUnusedByte(stream);
        writeSequenceNumber(stream);

        stream.writeWINDOW(window);
        stream.writeATOM(atom);
        stream.writeTIMESTAMP(time);
        stream.writeBYTE(state);
        
        stream.pad(15);
    }
}