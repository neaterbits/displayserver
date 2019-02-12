package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_Upper extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftBefore_RightAfter_TopBefore_LowerAtStart() {
        
        final LayerRectangle rectangle = new LayerRectangle(0, 0, RECTANGLE.getLeft() + RECTANGLE.getWidth() + 50, RECTANGLE.getTop() + 1);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 151, 200, 249));
    }

    @Test
    public void test_LeftAtStart_RightAtEnd_TopBefore_LowerAtStart() {

        final LayerRectangle rectangle = new LayerRectangle(RECTANGLE.getLeft(), 0, RECTANGLE.getWidth(), RECTANGLE.getTop() + 1);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 151, 200, 249));
    }

    @Test
    public void test_LeftBefore_RightAfter_TopAtStart_LowerAtStart() {

        final LayerRectangle rectangle = new LayerRectangle(0, RECTANGLE.getTop(), RECTANGLE.getLeft() + RECTANGLE.getWidth() + 50, 1);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 151, 200, 249));
    }

    @Test
    public void test_LeftBefore_RightAfter_TopBefore_LowerWithin() {

        final LayerRectangle rectangle = new LayerRectangle(0, 0, RECTANGLE.getLeft() + RECTANGLE.getWidth() + 50, RECTANGLE.getTop() + 20);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 170, 200, 230));
    }

    @Test
    public void test_LeftBefore_RightAfter_TopAtStart_LowerWithin() {

        final LayerRectangle rectangle = new LayerRectangle(0, RECTANGLE.getTop(), RECTANGLE.getLeft() + RECTANGLE.getWidth() + 50, 20);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(50, 170, 200, 230));
    }
}
