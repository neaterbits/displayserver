package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class Expose extends XEvent {

    private final WINDOW window;
    
    private final CARD16 x;
    private final CARD16 y;
    private final CARD16 width;
    private final CARD16 height;
    
    private final CARD16 count;

    public Expose(CARD16 sequenceNumber, WINDOW window, CARD16 x, CARD16 y, CARD16 width, CARD16 height, CARD16 count) {
        super(sequenceNumber);
    
        Objects.requireNonNull(window);
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        Objects.requireNonNull(width);
        Objects.requireNonNull(height);
        Objects.requireNonNull(count);
        
        this.window = window;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.count = count;
    }

    public WINDOW getWindow() {
        return window;
    }

    public CARD16 getX() {
        return x;
    }

    public CARD16 getY() {
        return y;
    }

    public CARD16 getWidth() {
        return width;
    }

    public CARD16 getHeight() {
        return height;
    }

    public CARD16 getCount() {
        return count;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeEventCode(stream, Events.EXPOSE);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        stream.writeWINDOW(window);
        
        stream.writeCARD16(x);
        stream.writeCARD16(y);
        stream.writeCARD16(width);
        stream.writeCARD16(height);

        stream.writeCARD16(count);
    }
}
