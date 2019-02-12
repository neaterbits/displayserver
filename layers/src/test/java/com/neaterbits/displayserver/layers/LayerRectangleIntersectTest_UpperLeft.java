package com.neaterbits.displayserver.layers;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

public class LayerRectangleIntersectTest_UpperLeft extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftBefore_RightAtStart_TopBefore_LowerAtStart() {
        
        final LayerRectangle rectangle = new LayerRectangle(0, 0, RECTANGLE.getLeft() + 1, RECTANGLE.getTop() + 1);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(51, 150, 199, 1));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 151, 200, 249));
    }

    @Test
    public void test_LeftAtStart_RightAtStart_TopBefore_LowerAtStart() {

        final LayerRectangle rectangle = new LayerRectangle(RECTANGLE.getLeft(), 0, 1, RECTANGLE.getTop() + 1);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(51, 150, 199, 1));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 151, 200, 249));
    }

    @Test
    public void test_LeftBefore_RightWithin_TopBefore_LowerAtStart() {

        final LayerRectangle rectangle = new LayerRectangle(0, 0, RECTANGLE.getLeft() + 10, RECTANGLE.getTop() + 1);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(60, 150, 190, 1));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 151, 200, 249));
    }

    @Test
    public void test_LeftAtStart_RightWithin_TopBefore_LowerAtStart() {

        final LayerRectangle rectangle = new LayerRectangle(RECTANGLE.getLeft(), 0, 10, RECTANGLE.getTop() + 1);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(60, 150, 190, 1));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 151, 200, 249));
    }


    @Test
    public void test_LeftBefore_RightWithin_TopBefore_LowerWithin() {

        final LayerRectangle rectangle = new LayerRectangle(0, 0, RECTANGLE.getLeft() + 10, RECTANGLE.getTop() + 20);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(60, 150, 190, 20));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 170, 200, 230));
    }

    @Test
    public void test_LeftAtStart_RightWithin_TopAtStart_LowerWithin() {

        final LayerRectangle rectangle = new LayerRectangle(RECTANGLE.getLeft(), RECTANGLE.getTop(), 10, 20);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.UPPER_LEFT);
        
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(60, 150, 190, 20));
        assertThat(list.get(1)).isEqualTo(new LayerRectangle(50, 170, 200, 230));
    }
}
