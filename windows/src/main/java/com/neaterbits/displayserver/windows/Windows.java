package com.neaterbits.displayserver.windows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.layers.Layer;
import com.neaterbits.displayserver.layers.LayerRectangle;
import com.neaterbits.displayserver.layers.LayerRegion;
import com.neaterbits.displayserver.layers.LayerRegions;
import com.neaterbits.displayserver.layers.Layers;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

final class Windows {

	private final WindowsDisplayArea displayArea;
	
	private final Layers layers;
	
	private final Window rootWindow;
	
	private final Map<Layer, Window> layerToWindow;
	
	Windows(WindowsDisplayArea displayArea) {
		
	    Objects.requireNonNull(displayArea);

	    this.displayArea = displayArea;
		
		this.layers = new Layers(displayArea.getSize());
		
		this.rootWindow = new Window(
		        displayArea,
				null,
				new WindowParameters(
						WindowClass.INPUT_OUTPUT,
						displayArea.getDepth(),
						null,
						0, 0,
						displayArea.getSize().getWidth(), displayArea.getSize().getHeight(),
						0,
						WindowContentStorage.ALWAYS),
				null,
				layers.getRootLayer());
		
		this.layerToWindow = new HashMap<>();
	}
	
	Window getRootWindow() {
		return rootWindow;
	}

	DisplayArea getDisplayArea() {
        return displayArea;
    }

    Window createWindow(Window parentWindow, WindowParameters parameters, WindowAttributes attributes) {
	
		final Layer parentLayer = parentWindow.getLayer();

		final Layer layer;
		
		final Position position = new Position(parameters.getX(), parameters.getY());
		
		final Size size = new Size(
                parameters.getWidth() + parameters.getBorderWidth() * 2,
                parameters.getHeight() + parameters.getBorderWidth() * 2);
		
		if (parentLayer.isRootLayer()) {
		    layer = layers.createAndAddToRootLayer(position, size);
		}
		else {
		    layer = layers.createAndAddSubLayer(parentLayer, position, size);
		}
		
		final Window window = new Window(displayArea, parentWindow, parameters, attributes, layer);
		
		layerToWindow.put(layer, window);
		
		return window;
	}
    
    
    LayerRegion showWindow(Window window) {

        Objects.requireNonNull(window);
        
        final LayerRegion region = window.showWindow();
        
        layers.showLayer(window.getLayer());
        
        return region;
    }
    
    LayerRegion getVisibleOrStoredRegion(Window window) {
        
        Objects.requireNonNull(window);
        
        final LayerRegion region;
        
        switch (window.getWindowContentStorage()) {
        case NONE:
            region = layers.getVisibleOrStoredRegion(window.getLayer());
            break;
            
        case WHEN_VISIBLE:
        case ALWAYS:
            final Layer layer = window.getLayer();
            final List<LayerRectangle> rectangles = Arrays.asList(
                    new LayerRectangle(0, 0, layer.getSize().getWidth(), layer.getSize().getHeight())
            );
            region = new LayerRegion(rectangles);
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        return region;
    }

    LayerRegions hideWindow(Window window) {

        Objects.requireNonNull(window);
        
        final LayerRegions regions = layers.hideLayer(window.getLayer());
        
        return regions;
    }

	void disposeWindow(Window window) {
		
		Objects.requireNonNull(window);
		
		if (window.getSubWindows() != null) {
			final List<Window> subWindows = new ArrayList<>(window.getSubWindows());
			
			for (Window subWindow : subWindows) {
				disposeWindow(subWindow);
			}
		}
		
		final Layer layer = window.getParentWindow().getLayer();
		
		if (layer.isRootLayer()) {
		    layers.removeFromRootLayer(window.getLayer());
		}
		else {
		    layers.removeSubLayer(layer, window.getLayer());
		}
		
		layerToWindow.remove(window);
		
		window.getParentWindow().removeSubWindow(window);
	}
	
    List<Window> getSubWindowsInOrder(Window window) {
        
        Objects.requireNonNull(window);
        
        final List<Window> subWindowsBackToFront;
        
        if (window.getSubWindows() == null || window.getSubWindows().isEmpty()) {
            subWindowsBackToFront = Collections.emptyList();
        }
        else {
            subWindowsBackToFront = new ArrayList<>(window.getSubWindows().size());
            
            window.getLayer().forEachSubLayerBackToFront(layer -> {
                
                Window found = null;
                
                for (Window subWindow : window.getSubWindows()) {
                    if (subWindow.getLayer() == layer) {
                        found = subWindow;
                        break;
                    }
                }
                
                if (found == null) {
                    throw new IllegalStateException();
                }
                
                subWindowsBackToFront.add(found);
            });
        }

        return subWindowsBackToFront;
    }

	Window findWindowAt(int x, int y) {
		
		final Layer layer = layers.findLayerAt(x, y);
		
		return layer != null ? layerToWindow.get(layer) : null;
	}

	TranslatedCoordinates translateCoordinates(Window window, int x, int y) {
	    
	    final Position positionInRootWindow = findRootWindowPosition(window);
	    
	    final int xPos = x + positionInRootWindow.getLeft();
	    final int yPos = y + positionInRootWindow.getTop();
	    
	    final Window foundWindow = findWindowAt(xPos, yPos);
	
	    return new TranslatedCoordinates(xPos, yPos, foundWindow);
	}
	
	private Position findRootWindowPosition(Window window) {
	 
	    int x = window.getPosition().getLeft();
	    int y = window.getPosition().getTop();
	    
	    
	    for (Window parentWindow = window.getParentWindow(); parentWindow != null; parentWindow = parentWindow.getParentWindow()) {

	        x += parentWindow.getPosition().getLeft();
	        y += parentWindow.getPosition().getTop();
	        
	    }
	    
	    return new Position(x, y);
	}
}
