package com.neaterbits.displayserver.events.xwindows;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.driver.xwindows.common.ReplyListener;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.events.common.BaseInputDriver;
import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.events.common.Modifier;
import com.neaterbits.displayserver.events.common.ModifierScancodes;
import com.neaterbits.displayserver.protocol.messages.Error;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.replies.GetModifierMappingReply;
import com.neaterbits.displayserver.protocol.messages.requests.GetModifierMapping;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class XWindowsInputDriver extends BaseInputDriver implements InputDriver {
	
    private final XWindowsDriverConnection driverConnection;
    
    private ModifierScancodes modifierScancodes;
    
    public XWindowsInputDriver(XWindowsDriverConnection driverConnection) {

        Objects.requireNonNull(driverConnection);
        
        driverConnection.registerEventListener(event -> {
            
        });
        
        this.driverConnection = driverConnection;
        
        asyncQueryModifierScancodes();
    }

    @Override
    public boolean isInitialized() {
        return modifierScancodes != null;
    }

    @Override
    public ModifierScancodes getModifierScancodes() {
        
        if (!isInitialized()) {
            throw new IllegalStateException();
        }
        
        return modifierScancodes;
    }

    private void initModifierScanCodes(ModifierScancodes modifierScancodes) {
        
        Objects.requireNonNull(modifierScancodes);
        
        if (this.modifierScancodes != null) {
            throw new IllegalStateException();
        }

        this.modifierScancodes = modifierScancodes;
    }
    
    private void asyncQueryModifierScancodes() {
        
        System.out.println("## initModifierScancodes");
        
        driverConnection.sendRequestWaitReply(new GetModifierMapping(), new ReplyListener() {
            
            @Override
            public void onReply(Reply reply) {
                
                System.out.println("## scancodes response" + reply);
                
                final GetModifierMappingReply modifierMappingReply = (GetModifierMappingReply)reply;

                initModifierScanCodes(makeModifierScancodes(modifierMappingReply));
            }
            
            @Override
            public void onError(Error error) {
                System.out.println("## scancodes error" + error);
            }
        });
    }
    
    private static ModifierScancodes makeModifierScancodes(GetModifierMappingReply modifierMappingReply) {
        final int codesPerModifier = modifierMappingReply.getKeycodesPerModifier().getValue();
        
        final CARD8 [] keycodes = modifierMappingReply.getKeycodes();
        
        final List<Modifier> modifiers = new ArrayList<>(8);

        int idx = 0;
        
        for (int modifierIdx = 0; modifierIdx < 8; ++ modifierIdx) {
            
            final short [] scancodes = new short[codesPerModifier];
            
            for (int code = 0; code < codesPerModifier; ++ code) {
                scancodes[code] = keycodes[idx ++].getValue();
            }
            
            modifiers.add(new Modifier(scancodes));
        }
        
        final ModifierScancodes modifierScancodes = new ModifierScancodes(
                codesPerModifier,
                modifiers);

        return modifierScancodes;
    }
}
