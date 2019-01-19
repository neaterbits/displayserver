package com.neaterbits.displayserver.layers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public class Layers {

	private int layerDescriptorSequence;
	
	private final Layer rootLayer;
	
	private final Map<Layer, Layer> subToParent;
	
	private int generateLayerDescriptor() {
		return layerDescriptorSequence ++;
	}
	
	public Layers(Size size) {
	    
		this.rootLayer = new Layer(generateLayerDescriptor(), new Position(0, 0), size, true);
		
		this.subToParent = new HashMap<>();
	}
	
	public Layer getRootLayer() {
	    
		return rootLayer;
	}

	public Layer createAndAddToRootLayer(Position position, Size size) {
	    
		final Layer layer = new Layer(generateLayerDescriptor(), position, size, false);

		addSubLayer(rootLayer, layer);
		
		return layer;
	}

	
    public Layer createAndAddSubLayer(Layer parentLayer, Position position, Size size) {
        
        Objects.requireNonNull(parentLayer);
        
        if (parentLayer.isRootLayer()) {
            throw new IllegalArgumentException();
        }
        
        final Layer subLayer = new Layer(generateLayerDescriptor(), position, size, false);
        
        addSubLayer(parentLayer, subLayer);
        
        return subLayer;
    }
	
	private void addSubLayer(Layer parentLayer, Layer subLayer) {
		
		Objects.requireNonNull(parentLayer);
		Objects.requireNonNull(subLayer);
		
		if (subLayer == rootLayer) {
			throw new IllegalArgumentException();
		}
		
		if (parentLayer != rootLayer && !subToParent.containsKey(parentLayer)) {
			throw new IllegalArgumentException();
		}
		
		if (subToParent.containsKey(subLayer)) {
			throw new IllegalStateException();
		}
		
		parentLayer.addSubLayer(subLayer);
		
		subToParent.put(subLayer, parentLayer);
	}

	public void removeFromRootLayer(Layer layer) {
	    
	    Objects.requireNonNull(layer);
	    
	    removeLayer(rootLayer, layer);
	}

	public void removeSubLayer(Layer parentLayer, Layer subLayer) {

	    Objects.requireNonNull(parentLayer);
	    Objects.requireNonNull(subLayer);

	    if (parentLayer.isRootLayer()) {
            throw new IllegalArgumentException();
        }
	    
	    if (subToParent.get(subLayer) != parentLayer) {
            throw new IllegalArgumentException("Not a sublayer");
        }
       
	    removeLayer(parentLayer, subLayer);
	}

	private void removeLayer(Layer parentLayer, Layer subLayer) {

	    Objects.requireNonNull(parentLayer);
	    Objects.requireNonNull(subLayer);

	    if (subLayer.isVisible()) {
	        throw new IllegalStateException();
	    }
	    
	    if (subLayer.hasSubLayers()) {
	        throw new IllegalStateException();
	    }
	    
		if (parentLayer != rootLayer && !subToParent.containsKey(parentLayer)) {
			throw new IllegalArgumentException();
		}
		
		if (subToParent.get(subLayer) != parentLayer) {
			throw new IllegalArgumentException();
		}
		
		parentLayer.removeSubLayer(subLayer);
	}
	
	public void showLayer(Layer layer, LayerUpdateListener listener) {
	    
	    Objects.requireNonNull(layer);
	    
	    layer.setVisible(true);
	    
	    if (listener != null) {
	        listener.onLayerUpdate(layer, recomputeLayers());
	    }
	}

    public void hideLayer(Layer layer, LayerUpdateListener listener) {

        Objects.requireNonNull(layer);

        layer.setVisible(false);

        if (listener != null) {
            listener.onLayerUpdate(layer, recomputeLayers());
        }
    }

	public Layer findLayerAt(int x, int y) {
		return rootLayer.findLayerAt(x, y);
	}
	
	private LayerRegions recomputeLayers() {
		
		final Map<Layer, LayerRegion> regions = new HashMap<>(subToParent.size() + 1);
		
        /*
		final List<Layer> stack = new ArrayList<>();

		final List<Rectangle> stillVisibleRectangles = new ArrayList<>();

		recomputeOneLayer(rootLayer, regions, stack, stillVisibleRectangles);
		
		recomputeSubLayers(rootLayer, regions, stack, stillVisibleRectangles);
		*/
		
		return new LayerRegions(regions);
	}

	private static void recomputeSubLayers(
			Layer layer,
			Map<Layer, LayerRegion> layerRegions,
			List<Layer> stack,
			List<LayerRectangle> stillVisibleRectangles) {

		stack.add(layer);

		
		for (Layer subLayer : layer.getSubLayers()) {

			stillVisibleRectangles.clear();

			recomputeOneLayer(subLayer, layerRegions, stack, stillVisibleRectangles);
			
			recomputeSubLayers(subLayer, layerRegions, stack, stillVisibleRectangles);
		}
	}
	
	private static void recomputeOneLayer(
			Layer layer,
			Map<Layer, LayerRegion> layerRegions,
			List<Layer> stack,
			List<LayerRectangle> stillVisibleRectangles) {

		
		intersectAllInFrontOf(layer, stack, stillVisibleRectangles);
		
		intersectAllSubLayers(layer, layer, stillVisibleRectangles);

		final List<LayerRectangle> newlyVisibleRectangles = layer.updateVisibleRectangles(stillVisibleRectangles);
		
		final LayerRegion newlyVisibleRegion = new LayerRegion(newlyVisibleRectangles);
		
		layerRegions.put(layer, newlyVisibleRegion);
	}
	
	
	private static void intersectAllInFrontOf(Layer layer, List<Layer> stack, List<LayerRectangle> stillVisibleRectangles) {

		for (Layer ancestor : stack) {
			
			boolean layerFound = false;
			
			final Layer [] subLayers = ancestor.getSubLayers();
			
			for (int i = subLayers.length; i >= 0; -- i) {

				final Layer ancestorSub = subLayers[i];
				
				if (!layerFound) {
					if (stack.contains(ancestorSub)) {
						layerFound = true;
					}
				}
				else {
					intersectLayerAndSubLayers(layer, ancestorSub, stillVisibleRectangles);
				}
			}
		}
	}
	
	private static void intersectLayerAndSubLayers(Layer layer, Layer toIntersect, List<LayerRectangle> stillVisibleRectangles) {
	
		Layer.intersectLayerOntoList(toIntersect, stillVisibleRectangles);
		
		intersectAllSubLayers(layer, toIntersect, stillVisibleRectangles);
	}
	
	private static void intersectAllSubLayers(Layer layer, Layer subLayersOf, List<LayerRectangle> stillVisibleRectangles) {
		
		for (Layer subLayer : subLayersOf.getSubLayers()) {
			intersectLayerAndSubLayers(layer, subLayer, stillVisibleRectangles);
		}
	}
}
