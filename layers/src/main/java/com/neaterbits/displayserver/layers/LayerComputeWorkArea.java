package com.neaterbits.displayserver.layers;

import java.util.ArrayList;
import java.util.List;

final class LayerComputeWorkArea {

    final List<LayerRectangle> intersectList;
    final List<LayerRectangle> splitTempList;
    final List<LayerRectangle> splitRemoveList;
    
    LayerComputeWorkArea() {
        this.intersectList = new ArrayList<>();
        this.splitTempList = new ArrayList<>();
        this.splitRemoveList = new ArrayList<>();
    }
}
