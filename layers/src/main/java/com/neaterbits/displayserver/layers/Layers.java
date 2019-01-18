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
	
	private int getLayerDescriptor() {
		return layerDescriptorSequence ++;
	}
	
	public Layers(Size size) {
		this.rootLayer = new Layer(getLayerDescriptor(), new Position(0, 0), size);
		
		this.subToParent = new HashMap<>();
	}
	
	public Layer getRootLayer() {
		return rootLayer;
	}

	public Layer createLayer(Position position, Size size) {
		return new Layer(getLayerDescriptor(), position, size);
	}
	
	public LayerRegions addLayer(Layer parentLayer, Layer subLayer) {
		
		Objects.requireNonNull(parentLayer);
		Objects.requireNonNull(subLayer);
		
		if (subLayer == rootLayer) {
			throw new IllegalArgumentException();
		}
		
		if (parentLayer != rootLayer && !subToParent.containsKey(parentLayer)) {
			throw new IllegalArgumentException();
		}
		
		if (subToParent.containsKey(subLayer)) {
			throw new IllegalArgumentException();
		}
		
		parentLayer.addSubLayer(subLayer);
		
		subToParent.put(subLayer, parentLayer);
	
		return recomputeLayers();
	}
	
	public LayerRegions removeLayer(Layer parentLayer, Layer subLayer) {

		if (parentLayer != rootLayer && !subToParent.containsKey(parentLayer)) {
			throw new IllegalArgumentException();
		}
		
		if (subToParent.get(subLayer) != parentLayer) {
			throw new IllegalArgumentException();
		}
		
		parentLayer.removeSubLayer(subLayer);
		
		return recomputeLayers();
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
