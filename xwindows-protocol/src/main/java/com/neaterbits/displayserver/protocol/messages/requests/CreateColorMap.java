package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.Alloc;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class CreateColorMap extends XRequest {

    private final BYTE alloc;
    private final COLORMAP mid;
    private final WINDOW window;
    private final VISUALID visual;

    public static CreateColorMap decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE alloc = stream.readBYTE();
        
        readRequestLength(stream);
        
        return new CreateColorMap(
                alloc,
                stream.readCOLORMAP(),
                stream.readWINDOW(),
                stream.readVISUALID());
    }
    
    public CreateColorMap(BYTE alloc, COLORMAP mid, WINDOW window, VISUALID visual) {
        
        Objects.requireNonNull(alloc);
        Objects.requireNonNull(mid);
        Objects.requireNonNull(window);
        Objects.requireNonNull(visual);
        
        this.alloc = alloc;
        this.mid = mid;
        this.window = window;
        this.visual = visual;
    }
    
    public BYTE getAlloc() {
        return alloc;
    }

    public COLORMAP getMid() {
        return mid;
    }

    public WINDOW getWindow() {
        return window;
    }

    public VISUALID getVisual() {
        return visual;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap("alloc", Alloc.name(alloc), "mid", mid, "window", window, "visual", visual);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        stream.writeBYTE(alloc);
        
        writeRequestLength(stream, 4);

        stream.writeCOLORMAP(mid);
        stream.writeWINDOW(window);
        stream.writeVISUALID(visual);
    }

    @Override
    public int getOpCode() {
        return OpCodes.CREATE_COLOR_MAP;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
