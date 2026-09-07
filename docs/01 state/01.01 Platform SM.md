# Platform State Machine #

This setup meets the following conditions:

- Game code never directly pings the lobby UI
- Game code emits a GameStateUpdate (or domain event)
- The Core Platform consumes it and decides:
    i. If player is in lobby, notify the player
    ii. If player is in game view, update the board and enable input

For use in [editor.plantuml.com](editor.plantuml.com), png attached:

```plantuml
@startuml
title OMG Platform - Player Session State Machine (platform owns notifications)

[*] --> LoggedOut

LoggedOut --> Authenticating : submitCredentials
Authenticating --> LoggedIn : authSuccess
Authenticating --> LoggedOut : authFail

LoggedIn --> Browsing : enterHome
Browsing --> ViewingProfile : viewProfile
ViewingProfile --> Browsing : back

Browsing --> InQueue : joinMatchmakingQueue
InQueue --> Browsing : leaveQueue (before lock)
InQueue --> MatchFound : matchAssigned

MatchFound --> EnteringRoom : accept/auto-join
EnteringRoom --> InGame : roomReady

state InGame {
  [*] --> WaitingForState
  WaitingForState --> WaitingForTurn : GameStateUpdate(opponentTurn)
  WaitingForState --> YourTurn : GameStateUpdate(yourTurn)

  WaitingForTurn --> YourTurn : GameStateUpdate(yourTurn)
  YourTurn --> WaitingForTurn : submitMove + accepted + GameStateUpdate(opponentTurn)
  YourTurn --> YourTurn : submitMove + rejected (validation error)

  WaitingForTurn --> GameEnded : GameStateUpdate(gameOver)
  YourTurn --> GameEnded : GameStateUpdate(gameOver)
}

InGame --> Browsing : exitToHome (after GameEnded)
InGame --> LoggedOut : logout

' Key platform idea:
note right of InGame
GameSession emits GameStateUpdate events.
Core routes notifications to:
- Lobby/Home/Dashboard
- In-game view
even if the user is not in the game screen.
end note

@enduml
```