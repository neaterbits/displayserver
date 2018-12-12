package com.neaterbits.displayserver.framebuffer.xwindows;

import java.io.IOException;

import com.neaterbits.displayserver.buffers.OffscreenBuffer;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.protocol.messages.requests.CreatePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.FreePixmap;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.types.Size;

final class XWindowsOffscreenBuffer extends XWindowsBaseBuffer implements OffscreenBuffer {

    private final PIXMAP pixmap;
    private final Size size;
    private final int depth;
    
    XWindowsOffscreenBuffer(XWindowsDriverConnection driverConnection, WINDOW window, Size size, int depth) throws IOException {
    
        super(driverConnection, XWindowsClientHelper.createGC(driverConnection, window));
        
        this.pixmap = new PIXMAP(driverConnection.allocateResourceId());
        this.size = size;
        this.depth = depth;
        
        final CreatePixmap createPixmap = new CreatePixmap(
                new CARD8((byte)depth),
                pixmap,
                window.toDrawable(),
                new CARD16(size.getWidth()),
                new CARD16(size.getHeight()));
        
        driverConnection.sendRequest(createPixmap);
    }
    
    @Override
    DRAWABLE getDrawable() {
        return pixmap.toDrawable();
    }
    
    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    void dispose() {
        getDriverConnection().sendRequest(new FreePixmap(pixmap));
    }
}
