package com.neaterbits.displayserver.layers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public final class Layer {

	private final int layerDescriptor;
	private final boolean rootLayer;
	
	private Position position;
	private Size size;
	private LayerRectangle rectangle;
	
	private boolean visible;
	
	private Layer [] subLayers;
	
	private final List<LayerRectangle> visibleRectangles;

	Layer(int layerDescriptor, Position position, Size size, boolean rootLayer) {
		
		Objects.requireNonNull(position);
		Objects.requireNonNull(size);
	
		this.layerDescriptor = layerDescriptor;
		this.rootLayer = rootLayer;
		this.position = position;
		this.size = size;
		this.visible = false;
		
		this.visibleRectangles = new ArrayList<>();
		
		updateRectangle();
	}

	public boolean isRootLayer() {
        return rootLayer;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Position getPosition() {
		return position;
	}
    
    boolean hasSubLayers() {
        return subLayers != null && subLayers.length != 0;
    }

	Layer findLayerAt(int x, int y) {
		
	    if (!size.contains(x, y)) {
	        throw new IllegalArgumentException();
	    }
	    
		Layer found = this;
		
		if (subLayers != null) {
    		for (Layer layer : subLayers) {
    			if (layer.isVisible() && layer.getRectangle().contains(x, y)) {
    			    
    				found = layer.findLayerAt(x - layer.position.getLeft(), y - layer.position.getTop());
    				break;
    			}
    		}
		}
		
		return found;
	}
	
	public void setPosition(Position position) {
		
		Objects.requireNonNull(position);
		
		this.position = position;
		
		updateRectangle();
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		
		Objects.requireNonNull(size);
		
		this.size = size;

		updateRectangle();
	}
	
	void addSubLayer(Layer subLayer) {
	    
		Objects.requireNonNull(subLayer);
		
		if (subLayers == null) {
			subLayers = new Layer[] { subLayer };
		}
		else {
			final Layer [] newArray = new Layer[subLayers.length + 1];
			
			for (int i = 0; i < subLayers.length; ++ i) {
				newArray[i + 1] = subLayers[i];
			}

			newArray[0] = subLayer;
			
			this.subLayers = newArray;
		}
	}

	void removeSubLayer(Layer subLayer) {

		Objects.requireNonNull(subLayer);

		int layerIdx = -1;
		
		if (subLayers != null) {
    		for (int i = 0; i < subLayers.length; ++ i) {
    		    
    		    if (subLayers[i] == subLayer) {
    		        layerIdx = i;
    		        break;
    		    }
    		}
		}
		
		if (layerIdx == -1) {
		    throw new IllegalArgumentException();
		}

		if (subLayers.length == 1) {
		    this.subLayers = null;
		}
		else {
		
            final Layer [] newArray = new Layer[subLayers.length - 1];
            
            for (int i = 0; i < layerIdx; ++ i) {
                newArray[i] = subLayers[i];
            }
            
            for (int i = layerIdx; i < newArray.length; ++ i) {
                newArray[i] = subLayers[i + 1];
            }
    		
    		this.subLayers = newArray;
		}
	}
	
	public void forEachSubLayerBackToFront(Consumer<Layer> onEach) {
	    if (subLayers != null) {
	        for (int i = subLayers.length - 1; i >= 0; --i) {
	            onEach.accept(subLayers[i]);
	        }
	    }
	}
	
	private void updateRectangle() {
	    this.rectangle = new LayerRectangle(position, size);	    
	}
	
	LayerRectangle getRectangle() {
		return rectangle;
	}
	
	Layer [] getSubLayers() {
		return subLayers;
	}

	void resetRectangles() {
		visibleRectangles.clear();
		
		visibleRectangles.add(getRectangle());
	}

	static void intersectLayerOntoList(Layer toIntersect, List<LayerRectangle> updatedList) {
		
		Objects.requireNonNull(toIntersect);

		final LayerRectangle layerRectangle = toIntersect.getRectangle();
		
		for (LayerRectangle updated : updatedList) {

			final int numUpdated = updatedList.size();
			
			final OverlapType overlap = updated.splitFromIntersectingButNotIn(layerRectangle, updatedList);

			switch (overlap) {
			case NONE:
				if (updatedList.size() != numUpdated) {
					throw new IllegalStateException();
				}
				break;
				
			case INTERSECTION:
				if (updatedList.size() == numUpdated) {
					throw new IllegalStateException();
				}
				break;
				
			case OBSCURED_BY:
				if (updatedList.size() != numUpdated) {
					throw new IllegalStateException();
				}
				
				updatedList.remove(updated);
				break;
			
			case OBSCURING:
				if (updatedList.size() == numUpdated) {
					throw new IllegalStateException();
				}
				break;
				
			case EQUALS:
                throw new UnsupportedOperationException();
			}

			
			updatedList.add(updated);
		}
	}

	LayerRegion intersectLayer(Layer layer) {
		
		Objects.requireNonNull(layer);
		
		final List<LayerRectangle> updated = new ArrayList<>(visibleRectangles.size());
		
		intersectLayerOntoList(layer, updated);
		
		return new LayerRegion(updated);
	}
	
	List<LayerRectangle> updateVisibleRectangles(List<LayerRectangle> updatedRectangles) {
		
		final List<LayerRectangle> newlyVisible = new ArrayList<>(updatedRectangles.size());
		
		for (LayerRectangle updated : updatedRectangles) {
			
			for (LayerRectangle previous : visibleRectangles) {

				final int numUpdated = updatedRectangles.size();
				
				final OverlapType overlap = updated.splitFromIntersectingButNotIn(previous, newlyVisible);
				
				switch (overlap) {
				case NONE:
					if (updatedRectangles.size() != numUpdated) {
						throw new IllegalStateException();
					}

					newlyVisible.add(updated);
					break;
					
				case INTERSECTION:
					if (updatedRectangles.size() == numUpdated) {
						throw new IllegalStateException();
					}
					break;
					
				case OBSCURED_BY:
					if (updatedRectangles.size() != numUpdated) {
						throw new IllegalStateException();
					}
					break;
					
				case OBSCURING:
					if (updatedRectangles.size() == numUpdated) {
						throw new IllegalStateException();
					}
					break;
					
				case EQUALS:
				    throw new UnsupportedOperationException();
				}
			}
		}
		
		visibleRectangles.clear();
		visibleRectangles.addAll(updatedRectangles);
		
		return newlyVisible;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + layerDescriptor;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Layer other = (Layer) obj;
		if (layerDescriptor != other.layerDescriptor)
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "Layer [layerDescriptor=" + layerDescriptor + ", position=" + position + ", size=" + size + ", visible="
                + visible + "]";
    }
}
