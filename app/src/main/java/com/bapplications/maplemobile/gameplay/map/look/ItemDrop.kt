package com.bapplications.maplemobile.gameplay.map.look

import com.bapplications.maplemobile.gameplay.map.map_objects.Drop
import com.bapplications.maplemobile.gameplay.textures.Texture
import com.bapplications.maplemobile.utils.DrawArgument
import com.bapplications.maplemobile.utils.Point

class ItemDrop(oid: Int, owner: Int,
               start: Point, state: State,
               val itemId: Int, playerdrop: Boolean,
               val icon: Texture) : Drop(oid, owner, start, state, playerdrop) {

    override fun draw(view: Point, alpha: Float)
    {
        if (!isActive)
            return;

        val absp = phobj.getAbsolute(view, alpha)
        icon.draw(DrawArgument(angle.get(alpha), absp, opacity.get(alpha)))
        super.draw(view, alpha)
    }
}
