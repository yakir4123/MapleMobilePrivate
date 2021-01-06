# MapleMobile
I believe almost every developer start to develop because he wanted to create his own game,
 then he or she understands it's not that easy than just knowing how to program.
 What stops me every time was that I didn't know how to draw animations for even the most basic game.
In case I wanted to create a real game, I need to learn how to use a game engine like unity or something similar that is boring because most of the actual programming is already written for me.

# Maple Story - Android Client
Maple Story is a childhood game MMORPG with a lot of good memories, plus Maple Story has a large community of developers for Private Servers with a lot of tools and knowledge.

# Build
Clone to Android Studio should be enough

# additional repositories

Repository for **server**, which support only few operations: https://github.com/nilnil47/MapleMobileServer

Repository for the game assets which also function as http file server found in: https://gitlab.com/nilnil47/MapleMobileAssets

# Networking
This app can connect to a server implemented with golang and grpc but it's very shallow, so on the master and the release apk I'm using NetworkHandlerDemo class instead of the NetworkHandler to work offline.
If you want to change it you need to run the server (read how on MapleMobileServer repo) and change:
* Configuration.kt class the serverip
* GameEngine.kt to use networkHandler and not networkHandlerDemo


# Features
![](screenshots/moves.gif)  
For more features
[Features with gifs](FEATURES.md)

# Events Queue
I am using event queue to notify different entities on the game engine, every entity can register to type of event and every entity can send any type of event (Similar to intents and broadcast receiver in android).
For example clicking on face button ui to notify the character to change the face Ill do it this way
```kotlin
events.kt:


enum class EventType {
    ...
    ExpressionButton,
    ...
}

data class ExpressionButtonEvent (val charid: Int, val expression: Expression): Event(EventType.ExpressionButton)
```

```kotlin
    Player.kt:
    init {
        ...
        EventsQueue.instance.registerListener(EventType.ExpressionButton, this)
        ...
    }

    override fun onEventReceive(event: Event) {
        when(event.type){
            ...
            EventType.ExpressionButton -> {
                val (charid, expression) = event as ExpressionButtonEvent
                if (charid == 0) {
                    setExpression(expression)
                }
            }
            ...
        }
```
NetworkHandler.kt needs it too, So keep using this pattern makes it really easy to communicate between entities.

# Apache License 2.0 (Apache-2.0)
