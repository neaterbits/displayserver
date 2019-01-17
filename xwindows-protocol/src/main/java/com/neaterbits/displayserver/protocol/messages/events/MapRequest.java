package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class MapRequest extends Event {

    private final WINDOW parent;
    private final WINDOW window;

    public static MapRequest decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        final CARD16 sequenceNumber = readSequenceNumber(stream);
        
        final WINDOW parent = stream.readWINDOW();
        final WINDOW window = stream.readWINDOW();
        
        stream.readPad(20);
    
        return new MapRequest(sequenceNumber, parent, window);
    }
    
    public MapRequest(CARD16 sequenceNumber, WINDOW parent, WINDOW window) {
        super(sequenceNumber);

        Objects.requireNonNull(parent);
        Objects.requireNonNull(window);
        
        this.parent = parent;
        this.window = window;
    }

    public WINDOW getParent() {
        return parent;
    }

    public WINDOW getWindow() {
        return window;
    }

    @Override
    public Object[] getDebugParams() {

        return wrap(
                "parent", parent,
                "window", window
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeEventCode(stream, Events.MAP_REQUEST);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        stream.writeWINDOW(parent);
        stream.writeWINDOW(window);
        
        stream.pad(20);
    }
}
