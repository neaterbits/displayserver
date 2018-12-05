package com.neaterbits.displayserver.windows;

import com.neaterbits.displayserver.layers.Region;

public interface WindowEventListener {

	void onUpdate(Window window, Region region);
	
}
