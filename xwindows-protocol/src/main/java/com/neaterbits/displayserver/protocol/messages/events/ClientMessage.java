package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class ClientMessage extends XEvent {

    private final CARD8 format;
    private final WINDOW window;
    private final ATOM type;
    private final byte [] data;
    
    public static ClientMessage decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final CARD8 format = stream.readCARD8();
        
        final CARD16 sequenceNumber = readSequenceNumber(stream);
        
        return new ClientMessage(
                sequenceNumber,
                format,
                stream.readWINDOW(),
                stream.readATOM(),
                stream.readData(20));
    }
    
    public ClientMessage(CARD16 sequenceNumber, CARD8 format, WINDOW window, ATOM type, byte[] data) {
        super(sequenceNumber);
    
        this.format = format;
        this.window = window;
        this.type = type;
        this.data = data;
    }

    public CARD8 getFormat() {
        return format;
    }

    public WINDOW getWindow() {
        return window;
    }

    public ATOM getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "format", format,
                "window", window,
                "type", type,
                "data", data.length
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeEventCode(stream, Events.CLIENT_MESSAGE);
        
        stream.writeCARD8(format);
        
        writeSequenceNumber(stream);
        
        stream.writeWINDOW(window);
        stream.writeATOM(type);
        
        stream.writeData(data);
    }
}
