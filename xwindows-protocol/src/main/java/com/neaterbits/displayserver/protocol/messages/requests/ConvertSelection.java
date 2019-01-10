package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class ConvertSelection extends Request {

    private final WINDOW requestor;
    private final ATOM selection;
    private final ATOM target;
    private final ATOM property;
    private final TIMESTAMP currentTime;

    public static ConvertSelection decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new ConvertSelection(
                stream.readWINDOW(),
                stream.readATOM(),
                stream.readATOM(),
                stream.readATOM(),
                stream.readTIMESTAMP());
    }
    
    public ConvertSelection(WINDOW requestor, ATOM selection, ATOM target, ATOM property, TIMESTAMP currentTime) {

        Objects.requireNonNull(requestor);
        Objects.requireNonNull(selection);
        Objects.requireNonNull(target);
        Objects.requireNonNull(property);
        Objects.requireNonNull(currentTime);
        
        this.requestor = requestor;
        this.selection = selection;
        this.target = target;
        this.property = property;
        this.currentTime = currentTime;
    }

    public WINDOW getRequestor() {
        return requestor;
    }

    public ATOM getSelection() {
        return selection;
    }

    public ATOM getTarget() {
        return target;
    }

    public ATOM getProperty() {
        return property;
    }

    public TIMESTAMP getCurrentTime() {
        return currentTime;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "requestor", requestor,
                "selection", selection,
                "target", target,
                "property", property,
                "currentTime", currentTime
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 6);
        
        stream.writeWINDOW(requestor);
        stream.writeATOM(selection);
        stream.writeATOM(target);
        stream.writeATOM(property);
        stream.writeTIMESTAMP(currentTime);
    }

    @Override
    public int getOpCode() {
        return OpCodes.CONVERT_SELECTION;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return null;
    }
}
