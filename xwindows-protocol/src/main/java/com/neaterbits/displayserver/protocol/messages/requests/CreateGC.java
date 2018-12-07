package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.util.logging.LogUtil;

public final class CreateGC extends Request {

	private final GCONTEXT cid;
	private final DRAWABLE drawable;
	
	private final GCAttributes attributes;

	public static CreateGC decode(XWindowsProtocolInputStream stream) throws IOException {
	    
	    readUnusedByte(stream);
	    
	    readRequestLength(stream);
	    
	    final GCONTEXT cid = stream.readGCONTEXT();
	    final DRAWABLE drawable = stream.readDRAWABLE();
	    
	    final GCAttributes attributes = GCAttributes.decode(stream);
	    
	    return new CreateGC(cid, drawable, attributes);
	}
	
	public CreateGC(GCONTEXT cid, DRAWABLE drawable, GCAttributes attributes) {
	    
	    Objects.requireNonNull(cid);
	    Objects.requireNonNull(drawable);
	    Objects.requireNonNull(attributes);
	    
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
    public Object[] getDebugParams() {
        return wrap(
                "cid", cid,
                "drawable", drawable,
                "attributes", LogUtil.outputParametersInBrackets(attributes.getDebugParams())
        );
    }

    @Override
	public void encode(XWindowsProtocolOutputStream stream) throws IOException {
		stream.writeGCONTEXT(cid);
		stream.writeDRAWABLE(drawable);
		
		attributes.encode(stream);
	}
}
