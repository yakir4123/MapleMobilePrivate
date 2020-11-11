package com.bapplications.maplemobile.input

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

import com.bapplications.maplemobile.input.events.Event
import com.bapplications.maplemobile.input.events.EventType
import com.bapplications.maplemobile.input.events.EventListener
import java.lang.ref.WeakReference


class EventsQueue {

    private object HOLDER {
        val INSTANCE = EventsQueue()
    }

    companion object {
        val instance: EventsQueue by lazy { HOLDER.INSTANCE }
    }


    val queue = ConcurrentLinkedQueue<Event>()
    var listeners = EnumMap<EventType, MutableList<WeakReference<EventListener>>>(EventType::class.java)

    fun enqueue(event: Event) {
        queue.add(event)
    }

    fun dequeue() {
        val event = queue.poll()
        event?.type?.let {
            listeners[event.type]?.forEach { eventListener -> eventListener.get()?.onEventReceive(event) }
        }
    }

    fun registerListener(to: EventType, listener: EventListener) {
        if(!listeners.containsKey(to)){
            listeners[to] = mutableListOf()
        }
        listeners[to]?.add(WeakReference(listener))
    }

    fun dequeueAll() {
        while(!queue.isEmpty())
        {
            dequeue()
        }
    }

}


