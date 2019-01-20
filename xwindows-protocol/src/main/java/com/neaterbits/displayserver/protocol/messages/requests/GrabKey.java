package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.KEYCODE;
import com.neaterbits.displayserver.protocol.types.SETofKEYMASK;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class GrabKey extends XRequest {

    private final BOOL ownerEvents;
    private final WINDOW grabWindow;
    private final SETofKEYMASK modifiers;
    private final KEYCODE key;
    private final BYTE pointerMode;
    private final BYTE keyboardMode;

    public static GrabKey decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BOOL ownerEvents = stream.readBOOL();
        
        readRequestLength(stream);
        
        final WINDOW grabWindow = stream.readWINDOW();
        final SETofKEYMASK modifiers = stream.readSETofKEYMASK();
        final KEYCODE key = stream.readKEYCODE();
        final BYTE pointerMode = stream.readBYTE();
        final BYTE keyboardMode = stream.readBYTE();
        
        stream.readPad(3);
        
        return new GrabKey(ownerEvents, grabWindow, modifiers, key, pointerMode, keyboardMode);
    }
    
    public GrabKey(BOOL ownerEvents, WINDOW grabWindow, SETofKEYMASK modifiers, KEYCODE key, BYTE pointerMode,
            BYTE keyboardMode) {
    
        Objects.requireNonNull(ownerEvents);
        Objects.requireNonNull(grabWindow);
        Objects.requireNonNull(modifiers);
        Objects.requireNonNull(key);
        Objects.requireNonNull(pointerMode);
        Objects.requireNonNull(keyboardMode);
        
        this.ownerEvents = ownerEvents;
        this.grabWindow = grabWindow;
        this.modifiers = modifiers;
        this.key = key;
        this.pointerMode = pointerMode;
        this.keyboardMode = keyboardMode;
    }

    public BOOL getOwnerEvents() {
        return ownerEvents;
    }

    public WINDOW getGrabWindow() {
        return grabWindow;
    }

    public SETofKEYMASK getModifiers() {
        return modifiers;
    }

    public KEYCODE getKey() {
        return key;
    }

    public BYTE getPointerMode() {
        return pointerMode;
    }

    public BYTE getKeyboardMode() {
        return keyboardMode;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "ownerEvents", ownerEvents,
                "grabWindow", grabWindow,
                "modifiers", modifiers,
                "key", key,
                "pointerMode", pointerMode,
                "keyboardMode", keyboardMode
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBOOL(ownerEvents);
        
        writeRequestLength(stream, 4);
        
        stream.writeWINDOW(grabWindow);
        stream.writeSETofKEYMASK(modifiers);
        stream.writeKEYCODE(key);
        stream.writeBYTE(pointerMode);
        stream.writeBYTE(keyboardMode);
        
        stream.pad(3);
    }

    @Override
    public int getOpCode() {
        return OpCodes.GRAB_KEY;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
