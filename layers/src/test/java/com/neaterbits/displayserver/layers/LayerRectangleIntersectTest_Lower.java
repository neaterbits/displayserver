package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_Lower extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftBefore_RightAfter_TopWithin_LowerAtEnd() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                20,
                RECTANGLE.getTop() + 1,
                RECTANGLE.getWidth() + 50,
                RECTANGLE.getHeight() - 1);

        checkPos(rectangle, Pos.BEFORE, Pos.AFTER, Pos.WITHIN, Pos.AT_END);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 1));
    }

    @Test
    public void test_LeftAtStart_RightAtEnd_TopWithin_LowerAtEnd() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft(),
                RECTANGLE.getTop() + 25,
                RECTANGLE.getWidth(),
                RECTANGLE.getHeight() - 25);

        checkPos(rectangle, Pos.AT_START, Pos.AT_END, Pos.WITHIN, Pos.AT_END);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 25));
    }

    @Test
    public void test_LeftBefore_RightAfter_TopAtEnd_LowerAtEnd() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() - 1,
                RECTANGLE.getTop() + RECTANGLE.getHeight() - 1,
                RECTANGLE.getWidth() + 50,
                1);

        checkPos(rectangle, Pos.BEFORE, Pos.AFTER, Pos.AT_END, Pos.AT_END);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 249));
    }

    @Test
    public void test_LeftBefore_RightAfter_TopWithin_LowerAfter() {

        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() - 1,
                RECTANGLE.getTop() + RECTANGLE.getHeight() - 30,
                RECTANGLE.getWidth() + 50,
                50);

        checkPos(rectangle, Pos.BEFORE, Pos.AFTER, Pos.WITHIN, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LOWER);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 150, 200, 220));
    }
}
