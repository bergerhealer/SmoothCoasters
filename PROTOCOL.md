# SmoothCoasters protocol

SmoothCoasters communicates using plugin channels.
Changes to this protocol require increasing the protocol version.

## Handshake

1. The server sends its list of supported protocol versions in `smoothcoasters:hs`.
2. The client selects the highest version which it supports from that list.
3. Then, the client responds with the selected protocol version and the mod version in `smoothcoasters:hs`.
4. The server receives this and now knows which protocol version it can use.

## SmoothCoasters versions

| SmoothCoasters | Game           | V1  | V2  | V3  | V4  | V5  | V6  |
|----------------|----------------|-----|-----|-----|-----|-----|-----|
| 1.16.1-13      | 1.15.2-1.16.3  | Yes | -   | -   | -   | -   | -   |
| 1.16.4-19      | 1.16.4-1.16.5  | Yes | Yes | -   | -   | -   | -   |
| **1.16.5-v1**  | 1.16.5         | Yes | Yes | Yes | Yes | -   | -   |
| 1.17.1-v1      | 1.17.1         | Yes | Yes | Yes | -   | -   | -   |
| 1.17.1-v2      | 1.17.1         | Yes | Yes | Yes | -   | -   | -   |
| **1.17.1-v3**  | 1.17.1         | Yes | Yes | Yes | Yes | -   | -   |
| 1.18.1-v1      | 1.18-1.18.1    | Yes | Yes | Yes | -   | -   | -   |
| **1.18.2-v1**  | 1.18-1.18.2    | Yes | Yes | Yes | Yes | -   | -   |
| **1.19-v1**    | 1.19-1.19.2    | Yes | Yes | Yes | Yes | -   | -   |
| **1.19.3-v1**  | 1.19.3         | Yes | Yes | Yes | Yes | -   | -   |
| **1.19.4-v1**  | 1.19.4         | -   | -   | -   | Yes | -   | -   |
| **1.19.4-v2**  | 1.19.4         | -   | -   | -   | Yes | Yes | -   |
| **1.20-v1**    | 1.20-1.20.4    | -   | -   | -   | Yes | Yes | -   |
| **1.20-v2**    | 1.20-1.20.4    | -   | -   | -   | Yes | Yes | -   |
| **1.20.6-v1**  | 1.20.6         | -   | -   | -   | Yes | Yes | -   |
| **1.21-v1**    | 1.21           | -   | -   | -   | Yes | Yes | -   |
| **1.21.3-v1**  | 1.21.3-1.21.4  | -   | -   | -   | Yes | Yes | -   |
| **1.21.5-v1**  | 1.21.5         | -   | -   | -   | Yes | Yes | -   |
| **1.21.6-v1**  | 1.21.6-1.21.8  | -   | -   | -   | Yes | Yes | -   |
| **1.21.10-v1** | 1.21.9-1.21.10 | -   | -   | -   | Yes | Yes | Yes |

## SmoothCoastersAPI versions

| SmoothCoastersAPI | V1  | V2  | V3  | V4  | V5  | V6  |
|-------------------|-----|-----|-----|-----|-----|-----|
| **1.1**           | Yes | -   | -   | -   | -   | -   |
| **1.2**           | Yes | -   | -   | -   | -   | -   |
| **1.3**           | Yes | Yes | -   | -   | -   | -   |
| **1.4**           | Yes | Yes | Yes | -   | -   | -   |
| **1.5**           | Yes | Yes | Yes | -   | -   | -   |
| **1.6**           | Yes | Yes | Yes | -   | -   | -   |
| **1.7**           | Yes | Yes | Yes | Yes | -   | -   |
| **1.8**           | -   | -   | -   | Yes | -   | -   |
| **1.9**           | -   | -   | -   | Yes | Yes | -   |
| **1.10**          | -   | -   | -   | Yes | Yes | -   |
| **1.11**          | -   | -   | -   | Yes | Yes | Yes |

**Note:** Server-side support of protocols V1-V3 was removed in SmoothCoastersAPI v1.8,
but protocol V4 has been backported to all affected game versions.

## Protocol versions

| Protocol | Camera | Rotation limit | Entity rotation | Bulk | Rotation mode |
|----------|--------|----------------|-----------------|------|---------------|
| **V1**   | Yes    | -              | -               | Yes  | -             |
| **V2**   | Yes    | -              | Yes             | Yes  | -             |
| **V3**   | Yes    | -              | Yes             | Yes  | Yes           |
| **V4**   | Yes    | Yes            | Yes             | -    | -             |
| **V5**   | Yes    | Yes            | Yes             | -    | -             |
| **V6**   | Yes    | Yes            | -               | -    | -             |

### Protocol V1

https://github.com/bergerhealer/SmoothCoasters/blob/2254e3a7692ebf0a1cfb0effe147dbca724244a3/src/main/java/me/m56738/smoothcoasters/implementation/ImplV1.java

**Initial version. Supports camera rotation and bulk packets.**

Bulk packets allow servers to send many packets inside a single packet (like bundle packets which were later added to
the game, but with better compression).

Only rotates the camera of the player without changing the player yaw/pitch (`CAMERA` rotation mode).
This causes some rendering and interaction issues, but has better compatibility with anti-cheats.

### Protocol V2

https://github.com/bergerhealer/SmoothCoasters/blob/2254e3a7692ebf0a1cfb0effe147dbca724244a3/src/main/java/me/m56738/smoothcoasters/implementation/ImplV2.java

**Adds an entity rotation packet which allows the server to apply an arbitrary rotation to any entity.**

### Protocol V3

https://github.com/bergerhealer/SmoothCoasters/blob/c5654199596c3ab2f2782467f6f3dc41e9405677/src/main/java/me/m56738/smoothcoasters/implementation/ImplV3.java

**Adds configurable entity rotation tick durations and allows selecting the rotation mode.**

The new `PLAYER` rotation mode turns the player head (yaw/pitch) and then adds the roll to the camera.
This fixes all issues caused by the previous `CAMERA` mode since the player looks in the same direction as the camera.
However, it might cause issues with anti-cheats since players automatically turn their heads.

### Protocol V4

https://github.com/bergerhealer/SmoothCoasters/blob/71f18cbbbae1ddcae6b96d9be174e6d2972586f2/src/main/java/me/m56738/smoothcoasters/implementation/ImplV4.java

**Adds configurable rotation limits.**

Rotation limits allow servers to limit the yaw/pitch range of the mouse rotation of the player.
For example, limiting yaw between -70 and 70 prevents them from looking behind them.

**Removes bulk packets and the configurable rotation mode.**

Bulk packets were rarely used because they were difficult to use and caused issues since they bypassed packet listeners.

Always uses `PLAYER` rotation mode since `CAMERA` mode is fundamentally broken.

### Protocol V5

https://github.com/bergerhealer/SmoothCoasters/blob/7064864933920c8e7b71e5bf2e104bb9cf62fa34/src/main/java/me/m56738/smoothcoasters/implementation/ImplV5.java

**No protocol changes.**

SmoothCoasters 1.19.4-v1 unintentionally delayed the processing of SmoothCoasters packets inside bundle packets until
the next tick, causing a race condition.
Protocol V5 only exists to let the server know that the fix is present and bundle packets may be used safely.
Servers should not send SmoothCoasters data inside bundle packets on versions before V5.

### Protocol V6

https://github.com/bergerhealer/SmoothCoasters/blob/66bd41b88ce30c2e6a548403dc2c6a190abacfe8/src/main/java/me/m56738/smoothcoasters/implementation/ImplV6.java

**Removes entity rotation.**

This feature was rarely used.
