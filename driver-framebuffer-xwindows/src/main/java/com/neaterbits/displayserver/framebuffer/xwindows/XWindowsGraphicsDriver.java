package com.neaterbits.displayserver.framebuffer.xwindows;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.driver.common.DisplayDeviceId;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.framebuffer.common.BaseGraphicsDriver;
import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public final class XWindowsGraphicsDriver extends BaseGraphicsDriver implements GraphicsDriver {

	public XWindowsGraphicsDriver(XWindowsDriverConnection driverConnection, String displayDeviceIdBase) throws IOException {
		
	    final List<XTestDisplay> testDisplays = Arrays.asList(
	            new XTestDisplay(new Position(250, 250), new Size(1500, 1200))
        );
	    
		Objects.requireNonNull(driverConnection);

		int idx = 0;
		
		for (XTestDisplay xTestDisplay : testDisplays) {
		    
		    final XWindowsDisplayer displayer = new XWindowsDisplayer(
		            driverConnection,
		            xTestDisplay.getPosition(),
		            xTestDisplay.getSize(),
		            new DisplayDeviceId(displayDeviceIdBase + idx));
		    
		    ++ idx;

		    driverConnection.addWindowToDisplayDevice(displayer.getWindow(), displayer.getDisplayDevice().getDisplayDeviceId());
		    
		    addRenderingProvider(displayer);
		    addDisplayDevice(displayer.getDisplayDevice());
		}
	}

    @Override
    public boolean isInitialized() {
        return true;
    }
}
