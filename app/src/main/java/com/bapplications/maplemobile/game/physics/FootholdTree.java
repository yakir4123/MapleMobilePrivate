package com.bapplications.maplemobile.game.physics;

import android.util.Range;

import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FootholdTree {

    private Range<Short> walls;
    private Range<Short> borders;
    private HashMap<Short, List<Short>> footholdsbyx;
    private HashMap<Short, Foothold> footholds;

    public FootholdTree(NXNode fhnode) {

        int topb = 30000;
        int leftw = 30000;
        int botb = -30000;
        int rightw = -30000;

        footholds = new HashMap<>();
        footholdsbyx = new HashMap<>();
        for (NXNode basef : fhnode)
        {
            int layer;

            try
            {
                layer = Integer.parseInt(basef.getName());
            }
            catch (Exception ex)
            {
                continue;
            }

            for (NXNode midf : basef)
            {
                for (NXNode lastf : midf)
                {
                    short id;

                    try
                    {
                        id = (short) Integer.parseInt(lastf.getName());
                    }
                    catch (Exception ex)
                    {
                        continue;
                    }

					Foothold foothold = new Foothold(lastf, id, layer);
                    footholds.put(id, foothold);

                    if (foothold.l() < leftw)
                        leftw = foothold.l();

                    if (foothold.r() > rightw)
                        rightw = foothold.r();

                    if (foothold.b() > botb)
                        botb = foothold.b();

                    if (foothold.t() < topb)
                        topb = foothold.t();

                    if (foothold.is_wall())
                        continue;

                    short start = foothold.l();
                    short end = foothold.r();

                    for (short i = start; i <= end; i++) {
                        if(!footholdsbyx.containsKey(i)){
                            footholdsbyx.put(i, new ArrayList<>());
                        }
                        footholdsbyx.get(i).add(id);
                    }
                }
            }
        }

        walls = new Range( leftw + 25, rightw - 25 );
        borders = new Range( topb - 300, botb + 100 );
    }

    public Range getBorders() {
        return borders;
    }

    public Range getWalls() {
        return walls;
    }
}
