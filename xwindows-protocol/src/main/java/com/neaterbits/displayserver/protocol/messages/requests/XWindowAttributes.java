package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.neaterbits.displayserver.protocol.IntPadXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.IntPadXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.BackingStore;
import com.neaterbits.displayserver.protocol.types.BITGRAVITY;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.SETofDEVICEEVENT;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;
import com.neaterbits.displayserver.util.logging.LogUtil;

public final class XWindowAttributes extends XAttributes {

    public static final int BACKGROUND_PIXMAP   = 0x00000001;
    public static final int BACKGROUND_PIXEL    = 0x00000002;
    public static final int BORDER_PIXMAP       = 0x00000004;
    public static final int BORDER_PIXEL        = 0x00000008;
    public static final int BIT_GRAVITY         = 0x00000010;
    public static final int WIN_GRAVITY         = 0x00000020;
    public static final int BACKING_STORE       = 0x00000040;
    public static final int BACKING_PLANES      = 0x00000080;
    public static final int BACKING_PIXEL       = 0x00000100;
    public static final int OVERRIDE_REDIRECT   = 0x00000200;
    public static final int SAVE_UNDER          = 0x00000400;
    public static final int EVENT_MASK          = 0x00000800;
    public static final int DO_NOT_PROPAGATE_MASK = 0x00001000;
    public static final int COLOR_MAP           = 0x00002000;
    public static final int CURSOR              = 0x00004000;

    public static final int ALL_VALUE_MASK =
            BACKGROUND_PIXMAP
          | BACKGROUND_PIXEL
          | BORDER_PIXMAP
          | BORDER_PIXEL
          | BIT_GRAVITY
          | WIN_GRAVITY
          | BACKING_STORE
          | BACKING_PLANES
          | BACKING_PIXEL
          | OVERRIDE_REDIRECT
          | SAVE_UNDER
          | EVENT_MASK
          | DO_NOT_PROPAGATE_MASK
          | COLOR_MAP
          | CURSOR;

    public static final BITMASK ALL = new BITMASK(ALL_VALUE_MASK);
    
    private final PIXMAP backgroundPixmap;
    private final CARD32 backgroundPixel;
    private final PIXMAP borderPixmap;
    private final CARD32 borderPixel;
    private final BITGRAVITY bitGravity;
    private final WINGRAVITY winGravity;
    private final BYTE backingStore;
    private final CARD32 backingPlanes;
    private final CARD32 backingPixel;
    private final BOOL overrideRedirect;
    private final BOOL saveUnder;
    private final SETofEVENT eventMask;
    private final SETofDEVICEEVENT doNotPropagateMask;
    private final COLORMAP colormap;
    private final CURSOR cursor;
    
    public static XWindowAttributes DEFAULT_ATTRIBUTES = new XWindowAttributes(
            new BITMASK(ALL.getValue() & ~(BACKGROUND_PIXEL|BORDER_PIXEL)),
            PIXMAP.None, null,
            PIXMAP.None, new CARD32(0),
            BITGRAVITY.Forget, WINGRAVITY.NorthWest,
            BackingStore.NotUseful,
            new CARD32(0xFFFFFFFFL), new CARD32(0x00000000L),
            new BOOL(false),
            new BOOL(false),
            new SETofEVENT(0),
            new SETofDEVICEEVENT(0),
            COLORMAP.CopyFromParent,
            com.neaterbits.displayserver.protocol.types.CURSOR.None);
    
    public XWindowAttributes applyImmutably(XWindowAttributes other) {
        return new XWindowAttributes(this, other);
    }
    
