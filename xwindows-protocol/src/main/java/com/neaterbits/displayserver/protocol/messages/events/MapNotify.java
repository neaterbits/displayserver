package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class MapNotify extends Event {

    private final WINDOW event;
    private final WINDOW window;
    private final BOOL overrideRedirect;
    
    public static MapNotify decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readReplyLength(stream);
        
        final CARD16 sequenceNumber = readSequenceNumber(stream);
        
        final WINDOW event = stream.readWINDOW();
        final WINDOW window = stream.readWINDOW();
        final BOOL overrideRedirect = stream.readBOOL();
    
        stream.readPad(19);
        
        return new MapNotify(sequenceNumber, event, window, overrideRedirect);
    }
    
    public MapNotify(CARD16 sequenceNumber, WINDOW event, WINDOW window, BOOL overrideRedirect) {
        super(sequenceNumber);
    
        Objects.requireNonNull(event);
        Objects.requireNonNull(window);
        Objects.requireNonNull(overrideRedirect);
        
        this.event = event;
        this.window = window;
        this.overrideRedirect = overrideRedirect;
    }

    public WINDOW getEvent() {
        return event;
    }

    public WINDOW getWindow() {
        return window;
    }

    public BOOL getOverrideRedirect() {
        return overrideRedirect;
    }

    @Override
    public Object[] getDebugParams() {
        
        return wrap(
                "event", event,
                "window", window,
                "overrideRedirect", overrideRedirect
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeEventCode(stream, Events.MAP_NOTIFY);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        stream.writeWINDOW(event);
        stream.writeWINDOW(window);
        
        stream.writeBOOL(overrideRedirect);
        
        stream.pad(19);
    }
}
