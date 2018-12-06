package com.neaterbits.displayserver.protocol.messages.protocolsetup;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class ClientMessage extends Encodeable {

	private final CARD8 byteOrder;
	
	private final CARD16 protocolMajorVersion;
	private final CARD16 protocolMinorVersion;

	private final String authorizationProtocolName;
	private final byte [] authorizationProtocolData;
	
	public static ClientMessage decode(XWindowsProtocolInputStream stream) throws IOException {
		
		final CARD8 byteOrder = stream.readCARD8();
		stream.readBYTE();

		final CARD16 protocolMajorVersion = stream.readCARD16();
		final CARD16 protocolMinorVersion = stream.readCARD16();
	
		final CARD16 authorizationProtocolNameLength = stream.readCARD16();
		final CARD16 authorizationProtocolDataLength = stream.readCARD16();

		stream.readCARD16();
		
		final String authorizationProtocolName = stream.readSTRING8(authorizationProtocolNameLength.getValue());
		
		stream.readPad(XWindowsProtocolUtil.getPadding(authorizationProtocolNameLength.getValue()));
		
		final byte [] authorizationProtocolData = stream.readData(authorizationProtocolDataLength.getValue());
		stream.readPad(XWindowsProtocolUtil.getPadding(authorizationProtocolDataLength.getValue()));
	
		return new ClientMessage(
				byteOrder,

				protocolMajorVersion,
				protocolMinorVersion,

				authorizationProtocolName,
				authorizationProtocolData);
	}
	
	
	public ClientMessage(
			
			CARD8 byteOrder,
			
			CARD16 protocolMajorVersion,
			CARD16 protocolMinorVersion,
			
			String authorizationProtocolName,
			byte [] authorizationProtocolData) {

		this.byteOrder = byteOrder;
		this.protocolMajorVersion = protocolMajorVersion;
		this.protocolMinorVersion = protocolMinorVersion;
		this.authorizationProtocolName = authorizationProtocolName;
		this.authorizationProtocolData = authorizationProtocolData;
	}
	
	@Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
	    stream.writeCARD8(byteOrder);
	    stream.writeBYTE(new BYTE((byte)0));
	    
	    stream.writeCARD16(protocolMajorVersion);
	    stream.writeCARD16(protocolMinorVersion);
	    
	    stream.writeCARD16(new CARD16(authorizationProtocolName.length()));
	    stream.writeCARD16(new CARD16(authorizationProtocolData.length));
	    
	    stream.writeCARD16(new CARD16(0));
	    
	    stream.writeSTRING8(authorizationProtocolName);
	    stream.pad(XWindowsProtocolUtil.getPadding(authorizationProtocolName.length()));
	    
	    stream.writeData(authorizationProtocolData);
	    stream.pad(XWindowsProtocolUtil.getPadding(authorizationProtocolData.length));
    }


    public CARD8 getByteOrder() {
		return byteOrder;
	}
	
	public CARD16 getProtocolMajorVersion() {
		return protocolMajorVersion;
	}
	
	public CARD16 getProtocolMinorVersion() {
		return protocolMinorVersion;
	}
	
	public String getAuthorizationProtocolName() {
		return authorizationProtocolName;
	}

	public byte [] getAuthorizationProtocolData() {
		return authorizationProtocolData;
	}


    @Override
    public String toString() {
        return "ClientMessage [byteOrder=" + byteOrder + ", protocolMajorVersion=" + protocolMajorVersion
                + ", protocolMinorVersion=" + protocolMinorVersion + ", authorizationProtocolName="
                + authorizationProtocolName + ", authorizationProtocolData=" + authorizationProtocolData + "]";
    }
}
