package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class ChangeProperty extends Request {

    private final BYTE mode;
    private final WINDOW window;
    private final ATOM property;
    private final ATOM type;
    private final CARD8 format;
    
    private final byte [] data;
    
    public static ChangeProperty decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE mode = stream.readBYTE();
        
        readRequestLength(stream);
        
        final WINDOW window = stream.readWINDOW();
        final ATOM property = stream.readATOM();
        final ATOM type = stream.readATOM();
        
        final CARD8 format = stream.readCARD8();
        
        stream.readPad(3);
        
        final CARD32 length = stream.readCARD32();
        
        final int dataLength = XWindowsProtocolUtil.getPropertyDataLength(format, (int)length.getValue());
    
        final byte [] data = stream.readData(dataLength);
        
        stream.readPad(XWindowsProtocolUtil.getPadding(data.length));
        
        return new ChangeProperty(mode, window, property, type, format, data);
    }

    public ChangeProperty(BYTE mode, WINDOW window, ATOM property, ATOM type, CARD8 format, byte[] data) {
        this.mode = mode;
        this.window = window;
        this.property = property;
        this.type = type;
        this.format = format;
        this.data = data;
    }

    public BYTE getMode() {
        return mode;
    }

    public WINDOW getWindow() {
        return window;
    }

    public ATOM getProperty() {
        return property;
    }

    public ATOM getType() {
        return type;
    }

    public CARD8 getFormat() {
        return format;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "mode", mode,
                "window", window,
                "property", property,
                "type", type,
                "format", format,
                "dataLength", data.length
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream, OpCodes.CHANGE_PROPERTY);

        stream.writeBYTE(mode);
        
        final int pad = XWindowsProtocolUtil.getPadding(data.length);
        
        writeRequestLength(stream, 6 + (data.length + pad) / 4);
        
        stream.writeWINDOW(window);
        stream.writeATOM(property);
        stream.writeATOM(type);

        stream.writeCARD8(format);
        
        stream.pad(3);
        
        final int lengthValue = XWindowsProtocolUtil.getPropertyDataFormatLength(format, data.length);
        stream.writeCARD32(new CARD32(lengthValue));

        stream.writeData(data);
        stream.pad(pad);
    }

    
    
}
