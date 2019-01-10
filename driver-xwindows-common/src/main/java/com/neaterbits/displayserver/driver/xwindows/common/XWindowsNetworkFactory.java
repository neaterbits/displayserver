package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;

import com.neaterbits.displayserver.io.common.MessageProcessor;

public interface XWindowsNetworkFactory {

    XWindowsNetwork connect(MessageProcessor listener) throws IOException;
    
}
