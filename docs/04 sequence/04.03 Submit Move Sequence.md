# Submit move, validate, update board, switch turn #

Description: The player submits a move from the game page. The platform core routes it through the session manager, which calls the game engine for validation and state updates. A GameStateUpdate is published so the game page can update the board.

```uml
@startuml
title Sequence - Submit Move -> Validate -> Update Board

actor Player
participant "GUI (Game Page)" as GUI
participant "PlatformCore" as Core
participant "GameSessionManager" as GSM
participant "GameEngine (example)" as Engine
participant "EventBus" as Bus

Player -> GUI : select move\nclick Submit
GUI -> Core : submitMove(sessionId, playerId, move)
Core -> GSM : submitMove(sessionId, playerId, move)

GSM -> Engine : validateMove(state, move)
Engine --> GSM : valid/invalid

alt invalid
  GSM --> Core : moveRejected(reason)
  Core --> GUI : showMoveRejected(reason)
else valid
  GSM -> Engine : applyMove(state, move)
  Engine --> GSM : newState
  GSM -> Engine : evaluateEnd(newState)
  Engine --> GSM : ended?/notEnded

  GSM -> Bus : publish(GameStateUpdate(newState))
  Bus --> Core : deliver(GameStateUpdate)
  Core --> GUI : onGameStateUpdate(newState)

  GUI -> GUI : render updated board\nenable/disable input
end

@enduml
```
