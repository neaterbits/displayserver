package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class UnmapNotify extends XEvent {

    private final WINDOW event;
    private final WINDOW window;
    private final BOOL fromConfigure;
    
    public static UnmapNotify decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        final CARD16 sequenceNumber = readSequenceNumber(stream);
        
        final WINDOW event = stream.readWINDOW();
        final WINDOW window = stream.readWINDOW();
        final BOOL fromConfigure = stream.readBOOL();
    
        stream.readPad(19);
        
        return new UnmapNotify(sequenceNumber, event, window, fromConfigure);
    }
    
    public UnmapNotify(CARD16 sequenceNumber, WINDOW event, WINDOW window, BOOL fromConfigure) {
        super(sequenceNumber);
    
        Objects.requireNonNull(event);
        Objects.requireNonNull(window);
        Objects.requireNonNull(fromConfigure);
        
        this.event = event;
        this.window = window;
        this.fromConfigure = fromConfigure;
    }

    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap(
                "event", event,
                "window", window,
                "fromConfigure", fromConfigure
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeEventCode(stream);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        stream.writeWINDOW(event);
        stream.writeWINDOW(window);
        stream.writeBOOL(fromConfigure);
        
        stream.pad(19);
    }

    @Override
    public int getEventCode() {
        return Events.UNMAP_NOTIFY;
    }
}
