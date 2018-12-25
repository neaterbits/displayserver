package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.neaterbits.displayserver.protocol.IntPadXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.IntPadXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class WindowConfiguration extends Attributes {
    
    public static final int X               = 0x0001;
    public static final int Y               = 0x0002;
    public static final int WIDTH           = 0x0004;
    public static final int HEIGHT          = 0x0008;
    public static final int BORDER_WIDTH    = 0x0010;
    public static final int SIBLING         = 0x0020;
    public static final int STACK_MODE      = 0x0040;

    private final INT16 x;
    private final INT16 y;
    private final CARD16 width;
    private final CARD16 height;
    private final CARD16 borderWidth;
    private final WINDOW sibling;
    private final BYTE stackMode;

    public static WindowConfiguration decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BITMASK bitmask = stream.readBITMASK16();
        
        stream.readCARD16();

        final IntPadXWindowsProtocolInputStream padStream = new IntPadXWindowsProtocolInputStream(stream);
        
        return new WindowConfiguration(bitmask,
                readIfSet(bitmask, X,               padStream::readINT16),
                readIfSet(bitmask, Y,               padStream::readINT16),
                readIfSet(bitmask, WIDTH,           padStream::readCARD16),
                readIfSet(bitmask, HEIGHT,          padStream::readCARD16),
                readIfSet(bitmask, BORDER_WIDTH,    padStream::readCARD16),
                readIfSet(bitmask, SIBLING,         padStream::readWINDOW),
                readIfSet(bitmask, STACK_MODE,      padStream::readBYTE));

    }
    
    public WindowConfiguration(
            BITMASK valueMask,
            INT16 x, INT16 y,
            CARD16 width, CARD16 height,
            CARD16 borderWidth,
            WINDOW sibling,
            BYTE stackMode) {
        
        super(valueMask);
        
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.borderWidth = borderWidth;
        this.sibling = sibling;
        this.stackMode = stackMode;
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

    public WINDOW getSibling() {
        return sibling;
    }

    public BYTE getStackMode() {
        return stackMode;
    }

    
    @Override
    public Object[] getDebugParams() {
        final List<Object> params = new ArrayList<>();
        
        params.add("bitmask");
        params.add(super.getValueMask());

        addIfSet(params, "x",           x,              X);
        addIfSet(params, "y",           y,              Y);
        addIfSet(params, "width",       width,          WIDTH);
        addIfSet(params, "height",      height,         HEIGHT);
        addIfSet(params, "borderWidth", borderWidth,    BORDER_WIDTH);
        addIfSet(params, "sibling",     sibling,        SIBLING);
        addIfSet(params, "stackMode",   stackMode,      STACK_MODE);
        
        return params.toArray(new Object[params.size()]);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        encodeBITMASK16(stream);
        
        stream.writeCARD16(new CARD16(0));
        
        final IntPadXWindowsProtocolOutputStream padStream = new IntPadXWindowsProtocolOutputStream(stream);
        
        writeIfSet(x,           X,              padStream::writeINT16);
        writeIfSet(y,           Y,              padStream::writeINT16);
        writeIfSet(width,       WIDTH,          padStream::writeCARD16);
        writeIfSet(height,      HEIGHT,         padStream::writeCARD16);
        writeIfSet(borderWidth, BORDER_WIDTH,   padStream::writeCARD16);
        writeIfSet(sibling,     SIBLING,        padStream::writeWINDOW);
        writeIfSet(stackMode,   STACK_MODE,     padStream::writeBYTE);
    }
}
