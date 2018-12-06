package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.IntPadXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.IntPadXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public final class GCAttributes extends Attributes {
	
	public static final int FUNCTION   = 0x00000001;
	public static final int PLANE_MASK = 0x00000002;
	public static final int FOREGROUND = 0x00000004;
	public static final int BACKGROUND = 0x00000008;
	public static final int LINE_WIDTH = 0x00000010;
	public static final int LINE_STYLE = 0x00000020;
	public static final int CAP_STYLE  = 0x00000040;
	public static final int JOIN_STYLE = 0x00000080;
	public static final int FILL_STYLE = 0x00000100;
	public static final int FILL_RULE  = 0x00000200;
	public static final int TILE       = 0x00000400;
	public static final int STIPPLE    = 0x00000800;
	public static final int TILE_STIPPLE_X_ORIGIN = 0x00001000;
	public static final int TILE_STIPPLE_Y_ORIGIN = 0x00002000;
	public static final int FONT       = 0x00004000;
	public static final int SUBWINDOW_MODE      = 0x00008000;
    public static final int GRAPHICS_EXPOSURES  = 0x00010000;
    public static final int CLIP_X_ORIGIN       = 0x00020000;
    public static final int CLIP_Y_ORIGIN       = 0x00040000; 
    public static final int CLIP_MASK   = 0x00080000;
    public static final int DASH_OFFSET = 0x00100000;
    public static final int DASHES      = 0x00200000;
    public static final int ARC_MODE    = 0x00400000;
	
	private final BYTE function;
	
	private final CARD32 planeMask;
	private final CARD32 foreground;
	private final CARD32 background;
	
	private final CARD16 lineWidth;
	
	private final BYTE lineStyle;
	private final BYTE joinStyle;
	private final BYTE fillStyle;
	private final BYTE fillRule;
	
	private final PIXMAP tile;
	private final PIXMAP stipple;
	
	private final INT16 tileStippleXOrigin;
	private final INT16 tileStippleYOrigin;
	
	private final FONT font;
	
	private final BYTE subwindowMode;
	
	private final BOOL graphicsExposures;
	
	private final INT16 clipXOrigin;
	private final INT16 clipYOrigin;
	
	private final PIXMAP clipMask;
	
	private final CARD16 dashOffset;
	
	private final CARD8 dashes;
	
	private final BYTE arcMode;

	public GCAttributes(BITMASK valueMask, BYTE function, CARD32 planeMask, CARD32 foreground, CARD32 background,
			CARD16 lineWidth, BYTE lineStyle, BYTE joinStyle, BYTE fillStyle, BYTE fillRule, PIXMAP tile,
			PIXMAP stipple, INT16 tileStippleXOrigin, INT16 tileStippleYOrigin, FONT font, BYTE subwindowMode,
			BOOL graphicsExposures, INT16 clipXOrigin, INT16 clipYOrigin, PIXMAP clipMask, CARD16 dashOffset,
			CARD8 dashes, BYTE arcMode) {

	    super(valueMask);
	    
		this.function = function;
		this.planeMask = planeMask;
		this.foreground = foreground;
		this.background = background;
		this.lineWidth = lineWidth;
		this.lineStyle = lineStyle;
		this.joinStyle = joinStyle;
		this.fillStyle = fillStyle;
		this.fillRule = fillRule;
		this.tile = tile;
		this.stipple = stipple;
		this.tileStippleXOrigin = tileStippleXOrigin;
		this.tileStippleYOrigin = tileStippleYOrigin;
		this.font = font;
		this.subwindowMode = subwindowMode;
		this.graphicsExposures = graphicsExposures;
		this.clipXOrigin = clipXOrigin;
		this.clipYOrigin = clipYOrigin;
		this.clipMask = clipMask;
		this.dashOffset = dashOffset;
		this.dashes = dashes;
		this.arcMode = arcMode;
	}

    public static GCAttributes decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BITMASK bitmask = stream.readBITMASK();

        final IntPadXWindowsProtocolInputStream padStream = new IntPadXWindowsProtocolInputStream(stream);
    
        return new GCAttributes(
                bitmask,
                readIfSet(bitmask, FUNCTION,            padStream::readBYTE),
                readIfSet(bitmask, PLANE_MASK,          padStream::readCARD32),
                readIfSet(bitmask, FOREGROUND,          padStream::readCARD32),
                readIfSet(bitmask, BACKGROUND,          padStream::readCARD32),
                readIfSet(bitmask, LINE_WIDTH,          padStream::readCARD16),
                readIfSet(bitmask, LINE_STYLE,          padStream::readBYTE),
                readIfSet(bitmask, JOIN_STYLE,          padStream::readBYTE),
                readIfSet(bitmask, FILL_STYLE,          padStream::readBYTE),
                readIfSet(bitmask, FILL_RULE,           padStream::readBYTE),
                readIfSet(bitmask, TILE,                padStream::readPIXMAP),
                readIfSet(bitmask, STIPPLE,             padStream::readPIXMAP),
                readIfSet(bitmask, TILE_STIPPLE_X_ORIGIN, padStream::readINT16),
                readIfSet(bitmask, TILE_STIPPLE_Y_ORIGIN, padStream::readINT16),
                readIfSet(bitmask, FONT,                padStream::readFONT),
                readIfSet(bitmask, SUBWINDOW_MODE,      padStream::readBYTE),
                readIfSet(bitmask, GRAPHICS_EXPOSURES,  padStream::readBOOL),
                readIfSet(bitmask, CLIP_X_ORIGIN,       padStream::readINT16),
                readIfSet(bitmask, CLIP_Y_ORIGIN,       padStream::readINT16),
                readIfSet(bitmask, CLIP_MASK,           padStream::readPIXMAP),
                readIfSet(bitmask, DASH_OFFSET,         padStream::readCARD16),
                readIfSet(bitmask, DASHES,              padStream::readCARD8),
                readIfSet(bitmask, ARC_MODE,            padStream::readBYTE));
    }

    @Override
	public void encode(XWindowsProtocolOutputStream stream) throws IOException {
		
	    super.encode(stream);
	 
	    final IntPadXWindowsProtocolOutputStream padStream = new IntPadXWindowsProtocolOutputStream(stream);
	    
	    writeIfSet(function,       FUNCTION,           padStream::writeBYTE);
	    writeIfSet(planeMask,      PLANE_MASK,         padStream::writeCARD32);
	    writeIfSet(foreground,     FOREGROUND,         padStream::writeCARD32);
	    writeIfSet(background,     BACKGROUND,         padStream::writeCARD32);
	    writeIfSet(lineWidth,      LINE_WIDTH,         padStream::writeCARD16);
	    writeIfSet(lineStyle,      LINE_STYLE,         padStream::writeBYTE);
	    writeIfSet(joinStyle,      JOIN_STYLE,         padStream::writeBYTE);
	    writeIfSet(fillStyle,      FILL_STYLE,         padStream::writeBYTE);
	    writeIfSet(fillRule,       FILL_RULE,          padStream::writeBYTE);
	    writeIfSet(tile,           TILE,               padStream::writePIXMAP);
	    writeIfSet(stipple,        STIPPLE,            padStream::writePIXMAP);
	    writeIfSet(tileStippleXOrigin, TILE_STIPPLE_X_ORIGIN, padStream::writeINT16);
	    writeIfSet(tileStippleYOrigin, TILE_STIPPLE_Y_ORIGIN, padStream::writeINT16);
	    writeIfSet(font,           FONT,               padStream::writeFONT);
	    writeIfSet(subwindowMode,  SUBWINDOW_MODE,     padStream::writeBYTE);
	    writeIfSet(graphicsExposures, GRAPHICS_EXPOSURES, padStream::writeBOOL);
	    writeIfSet(clipXOrigin,    CLIP_X_ORIGIN,      padStream::writeINT16);
	    writeIfSet(clipYOrigin,    CLIP_Y_ORIGIN,      padStream::writeINT16);
	    writeIfSet(clipMask,       CLIP_MASK,          padStream::writePIXMAP);
	    writeIfSet(dashOffset,     DASH_OFFSET,        padStream::writeCARD16);
	    writeIfSet(dashes,         DASHES,             padStream::writeCARD8);
	    writeIfSet(arcMode,        ARC_MODE,           padStream::writeBYTE);
	}

	public BYTE getFunction() {
		return function;
	}

	public CARD32 getPlaneMask() {
		return planeMask;
	}

	public CARD32 getForeground() {
		return foreground;
	}

	public CARD32 getBackground() {
		return background;
	}

	public CARD16 getLineWidth() {
		return lineWidth;
	}

	public BYTE getLineStyle() {
		return lineStyle;
	}

	public BYTE getJoinStyle() {
		return joinStyle;
	}

	public BYTE getFillStyle() {
		return fillStyle;
	}

	public BYTE getFillRule() {
		return fillRule;
	}

	public PIXMAP getTile() {
		return tile;
	}

	public PIXMAP getStipple() {
		return stipple;
	}

	public INT16 getTileStippleXOrigin() {
		return tileStippleXOrigin;
	}

	public INT16 getTileStippleYOrigin() {
		return tileStippleYOrigin;
	}

	public FONT getFont() {
		return font;
	}

	public BYTE getSubwindowMode() {
		return subwindowMode;
	}

	public BOOL getGraphicsExposures() {
		return graphicsExposures;
	}

	public INT16 getClipXOrigin() {
		return clipXOrigin;
	}

	public INT16 getClipYOrigin() {
		return clipYOrigin;
	}

	public PIXMAP getClipMask() {
		return clipMask;
	}

	public CARD16 getDashOffset() {
		return dashOffset;
	}

	public CARD8 getDashes() {
		return dashes;
	}

	public BYTE getArcMode() {
		return arcMode;
	}
}
