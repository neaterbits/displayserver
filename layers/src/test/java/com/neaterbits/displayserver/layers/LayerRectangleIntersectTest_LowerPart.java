package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_LowerPart extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftWithin_RightWithin_UpperWithin_LowerAtEnd() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 1,
                RECTANGLE.getTop() + 20,
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getHeight() - 20);

        checkPos(rectangle, Pos.WITHIN, Pos.WITHIN, Pos.WITHIN, Pos.AT_END);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_PART);
        
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 20));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 170, 1, 230));
        assertThat(list.get(2)).isEqualTo(new LayerRectangle(201, 170, 49, 230));
    }

    @Test
    public void test_LeftWithin_RightWithin_UpperWithin_LowerAfter() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 15,
                RECTANGLE.getTop() + 20,
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getHeight() + 15);

        checkPos(rectangle, Pos.WITHIN, Pos.WITHIN, Pos.WITHIN, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_PART);
        
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 20));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 170, 15, 230));
        assertThat(list.get(2)).isEqualTo(new LayerRectangle(215, 170, 35, 230));
    }
}
