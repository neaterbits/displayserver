package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.replies.GrabPointerReply;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.SETofPOINTEREVENT;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class GrabPointer extends XRequest {

    private final BOOL ownerEvents;
    private final WINDOW grabWindow;
    private final SETofPOINTEREVENT eventMask;
    private final BYTE pointerMode;
    private final BYTE keyboardMode;
    private final WINDOW confineTo;
    private final CURSOR cursor;
    private final TIMESTAMP timestamp;

    public static GrabPointer decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BOOL ownerEvents = stream.readBOOL();
        
        readRequestLength(stream);
        
        return new GrabPointer(
                ownerEvents,
                stream.readWINDOW(),
                stream.readSETofPOINTEREVENT(),
                stream.readBYTE(),
                stream.readBYTE(),
                stream.readWINDOW(),
                stream.readCURSOR(),
                stream.readTIMESTAMP());
    }
    
    public GrabPointer(BOOL ownerEvents, WINDOW grabWindow, SETofPOINTEREVENT eventMask, BYTE pointerMode,
            BYTE keyboardMode, WINDOW confineTo, CURSOR cursor, TIMESTAMP timestamp) {

        Objects.requireNonNull(ownerEvents);
        Objects.requireNonNull(grabWindow);
        Objects.requireNonNull(eventMask);
        Objects.requireNonNull(pointerMode);
        Objects.requireNonNull(keyboardMode);
        Objects.requireNonNull(confineTo);
        Objects.requireNonNull(cursor);
        Objects.requireNonNull(timestamp);
        
        this.ownerEvents = ownerEvents;
        this.grabWindow = grabWindow;
        this.eventMask = eventMask;
        this.pointerMode = pointerMode;
        this.keyboardMode = keyboardMode;
        this.confineTo = confineTo;
        this.cursor = cursor;
        this.timestamp = timestamp;
    }

    public BOOL getOwnerEvents() {
        return ownerEvents;
    }

    public WINDOW getGrabWindow() {
        return grabWindow;
    }

    public SETofPOINTEREVENT getEventMask() {
        return eventMask;
    }

    public BYTE getPointerMode() {
        return pointerMode;
    }

    public BYTE getKeyboardMode() {
        return keyboardMode;
    }

    public WINDOW getConfineTo() {
        return confineTo;
    }

    public CURSOR getCursor() {
        return cursor;
    }

    public TIMESTAMP getTimestamp() {
        return timestamp;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "ownerEvents",ownerEvents,
                "grabWindow",grabWindow,
                "eventMask",eventMask,
                "pointerMode",pointerMode,
                "keyboardMode",keyboardMode,
                "confineTo",confineTo,
                "cursor",cursor,
                "timestamp",timestamp
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBOOL(ownerEvents);
        stream.writeWINDOW(grabWindow);
        stream.writeSETofPOINTEREVENT(eventMask);
        stream.writeBYTE(pointerMode);
        stream.writeBYTE(keyboardMode);
        stream.writeWINDOW(confineTo);
        stream.writeCURSOR(cursor);
        stream.writeTIMESTAMP(timestamp);
    }

    @Override
    public int getOpCode() {
        return OpCodes.GRAB_POINTER;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return GrabPointerReply.class;
    }
}
