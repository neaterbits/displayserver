package com.neaterbits.displayserver.framebuffer.xwindows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.framebuffer.common.BaseGraphicsDriver;
import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;
import com.neaterbits.displayserver.framebuffer.common.GraphicsScreen;
import com.neaterbits.displayserver.types.Size;

public final class XWindowsGraphicsDriver extends BaseGraphicsDriver implements GraphicsDriver {

	private final List<GraphicsScreen> screens;
	
	public XWindowsGraphicsDriver(XWindowsDriverConnection driverConnection) {
		
		Objects.requireNonNull(driverConnection);
		
		this.screens = Arrays.asList(new XWindowsGraphicsScreen(driverConnection, new Size(1280, 1024), 24));
	}

	
	@Override
    public List<GraphicsScreen> getScreens() {
        return Collections.unmodifiableList(screens);
    }
}
