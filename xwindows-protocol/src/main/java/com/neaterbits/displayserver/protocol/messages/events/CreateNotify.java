package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class CreateNotify extends XEvent {

    private final WINDOW parent;
    private final WINDOW window;
    
    private final INT16 x;
    private final INT16 y;
    
    private final CARD16 width;
    private final CARD16 height;
    
    private final CARD16 borderWidth;

    private final BOOL overrideRedirect;

    public static CreateNotify decode(XWindowsProtocolInputStream stream) throws IOException {

        readUnusedByte(stream);
        
        final CARD16 sequenceNumber = readSequenceNumber(stream);
        
        final WINDOW parent = stream.readWINDOW();
        final WINDOW window = stream.readWINDOW();
        
        final INT16 x = stream.readINT16();
        final INT16 y = stream.readINT16();
        
        final CARD16 width = stream.readCARD16();
        final CARD16 height = stream.readCARD16();
        
        final CARD16 borderWidth = stream.readCARD16();

        final BOOL overrideRedirect = stream.readBOOL();

        stream.readPad(9);
        
        return new CreateNotify(sequenceNumber, parent, window, x, y, width, height, borderWidth, overrideRedirect);
    }
    
    public CreateNotify(CARD16 sequenceNumber, WINDOW parent, WINDOW window, INT16 x, INT16 y, CARD16 width,
            CARD16 height, CARD16 borderWidth, BOOL overrideRedirect) {
        super(sequenceNumber);
    
        this.parent = parent;
        this.window = window;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.borderWidth = borderWidth;
        this.overrideRedirect = overrideRedirect;
    }

    public WINDOW getParent() {
        return parent;
    }

    public WINDOW getWindow() {
        return window;
    }

    public INT16 getX() {
        return x;
    }

    public INT16 getY() {
        return y;
    }

    public CARD16 getWidth() {
        return width;
    }

    public CARD16 getHeight() {
        return height;
    }

    public CARD16 getBorderWidth() {
        return borderWidth;
    }

    public BOOL getOverrideRedirect() {
        return overrideRedirect;
    }

    @Override
    public Object[] getDebugParams() {

        return wrap(
                "parent", parent,
                "window", window,
                "x", x,
                "y", y,
                "width", width,
                "height", height,
                "borderWidth", borderWidth,
                "overrideRedirect", overrideRedirect
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeEventCode(stream, Events.CREATE_NOTIFY);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        stream.writeWINDOW(parent);
        stream.writeWINDOW(window);
        
        stream.writeINT16(x);
        stream.writeINT16(y);
    
        stream.writeCARD16(width);
        stream.writeCARD16(height);
        
        stream.writeCARD16(borderWidth);

        stream.writeBOOL(overrideRedirect);
        
        stream.pad(9);
    }
}
