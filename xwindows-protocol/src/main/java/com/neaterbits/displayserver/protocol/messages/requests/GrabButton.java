package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BUTTON;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.SETofKEYMASK;
import com.neaterbits.displayserver.protocol.types.SETofPOINTEREVENT;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class GrabButton extends Request {

    private final BOOL ownerEvents;
    private final WINDOW grabWindow;
    
    private final SETofPOINTEREVENT eventMask;
    
    private final BYTE pointerMode;
    private final BYTE keyboardMode;
    
    private final WINDOW confineTo;
    
    private final CURSOR cursor;
    
    private final BUTTON button;
    
    private final SETofKEYMASK modifiers;

    public static GrabButton decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BOOL ownerEvents = stream.readBOOL();
        
        readRequestLength(stream);

        final WINDOW grabWindow = stream.readWINDOW();
        final SETofPOINTEREVENT eventMask = stream.readSETofPOINTEREVENT();
        final BYTE pointerMode = stream.readBYTE();
        final BYTE keyboardMode = stream.readBYTE();
        final WINDOW confineTo = stream.readWINDOW();
        final CURSOR cursor = stream.readCURSOR();
        final BUTTON button = stream.readBUTTON();
        
        readUnusedByte(stream);
        
        final SETofKEYMASK modifiers = stream.readSETofKEYMASK();
        
        return new GrabButton(
                ownerEvents,
                grabWindow,
                eventMask,
                pointerMode,
                keyboardMode,
                confineTo,
                cursor,
                button,
                modifiers);
    }

    public GrabButton(BOOL ownerEvents, WINDOW grabWindow, SETofPOINTEREVENT eventMask, BYTE pointerMode, BYTE keyboardMode,
            WINDOW confineTo, CURSOR cursor, BUTTON button, SETofKEYMASK modifiers) {
        
        Objects.requireNonNull(ownerEvents);
        Objects.requireNonNull(grabWindow);
        Objects.requireNonNull(eventMask);
        Objects.requireNonNull(pointerMode);
        Objects.requireNonNull(keyboardMode);
        Objects.requireNonNull(confineTo);
        Objects.requireNonNull(cursor);
        Objects.requireNonNull(button);
        Objects.requireNonNull(modifiers);
        
        this.ownerEvents = ownerEvents;
        this.grabWindow = grabWindow;
        this.eventMask = eventMask;
        this.pointerMode = pointerMode;
        this.keyboardMode = keyboardMode;
        this.confineTo = confineTo;
        this.cursor = cursor;
        this.button = button;
        this.modifiers = modifiers;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "ownerEvents", ownerEvents,
                "grabWindow", grabWindow,
                "eventMask", eventMask,
                "pointerMode", pointerMode,
                "keyboardMode", keyboardMode,
                "confineTo", confineTo,
                "cursor", cursor,
                "button", button,
                "modifiers", modifiers
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBOOL(ownerEvents);
        
        writeRequestLength(stream, 6);
        
        stream.writeWINDOW(grabWindow);
        stream.writeSETofPOINTEREVENT(eventMask);
        stream.writeBYTE(pointerMode);
        stream.writeBYTE(keyboardMode);
        stream.writeWINDOW(confineTo);
        stream.writeCURSOR(cursor);
        stream.writeBUTTON(button);
        stream.writeSETofKEYMASK(modifiers);
    }

    @Override
    public int getOpCode() {
        return OpCodes.GRAB_BUTTON;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return null;
    }
}
