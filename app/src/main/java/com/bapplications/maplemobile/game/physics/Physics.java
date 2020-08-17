package com.bapplications.maplemobile.game.physics;

import com.bapplications.maplemobile.pkgnx.NXNode;

public class Physics {
    private FootholdTree footholdtree;
    public Physics(NXNode fhnode) {
        footholdtree = new FootholdTree(fhnode);
    }

    public FootholdTree getFHT() {
        return footholdtree;
    }
}
