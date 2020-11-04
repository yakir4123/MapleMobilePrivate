package com.bapplications.maplemobile.gameplay.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class PlayerStatsViewModel : ViewModel() {
    enum class Id {
        SKIN, FACE, HAIR, LEVEL, JOB, STR, DEX, INT, LUK, HP, MAX_HP, MP, MAX_MP, AP, SP, EXP, FAME, MESO, PET
    }

    val name = MutableLiveData<String>()
    private val stats: MutableMap<Id, MutableLiveData<Short>> = EnumMap(Id::class.java)

    init {
        for(id in Id.values()) {
            stats[id] = MutableLiveData<Short>()
        }
    }

    fun getStat(id: Id): LiveData<Short> {
        return stats[id]!!
    }

    fun setStat(id: Id, value: Short): PlayerStatsViewModel {
        stats[id]!!.postValue(value)
        return this
    }

    fun addStat(id: Id, value: Short): PlayerStatsViewModel {
        val curVal = getStat(id).value!!
        var newVal = curVal + value
        if (Short.MAX_VALUE - value < curVal) newVal = Short.MAX_VALUE.toInt()
        if (Short.MIN_VALUE + value > curVal) newVal = Short.MIN_VALUE.toInt()
        return setStat(id, newVal.toShort())
        return this
    }
}