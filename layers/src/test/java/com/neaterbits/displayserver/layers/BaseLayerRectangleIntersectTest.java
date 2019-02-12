package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseLayerRectangleIntersectTest {

    static final LayerRectangle RECTANGLE = new LayerRectangle(50, 150, 200, 250);

    static void checkPos(LayerRectangle rectangle, Pos left, Pos right, Pos top, Pos lower) {

        assertThat(RECTANGLE.getHPos(rectangle.getLeft())).isEqualTo(left);
        assertThat(RECTANGLE.getHPos(rectangle.getRight())).isEqualTo(right);
        assertThat(RECTANGLE.getVPos(rectangle.getTop())).isEqualTo(top);
        assertThat(RECTANGLE.getVPos(rectangle.getLower())).isEqualTo(lower);
    }
}
