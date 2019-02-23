package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_UpperRight extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftWithin_RightAtEnd_TopBefore_LowerAtStart() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 30,
                0,
                RECTANGLE.getWidth() - 30,
                RECTANGLE.getTop() + 1);

        checkPos(rectangle, Pos.WITHIN, Pos.AT_END, Pos.BEFORE, Pos.AT_START);
        
        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_RIGHT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 30, 1));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 151, 200, 249));
    }

    @Test
    public void test_LeftWithin_RightAfter_TopBefore_LowerAtStart() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 30,
                0,
                RECTANGLE.getWidth() + 30,
                RECTANGLE.getTop() + 1);

        checkPos(rectangle, Pos.WITHIN, Pos.AFTER, Pos.BEFORE, Pos.AT_START);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_RIGHT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 30, 1));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 151, 200, 249));
    }

    @Test
    public void test_LeftWithin_RightAfter_TopBefore_LowerWithin() {

        final LayerRectangle rectangle = new LayerRectangle(0, 0, RECTANGLE.getLeft() + 10, RECTANGLE.getTop() + 20);

        checkPos(rectangle, Pos.WITHIN, Pos.AFTER, Pos.BEFORE, Pos.WITHIN);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(60, 150, 190, 20));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 170, 200, 230));
    }

    @Test
    public void test_LeftWithin_RightAfter_TopAtStart_LowerWithin() {

        final LayerRectangle rectangle = new LayerRectangle(RECTANGLE.getLeft(), RECTANGLE.getTop(), 10, 20);

        checkPos(rectangle, Pos.WITHIN, Pos.AFTER, Pos.AT_START, Pos.WITHIN);
        
        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(60, 150, 190, 20));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 170, 200, 230));
    }
}
