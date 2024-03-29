package com.neaterbits.displayserver.layers;

import java.util.List;

public abstract class LayerRectangleIntersection extends LayerRectangleBase {

    LayerRectangleIntersection(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    LayerRectangleIntersection(LayerRectangle toCopy) {
        super(toCopy);
    }
    
    final Pos getHPos(int x) {
        return Pos.getHPos(left, width, x);
    }
    

    final Pos getVPos(int y) {
        return Pos.getVPos(top, height, y);
    }
        

    private IntersectionType intersectLeftBeforeOrAtStartRightAtStartOrWithin(LayerRectangle inFront) {
        
        final IntersectionType intersection;
        
        switch (getVPos(inFront.top)) {
        case BEFORE:
        case AT_START:
            
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
                if (getVPos(inFront.top) != Pos.BEFORE) {
                    throw new IllegalStateException();
                }
                intersection = IntersectionType.NONE;
                break;
                
            case AT_START:
            case WITHIN:
                intersection = IntersectionType.UPPER_LEFT;
                break;
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LEFT;
                break;
                
            default:
                throw new UnsupportedOperationException();
            }
            break;
            
            
        case WITHIN:
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
            case AT_START:
                throw new IllegalStateException();
                
            case WITHIN:
                intersection = IntersectionType.LEFT_PART;
                break;
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LOWER_LEFT;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case AT_END:
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
            case AT_START:
            case WITHIN:
                throw new IllegalStateException();
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LOWER_LEFT;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case AFTER:
            intersection = IntersectionType.NONE;
            break;

        default:
            throw new UnsupportedOperationException();
        }
        
        return intersection;
    }

    private IntersectionType intersectLeftBeforeOrAtStartRightAtEndOrAfter(LayerRectangle inFront) {
        
        final IntersectionType intersection;
        
        switch (getVPos(inFront.top)) {
        case BEFORE:
        case AT_START:
            
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
                if (getVPos(inFront.top) != Pos.BEFORE) {
                    throw new IllegalStateException();
                }
                intersection = IntersectionType.NONE;
                break;
                
            case AT_START:
            case WITHIN:
                intersection = IntersectionType.UPPER;
                break;
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.OBSCURED;
                break;
                
            default:
                throw new UnsupportedOperationException();
            }
            break;
            
            
        case WITHIN:
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
            case AT_START:
                throw new IllegalStateException();
                
            case WITHIN:
                intersection = IntersectionType.MID_VERTICALLY;
                break;
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LOWER;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case AT_END:
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
            case AT_START:
            case WITHIN:
                throw new IllegalStateException();
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LOWER;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case AFTER:
            intersection = IntersectionType.NONE;
            break;

        default:
            throw new UnsupportedOperationException();
        }
        
