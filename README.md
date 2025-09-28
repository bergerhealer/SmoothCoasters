# SmoothCoasters

SmoothCoasters is a client-side mod which contains significant enhancements to make your experience on roller coasters more realistic.

This mod does not add roller coasters to the game, it only improves existing ones.

## Camera rotation
On supported servers, your head will be rotated automatically while you ride a roller coaster. Your mouse is not locked - you can still look around.
Spigot plugin developers can use [SmoothCoastersAPI](https://github.com/bergerhealer/SmoothCoastersAPI) to support this feature. TrainCarts 1.16.1+ already supports it - simply enable the Lock Rotation seat option.

## Rendering improvements
The following features work on all servers (and in single player):

* Fixes jittering of armor stands (applies rotations slowly instead of setting them instantly, [MC-124519](https://bugs.mojang.com/browse/MC-124519))
* Fixes custom models on armor stands disappearing if you look away ([MC-96853](https://bugs.mojang.com/browse/MC-96853))

**NOTE:** This mod requires the Fabric mod loader.
