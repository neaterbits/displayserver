package com.neaterbits.displayserver.layers;

import com.neaterbits.displayserver.types.Size;

abstract class BaseLayersTest {

    final Layers layers;
    
    BaseLayersTest() {
        this(new Size(1280, 1024));
    }

    BaseLayersTest(Size size) {
        this.layers = new Layers(size);
    }
}
