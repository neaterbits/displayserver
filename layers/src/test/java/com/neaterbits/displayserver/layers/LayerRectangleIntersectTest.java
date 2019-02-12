package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class LayerRectangleIntersectTest extends BaseLayerRectangleIntersectTest {

    @Test
    public void testHPos() {

        assertThat(RECTANGLE.getHPos(0)).isEqualTo(Pos.BEFORE);
        assertThat(RECTANGLE.getHPos(RECTANGLE.getLeft() - 1)).isEqualTo(Pos.BEFORE);
        assertThat(RECTANGLE.getHPos(RECTANGLE.getLeft())).isEqualTo(Pos.AT_START);
        assertThat(RECTANGLE.getHPos(RECTANGLE.getLeft() + 1)).isEqualTo(Pos.WITHIN);
        assertThat(RECTANGLE.getHPos(RECTANGLE.getLeft() + RECTANGLE.getWidth() - 2)).isEqualTo(Pos.WITHIN);
        assertThat(RECTANGLE.getHPos(RECTANGLE.getLeft() + RECTANGLE.getWidth() - 1)).isEqualTo(Pos.AT_END);
        assertThat(RECTANGLE.getHPos(RECTANGLE.getRight())).isEqualTo(Pos.AT_END);
        assertThat(RECTANGLE.getHPos(RECTANGLE.getLeft() + RECTANGLE.getWidth())).isEqualTo(Pos.AFTER);
        assertThat(RECTANGLE.getHPos(RECTANGLE.getLeft() + RECTANGLE.getWidth() + 1)).isEqualTo(Pos.AFTER);
    }

    @Test
    public void testVPos() {

        assertThat(RECTANGLE.getVPos(0)).isEqualTo(Pos.BEFORE);
        assertThat(RECTANGLE.getVPos(RECTANGLE.getTop() - 1)).isEqualTo(Pos.BEFORE);
        assertThat(RECTANGLE.getVPos(RECTANGLE.getTop())).isEqualTo(Pos.AT_START);
        assertThat(RECTANGLE.getVPos(RECTANGLE.getTop() + 1)).isEqualTo(Pos.WITHIN);
        assertThat(RECTANGLE.getVPos(RECTANGLE.getTop() + RECTANGLE.getHeight() - 2)).isEqualTo(Pos.WITHIN);
        assertThat(RECTANGLE.getVPos(RECTANGLE.getTop() + RECTANGLE.getHeight() - 1)).isEqualTo(Pos.AT_END);
        assertThat(RECTANGLE.getVPos(RECTANGLE.getLower())).isEqualTo(Pos.AT_END);
        assertThat(RECTANGLE.getVPos(RECTANGLE.getTop() + RECTANGLE.getHeight())).isEqualTo(Pos.AFTER);
        assertThat(RECTANGLE.getVPos(RECTANGLE.getTop() + RECTANGLE.getHeight() + 1)).isEqualTo(Pos.AFTER);
    }
}
