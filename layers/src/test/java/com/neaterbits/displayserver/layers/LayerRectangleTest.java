package com.neaterbits.displayserver.layers;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

public class LayerRectangleTest {

    @Test
    public void testGetters() {
        
        final LayerRectangle rectangle = new LayerRectangle(50, 100, 200, 150);

        assertThat(rectangle.getLeft()).isEqualTo(50);
        assertThat(rectangle.getTop()).isEqualTo(100);
        assertThat(rectangle.getWidth()).isEqualTo(200);
        assertThat(rectangle.getHeight()).isEqualTo(150);
    }

    @Test
    public void testEquals() {

        final LayerRectangle rectangle = new LayerRectangle(50, 100, 200, 150);

        assertThat(rectangle.equals(new LayerRectangle(rectangle))).isTrue();
    }

    @Test
    public void testContains() {

        final LayerRectangle rectangle = new LayerRectangle(50, 100, 250, 150);
        
        assertThat(rectangle.contains(50, 100)).isTrue();
        assertThat(rectangle.contains(49, 100)).isFalse();
        assertThat(rectangle.contains(50, 99)).isFalse();

        assertThat(rectangle.contains(299, 100)).isTrue();
        assertThat(rectangle.contains(300, 100)).isFalse();
        
        assertThat(rectangle.contains(50, 249)).isTrue();
        assertThat(rectangle.contains(50, 250)).isFalse();
        
        assertThat(rectangle.contains(299, 249)).isTrue();
        assertThat(rectangle.contains(300, 249)).isFalse();
        assertThat(rectangle.contains(299, 250)).isFalse();
    }

    @Test
    public void testIntersects() {
        
        final LayerRectangle rectangle = new LayerRectangle(50, 150, 200, 250);

        try {
            rectangle.intersects(null);
        
            fail("Expected exception");
        }
        catch (NullPointerException ex) {
        }

        try {
            rectangle.intersects(rectangle);
        
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        // above
        assertThat(rectangle.intersects(new LayerRectangle(0, 0, 400, rectangle.getTop()    ))).isFalse();
        assertThat(rectangle.intersects(new LayerRectangle(0, 0, 400, rectangle.getTop() + 1))).isTrue();

        // left of
        assertThat(rectangle.intersects(new LayerRectangle(0, 0, rectangle.getLeft(),     400))).isFalse();
        assertThat(rectangle.intersects(new LayerRectangle(0, 0, rectangle.getLeft() + 1, 400))).isTrue();

        // below
        assertThat(rectangle.intersects(new LayerRectangle(0, rectangle.getTop() + rectangle.getHeight(),     400, 50))).isFalse();
        assertThat(rectangle.intersects(new LayerRectangle(0, rectangle.getTop() + rectangle.getHeight() - 1, 400, 50))).isTrue();
        
        // right of
        assertThat(rectangle.intersects(new LayerRectangle(rectangle.getLeft() + rectangle.getWidth(),     0, 50, 400))).isFalse();
        assertThat(rectangle.intersects(new LayerRectangle(rectangle.getLeft() + rectangle.getWidth() - 1, 0, 50, 400))).isTrue();

        // obscured by
        assertThat(rectangle.intersects(new LayerRectangle(rectangle))).isTrue();
        
        final LayerRectangle obscured = new LayerRectangle(
                rectangle.getLeft() + rectangle.getWidth() / 4,
                rectangle.getTop() + rectangle.getHeight() / 4,
                rectangle.getWidth() - rectangle.getWidth() / 4,
                rectangle.getHeight() - rectangle.getHeight() / 4);
        
        assertThat(rectangle.intersects(obscured)).isTrue();
        assertThat(obscured.intersects(rectangle)).isTrue();
    }

    @Test
    public void testObscurs() {
        
        final LayerRectangle rectangle = new LayerRectangle(50, 150, 200, 250);

        try {
            rectangle.obscurs(null);
        
            fail("Expected exception");
        }
        catch (NullPointerException ex) {
        }

        try {
            rectangle.obscurs(rectangle);
        
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        assertThat(rectangle.obscurs(new LayerRectangle(0, 0, 50, 100))).isFalse();
        assertThat(rectangle.obscurs(new LayerRectangle(0, 0, 51, 100))).isFalse();
        assertThat(rectangle.obscurs(new LayerRectangle(250, 0, 50, 100))).isFalse();
        assertThat(rectangle.obscurs(new LayerRectangle(249, 0, 50, 100))).isFalse();
        
        assertThat(rectangle.obscurs(new LayerRectangle(50, 0, 50, 100))).isFalse();
        assertThat(rectangle.obscurs(new LayerRectangle(50, 0, 50, 101))).isFalse();
        assertThat(rectangle.obscurs(new LayerRectangle(250, 0, 50, 100))).isFalse();
        assertThat(rectangle.obscurs(new LayerRectangle(249, 0, 50, 100))).isFalse();

        assertThat(rectangle.obscurs(new LayerRectangle(50, 170, 50, 100))).isTrue();
        assertThat(rectangle.obscurs(new LayerRectangle(49, 170, 50, 100))).isFalse();
        assertThat(rectangle.obscurs(new LayerRectangle(100, 170, 150, 100))).isTrue();
        assertThat(rectangle.obscurs(new LayerRectangle(100, 170, 151, 100))).isFalse();

        assertThat(rectangle.obscurs(new LayerRectangle(50, 150, 50, 100))).isTrue();
        assertThat(rectangle.obscurs(new LayerRectangle(50, 150, 50, 100))).isTrue();
        assertThat(rectangle.obscurs(new LayerRectangle(50, 99, 50, 100))).isFalse();

        assertThat(rectangle.obscurs(new LayerRectangle(50, 300, 50, 100))).isTrue();
        assertThat(rectangle.obscurs(new LayerRectangle(50, 301, 50, 100))).isFalse();
        assertThat(rectangle.obscurs(new LayerRectangle(50, 300, 50, 101))).isFalse();
    
        assertThat(rectangle.obscurs(new LayerRectangle(rectangle))).isTrue();
    }

    @Test
    public void testSplitFromIntersectingButNotIn() {

        final LayerRectangle rectangle = new LayerRectangle(50, 150, 200, 250);

        try {
            rectangle.splitFromInFront(null, null);
        
            fail("Expected exception");
        }
        catch (NullPointerException ex) {
        }

        try {
            rectangle.splitFromInFront(rectangle, null);
        
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        final List<LayerRectangle> list = new ArrayList<>();
        
        // above
        assertThat(rectangle.splitFromInFront(new LayerRectangle(0, 0, 400, rectangle.getTop()    ), list))
            .isEqualTo(OverlapType.NONE);
        
        assertThat(list.isEmpty()).isTrue();
        
        assertThat(rectangle.splitFromInFront(new LayerRectangle(0, 0, 400, rectangle.getTop() + 1), list))
            .isEqualTo(OverlapType.INTERSECTION);

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(
                    rectangle.getLeft(),
                    rectangle.getTop() + 1,
                    rectangle.getWidth(),
                    rectangle.getHeight() - 1));
        
        list.clear();
        
        // left of
        assertThat(rectangle.splitFromInFront(new LayerRectangle(0, 0, rectangle.getLeft(),     400), list))
            .isEqualTo(OverlapType.NONE);
        assertThat(list.isEmpty()).isTrue();

        assertThat(rectangle.splitFromInFront(new LayerRectangle(0, 0, rectangle.getLeft() + 1, 400), list))
            .isEqualTo(OverlapType.INTERSECTION);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(
                rectangle.getLeft() + 1,
                rectangle.getTop(),
                rectangle.getWidth() - 1,
                rectangle.getHeight()));

        list.clear();
        
        // below
        assertThat(rectangle.splitFromInFront(new LayerRectangle(0, rectangle.getTop() + rectangle.getHeight(),     400, 50), list))
            .isEqualTo(OverlapType.NONE);
        assertThat(list.isEmpty()).isTrue();

        assertThat(rectangle.splitFromInFront(new LayerRectangle(0, rectangle.getTop() + rectangle.getHeight() - 1, 400, 50), list))
            .isEqualTo(OverlapType.INTERSECTION);
        
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(
                rectangle.getLeft(),
                rectangle.getTop(),
                rectangle.getWidth(),
                rectangle.getHeight() - 1));

        list.clear();

        // right of
        assertThat(rectangle.splitFromInFront(new LayerRectangle(rectangle.getLeft() + rectangle.getWidth(),     0, 50, 400), list))
            .isEqualTo(OverlapType.NONE);
        assertThat(list.isEmpty()).isTrue();

        assertThat(rectangle.splitFromInFront(new LayerRectangle(rectangle.getLeft() + rectangle.getWidth() - 1, 0, 50, 400), list))
            .isEqualTo(OverlapType.INTERSECTION);

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new LayerRectangle(
                rectangle.getLeft(),
                rectangle.getTop(),
                rectangle.getWidth() - 1,
                rectangle.getHeight()));

