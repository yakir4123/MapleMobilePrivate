# MapleMobile
I believe almost every developer start develop because he wanted to create his own game,
 than he or she understand that's not that easy than just knowing how to program.
 What stops me every time was that I didn't know how to draw animations for every even the most basic game,
 Plus if I wanted to create a real game I need to learn how to use a game engine like unity which is
 boring because most of the real programming is already done...

# Maple Story - Android Client
Maple Story is childhood game MMORPG with a lot of good memories, plus Maple Story has a large
 community of developers for Private Servers with a lot of tools and knowledge.

# Build
Clone to Android Studio should be enough

# additional repositories

Repository for **server**, which support only few operations: https://github.com/nilnil47/MapleMobileServer

Repository for the game assets which also function as http file server found in: https://gitlab.com/nilnil47/MapleMobileAssets

# Networking
This app can connect to a server implemented with golang and grpc but its very shallow, so on the master and the release apk I'm using NetworkHandlerDemo class instead the NetworkHandler to work offline.
If you want to change it you need to run the server (read how on MapleMobileServer repo) and change:
* Configuration.kt class the serverip
* GameEngine.kt to use networkHandler and not networkHandlerDemo


# Features
[Features with gifs](FEATURES.md)

# Apache License 2.0 (Apache-2.0)
