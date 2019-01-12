package com.neaterbits.displayserver.layers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public final class Layer {

	private final int layerDescriptor;
	private Position position;
	private Size size;
	
	private Layer [] subLayers;
	
	private final List<Rectangle> visibleRectangles;

	Layer(int layerDescriptor, Position position, Size size) {
		
		Objects.requireNonNull(position);
		Objects.requireNonNull(size);
	
		this.layerDescriptor = layerDescriptor;
		this.position = position;
		this.size = size;
		
		this.visibleRectangles = new ArrayList<>();
	}

	public Position getPosition() {
		return position;
	}

	Layer findLayerAt(int x, int y) {
		
		if (!getRectangle().contains(x, y)) {
			throw new IllegalArgumentException();
		}
		
		Layer found = null;
		
		for (Layer layer : subLayers) {
			if (layer.getRectangle().contains(x, y)) {
				found = layer.findLayerAt(x - layer.position.getLeft(), y - layer.position.getTop());
				break;
			}
		}
		
		if (found == null) {
			found = this;
		}
		
		return found;
	}
	
	public void setPosition(Position position) {
		
		Objects.requireNonNull(position);
		
		this.position = position;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		
		Objects.requireNonNull(size);
		
		this.size = size;
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

		final Layer [] newArray = new Layer[subLayers.length - 1];
		
		int dstIdx = 0;
		
		for (Layer layer : subLayers) {
			if (layer != subLayer) {
				newArray[dstIdx ++] = layer;
			}
		}
		
		if (dstIdx != newArray.length) {
			throw new IllegalStateException();
		}
		
		this.subLayers = newArray;
	}
	
	public void forEachSubLayerBackToFront(Consumer<Layer> onEach) {
	    if (subLayers != null) {
	        for (int i = subLayers.length - 1; i >= 0; --i) {
	            onEach.accept(subLayers[i]);
	        }
	    }
	}
	
	Rectangle getRectangle() {
		return new Rectangle(position, size);
	}
	
	Layer [] getSubLayers() {
		return subLayers;
	}

	void resetRectangles() {
		visibleRectangles.clear();
		
		visibleRectangles.add(getRectangle());
	}

	static void intersectLayerOntoList(Layer toIntersect, List<Rectangle> updatedList) {
		
		Objects.requireNonNull(toIntersect);

		final Rectangle layerRectangle = toIntersect.getRectangle();
		
		for (Rectangle updated : updatedList) {

			final int numUpdated = updatedList.size();
			
			final Intersection intersection = updated.splitFromIntersectingButNotIn(layerRectangle, updatedList);

			switch (intersection) {
			case NONE:
				if (updatedList.size() != numUpdated) {
					throw new IllegalStateException();
				}
				break;
				
			case OVERLAP:
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
			}

			
			updatedList.add(updated);
		}
	}

	Region intersectLayer(Layer layer) {
		
		Objects.requireNonNull(layer);
		
		final List<Rectangle> updated = new ArrayList<>(visibleRectangles.size());
		
		intersectLayerOntoList(layer, updated);
		
		return new Region(updated);
	}
	
	List<Rectangle> updateVisibleRectangles(List<Rectangle> updatedRectangles) {
		
		final List<Rectangle> newlyVisible = new ArrayList<>(updatedRectangles.size());
		
		for (Rectangle updated : updatedRectangles) {
			
			for (Rectangle previous : visibleRectangles) {

				final int numUpdated = updatedRectangles.size();
				
				final Intersection intersection = updated.splitFromIntersectingButNotIn(previous, newlyVisible);
				
				switch (intersection) {
				case NONE:
					if (updatedRectangles.size() != numUpdated) {
						throw new IllegalStateException();
					}

					newlyVisible.add(updated);
					break;
					
				case OVERLAP:
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
}
