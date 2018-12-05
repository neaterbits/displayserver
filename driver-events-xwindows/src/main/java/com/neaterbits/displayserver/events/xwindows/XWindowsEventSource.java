package com.neaterbits.displayserver.events.xwindows;


import java.util.Objects;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.events.common.BaseEventSource;
import com.neaterbits.displayserver.events.common.EventSource;

public final class XWindowsEventSource extends BaseEventSource implements EventSource {
	
    public XWindowsEventSource(XWindowsDriverConnection driverConnection) {

        Objects.requireNonNull(driverConnection);
        
        driverConnection.registerEventListener(event -> {
            
        });
    }
}
