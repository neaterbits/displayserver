package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class XFocusState {

    private WINDOW inputFocus;
    private BYTE inputFocusRevertTo;
    
    public WINDOW getInputFocus() {
        return inputFocus;
    }
    
    public BYTE getInputFocusRevertTo() {
        return inputFocusRevertTo;
    }
    
    public void setInputFocus(WINDOW inputFocus, BYTE inputFocusRevertTo) {
        
        this.inputFocus = inputFocus;
        this.inputFocusRevertTo = inputFocusRevertTo;
    }
}
