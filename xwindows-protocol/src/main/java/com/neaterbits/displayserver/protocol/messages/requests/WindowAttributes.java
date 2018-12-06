package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.IntPadXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.IntPadXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
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

public final class WindowAttributes extends Attributes {

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

    public static final BITMASK ALL = new BITMASK(
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
            | CURSOR
    );
    
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
    
    public WindowAttributes(BITMASK valueMask, PIXMAP backgroundPixmap, CARD32 backgroundPixel, PIXMAP borderPixmap,
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

    public static WindowAttributes decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BITMASK bitmask = stream.readBITMASK();

        final IntPadXWindowsProtocolInputStream padStream = new IntPadXWindowsProtocolInputStream(stream);

        return new WindowAttributes(
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
    void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
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
