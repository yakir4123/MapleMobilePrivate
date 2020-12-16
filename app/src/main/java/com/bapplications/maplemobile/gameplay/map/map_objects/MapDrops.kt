package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.gameplay.components.ColliderComponent
import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.map.map_objects.spawns.DropSpawn
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.player.inventory.ItemData
import com.bapplications.maplemobile.gameplay.textures.Texture
import com.bapplications.maplemobile.utils.Point

import java.util.*

class MapDrops {
    var lootEnabled: Boolean = false
    val drops: MapObjects<Drop> = MapObjects()
    val spawns: Queue<DropSpawn> = LinkedList()

    fun draw(layer: Layer, view: Point, alpha: Float)
    {
        drops.draw(layer, view, alpha);
    }

    fun update(physics: Physics, deltaTime: Int)
    {
        while(!spawns.isEmpty())
        {
            val spawn = spawns.poll()

            val oid = spawn.oid
            val drop = drops[oid]
            if (drop != null)
            {
                drop.makeActive()
            }
            else
            {
                val itemid = spawn.itemId
                val meso = spawn.isMeso()

                if (meso)
                {
                    // todo spawn meso
                }
                else
                {
                    val itemdata: ItemData? = ItemData.get(itemid)
                    val icon = Texture(itemdata?.icon(true))
                    // icon is actually the "DropModel"
                    // so it is not necessary to create new class for it
                    icon.pos = icon.dimension.scalarMul(-0.5f)//Point(-16f, -24f)
                    drops.add(spawn.instantiate(icon));
                }
            }
        }

        drops.update(physics, deltaTime);

        lootEnabled = true
    }

    fun spawn(spawn: DropSpawn)
    {
        spawns.add(spawn)
    }

    operator fun get(index: Int) = drops[index]

    fun remove(oid: Int, state: Drop.State, looter: MapObject)
    {
        val drop: Drop? = drops[oid] as Drop
        drop?.expire(state, looter);
    }

    fun clear()
    {
        drops.clear();
    }

    fun inRange(collider: ColliderComponent): Drop? {
        for(drop in drops.objects.values){
            if(drop.isActive && drop.collider.overlaps(collider.collider)){
                return drop
            }
        }
        return null
    }

}