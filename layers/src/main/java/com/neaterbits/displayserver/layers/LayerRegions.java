package com.neaterbits.displayserver.layers;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class LayerRegions {

	private final Map<Layer, Region> layerRegions;

	LayerRegions(Map<Layer, Region> layerRegions) {
		
		Objects.requireNonNull(layerRegions);
		
		this.layerRegions = layerRegions;
	}
	
	public Set<Layer> getLayers() {
		return layerRegions.keySet();
	}
	
	public Region getRegion(Layer layer) {
		Objects.requireNonNull(layer);

		return layerRegions.get(layer);
	}
}
