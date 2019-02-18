package com.neaterbits.displayserver.layers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.util.StringUtil;

public class Layers {

    private static final boolean DEBUG = true;
    
	private int layerDescriptorSequence;
	
	private final Layer rootLayer;
	
	private final Map<Layer, Layer> subToParent;
	
	private int generateLayerDescriptor() {
		return layerDescriptorSequence ++;
	}
	
	public Layers(Size size) {
	    
		this.rootLayer = new Layer(generateLayerDescriptor(), new Position(0, 0), size, null);
		
		this.subToParent = new HashMap<>();
	}
	
	public Layer getRootLayer() {
	    
		return rootLayer;
	}

	public Layer createAndAddToRootLayer(Position position, Size size) {
	    
		final Layer layer = new Layer(generateLayerDescriptor(), position, size, rootLayer);

		addSubLayer(rootLayer, layer);
		
		return layer;
	}

	
    public Layer createAndAddSubLayer(Layer parentLayer, Position position, Size size) {
        
        Objects.requireNonNull(parentLayer);
        
        if (parentLayer.isRootLayer()) {
            throw new IllegalArgumentException();
        }
        
        final Layer subLayer = new Layer(generateLayerDescriptor(), position, size, parentLayer);
        
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
	
	public void showLayer(Layer layer) {
	    
	    Objects.requireNonNull(layer);
	    
	    layer.setVisible(true);

	}

    public LayerRegions hideLayer(Layer layer) {

        Objects.requireNonNull(layer);

        layer.setVisible(false);

        return recomputeLayers();
    }

    public LayerRegion getVisibleOrStoredRegion(Layer layer) {

        Objects.requireNonNull(layer);

        return recomputeLayers().getRegion(layer);
    }
    
	public Layer findLayerAt(int x, int y) {
		return rootLayer.findLayerAt(x, y);
	}
	
	private LayerRegions recomputeLayers() {
		
		final Map<Layer, LayerRegion> regions = new HashMap<>(subToParent.size() + 1);
		
		final List<Layer> stack = new ArrayList<>();

		final LayerComputeWorkArea workArea = new LayerComputeWorkArea();

		recomputeLayerAndSubLayers(rootLayer, regions, stack, workArea);
		
		return new LayerRegions(regions);
	}

	private static void recomputeLayerAndSubLayers(
	        Layer layer,
	        Map<Layer, LayerRegion> layerRegions,
            List<Layer> stack,
            LayerComputeWorkArea workArea) {
	    
        workArea.intersectList.clear();
        
        workArea.intersectList.add(layer.getRectangle());
        
        recomputeOneLayer(layer, layerRegions, stack, workArea);
        
        recomputeSubLayers(layer, layerRegions, stack, workArea);
	}
	
   private static void enter(int level, String msg) {
       debug(level, "ENTER " + msg);
   }

   private static void exit(int level, String msg) {
       debug(level, "EXIT " + msg);
   }

   private static void debug(int level, String msg) {
        if (DEBUG) {
            StringUtil.indent(System.out, level);
            
            System.out.println(msg);
        }
	}
	
	private static void recomputeSubLayers(
			Layer layer,
			Map<Layer, LayerRegion> layerRegions,
			List<Layer> stack,
			LayerComputeWorkArea workArea) {

        enter(stack.size(), "recomputeSubLayers layer=" + layer + ", stack=" + stack + ", visible=" + workArea.intersectList);
	    
	    if (stack.contains(layer)) {
	        throw new IllegalArgumentException();
	    }
	    
		stack.add(layer);

		if (layer.getSubLayers() != null) {
    		for (Layer subLayer : layer.getSubLayers()) {
    		    recomputeLayerAndSubLayers(subLayer, layerRegions, stack, workArea);
    		}
		}

		stack.remove(layer);
		
        exit(stack.size(), "recomputeSubLayers layer=" + layer + ", stack=" + stack + ", visible=" + workArea.intersectList);
	}
	
	private static void recomputeOneLayer(
			Layer layer,
			Map<Layer, LayerRegion> layerRegions,
			List<Layer> stack,
			LayerComputeWorkArea workArea) {


        enter(stack.size(), "recomputeOneLayer layer=" + layer + ", stack=" + stack + ", visible=" + workArea.intersectList);

        // Intersect any layer in front of this one that could possibly hide parts of this layer
		intersectAllInFrontOf(layer, stack, workArea);
		
		// Intersect sublayers that could possibly hide part of this layer
		intersectAllSubLayers(stack.size(), layer, layer, workArea);

        exit(stack.size(), "recomputeOneLayer intersected layer=" + layer + ", region=" + workArea.intersectList);

		final List<LayerRectangle> newlyVisibleRectangles = layer.updateVisibleRectangles(workArea.intersectList);
		
		final List<LayerRectangle> relativeToLayer = newlyVisibleRectangles.stream()
		        .map(lr -> new LayerRectangle(
		                lr.left - layer.getAbsoluteLeft(),
		                lr.top - layer.getAbsoluteTop(),
		                lr.width,
		                lr.height))
		        .collect(Collectors.toList());
		
		final LayerRegion newlyVisibleRegion = new LayerRegion(relativeToLayer);
		
        exit(stack.size(), "recomputeOneLayer add region layer=" + layer + ", region=" + newlyVisibleRectangles);
		
		layerRegions.put(layer, newlyVisibleRegion);

		exit(stack.size(), "recomputeOneLayer layer=" + layer + ", stack=" + stack + ", visible=" + workArea.intersectList);
	}
	
	private static void intersectAllInFrontOf(Layer layer, List<Layer> stack, LayerComputeWorkArea workArea) {

        enter(stack.size(), "intersectAllInFrontOf layer=" + layer + ", stack=" + stack + ", visible=" + workArea.intersectList);

		for (Layer ancestor : stack) {
			
			boolean layerFound = false;
			
			final Layer [] subLayers = ancestor.getSubLayers();
			
			debug(stack.size(), "iterate sublayers of " + ancestor + ": " + Arrays.toString(subLayers));
			
			for (int i = subLayers.length - 1; i >= 0; -- i) {

				final Layer ancestorSub = subLayers[i];
				
				if (!layerFound) {
					if (stack.contains(ancestorSub)) {
						layerFound = true;
					}
				}
				else {
				    
				    debug(stack.size(), "found sublayer ahead of layer " + layer + ": " + ancestorSub);
				    
					intersectLayerAndSubLayers(stack.size(), layer, ancestorSub, workArea);
				}
			}
		}

		exit(stack.size(), "intersectAllInFrontOf layer=" + layer + ", stack=" + stack + ", visible=" + workArea.intersectList);
	}
	
	private static void intersectLayerAndSubLayers(int level, Layer layer, Layer subToIntersect, LayerComputeWorkArea workArea) {

	    enter(level, "intersectLayerAndSubLayers layer=" + layer + ", subToIntersect=" + subToIntersect+ ", visible=" + workArea.intersectList);

		Layer.intersectFrontLayerOntoList(subToIntersect, workArea);
		
		intersectAllSubLayers(level, layer, subToIntersect, workArea);

		exit(level, "intersectLayerAndSubLayers layer=" + layer + ", subToIntersect=" + subToIntersect+ ", visible=" + workArea.intersectList);
	}
	
	private static void intersectAllSubLayers(int level, Layer layer, Layer subLayersOf, LayerComputeWorkArea workArea) {
		
	    enter(level, "intersectAllSubLayers layer=" + layer + ", subLayersOf=" + subLayersOf + ", visible=" + workArea.intersectList);
	    
	    if (subLayersOf.getSubLayers() != null) {
	        
    		for (Layer subLayer : subLayersOf.getSubLayers()) {
    			intersectLayerAndSubLayers(level + 1, layer, subLayer, workArea);
    		}
	    }

	    exit(level, "intersectAllSubLayers layer=" + layer + ", subLayersOf=" + subLayersOf + ", visible=" + workArea.intersectList);
	}
}
