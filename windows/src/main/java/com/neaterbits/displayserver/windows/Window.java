package com.neaterbits.displayserver.windows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.layers.Layer;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public final class Window {

    private final WindowsDisplayArea displayArea;
	private final Window parentWindow;
	
	private final WindowParameters parameters;
	private final WindowAttributes attributes;

	private final Layer layer;
	
	private final List<Window> subWindows;
	
	Window(
	        WindowsDisplayArea displayArea,
	        Window parentWindow,
	        WindowParameters parameters,
	        WindowAttributes attributes,
	        Layer layer) {
		
	    // Objects.requireNonNull(screen);
		Objects.requireNonNull(parameters);
		// Objects.requireNonNull(attributes);
		Objects.requireNonNull(layer);
		
		this.displayArea = displayArea;
		this.parentWindow = parentWindow;
		this.parameters = parameters;
		this.attributes = attributes;
		this.layer = layer;
		
		this.subWindows = new ArrayList<>();
		
		if (parentWindow != null) {
			parentWindow.subWindows.add(this);
		}
	}

	public WindowParameters getParameters() {
		return parameters;
	}
	
	public int getDepth() {
	    return displayArea.getDepth();
	}
	
	public Position getPosition() {
	    return layer.getPosition();
	}
	
	public Size getSize() {
	    return layer.getSize();
	}

	WindowAttributes getAttributes() {
        return attributes;
    }

    public WindowsDisplayArea getDisplayArea() {
	    return displayArea;
	}
    
    public PixelFormat getPixelFormat() {
        return displayArea.getPixelFormat();
    }
	
	Window getParentWindow() {
		return parentWindow;
	}

	Layer getLayer() {
		return layer;
	}

	List<Window> getSubWindows() {
		return Collections.unmodifiableList(subWindows);
	}
	
	void removeSubWindow(Window subWindow) {
		
		if (!subWindows.contains(subWindow)) {
			throw new IllegalArgumentException();
		}
		
		subWindows.remove(subWindow);
	}
}