        list.clear();
        
        // obscured by
        assertThat(rectangle.splitFromInFront(new LayerRectangle(rectangle), list))
            .isEqualTo(OverlapType.EQUALS);
        
        assertThat(list.isEmpty()).isTrue();
        
        final LayerRectangle within = new LayerRectangle(
                rectangle.getLeft() + rectangle.getWidth() / 4,
                rectangle.getTop() + rectangle.getHeight() / 4,
                rectangle.getWidth() - rectangle.getWidth() / 2,
                rectangle.getHeight() - rectangle.getHeight() / 2);

        System.out.println("## split within " + within);
        
        assertThat(rectangle.splitFromInFront(within, list)).isEqualTo(OverlapType.OTHER_WITHIN);
        assertThat(list.size()).isEqualTo(4);

        assertThat(list.get(0)).isEqualTo(new LayerRectangle(
                rectangle.getLeft(),
                rectangle.getTop(),
                rectangle.getWidth(),
                within.getTop() - rectangle.getTop()));

        assertThat(list.get(1)).isEqualTo(new LayerRectangle(
                within.getLeft() + within.getWidth(),
                within.getTop(),
                rectangle.getWidth() - (within.getLeft() - rectangle.getLeft() + within.getWidth()),
                within.getHeight()));

        assertThat(list.get(2)).isEqualTo(new LayerRectangle(
                rectangle.getLeft(),
                within.getTop() + within.getHeight(),
                rectangle.getWidth(),
                rectangle.getHeight() - (within.getTop() - rectangle.getTop() + within.getHeight())));

        assertThat(list.get(3)).isEqualTo(new LayerRectangle(
                rectangle.getLeft(),
                within.getTop(),
                within.getLeft() - rectangle.getLeft(),
                within.getHeight()));

        list.clear();
        
        assertThat(within.splitFromInFront(rectangle, list)).isEqualTo(OverlapType.THIS_WITHIN);
        assertThat(list.isEmpty()).isTrue();
    }
}
