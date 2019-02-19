package com.neaterbits.displayserver.windows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.layers.Layer;
import com.neaterbits.displayserver.layers.LayerRectangle;
import com.neaterbits.displayserver.layers.LayerRegion;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public final class Window {

    private final WindowsDisplayArea displayArea;
	private final Window parentWindow;
	
	private final WindowParameters parameters;
	private final WindowAttributes attributes;

	private final Layer layer;
	
	private final List<Window> subWindows;
	
	private boolean previouslyVisible;
	
	public Window(
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
		
		this.previouslyVisible = false;
		
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
	
    public void setPosition(Position position) {
        layer.setPosition(position);
    }

    public int getAbsoluteLeft() {
        return layer.getAbsoluteLeft();
    }
    
    public int getAbsoluteTop() {
        return layer.getAbsoluteTop();
    }

    public Size getSize() {
	    return layer.getSize();
	}

    public void setSize(Size size) {
        layer.setSize(size);
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
    
    public WindowContentStorage getWindowContentStorage() {
        return parameters.getWindowContentStorage();
    }
	
    LayerRegion showWindow() {

        final boolean returnRegion;
        
        switch (parameters.getWindowContentStorage()) {
        
        case NONE:
        case WHEN_VISIBLE:
            returnRegion = true;
            break;
        
        case ALWAYS:
            returnRegion = !previouslyVisible;
            break;

        default:
            throw new IllegalStateException();
        }
        
        final LayerRegion region;
    
        if (returnRegion) {
            final List<LayerRectangle> rectangles = Arrays.asList(
                    new LayerRectangle(0, 0, layer.getSize().getWidth(), layer.getSize().getHeight())
            );
            
            region = new LayerRegion(rectangles);
        }
        else {
            region = null;
        }
        
        this.previouslyVisible = true;
        
        return region;
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
