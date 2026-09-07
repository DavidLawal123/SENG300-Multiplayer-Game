# 03 - Games & Game Engines #

## About this Diagram ##

This diagram shows the game-specific logic layer. Each game implements a common GameEngine interface so it can run on the shared platform. Game engines are responsible for validating moves, applying game rules, and determining win or draw conditions. They return updated game state to the platform but never interact directly with the lobby, matchmaking system, or user interface.

## PlantUML Editor Code ##

```uml
@startuml
title 03 - Games & Game Engines

hide empty members
skinparam classAttributeIconSize 0
skinparam linetype ortho

' Scope:
' - GameEngine interface
' - Per-game implementation classes + game domain objects
' Excludes:
' - UI classes
' - Matchmaking/session services (except a note about who calls what)

interface GameEngine <<interface>> {
  + createInitialState(players): Object
  + validateMove(state, move): boolean
  + applyMove(state, move): Object
  + evaluateEnd(state): boolean
}

note "Rule:\nGameEngine implementations return state (or deltas) only.\nThey do NOT notify lobby/dashboard or call UI.\nCore services consume returned state and emit GameStateUpdate." as N_RULE

package "Checkers" {
  class CheckersGame <<implements GameEngine>> {
    - board: Board
    + createInitialState(players): Object
    + validateMove(state, move): boolean
    + applyMove(state, move): Object
    + evaluateEnd(state): boolean
  }

  class Board
  class CheckersPiece
  class HumanPlayer
  class Computer
  class Move
  class Position

  CheckersGame o-- Board
  Board o-- CheckersPiece
  HumanPlayer --> Move
  Computer --> Move
  Move --> Position
}

N_RULE .. GameEngine
N_RULE .. CheckersGame

@enduml
```
