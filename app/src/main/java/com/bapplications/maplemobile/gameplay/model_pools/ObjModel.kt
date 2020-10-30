package com.bapplications.maplemobile.gameplay.model_pools

import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.constatns.Loaded

class ObjModel(oS: String, l0: String, l1: String, l2: String, z: Byte) :
        AnimationModel(Loaded.getFile(Loaded.WzFileName.MAP).root.getChild<NXNode>("Obj")
                    .getChild<NXNode>(oS)
                    .getChild<NXNode>(l0)
                    .getChild<NXNode>(l1)
                    .getChild(l2),
                z)



