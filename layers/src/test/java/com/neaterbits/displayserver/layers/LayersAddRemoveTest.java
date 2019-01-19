package com.neaterbits.displayserver.layers;

import org.junit.Test;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class LayersAddRemoveTest extends BaseLayersTest {

    public LayersAddRemoveTest() {
        super(new Size(1280, 1024));
    }
    
    @Test
    public void testCreateLayer() {

        final Position position = new Position(250, 150);
        final Size size = new Size(450, 350);
        
        final Layer layer = layers.createAndAddToRootLayer(position, size);
        
        assertThat(layer).isNotNull();
        assertThat(layer.getPosition()).isEqualTo(position);
        assertThat(layer.getSize()).isEqualTo(size);
        
        assertThat(layer.getSubLayers()).isNull();
        
        assertThat(layer.getRectangle()).isEqualTo(new LayerRectangle(position, size));
        
        assertThat(layer.isVisible()).isFalse();
        
        assertThat(layers.getRootLayer()).isNotSameAs(layer);
        
        assertThat(layers.getRootLayer().getSubLayers().length).isEqualTo(1);
        assertThat(layers.getRootLayer().getSubLayers()[0]).isSameAs(layer);
    }


    @Test
    public void testRemoveLayerArguments() {
        
        try {
            layers.removeSubLayer(null, null);
            
            fail("Expected exception");
        }
        catch (NullPointerException ex) {
        }

        try {
            layers.removeFromRootLayer(null);
            
            fail("Expected exception");
        }
        catch (NullPointerException ex) {
        }
        
        final Layer layer = layers.createAndAddToRootLayer(new Position(250,  150), new Size(450, 350));
        
        assertThat(layers.getRootLayer().getSubLayers().length).isEqualTo(1);

        final Layer otherLayerUnderRootLayer = layers.createAndAddToRootLayer(new Position(150,  75), new Size(250, 150));

        try {
            layers.removeSubLayer(layer, otherLayerUnderRootLayer);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        final Layer subLayer = layers.createAndAddSubLayer(layer, new Position(50, 75), new Size(150, 100));
        
        assertThat(layer.getSubLayers().length).isEqualTo(1);
        assertThat(layer.getSubLayers()[0]).isEqualTo(subLayer);

        try {
            layers.removeSubLayer(layer, otherLayerUnderRootLayer);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        try {
            layers.removeSubLayer(subLayer, otherLayerUnderRootLayer);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        try {
            layers.removeFromRootLayer(subLayer);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        try {
            layers.removeSubLayer(layer, null);
            
            fail("Expected exception");
        }
        catch (NullPointerException ex) {
        }

        try {
            layers.removeSubLayer(subLayer, layer);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        try {
            layers.removeSubLayer(layers.getRootLayer(), subLayer);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        try {
            layers.removeSubLayer(otherLayerUnderRootLayer, layer);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        try {
            layers.removeSubLayer(otherLayerUnderRootLayer, subLayer);
            
            fail("Expected exception");
        }
        catch (IllegalArgumentException ex) {
        }

        try {
            layers.removeFromRootLayer(layer);
            
            fail("Expected exception");
        }
        catch (IllegalStateException ex) {
        }

        assertThat(subLayer.isVisible()).isFalse();
    
        layers.removeSubLayer(layer, subLayer);
        
        assertThat(layer.getSubLayers()).isNull();;

        layers.removeFromRootLayer(layer);
        assertThat(layers.getRootLayer().getSubLayers().length).isEqualTo(1);

        assertThat(layers.getRootLayer().getSubLayers()[0]).isEqualTo(otherLayerUnderRootLayer);

        layers.removeFromRootLayer(otherLayerUnderRootLayer);
        
        assertThat(layers.getRootLayer().getSubLayers()).isNull();
    }

    @Test
    public void testRemoveVisibleLayer() {

        final Layer layer = layers.createAndAddToRootLayer(new Position(250,  150), new Size(450, 350));
        
        assertThat(layers.getRootLayer().getSubLayers().length).isEqualTo(1);

        Layer subLayer = layers.createAndAddSubLayer(layer, new Position(50, 75), new Size(150, 100));

        layers.showLayer(layer, null);

        assertThat(layer.isVisible()).isTrue();
        assertThat(subLayer.isVisible()).isFalse();
        
        try {
            layers.removeFromRootLayer(layer);
            
            fail("Expected exception");
        }
        catch (IllegalStateException ex) {
        }
        
        // Still OK to remove sublayer
        layers.removeSubLayer(layer, subLayer);
        
        // Re-add sublayer and set visible
        
        subLayer = layers.createAndAddSubLayer(layer, new Position(50, 75), new Size(150, 100));

        layers.showLayer(subLayer, null);
        
        try {
            layers.removeSubLayer(layer, subLayer);
            
            fail("Expected exception");
        }
        catch (IllegalStateException ex) {
        }
        
        layers.hideLayer(subLayer, null);
        layers.removeSubLayer(layer, subLayer);
        layers.hideLayer(layer, null);
        layers.removeFromRootLayer(layer);
    }
}