    private XWindowAttributes(XWindowAttributes existing, XWindowAttributes toApply) {
        
        super(existing.getValueMask().bitwiseOr(toApply.getValueMask()));
        
        final BITMASK e = existing.getValueMask();
        final BITMASK a = toApply.getValueMask();
        
        this.backgroundPixmap   = returnIfSet(e, a, BACKGROUND_PIXMAP,   existing.backgroundPixmap,   toApply.backgroundPixmap);
        this.backgroundPixel    = returnIfSet(e, a, BACKGROUND_PIXEL,    existing.backgroundPixel,    toApply.backgroundPixel);
        this.borderPixmap       = returnIfSet(e, a, BORDER_PIXMAP,       existing.borderPixmap,       toApply.borderPixmap);
        this.borderPixel        = returnIfSet(e, a, BORDER_PIXEL,        existing.borderPixel,        toApply.borderPixel);
        this.bitGravity         = returnIfSet(e, a, BIT_GRAVITY,         existing.bitGravity,         toApply.bitGravity);
        this.winGravity         = returnIfSet(e, a, WIN_GRAVITY,         existing.winGravity,         toApply.winGravity);
        this.backingStore       = returnIfSet(e, a, BACKING_STORE,       existing.backingStore,       toApply.backingStore);
        this.backingPlanes      = returnIfSet(e, a, BACKING_PLANES,      existing.backingPlanes,      toApply.backingPlanes);
        this.backingPixel       = returnIfSet(e, a, BACKING_PIXEL,       existing.backingPixel,       toApply.backingPixel);
        this.overrideRedirect   = returnIfSet(e, a, OVERRIDE_REDIRECT,   existing.overrideRedirect,   toApply.overrideRedirect);
        this.saveUnder          = returnIfSet(e, a, SAVE_UNDER,          existing.saveUnder,          toApply.saveUnder);
        this.eventMask          = returnIfSet(e, a, EVENT_MASK,          existing.eventMask,          toApply.eventMask);
        this.doNotPropagateMask = returnIfSet(e, a, DO_NOT_PROPAGATE_MASK, existing.doNotPropagateMask, toApply.doNotPropagateMask);
        this.colormap           = returnIfSet(e, a, COLOR_MAP,           existing.colormap,           toApply.colormap);
        this.cursor             = returnIfSet(e, a, CURSOR,              existing.cursor,             toApply.cursor);
    }
    
    
    public XWindowAttributes(BITMASK valueMask, PIXMAP backgroundPixmap, CARD32 backgroundPixel, PIXMAP borderPixmap,
            CARD32 borderPixel, BITGRAVITY bitGravity, WINGRAVITY winGravity, BYTE backingStore, CARD32 backingPlanes,
            CARD32 backingPixel, BOOL overrideRedirect, BOOL saveUnder, SETofEVENT eventMask,
            SETofDEVICEEVENT doNotPropagateMask, COLORMAP colormap, CURSOR cursor) {
        super(valueMask);

        this.backgroundPixmap = backgroundPixmap;
        this.backgroundPixel = backgroundPixel;
        this.borderPixmap = borderPixmap;
        this.borderPixel = borderPixel;
        this.bitGravity = bitGravity;
        this.winGravity = winGravity;
        this.backingStore = backingStore;
        this.backingPlanes = backingPlanes;
        this.backingPixel = backingPixel;
        this.overrideRedirect = overrideRedirect;
        this.saveUnder = saveUnder;
        this.eventMask = eventMask;
        this.doNotPropagateMask = doNotPropagateMask;
        this.colormap = colormap;
        this.cursor = cursor;
    }

    public static XWindowAttributes decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BITMASK bitmask = stream.readBITMASK();

        final IntPadXWindowsProtocolInputStream padStream = new IntPadXWindowsProtocolInputStream(stream);

