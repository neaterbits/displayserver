package com.neaterbits.displayserver.layers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LayerRectangleIntersectTest_Obscured extends BaseLayerRectangleIntersectTest {

    @Test
    public void test_LeftWithin_RightWithin_TopWithin_LowerWithin() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft() - 20,
                RECTANGLE.getTop() - 30,
                RECTANGLE.getWidth() + 70,
                RECTANGLE.getHeight() + 50);

        checkPos(rectangle, Pos.BEFORE, Pos.AFTER, Pos.BEFORE, Pos.AFTER);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.OBSCURED);

        assertThat(list.isEmpty()).isTrue();
        
    }

    @Test
    public void test_LeftAtStart_RightAtEnd_TopAtStart_LowerAtEnd() {
        
        final LayerRectangle rectangle = new LayerRectangle(
                RECTANGLE.getLeft(),
                RECTANGLE.getTop(),
                RECTANGLE.getWidth(),
                RECTANGLE.getHeight());

        checkPos(rectangle, Pos.AT_START, Pos.AT_END, Pos.AT_START, Pos.AT_END);

        final List<LayerRectangle> list = new ArrayList<>();
        
        assertThat(RECTANGLE.intersect(rectangle, list)).isEqualTo(IntersectionType.OBSCURED);
        
        assertThat(list.isEmpty()).isTrue();
    }
}
