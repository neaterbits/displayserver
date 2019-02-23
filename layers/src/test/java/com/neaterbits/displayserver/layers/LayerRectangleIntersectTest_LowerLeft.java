package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_LowerLeft extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftBefore_RightAtStart_TopAtEnd_LowerAfter() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                0,
                RECTANGLE.getTop() + RECTANGLE.getHeight() - 1,
                RECTANGLE.getLeft() + 1,
                20);

        checkPos(rectangle, Pos.BEFORE, Pos.AT_START, Pos.AT_END, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 249));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(51, 399, 199, 1));
    }

    @Test
    public void test_LeftAtStart_RightAtStart_TopAtEnd_LowerAfter() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft(),
                RECTANGLE.getTop() + RECTANGLE.getHeight() - 1,
                1,
                20);

        checkPos(rectangle, Pos.AT_START, Pos.AT_START, Pos.AT_END, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 249));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(51, 399, 199, 1));
    }

    @Test
    public void test_LeftBefore_RightWithin_TopAtEnd_LowerAfter() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() - 20,
                RECTANGLE.getTop() + RECTANGLE.getHeight() - 1,
                RECTANGLE.getWidth() - 50,
                30);

        checkPos(rectangle, Pos.BEFORE, Pos.WITHIN, Pos.AT_END, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 249));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(180, 399, 70, 1));
    }

    @Test
    public void test_LeftAtStart_RightWithin_TopAtEnd_LowerAfter() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft(),
                RECTANGLE.getTop() + RECTANGLE.getHeight() - 1,
                RECTANGLE.getWidth() - 50,
                30);

        checkPos(rectangle, Pos.AT_START, Pos.WITHIN, Pos.AT_END, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 249));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(200, 399, 50, 1));
    }


    @Test
    public void test_LeftBefore_RightWithin_TopWithin_LowerAfter() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() - 30,
                RECTANGLE.getTop() + 15,
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getHeight());

        checkPos(rectangle, Pos.BEFORE, Pos.WITHIN, Pos.WITHIN, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 15));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(170, 165, 80, 235));
    }

    @Test
    public void test_LeftAtStart_RightWithin_TopWithin_LowerAtEnd() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft(),
                RECTANGLE.getTop() + 15,
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getHeight() - 15);

        checkPos(rectangle, Pos.AT_START, Pos.WITHIN, Pos.WITHIN, Pos.AT_END);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 15));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(200, 165, 50, 235));
    }
}
