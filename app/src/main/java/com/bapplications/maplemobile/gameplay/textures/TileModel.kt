package com.bapplications.maplemobile.gameplay.textures

import com.bapplications.maplemobile.constatns.Loaded
import com.bapplications.maplemobile.pkgnx.NXNode

class TileModel(tileset: String, u: String, no: Int) :
        Texture(Loaded.getFile(Loaded.WzFileName.MAP).root
                .getChild<NXNode>("Tile").getChild<NXNode>(tileset)
                .getChild<NXNode>(u)
                .getChild(no), true)
