package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_LowerRight extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftWithin_RightAtEnd_TopAtEnd_LowerAtEnd() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 30,
                RECTANGLE.getTop() + RECTANGLE.getHeight() - 1,
                RECTANGLE.getWidth() - 30,
                1);

        checkPos(rectangle, Pos.WITHIN, Pos.AT_END, Pos.AT_END, Pos.AT_END);
        
        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_RIGHT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 249));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 399, 30, 1));
    }

    @Test
    public void test_LeftWithin_RightAfter_TopAtEnd_LowerAfter() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 30,
                RECTANGLE.getTop() + RECTANGLE.getHeight() - 1,
                RECTANGLE.getWidth() + 50,
                RECTANGLE.getHeight() + 20);

        checkPos(rectangle, Pos.WITHIN, Pos.AFTER, Pos.AT_END, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_RIGHT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 249));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 399, 30, 1));
    }

    @Test
    public void test_LeftWithin_RightAfter_TopWithin_LowerAfter() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 30,
                RECTANGLE.getTop() + 15,
                RECTANGLE.getWidth(),
                RECTANGLE.getHeight() + 20);

        checkPos(rectangle, Pos.WITHIN, Pos.AFTER, Pos.WITHIN, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_RIGHT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 15));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 165, 30, 235));
    }

    @Test
    public void test_LeftWithin_RightAfter_TopWithin_LowerAtEnd() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() + 30,
                RECTANGLE.getTop() + 15,
                RECTANGLE.getWidth(),
                RECTANGLE.getHeight() - 15);

        checkPos(rectangle, Pos.WITHIN, Pos.AFTER, Pos.WITHIN, Pos.AT_END);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_RIGHT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 15));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 165, 30, 235));
    }
}
