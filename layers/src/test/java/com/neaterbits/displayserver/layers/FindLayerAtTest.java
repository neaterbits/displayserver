package com.neaterbits.displayserver.layers;

import org.junit.Test;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class FindLayerAtTest extends BaseLayersTest {

    public FindLayerAtTest() {
        super(new Size(1280, 1024));
    }

    @Test
    public void testFindRootLayerArguments() {

        try {
            layers.findLayerAt(-1, -1);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        try {
            layers.findLayerAt(-1, 0);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }
        
        try {
            layers.findLayerAt(0, -1);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        try {
            layers.findLayerAt(1280, 0);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        try {
            layers.findLayerAt(7, 1024);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testFindRootLayer() {

        assertThat(layers.findLayerAt(0, 0)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(600, 0)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(1279, 0)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(0, 0)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(0, 500)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(600, 500)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(0, 1023)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(600, 1023)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(1279, 1023)).isSameAs(layers.getRootLayer());
    }

    @Test
    public void testLayersDirectlyUnderRoot() {
        
        final Layer leftLayer = layers.createAndAddToRootLayer(new Position(50, 50), new Size(300, 200));
        final Layer rightLayer = layers.createAndAddToRootLayer(new Position(600,75), new Size(350, 250));
        final Layer aboveLayer = layers.createAndAddToRootLayer(new Position(250, 120), new Size(450, 350));

        // Layers not visible yet
        assertThat(layers.findLayerAt(60, 60)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(650, 160)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(260, 100)).isSameAs(layers.getRootLayer());

        layers.showLayer(leftLayer, null);

        assertThat(layers.findLayerAt(0, 0)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(600, 500)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(1279, 1023)).isSameAs(layers.getRootLayer());

        assertThat(layers.findLayerAt(49, 50)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(50, 49)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(350, 50)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(50, 250)).isSameAs(layers.getRootLayer());
        
        assertThat(layers.findLayerAt(50, 50)).isSameAs(leftLayer);
        assertThat(layers.findLayerAt(349, 50)).isSameAs(leftLayer);
        assertThat(layers.findLayerAt(50, 249)).isSameAs(leftLayer);
        assertThat(layers.findLayerAt(349, 249)).isSameAs(leftLayer);
        
        layers.hideLayer(leftLayer, null);

        assertThat(layers.findLayerAt(50, 50)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(349, 50)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(50, 249)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(349, 249)).isSameAs(layers.getRootLayer());

        layers.showLayer(leftLayer, null);
        
        layers.showLayer(rightLayer, null);

        assertThat(layers.findLayerAt(599, 75)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(600, 74)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(950, 324)).isSameAs(layers.getRootLayer());
        assertThat(layers.findLayerAt(949, 325)).isSameAs(layers.getRootLayer());

        assertThat(layers.findLayerAt(650, 75)).isSameAs(rightLayer);
        assertThat(layers.findLayerAt(949, 75)).isSameAs(rightLayer);
        assertThat(layers.findLayerAt(649, 324)).isSameAs(rightLayer);
        assertThat(layers.findLayerAt(949, 324)).isSameAs(rightLayer);
        
        layers.showLayer(aboveLayer, null);

        assertThat(layers.findLayerAt(249, 120)).isSameAs(leftLayer);
        assertThat(layers.findLayerAt(250, 119)).isSameAs(leftLayer);
        assertThat(layers.findLayerAt(700, 120)).isSameAs(rightLayer);
        assertThat(layers.findLayerAt(749, 119)).isSameAs(rightLayer);

        assertThat(layers.findLayerAt(250, 120)).isSameAs(aboveLayer);
        assertThat(layers.findLayerAt(699, 120)).isSameAs(aboveLayer);
        assertThat(layers.findLayerAt(250, 469)).isSameAs(aboveLayer);
        assertThat(layers.findLayerAt(699, 469)).isSameAs(aboveLayer);
        
        // Add sublayer
        final Layer leftSubLayer = layers.createAndAddSubLayer(leftLayer, new Position(30, 30), new Size(270, 170));
        
        layers.showLayer(leftSubLayer, null);

        assertThat(layers.findLayerAt(249, 120)).isSameAs(leftSubLayer);
        assertThat(layers.findLayerAt(250, 119)).isSameAs(leftSubLayer);
        assertThat(layers.findLayerAt(700, 120)).isSameAs(rightLayer);
        assertThat(layers.findLayerAt(749, 119)).isSameAs(rightLayer);

        assertThat(layers.findLayerAt(250, 120)).isSameAs(aboveLayer);
        assertThat(layers.findLayerAt(699, 120)).isSameAs(aboveLayer);
        assertThat(layers.findLayerAt(250, 469)).isSameAs(aboveLayer);
        assertThat(layers.findLayerAt(699, 469)).isSameAs(aboveLayer);
    }
}