        return intersection;
    }

    private IntersectionType intersectLeftBeforeOrAtStart(LayerRectangle inFront) {

        final IntersectionType intersection;
        
        switch (getHPos(inFront.getRight())) {
        case BEFORE:
            if (getHPos(inFront.left) != Pos.BEFORE) {
                throw new IllegalStateException();
            }
            intersection = IntersectionType.NONE;
            break;
            
        case AT_START:
        case WITHIN:
            intersection = intersectLeftBeforeOrAtStartRightAtStartOrWithin(inFront);
            break;
            
        case AT_END:
        case AFTER:
            intersection = intersectLeftBeforeOrAtStartRightAtEndOrAfter(inFront);
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        return intersection;
    }

    private IntersectionType intersectLeftWithinRightWithin(LayerRectangle inFront) {
        
        final IntersectionType intersection;
        
        switch (getVPos(inFront.top)) {
        case BEFORE:
        case AT_START:
            
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
                if (getVPos(inFront.top) != Pos.BEFORE) {
                    throw new IllegalStateException();
                }
                intersection = IntersectionType.NONE;
                break;
                
            case AT_START:
            case WITHIN:
                intersection = IntersectionType.UPPER_PART;
                break;
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.MID_HORIZONTALLY;
                break;
                
            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case WITHIN:
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
            case AT_START:
                throw new IllegalStateException();
                
            case WITHIN:
                intersection = IntersectionType.WITHIN;
                break;
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LOWER_PART;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case AT_END:
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
            case AT_START:
            case WITHIN:
                throw new IllegalStateException();
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LOWER_PART;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case AFTER:
            intersection = IntersectionType.NONE;
            break;

        default:
            throw new UnsupportedOperationException();
        }
        
        return intersection;
    }

    private IntersectionType intersectLeftWithinRightAtEndOrAfter(LayerRectangle inFront) {
        
        final IntersectionType intersection;
        
        switch (getVPos(inFront.top)) {
        case BEFORE:
        case AT_START:
            
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
                if (getVPos(inFront.top) != Pos.BEFORE) {
                    throw new IllegalStateException();
                }
                intersection = IntersectionType.NONE;
                break;
                
            case AT_START:
            case WITHIN:
                intersection = IntersectionType.UPPER_RIGHT;
                break;
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.RIGHT;
                break;
                
            default:
                throw new UnsupportedOperationException();
            }
            break;
            
            
        case WITHIN:
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
            case AT_START:
                throw new IllegalStateException();
                
            case WITHIN:
                intersection = IntersectionType.RIGHT_PART;
                break;
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LOWER_RIGHT;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case AT_END:
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
            case AT_START:
            case WITHIN:
                throw new IllegalStateException();
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LOWER_RIGHT;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case AFTER:
            intersection = IntersectionType.NONE;
            break;

        default:
            throw new UnsupportedOperationException();
        }
        
        return intersection;
    }

    private IntersectionType intersectLeftWithin(LayerRectangle inFront) {

        final IntersectionType intersection;
        
        switch (getHPos(inFront.getRight())) {
        case BEFORE:
        case AT_START:
            throw new IllegalStateException();
            
        case WITHIN:
            intersection = intersectLeftWithinRightWithin(inFront);
            break;
            
        case AT_END:
        case AFTER:
            intersection = intersectLeftWithinRightAtEndOrAfter(inFront);
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        return intersection;
    }

    private IntersectionType intersectLeftAtEndRightAtEndOrAfter(LayerRectangle inFront) {
        
        final IntersectionType intersection;
        
        switch (getVPos(inFront.top)) {
        case BEFORE:
        case AT_START:
            
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
                if (getVPos(inFront.top) != Pos.BEFORE) {
                    throw new IllegalStateException();
                }
                intersection = IntersectionType.NONE;
                break;
                
            case AT_START:
            case WITHIN:
                intersection = IntersectionType.UPPER_RIGHT;
                break;
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.RIGHT;
                break;
                
            default:
                throw new UnsupportedOperationException();
            }
            break;
            
            
        case WITHIN:
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
            case AT_START:
                throw new IllegalStateException();
                
            case WITHIN:
                intersection = IntersectionType.RIGHT_PART;
                break;
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LOWER_RIGHT;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case AT_END:
            switch (getVPos(inFront.getLower())) {
            case BEFORE:
            case AT_START:
            case WITHIN:
                throw new IllegalStateException();
                
            case AT_END:
            case AFTER:
                intersection = IntersectionType.LOWER_RIGHT;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
            
        case AFTER:
            intersection = IntersectionType.NONE;
            break;

        default:
            throw new UnsupportedOperationException();
        }
        
        return intersection;
    }

    private IntersectionType intersectLeftAtEnd(LayerRectangle inFront) {

        final IntersectionType intersection;
        
        switch (getHPos(inFront.getRight())) {
        case BEFORE:
        case AT_START:
        case WITHIN:
            throw new IllegalStateException();
            
        case AT_END:
        case AFTER:
            intersection = intersectLeftAtEndRightAtEndOrAfter(inFront);
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        return intersection;
    }

    private IntersectionType intersectAlternative(LayerRectangle inFront) {
        
        final IntersectionType intersection;

        final Pos leftPos = getHPos(inFront.left);
        
        System.out.format("## left %s right %s top %s lower %s\n",
                getHPos(inFront.left),
                getHPos(inFront.getRight()),
                getVPos(inFront.top),
                getVPos(inFront.getLower()));
        
        switch (leftPos) {
        
        case BEFORE:
        case AT_START:
            intersection = intersectLeftBeforeOrAtStart(inFront);
            break;
        
        case WITHIN:
            intersection = intersectLeftWithin(inFront);
            break;

        case AT_END:
            intersection = intersectLeftAtEnd(inFront);
            break;
            
        case AFTER:
            intersection = IntersectionType.NONE;
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        System.out.println("## intersection " + intersection);
        
        return intersection;
    }

    private void buildSplitList(LayerRectangle inFront, IntersectionType inFrontIntersection, List<LayerRectangle> splitList) {
        
        switch (inFrontIntersection) {
        case UPPER_LEFT:
            
            splitList.add(new LayerRectangle(
                    inFront.left + inFront.width,
                    top,
                    width - (inFront.left + inFront.width - left),
                    inFront.top + inFront.height - top));
            
            splitList.add(new LayerRectangle(
                    left,
                    inFront.top + inFront.height,
                    width,
                    height - (inFront.top + inFront.height - top)));
            break;
        
        case UPPER:
            splitList.add(new LayerRectangle(
                    left,
                    inFront.top + inFront.height,
                    width,
                    height - (inFront.top + inFront.height - top)));
            break;

        case UPPER_PART:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    inFront.left - left,
                    height));
            
            splitList.add(new LayerRectangle(
                    inFront.left,
                    inFront.top + inFront.height,
                    inFront.width,
                    height - (inFront.top - top + inFront.height)));

            splitList.add(new LayerRectangle(
                    inFront.left + inFront.width,
                    top,
                    width - (inFront.left - left) - inFront.width,
                    height));
            break;
            
        case UPPER_RIGHT:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    inFront.left - left,
                    inFront.top + inFront.height - top));
            
            splitList.add(new LayerRectangle(
                    left,
                    inFront.top + inFront.height,
                    width,
                    height - (inFront.top + inFront.height - top)));
            break;

        case RIGHT:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    inFront.left - left,
                    height));
            break;
            
        case RIGHT_PART:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    width,
                    inFront.top - top));

            splitList.add(new LayerRectangle(
                    left,
                    inFront.top,
                    inFront.left - left,
                    inFront.height));

            splitList.add(new LayerRectangle(
                    left,
                    inFront.top + inFront.height,
                    width,
                    height - (inFront.top - top) - inFront.height));
            break;

        case LOWER_RIGHT:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    width,
                    inFront.top - top));
            
            splitList.add(new LayerRectangle(
                    left,
                    inFront.top,
                    inFront.left - left,
                    height - (inFront.top - top)));
            break;

        case LOWER:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    width,
                    inFront.top - top));
            break;

        case LOWER_PART:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    width,
                    inFront.top - top));

            splitList.add(new LayerRectangle(
                    left,
                    inFront.top,
                    inFront.left - left,
                    height - (inFront.top - top)));
            
            splitList.add(new LayerRectangle(
                    inFront.left + inFront.width,
                    inFront.top,
                    width - (inFront.left - left) - inFront.width,
                    height - (inFront.top - top)));
            break;

            
        case LOWER_LEFT:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    width,
                    inFront.top - top));

            splitList.add(new LayerRectangle(
                    inFront.left + inFront.width,
                    inFront.top,
                    width - (inFront.left - left) - inFront.width,
                    height - (inFront.top - top)));
            break;
            
        case LEFT:
            splitList.add(new LayerRectangle(
                    inFront.left + inFront.width,
                    top,
                    width - (inFront.left + inFront.width - left),
                    height));
            break;
            
            
        case LEFT_PART:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    width,
                    inFront.top - top));

            splitList.add(new LayerRectangle(
                    inFront.left + inFront.width,
                    inFront.top,
                    width - (inFront.left - left)- inFront.width,
                    inFront.height));

            splitList.add(new LayerRectangle(
                    left,
                    inFront.top + inFront.height,
                    width,
                    height - (inFront.top - top) - inFront.height));
            break;

        case MID_HORIZONTALLY:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    inFront.left - left,
                    height));

            splitList.add(new LayerRectangle(
                    inFront.left + inFront.width,
                    top,
                    width - (inFront.left - left) - inFront.width,
                    height));
           break; 

        case MID_VERTICALLY:
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    width,
                    inFront.top - top));

            splitList.add(new LayerRectangle(
                    left,
                    inFront.top + inFront.height,
                    width,
                    height - (inFront.top - top) - inFront.height));
           break; 
            
        case WITHIN:
            // upper
            splitList.add(new LayerRectangle(
                    left,
                    top,
                    width,
                    inFront.top - top));
            
            // right
            splitList.add(new LayerRectangle(
                    inFront.left + inFront.width,
                    inFront.top,
                    width - inFront.width - (inFront.left - left),
                    inFront.height));
            
            // lower
            splitList.add(new LayerRectangle(
                    left,
                    inFront.top + inFront.height,
                    width,
                    height - inFront.height - (inFront.top - top)));

            // left
            splitList.add(new LayerRectangle(
                    left,
                    inFront.top,
                    inFront.left - left,
                    inFront.height));
            break;
            
        case OBSCURED:
            break;
        
        default:
            throw new UnsupportedOperationException(inFrontIntersection.toString());
        }
        
    }

    
    final IntersectionType intersect(LayerRectangle inFront, List<LayerRectangle> splitList) {

        final IntersectionType intersection = intersectAlternative(inFront);

        if (intersection != IntersectionType.NONE && splitList != null) {
            buildSplitList(inFront, intersection, splitList);
        }
        
        return intersection;
    }
}
