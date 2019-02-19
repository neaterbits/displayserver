package com.neaterbits.displayserver.layers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public final class Layer {

	private final int layerDescriptor;
	private final Layer parentLayer;
	
	private Position position;
	private Size size;
	private LayerRectangle rectangle;
	
	private boolean visible;
	
	private Layer [] subLayers;
	
	private final List<LayerRectangle> visibleRectangles;

	Layer(int layerDescriptor, Position position, Size size, Layer parentLayer) {
		
		Objects.requireNonNull(position);
		Objects.requireNonNull(size);
	
		this.layerDescriptor = layerDescriptor;
		this.parentLayer = parentLayer;
		this.position = position;
		this.size = size;
		this.visible = false;
		
		this.visibleRectangles = new ArrayList<>();
		
		updateRectangle();
	}

	public boolean isRootLayer() {
        return parentLayer == null;
    }

    public boolean isVisible() {
        return visible;
    }

    void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Position getPosition() {
		return position;
	}
    
    boolean hasSubLayers() {
        return subLayers != null && subLayers.length != 0;
    }

    public int getAbsoluteLeft() {
        
        int left = position.getLeft();
        
        for (Layer layer = parentLayer; layer != null; layer = layer.parentLayer) {
            left += layer.getPosition().getLeft();
        }

        return left;
    }

    public int getAbsoluteTop() {
        
        int top = position.getTop();
        
        for (Layer layer = parentLayer; layer != null; layer = layer.parentLayer) {
            top += layer.getPosition().getTop();
        }

        return top;
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

	static void intersectFrontLayerOntoList(Layer toIntersect, LayerComputeWorkArea workArea) {
		
		Objects.requireNonNull(toIntersect);

		final LayerRectangle layerRectangle = toIntersect.getRectangle();

        workArea.splitTempList.clear();
        workArea.splitRemoveList.clear();

		for (LayerRectangle updated : workArea.intersectList) {
			
            int prevSplitListSize = workArea.splitTempList.size();

            final OverlapType overlap = updated.splitFromIntersectingButNotIn(layerRectangle, workArea.splitTempList);

			switch (overlap) {
			case NONE:
				if (workArea.splitTempList.size() != prevSplitListSize) {
					throw new IllegalStateException();
				}
				break;
				
			case INTERSECTION:
                if (workArea.splitTempList.size() == prevSplitListSize) {
					throw new IllegalStateException();
				}
				break;
				
			case THIS_WITHIN:
                if (workArea.splitTempList.size() != prevSplitListSize) {
					throw new IllegalStateException();
				}
				
				workArea.splitRemoveList.add(updated);
				break;
			
			case OTHER_WITHIN:
                if (workArea.splitTempList.size() == prevSplitListSize) {
					throw new IllegalStateException("## " + workArea.splitTempList + "/" + prevSplitListSize);
				}
                workArea.splitRemoveList.add(updated);
				break;
				
			case EQUALS:
                throw new UnsupportedOperationException();
			}
		}

		workArea.intersectList.removeAll(workArea.splitRemoveList);
		workArea.intersectList.addAll(workArea.splitTempList);
	}

	LayerRegion intersectLayer(Layer layer) {
		
		Objects.requireNonNull(layer);
		
		final LayerComputeWorkArea workArea = new LayerComputeWorkArea();
		
		intersectFrontLayerOntoList(layer, workArea);
		
		return new LayerRegion(workArea.intersectList);
	}
	
	List<LayerRectangle> updateVisibleRectangles(List<LayerRectangle> updatedRectangles) {
		
		final List<LayerRectangle> newlyVisible = new ArrayList<>(updatedRectangles.size());
		
		if (visibleRectangles.isEmpty()) {
		    newlyVisible.addAll(updatedRectangles);
		}
		else {
		
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
    					
    				case THIS_WITHIN:
    					if (updatedRectangles.size() != numUpdated) {
    						throw new IllegalStateException();
    					}
    					break;
    					
    				case OTHER_WITHIN:
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
		}
		
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
        
        return String.format("%d (%d,%d,%d,%d)",
                layerDescriptor,
                position.getLeft(), position.getTop(),
                size.getWidth(), size.getHeight());
    }
}