        return new XWindowAttributes(
                bitmask,
                readIfSet(bitmask, BACKGROUND_PIXMAP,   padStream::readPIXMAP),
                readIfSet(bitmask, BACKGROUND_PIXEL,    padStream::readCARD32),
                readIfSet(bitmask, BORDER_PIXMAP,       padStream::readPIXMAP),
                readIfSet(bitmask, BORDER_PIXEL,        padStream::readCARD32),
                readIfSet(bitmask, BIT_GRAVITY,         padStream::readBITGRAVITY),
                readIfSet(bitmask, WIN_GRAVITY,         padStream::readWINGRAVITY),
                readIfSet(bitmask, BACKING_STORE,       padStream::readBYTE),
                readIfSet(bitmask, BACKING_PLANES,      padStream::readCARD32),
                readIfSet(bitmask, BACKING_PIXEL,       padStream::readCARD32),
                readIfSet(bitmask, OVERRIDE_REDIRECT,   padStream::readBOOL),
                readIfSet(bitmask, SAVE_UNDER,          padStream::readBOOL),
                readIfSet(bitmask, EVENT_MASK,          padStream::readSETofEVENT),
                readIfSet(bitmask, DO_NOT_PROPAGATE_MASK, padStream::readSETofDEVICEEVENT),
                readIfSet(bitmask, COLOR_MAP,           padStream::readCOLORMAP),
                readIfSet(bitmask, CURSOR,              padStream::readCURSOR));
        
    }

    @Override
    public Object[] getDebugParams() {
        
        final List<Object> params = new ArrayList<>();
        
        params.add("bitmask");
        params.add(super.getValueMask());

        final String eventMaskString = eventMask != null
                ? LogUtil.join(eventMask.getEventStrings(), "|")
                : null;
        
        addIfSet(params, "bgPixmap",        backgroundPixmap,    BACKGROUND_PIXMAP);
        addIfSet(params, "bgPixel",         hex32(backgroundPixel), BACKGROUND_PIXEL);
        addIfSet(params, "borderPixmap",    borderPixmap,        BORDER_PIXMAP);
        addIfSet(params, "borderPixel",     hex32(borderPixel),  BORDER_PIXEL);
        addIfSet(params, "bitGravity",      bitGravity,          BIT_GRAVITY);
        addIfSet(params, "winGravity",      winGravity,          WIN_GRAVITY);
        addIfSet(params, "backingStore",    backingStore,        BACKING_STORE);
        addIfSet(params, "backingPlanes",   backingPlanes,       BACKING_PLANES);
        addIfSet(params, "backingPixel",    hex32(backingPixel), BACKING_PIXEL);
        addIfSet(params, "overrideRedirect", overrideRedirect,   OVERRIDE_REDIRECT);
        addIfSet(params, "saveUnder",       saveUnder,           SAVE_UNDER);
        addIfSet(params, "eventMask",       eventMaskString,           EVENT_MASK);
        addIfSet(params, "doNotPropagateMask", doNotPropagateMask,  DO_NOT_PROPAGATE_MASK);
        addIfSet(params, "colorMap",        colormap,            COLOR_MAP);
        addIfSet(params, "cursor",          cursor,              CURSOR);
        
        return params.toArray(new Object[params.size()]);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        super.encode(stream);
     
        final IntPadXWindowsProtocolOutputStream padStream = new IntPadXWindowsProtocolOutputStream(stream);

        writeIfSet(backgroundPixmap,    BACKGROUND_PIXMAP,  padStream::writePIXMAP);
        writeIfSet(backgroundPixel,     BACKGROUND_PIXEL,   padStream::writeCARD32);
        writeIfSet(borderPixmap,        BORDER_PIXMAP,      padStream::writePIXMAP);
        writeIfSet(borderPixel,         BORDER_PIXEL,       padStream::writeCARD32);
        writeIfSet(bitGravity,          BIT_GRAVITY,        padStream::writeBITGRAVITY);
        writeIfSet(winGravity,          WIN_GRAVITY,        padStream::writeWINGRAVITY);
        writeIfSet(backingStore,        BACKING_STORE,      padStream::writeBYTE);
        writeIfSet(backingPlanes,       BACKING_PLANES,     padStream::writeCARD32);
        writeIfSet(backingPixel,        BACKING_PIXEL,      padStream::writeCARD32);
        writeIfSet(overrideRedirect,    OVERRIDE_REDIRECT,  padStream::writeBOOL);
        writeIfSet(saveUnder,           SAVE_UNDER,         padStream::writeBOOL);
        writeIfSet(eventMask,           EVENT_MASK,         padStream::writeSETofEVENT);
        writeIfSet(doNotPropagateMask,  DO_NOT_PROPAGATE_MASK, padStream::writeSETofDEVICEEVENT);
        writeIfSet(colormap,            COLOR_MAP,          padStream::writeCOLORMAP);
        writeIfSet(cursor,              CURSOR,             padStream::writeCURSOR);
    }

    public PIXMAP getBackgroundPixmap() {
        return backgroundPixmap;
    }

    public CARD32 getBackgroundPixel() {
        return backgroundPixel;
    }

    public PIXMAP getBorderPixmap() {
        return borderPixmap;
    }

    public CARD32 getBorderPixel() {
        return borderPixel;
    }

    public BITGRAVITY getBitGravity() {
        return bitGravity;
    }

    public WINGRAVITY getWinGravity() {
        return winGravity;
    }

    public BYTE getBackingStore() {
        return backingStore;
    }

    public CARD32 getBackingPlanes() {
        return backingPlanes;
    }

    public CARD32 getBackingPixel() {
        return backingPixel;
    }

    public BOOL getOverrideRedirect() {
        return overrideRedirect;
    }

    public BOOL getSaveUnder() {
        return saveUnder;
    }

    public SETofEVENT getEventMask() {
        return eventMask;
    }

    public SETofDEVICEEVENT getDoNotPropagateMask() {
        return doNotPropagateMask;
    }

    public COLORMAP getColormap() {
        return colormap;
    }
}
