package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_UpperPart extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftWithin_RightWithin_UpperBefore_LowerAtStart() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 1,
                0,
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getTop() + 1);

        checkPos(rectangle, Pos.WITHIN, Pos.WITHIN, Pos.BEFORE, Pos.AT_START);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_PART);
        
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 1, 250));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(51, 151, 150, 249));
        assertThat(list.get(2)).isEqualTo(new LayerRectangle(201, 150, 49, 250));
    }

    @Test
    public void test_LeftWithin_RightWithin_UpperBefore_LowerWithin() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 1,
                0,
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getTop() + 20);

        checkPos(rectangle, Pos.WITHIN, Pos.WITHIN, Pos.BEFORE, Pos.WITHIN);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_PART);
        
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 1, 250));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(51, 170, 150, 230));
        assertThat(list.get(2)).isEqualTo(new LayerRectangle(201, 150, 49, 250));
    }
}
