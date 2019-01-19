package com.neaterbits.displayserver.layers;

@FunctionalInterface
public interface LayerUpdateListener {

    void onLayerUpdate(Layer layer, LayerRegions updated);
    
}
