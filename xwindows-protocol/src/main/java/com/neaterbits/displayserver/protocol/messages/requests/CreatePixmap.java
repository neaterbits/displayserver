package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public final class CreatePixmap extends Request {

    private final CARD8 depth;
    private final PIXMAP pid;
    private final DRAWABLE drawable;
    private final CARD16 width;
    private final CARD16 height;
    
    public CreatePixmap(CARD8 depth, PIXMAP pid, DRAWABLE drawable, CARD16 width, CARD16 height) {
        this.depth = depth;
        this.pid = pid;
        this.drawable = drawable;
        this.width = width;
        this.height = height;
    }

    public static CreatePixmap decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final CARD8 depth = stream.readCARD8();
        
        stream.readCARD16();
        
        final PIXMAP pid = stream.readPIXMAP();
        final DRAWABLE drawable = stream.readDRAWABLE();
        
        final CARD16 width = stream.readCARD16();
        final CARD16 height = stream.readCARD16();
        
        return new CreatePixmap(depth, pid, drawable, width, height);
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "depth", depth,
                "pid", pid,
                "drawable", drawable,
                "width", width,
                "height", height
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeCARD8(depth);
        
        writeRequestLength(stream, 4);
        
        stream.writePIXMAP(pid);
        stream.writeDRAWABLE(drawable);
        stream.writeCARD16(width);
        stream.writeCARD16(height);
    }

    
    @Override
    public int getOpCode() {
        return OpCodes.CREATE_PIXMAP;
    }

    public CARD8 getDepth() {
        return depth;
    }

    public PIXMAP getPid() {
        return pid;
    }

    public DRAWABLE getDrawable() {
        return drawable;
    }

    public CARD16 getWidth() {
        return width;
    }

    public CARD16 getHeight() {
        return height;
    }
}
