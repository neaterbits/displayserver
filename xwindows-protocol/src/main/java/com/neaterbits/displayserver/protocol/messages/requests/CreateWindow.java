package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.util.logging.LogUtil;

public final class CreateWindow extends Request {

	private final CARD8 depth;
	
	private final WINDOW wid;
	private final WINDOW parent;
	
	private final INT16 x;
	private final INT16 y;
	
	private final CARD16 width;
	private final CARD16 height;
	
	private final CARD16 borderWidth;
	
	private final CARD16 windowClass;
	
	private final VISUALID visual;
	
	private final WindowAttributes attributes;

   public static CreateWindow decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final CARD8 depth = stream.readCARD8();
        
        // message length
        readRequestLength(stream);
        
        final WINDOW wid = stream.readWINDOW();
        final WINDOW parent = stream.readWINDOW();
        
        final INT16 x = stream.readINT16();
        final INT16 y = stream.readINT16();
        
        final CARD16 width = stream.readCARD16();
        final CARD16 height = stream.readCARD16();
        
        final CARD16 borderWidth = stream.readCARD16();
        final CARD16 windowClass = stream.readCARD16();
        
        final VISUALID visual = stream.readVISUALID();
        
        return new CreateWindow(
                depth,
                
                wid, parent,
                x, y,
                width, height,
                borderWidth,
                windowClass,
                visual,
                WindowAttributes.decode(stream));
   }

	public CreateWindow(
			CARD8 depth,
			WINDOW wid, WINDOW parent,
			INT16 x, INT16 y,
			CARD16 width, CARD16 height,
			CARD16 borderWidth,
			CARD16 windowClass,
			VISUALID visual,
			WindowAttributes attributes) {

	    Objects.requireNonNull(attributes);
	    
		this.depth = depth;
		this.wid = wid;
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.borderWidth = borderWidth;
		this.windowClass = windowClass;
		this.visual = visual;
		this.attributes = attributes;
	}

	@Override
    public Object[] getDebugParams() {
        return wrap(
                "depth", depth,
                "wid", wid,
                "parent", parent,
                "x", x,
                "y", y,
                "width", width,
                "height", height,
                "borderWidth", borderWidth,
                "windowClass", windowClass,
                "visual", visual,
                "attributes", LogUtil.outputParametersInBrackets(attributes.getDebugParams())
        );
    }

    @Override
	public void encode(XWindowsProtocolOutputStream stream) throws IOException {

	    writeOpCode(stream);
	    
		stream.writeCARD8(depth);
		
		writeRequestLength(stream, 8 + attributes.getCount());
		
		stream.writeWINDOW(wid);
		stream.writeWINDOW(parent);
		
		stream.writeINT16(x);
		stream.writeINT16(y);
	
		stream.writeCARD16(width);
		stream.writeCARD16(height);
		
		stream.writeCARD16(borderWidth);
		stream.writeCARD16(windowClass);
		
		stream.writeVISUALID(visual);
		
		if (attributes != null) {
		    attributes.encode(stream);
		}
	}
    
	@Override
    public int getOpCode() {
        return OpCodes.CREATE_WINDOW;
    }

    public CARD8 getDepth() {
		return depth;
	}

	public WINDOW getWid() {
		return wid;
	}

	public WINDOW getParent() {
		return parent;
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

	public CARD16 getWindowClass() {
		return windowClass;
	}

	public VISUALID getVisual() {
		return visual;
	}

	public WindowAttributes getAttributes() {
		return attributes;
	}
}

