package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;

public final class CreateGC extends Request {

	private final GCONTEXT cid;
	private final DRAWABLE drawable;
	
	private final GCAttributes attributes;

	public static CreateGC decode(XWindowsProtocolInputStream stream) throws IOException {
	    
	    stream.readBYTE();
	    
	    stream.readCARD16();
	    
	    
	    final GCONTEXT cid = stream.readGCONTEXT();
	    final DRAWABLE drawable = stream.readDRAWABLE();
	    
	    final GCAttributes attributes = GCAttributes.decode(stream);
	    
	    return new CreateGC(cid, drawable, attributes);
	}
	
	public CreateGC(GCONTEXT cid, DRAWABLE drawable, GCAttributes attributes) {
		this.cid = cid;
		this.drawable = drawable;
		this.attributes = attributes;
	}

	public GCONTEXT getCid() {
		return cid;
	}

	public DRAWABLE getDrawable() {
		return drawable;
	}

	public GCAttributes getAttributes() {
		return attributes;
	}

	@Override
	public void encode(XWindowsProtocolOutputStream stream) throws IOException {
		stream.writeGCONTEXT(cid);
		stream.writeDRAWABLE(drawable);
		
		attributes.encode(stream);
	}
}
