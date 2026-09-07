# Turn notification while player is in the lobby/dashboard #

Description: A key requirement is that game state changes must flow back to the platform core so it can notify a player even when they are not on the game page. When the session manager publishes a state update indicating a specific active player, the core triggers a lobby/dashboard notification.

```uml
@startuml
title Sequence - Turn Notification in Lobby/Dashboard

actor Player as P1
actor "Other Player" as P2

participant "GUI (Lobby/Dashboard)" as LobbyGUI
participant "GUI (Other: Game Page)" as GameGUI
participant "PlatformCore" as Core
participant "GameSessionManager" as GSM
participant "EventBus" as Bus
participant "NotificationService" as Notify

' Other player makes a move
P2 -> GameGUI : submit move
GameGUI -> Core : submitMove(sessionId, P2, move)
Core -> GSM : submitMove(sessionId, P2, move)

GSM -> Bus : publish(GameStateUpdate(activePlayerId=P1))
Bus --> Core : deliver(GameStateUpdate)

Core -> Notify : createTurnAlert(P1, sessionId)
Notify --> LobbyGUI : showNotification("Your turn in session ...")

LobbyGUI -> LobbyGUI : display banner/badge
@enduml
```
