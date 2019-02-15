package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class QueryTreeReply extends XReply {

    private final WINDOW root;
    private final WINDOW parent;
    private final List<WINDOW> children;

    public static QueryTreeReply decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        final CARD16 sequenceNumber = stream.readCARD16();
        
        readReplyLength(stream);
        
        final WINDOW root = stream.readWINDOW();
        final WINDOW parent = stream.readWINDOW();
        
        final CARD16 numberOfChildren = stream.readCARD16();
        
        stream.readPad(14);
        
        final List<WINDOW> children = new ArrayList<>(numberOfChildren.getValue());
        
        for (int i = 0; i < numberOfChildren.getValue(); ++ i) {
            children.add(stream.readWINDOW());
        }
        
        return new QueryTreeReply(sequenceNumber, root, parent, children);
    }
    
    public QueryTreeReply(CARD16 sequenceNumber, WINDOW root, WINDOW parent, List<WINDOW> children) {
        super(sequenceNumber);
    
        Objects.requireNonNull(root);
        Objects.requireNonNull(parent);
        Objects.requireNonNull(children);
        
        this.root = root;
        this.parent = parent;
        this.children = children;
    }

    public WINDOW getRoot() {
        return root;
    }

    public WINDOW getParent() {
        return parent;
    }

    public List<WINDOW> getChildren() {
        return children;
    }

    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap(
                "root", root,
                "parent", parent,
                "children", children
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        writeUnusedByte(stream);
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, children.size());

        stream.writeWINDOW(root);
        stream.writeWINDOW(parent);
        stream.writeCARD16(new CARD16(children.size()));
        
        stream.pad(14);
        
        for (WINDOW child : children) {
            stream.writeWINDOW(child);
        }
    }
}
