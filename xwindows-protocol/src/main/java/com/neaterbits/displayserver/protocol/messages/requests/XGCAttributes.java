package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.neaterbits.displayserver.protocol.IntPadXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.IntPadXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.gc.ArcMode;
import com.neaterbits.displayserver.protocol.enums.gc.CapStyle;
import com.neaterbits.displayserver.protocol.enums.gc.FillRule;
import com.neaterbits.displayserver.protocol.enums.gc.FillStyle;
import com.neaterbits.displayserver.protocol.enums.gc.Function;
import com.neaterbits.displayserver.protocol.enums.gc.JoinStyle;
import com.neaterbits.displayserver.protocol.enums.gc.LineStyle;
import com.neaterbits.displayserver.protocol.enums.gc.SubwindowMode;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public final class XGCAttributes extends XAttributes {
	
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
	
    public static final BITMASK ALL = new BITMASK(
              FUNCTION
            | PLANE_MASK 
            | FOREGROUND
            | BACKGROUND
            | LINE_WIDTH
            | LINE_STYLE
            | CAP_STYLE
            | JOIN_STYLE
            | FILL_STYLE
            | FILL_RULE
            | TILE
            | STIPPLE
            | TILE_STIPPLE_X_ORIGIN
            | TILE_STIPPLE_Y_ORIGIN
            | FONT
            | SUBWINDOW_MODE
            | GRAPHICS_EXPOSURES
            | CLIP_X_ORIGIN
            | CLIP_Y_ORIGIN
            | CLIP_MASK
            | DASH_OFFSET
            | DASHES
            | ARC_MODE
    );

    
	private final BYTE function;
	
	private final CARD32 planeMask;
	private final CARD32 foreground;
	private final CARD32 background;
	
	private final CARD16 lineWidth;
	
	private final BYTE lineStyle;
	private final BYTE capStyle;
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

	public static final XGCAttributes DEFAULT_ATTRIBUTES = new XGCAttributes(
	        new BITMASK(ALL.getValue() & ~FONT),
	        Function.Copy,
	        new CARD32(0xFFFFFFFFL),
	        new CARD32(0), new CARD32(1),
	        new CARD16(0),
	        LineStyle.Solid, CapStyle.Butt, JoinStyle.Miter,
	        FillStyle.Solid, FillRule.EvenOdd,
	        PIXMAP.None,
	        PIXMAP.None, new INT16((short)0), new INT16((short)0),
	        null,
	        SubwindowMode.ClipByChildren,
	        new BOOL(true),
	        new INT16((short)0), new INT16((short)0), PIXMAP.None,
	        new CARD16(0), new CARD8((short)(4 << 4 | 4)),
	        ArcMode.PieSlice);
	
    public XGCAttributes applyImmutably(XGCAttributes other) {
        return new XGCAttributes(this, other);
    }
    
    private XGCAttributes(XGCAttributes existing, XGCAttributes toApply) {
        super(existing.getValueMask().bitwiseOr(toApply.getValueMask()));
        
        final BITMASK e = existing.getValueMask();
        final BITMASK a = toApply.getValueMask();
        
        this.function       = returnIfSet(e, a, FUNCTION,           existing.function,      toApply.function);
        this.planeMask      = returnIfSet(e, a, PLANE_MASK,         existing.planeMask,     toApply.planeMask);
        this.foreground     = returnIfSet(e, a, FOREGROUND,         existing.foreground,    toApply.foreground);
        this.background     = returnIfSet(e, a, BACKGROUND,         existing.background,    toApply.background);
        this.lineWidth      = returnIfSet(e, a, LINE_WIDTH,         existing.lineWidth,     toApply.lineWidth);
        this.lineStyle      = returnIfSet(e, a, LINE_STYLE,         existing.lineStyle,     toApply.lineStyle);
        this.capStyle       = returnIfSet(e, a, CAP_STYLE,          existing.capStyle,      toApply.capStyle);
        this.joinStyle      = returnIfSet(e, a, JOIN_STYLE,         existing.joinStyle,     toApply.joinStyle);
        this.fillStyle      = returnIfSet(e, a, FILL_STYLE,         existing.fillStyle,     toApply.fillStyle);
        this.fillRule       = returnIfSet(e, a, FILL_RULE,          existing.fillRule,      toApply.fillRule);
        this.tile           = returnIfSet(e, a, TILE,               existing.tile,          toApply.tile);
        this.stipple        = returnIfSet(e, a, STIPPLE,            existing.stipple,       toApply.stipple);
        this.tileStippleXOrigin = returnIfSet(e, a, TILE_STIPPLE_X_ORIGIN, existing.tileStippleXOrigin, toApply.tileStippleXOrigin);
        this.tileStippleYOrigin = returnIfSet(e, a, TILE_STIPPLE_Y_ORIGIN, existing.tileStippleYOrigin, toApply.tileStippleYOrigin);
        this.font           = returnIfSet(e, a, FONT,               existing.font,          toApply.font);
        this.subwindowMode  = returnIfSet(e, a, SUBWINDOW_MODE, existing.subwindowMode, toApply.subwindowMode);
        this.graphicsExposures = returnIfSet(e, a, GRAPHICS_EXPOSURES, existing.graphicsExposures, toApply.graphicsExposures);
        this.clipXOrigin    = returnIfSet(e, a, CLIP_X_ORIGIN,      existing.clipXOrigin,   toApply.clipXOrigin);
        this.clipYOrigin    = returnIfSet(e, a, CLIP_Y_ORIGIN,      existing.clipYOrigin,   toApply.clipYOrigin);
        this.clipMask       = returnIfSet(e, a, CLIP_MASK,          existing.clipMask,      toApply.clipMask);
        this.dashOffset     = returnIfSet(e, a, DASH_OFFSET,        existing.dashOffset,    toApply.dashOffset);
        this.dashes         = returnIfSet(e, a, DASHES,             existing.dashes,        toApply.dashes);
        this.arcMode        = returnIfSet(e, a, ARC_MODE,           existing.arcMode,       toApply.arcMode);
    }

	
	public XGCAttributes(BITMASK valueMask, BYTE function, CARD32 planeMask, CARD32 foreground, CARD32 background,
			CARD16 lineWidth, BYTE lineStyle, BYTE capStyle, BYTE joinStyle, BYTE fillStyle, BYTE fillRule, PIXMAP tile,
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
		this.capStyle = capStyle;
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

    public static XGCAttributes decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BITMASK bitmask = stream.readBITMASK();

        final IntPadXWindowsProtocolInputStream padStream = new IntPadXWindowsProtocolInputStream(stream);
    
        return new XGCAttributes(
                bitmask,
                readIfSet(bitmask, FUNCTION,            padStream::readBYTE),
                readIfSet(bitmask, PLANE_MASK,          padStream::readCARD32),
                readIfSet(bitmask, FOREGROUND,          padStream::readCARD32),
                readIfSet(bitmask, BACKGROUND,          padStream::readCARD32),
                readIfSet(bitmask, LINE_WIDTH,          padStream::readCARD16),
                readIfSet(bitmask, LINE_STYLE,          padStream::readBYTE),
                readIfSet(bitmask, CAP_STYLE,           padStream::readBYTE),
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
    public Object[] getDebugParams() {
        
        final List<Object> params = new ArrayList<>();

        addIfSet(params, "function",    function,       FUNCTION);
        addIfSet(params, "planeMask",   planeMask,      PLANE_MASK);
        addIfSet(params, "fg",          hex32(foreground),     FOREGROUND);
        addIfSet(params, "bg",          hex32(background),     BACKGROUND);
        addIfSet(params, "linewidth",   lineWidth,      LINE_WIDTH);
        addIfSet(params, "lineStyle",   lineStyle,      LINE_STYLE);
        addIfSet(params, "capStyle",    capStyle,       CAP_STYLE);
        addIfSet(params, "joinStyle",   joinStyle,      JOIN_STYLE);
        addIfSet(params, "fillStyle",   fillStyle,      FILL_STYLE);
        addIfSet(params, "fillRule",    fillRule,       FILL_RULE);
        addIfSet(params, "tile",        tile,           TILE);
        addIfSet(params, "stipple",     stipple,        STIPPLE);
        addIfSet(params, "tileStippleXOrigin", tileStippleXOrigin, TILE_STIPPLE_X_ORIGIN);
        addIfSet(params, "tileStippleYOrigin", tileStippleYOrigin, TILE_STIPPLE_Y_ORIGIN);
        addIfSet(params, "font",        font,           FONT);
        addIfSet(params, "subwindowMode", subwindowMode,  SUBWINDOW_MODE);
        addIfSet(params, "graphicsExposures", graphicsExposures, GRAPHICS_EXPOSURES);
        addIfSet(params, "clipXOrigin", clipXOrigin,    CLIP_X_ORIGIN);
        addIfSet(params, "clipYOrigin", clipYOrigin,    CLIP_Y_ORIGIN);
        addIfSet(params, "clipMask",    clipMask,       CLIP_MASK);
        addIfSet(params, "dashOffset",  dashOffset,     DASH_OFFSET);
        addIfSet(params, "dashes",      dashes,         DASHES);
        addIfSet(params, "arcMod",      arcMode,        ARC_MODE);
        
        return params.toArray(new Object[params.size()]);
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
        writeIfSet(capStyle,       CAP_STYLE,          padStream::writeBYTE);
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

	public BYTE getCapStyle() {
        return capStyle;
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
