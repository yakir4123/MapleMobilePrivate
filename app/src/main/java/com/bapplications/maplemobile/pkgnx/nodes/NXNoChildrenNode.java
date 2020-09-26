package com.bapplications.maplemobile.pkgnx.nodes;

import com.bapplications.maplemobile.pkgnx.NXNode;

public class NXNoChildrenNode extends NXNode {

    public byte nulls = 0;

    @Override
    public Object get() {
        return null;
    }

    @Override
    public NXNode getChild(String name) {
        nulls++;
        return this;
    }
}
