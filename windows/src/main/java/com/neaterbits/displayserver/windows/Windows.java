package com.neaterbits.displayserver.windows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.framebuffer.common.FrameBuffer;
import com.neaterbits.displayserver.framebuffer.common.GraphicsScreen;
import com.neaterbits.displayserver.layers.Layer;
import com.neaterbits.displayserver.layers.LayerRegions;
import com.neaterbits.displayserver.layers.Layers;
import com.neaterbits.displayserver.layers.Position;
import com.neaterbits.displayserver.types.Size;

public class Windows {

	private final WindowEventListener windowEventListener;
	
	private final Screen screen;
	private final GraphicsScreen driverScreen;
	
	private final Layers layers;
	
	private final Window rootWindow;
	
	private final Map<Layer, Window> layerToWindow;
	
	Windows(Screen screen, GraphicsScreen driverScreen, WindowEventListener windowEventListener) {
		
	    Objects.requireNonNull(screen);
	    Objects.requireNonNull(driverScreen);
	    Objects.requireNonNull(windowEventListener);

	    this.screen = screen;
	    this.driverScreen = driverScreen;
		this.windowEventListener = windowEventListener;
		
		this.layers = new Layers(driverScreen.getSize());
		
		this.rootWindow = new Window(
		        screen,
				null,
				new WindowParameters(
						WindowClass.INPUT_OUTPUT,
						driverScreen.getDepth(),
						null,
						0, 0,
						driverScreen.getSize().getWidth(), driverScreen.getSize().getHeight(),
						0),
				null,
				layers.getRootLayer());
		
		this.layerToWindow = new HashMap<>();
	}
	
	Window getRootWindow() {
		return rootWindow;
	}
	
	FrameBuffer getFrameBuffer() {
	    return driverScreen.getFrameBuffer();
	}
	
	GraphicsScreen getDriverScreen() {
        return driverScreen;
    }

    Window createWindow(Window parentWindow, WindowParameters parameters, WindowAttributes attributes) {
	
		final Layer parentLayer = parentWindow.getLayer();
		
		final Layer layer = layers.createLayer(
				new Position(parameters.getX(), parameters.getY()),
				new Size(
						parameters.getWidth() + parameters.getBorderWidth(),
						parameters.getHeight() + parameters.getBorderWidth()));
		
		final LayerRegions toUpdate = layers.addLayer(parentLayer, layer);
		
		final Window window = new Window(screen, parentWindow, parameters, attributes, layer);
		
		layerToWindow.put(layer, window);
		
		sendUpdateEvents(toUpdate);
		
		return window;
	}
	
	void disposeWindow(Window window) {
		
		Objects.requireNonNull(window);
		
		if (window.getSubWindows() != null) {
			final List<Window> subWindows = new ArrayList<>(window.getSubWindows());
			
			for (Window subWindow : subWindows) {
				disposeWindow(subWindow);
			}
		}
		
		final LayerRegions toUpdate = layers.removeLayer(window.getParentWindow().getLayer(), window.getLayer());
		
		layerToWindow.remove(window);
		
		window.getParentWindow().removeSubWindow(window);
		
		sendUpdateEvents(toUpdate);
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

	private void sendUpdateEvents(LayerRegions regions) {
		
		Objects.requireNonNull(regions);

		for (Layer layer : regions.getLayers()) {
			
			final Window window = layerToWindow.get(layer);
			
			windowEventListener.onUpdate(window, regions.getRegion(layer));
		}
	}
	
	Window findWindowAt(int x, int y) {
		
		final Layer layer = layers.findLayerAt(x, y);
		
		return layer != null ? layerToWindow.get(layer) : null;
	}
}
