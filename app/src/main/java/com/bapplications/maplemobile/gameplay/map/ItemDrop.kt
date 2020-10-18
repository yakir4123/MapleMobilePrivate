package com.bapplications.maplemobile.gameplay.map

import com.bapplications.maplemobile.gameplay.textures.Texture
import com.bapplications.maplemobile.opengl.utils.DrawArgument
import com.bapplications.maplemobile.opengl.utils.Point

class ItemDrop(oid: Int, owner: Int,
               start: Point, dest: Point, state: State,
               val itemId: Int, playerdrop: Boolean,
               val icon: Texture) : Drop(oid, owner, start, dest, state, playerdrop) {

    override fun draw(view: Point, alpha: Float)
    {
        if (!active)
            return;

        val absp = phobj.getAbsolute(view, alpha)
        icon.draw(DrawArgument(angle.get(alpha), absp, opacity.get(alpha)));
    }
}
