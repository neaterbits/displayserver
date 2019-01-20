package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.util.logging.LogUtil;

public final class ConfigureWindow extends XRequest {

    private final WINDOW window;
    private final XWindowConfiguration configuration;

    public static ConfigureWindow decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new ConfigureWindow(stream.readWINDOW(), XWindowConfiguration.decode(stream));
    }
    
    public ConfigureWindow(WINDOW window, XWindowConfiguration configuration) {
        this.window = window;
        this.configuration = configuration;
    }
    
    public WINDOW getWindow() {
        return window;
    }

    public XWindowConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "window", window,
                "configuration", LogUtil.outputParametersInBrackets(configuration.getDebugParams())
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 3 + configuration.getCount());
    
        configuration.encode(stream);
    }

    @Override
    public int getOpCode() {
        return OpCodes.CONFIGURE_WINDOW;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
