package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_MidVertically extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftBefore_RightAfter_TopWithin_LowerWithin() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() - 20,
                RECTANGLE.getTop() + 30,
                RECTANGLE.getWidth() + 70,
                RECTANGLE.getHeight() - 50);

        checkPos(rectangle, Pos.BEFORE, Pos.AFTER, Pos.WITHIN, Pos.WITHIN);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.MID_VERTICALLY);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 30));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 380, 200, 20));
    }

    @Test
    public void test_LeftAtStart_RightAtEnd_TopWithin_LowerWithin() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft(),
                RECTANGLE.getTop() + 30,
                RECTANGLE.getWidth(),
                RECTANGLE.getHeight() - 50);

        checkPos(rectangle, Pos.AT_START, Pos.AT_END, Pos.WITHIN, Pos.WITHIN);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.MID_VERTICALLY);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 30));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 380, 200, 20));
    }
}
