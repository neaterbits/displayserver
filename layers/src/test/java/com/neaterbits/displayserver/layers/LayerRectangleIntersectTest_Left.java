package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_Left extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftBefore_RightWithin_TopAtStart_LowerAtEnd() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() - 15,
                RECTANGLE.getTop(),
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getHeight());

        checkPos(rectangle, Pos.BEFORE, Pos.WITHIN, Pos.AT_START, Pos.AT_END);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LEFT);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(185, 150, 65, 250));
    }

    @Test
    public void test_LeftAtStart_RightWithin_TopBefore_LowerAfter() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft(),
                RECTANGLE.getTop() - 15,
                RECTANGLE.getWidth() - 50,
                RECTANGLE.getHeight() + 25);

        checkPos(rectangle, Pos.AT_START, Pos.WITHIN, Pos.BEFORE, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.LEFT);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(200, 150, 50, 250));
    }

}
