package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_MidHorizontally extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftWithin_RightWithin_UpperAtStart_LowerAtEnd() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 1,
                RECTANGLE.getTop(),
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getHeight());

        checkPos(rectangle, Pos.WITHIN, Pos.WITHIN, Pos.AT_START, Pos.AT_END);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.MID_HORIZONTALLY);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 1, 250));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(201, 150, 49, 250));
    }

    @Test
    public void test_LeftWithin_RightWithin_UpperBefore_LowerAfter() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 1,
                RECTANGLE.getTop() - 15,
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getHeight() + 20);

        checkPos(rectangle, Pos.WITHIN, Pos.WITHIN, Pos.BEFORE, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.MID_HORIZONTALLY);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 1, 250));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(201, 150, 49, 250));
    }
}
