package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_RightPart extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftWithin_RightAfter_TopWithin_LowerWithin() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 20,
                RECTANGLE.getTop() + 30,
                RECTANGLE.getWidth() + 70,
                RECTANGLE.getHeight() - 50);

        checkPos(rectangle, Pos.WITHIN, Pos.AFTER, Pos.WITHIN, Pos.WITHIN);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.RIGHT_PART);
        
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 30));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 180, 20, 200));
        assertThat(list.get(2)).isEqualTo(new LayerRectangle(50, 380, 200, 20));
    }

    @Test
    public void test_LeftWithin_RightAtEnd_TopWithin_LowerWithin() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 20,
                RECTANGLE.getTop() + 30,
                RECTANGLE.getWidth() - 20,
                RECTANGLE.getHeight() - 50);

        checkPos(rectangle, Pos.WITHIN, Pos.AT_END, Pos.WITHIN, Pos.WITHIN);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.RIGHT_PART);
        
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 30));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 180, 20, 200));
        assertThat(list.get(2)).isEqualTo(new LayerRectangle(50, 380, 200, 20));
    }
}

