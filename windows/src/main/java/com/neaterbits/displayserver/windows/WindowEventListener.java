package com.neaterbits.displayserver.windows;

import com.neaterbits.displayserver.layers.LayerRegion;

public interface WindowEventListener {

	void onUpdate(Window window, LayerRegion region);
	
}
