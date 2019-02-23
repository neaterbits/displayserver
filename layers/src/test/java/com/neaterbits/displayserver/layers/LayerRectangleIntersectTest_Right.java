package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_Right extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftWithin_RightAfter_TopAtStart_LowerAtEnd() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 1,
                RECTANGLE.getTop(),
                RECTANGLE.getWidth() + 50,
                RECTANGLE.getHeight());

        checkPos(rectangle, Pos.WITHIN, Pos.AFTER, Pos.AT_START, Pos.AT_END);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.RIGHT);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 1, 250));
    }

    @Test
    public void test_LeftWithin_RightAtEnd_TopBefore_LowerAfter() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 50,
                RECTANGLE.getTop() - 1,
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getHeight() + 1);

        checkPos(rectangle, Pos.WITHIN, Pos.AT_END, Pos.BEFORE, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.RIGHT);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 50, 250));
    }
}
