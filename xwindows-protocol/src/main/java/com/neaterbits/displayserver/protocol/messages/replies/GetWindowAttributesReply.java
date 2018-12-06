package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.BITGRAVITY;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.SETofDEVICEEVENT;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;

public final class GetWindowAttributesReply extends Reply {

    private final BYTE backingStore;
    
    private final VISUALID visual;
    
    private final CARD16 windowClass;
    
    private final BITGRAVITY bitGravity;
    private final WINGRAVITY winGravity;
    
    private final CARD32 backingPlanes;
    private final CARD32 backingPixel;
    
    private final BOOL saveUnder;
    private final BOOL mapIsInstalled;
    
    private final BYTE mapState;
    
    private final BOOL overrideRedirect;
    
    private final COLORMAP colormap;
    
    private final SETofEVENT allEventMasks;
    private final SETofEVENT yourEventMasks;
    
    private final SETofDEVICEEVENT doNotPropagateMask;

    public GetWindowAttributesReply(CARD16 sequenceNumber, BYTE backingStore, VISUALID visual, CARD16 windowClass,
            BITGRAVITY bitGravity, WINGRAVITY winGravity, CARD32 backingPlanes, CARD32 backingPixel, BOOL saveUnder,
            BOOL mapIsInstalled, BYTE mapState, BOOL overrideRedirect, COLORMAP colormap, SETofEVENT allEventMasks,
            SETofEVENT yourEventMasks, SETofDEVICEEVENT doNotPropagateMask) {
        super(sequenceNumber);
    
        this.backingStore = backingStore;
        this.visual = visual;
        this.windowClass = windowClass;
        this.bitGravity = bitGravity;
        this.winGravity = winGravity;
        this.backingPlanes = backingPlanes;
        this.backingPixel = backingPixel;
        this.saveUnder = saveUnder;
        this.mapIsInstalled = mapIsInstalled;
        this.mapState = mapState;
        this.overrideRedirect = overrideRedirect;
        this.colormap = colormap;
        this.allEventMasks = allEventMasks;
        this.yourEventMasks = yourEventMasks;
        this.doNotPropagateMask = doNotPropagateMask;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeReplyHeader(stream);
        
        stream.writeBYTE(backingStore);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 3);
        
        stream.writeVISUALID(visual);
        
        stream.writeCARD16(windowClass);
        
        stream.writeBITGRAVITY(bitGravity);
        stream.writeWINGRAVITY(winGravity);
        
        stream.writeCARD32(backingPlanes);
        stream.writeCARD32(backingPixel);
        
        stream.writeBOOL(saveUnder);
        
        stream.writeBOOL(mapIsInstalled);
        stream.writeBYTE(mapState);
        
        stream.writeBOOL(overrideRedirect);
        
        stream.writeCOLORMAP(colormap);
        
        stream.writeSETofEVENT(allEventMasks);
        stream.writeSETofEVENT(yourEventMasks);
        
        stream.writeSETofDEVICEEVENT(doNotPropagateMask);
        
        writeUnusedCARD16(stream);
    }
}
