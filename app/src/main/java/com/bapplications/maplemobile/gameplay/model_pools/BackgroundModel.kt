package com.bapplications.maplemobile.gameplay.model_pools

import com.bapplications.maplemobile.constatns.Loaded
import com.bapplications.maplemobile.pkgnx.NXNode

class BackgroundModel(bs: String, ani: String, no: Int):
        AnimationModel(Loaded.getFile(Loaded.WzFileName.MAP).root.getChild<NXNode>("Back")
                    .getChild<NXNode>(bs)
                    .getChild<NXNode>(ani)
                    .getChild(no),
                "0")