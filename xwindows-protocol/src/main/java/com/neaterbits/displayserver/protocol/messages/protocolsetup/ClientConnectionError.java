package com.neaterbits.displayserver.protocol.messages.protocolsetup;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;

public class ClientConnectionError extends Encodeable {

    private final CARD16 protocolMajorVersion;
    private final CARD16 protocolMinorVersion;

    private final String reason;

    public static ClientConnectionError decode(XWindowsProtocolInputStream stream) throws IOException {
        final BYTE errorCode = stream.readBYTE();
        
        if (errorCode.getValue() != 0) {
            throw new IllegalStateException();
        }
        
        final BYTE reasonLength = stream.readBYTE();

        final CARD16 protocolMajorVersion = stream.readCARD16();
        final CARD16 protocolMinorVersion = stream.readCARD16();
        
        stream.readCARD16();
        
        final String reason = stream.readSTRING8(reasonLength.getValue());
        
        final int padding = XWindowsProtocolUtil.getPadding(reason.length());
        
        System.out.println("## read padding " + padding + " from " + reason.length() );
        
        stream.readPad(padding);
        
        return new ClientConnectionError(protocolMajorVersion, protocolMinorVersion, reason);
    }
    
    public ClientConnectionError(CARD16 protocolMajorVersion, CARD16 protocolMinorVersion, String reason) {
        this.protocolMajorVersion = protocolMajorVersion;
        this.protocolMinorVersion = protocolMinorVersion;
        this.reason = reason;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        stream.writeBYTE(new BYTE((byte)0));
        stream.writeBYTE(new BYTE((byte)reason.length()));
        stream.writeCARD16(protocolMajorVersion);
        stream.writeCARD16(getProtocolMinorVersion());
        
        final int pad = XWindowsProtocolUtil.getPadding(reason.length());
        
        stream.writeCARD16(new CARD16((reason.length() + pad) / 4));
        stream.writeSTRING8(reason);
        stream.pad(pad);
    }


    public CARD16 getProtocolMajorVersion() {
        return protocolMajorVersion;
    }

    public CARD16 getProtocolMinorVersion() {
        return protocolMinorVersion;
    }

    public String getReason() {
        return reason;
    }
}
