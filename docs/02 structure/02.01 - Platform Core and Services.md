# 01 - Platform Core & Services #

## About this diagram ##

This diagram shows the platform architecture. It defines which components own game sessions, matchmaking, rankings, and event routing. All games and UI interactions must flow through these services. Game logic does not directly notify players or update the UI; instead, it emits state updates that are processed by the platform core, which then decides how and when players are notified (for example, alerting a player in the lobby that it is their turn).

## PlantUML Editor Code ##

```uml
@startuml
title 01 - Platform Core & Services

hide empty members
skinparam classAttributeIconSize 0
skinparam linetype ortho

' Scope:
' - Core services, session lifecycle, matchmaking, persistence stubs
' - Event-driven state return to core (GameStateUpdate)
' Excludes:
' - UI pages/screens
' - Game-specific board/piece/move classes

note "Architecture rule:\nGame code emits GameStateUpdate.\nPlatformCore decides notifications\n(including lobby turn alerts).\nUI subscribes; UI does not call game internals directly." as N_RULE
N_RULE .. PlatformCore

enum GameStatus {
  LOBBY
  MATCHMAKING
  STARTING
  IN_PROGRESS
  GAME_OVER
}

class PlatformCore <<service>> {
  + dispatch(event): void
  + notifyPlayer(playerId: String, msg: String): void
}

class EventBus <<service>> {
  + publish(event): void
  + subscribe(handler): void
}

class GameStateUpdate <<event>> {
  + sessionId: String
  + recipients: List<String>
  + status: String
  + activePlayerId: String
  + payload: Object
}

interface GameEngine <<interface>> {
  + createInitialState(players): Object
  + validateMove(state, move): boolean
  + applyMove(state, move): Object
  + evaluateEnd(state): boolean
}

class GameSessionManager <<service>> {
  + startSession(roomId: String, gameType: String): String
  + submitMove(sessionId: String, playerId: String, move): GameStateUpdate
  + endSession(sessionId: String): void
}

class MatchMaking <<service>> {
  + addPlayerToQueue(playerId: String, gameType: String): void
  + removePlayer(playerId: String): void
  + matchPlayers(gameType: String): List<String>
}

class PartySystem <<service>> {
  + createParty(): String
  + joinParty(partyId: String, playerId: String): void
}

class Leaderboard <<service>> {
  + logGameResult(sessionId: String): void
  + updatePlayer(playerId: String): void
  + getTopPlayersByRank(gameType: String): List<String>
}

abstract class Networking <<stub>> {
  + handleConnections(): void
  + syncPlayers(): void
  + sendPlayerActions(): void
  + disconnectPlayer(): void
  + reconnectToGame(): void
}

class Database <<stub>> {
  + storeData(): void
  + retrieveData(): void
}

class GameState {
  + sessionId
  + gameType
  + status : GameStatus
  + activePlayerId
  + payload board
}

' Core wiring
PlatformCore o-- EventBus
PlatformCore o-- MatchMaking
PlatformCore o-- GameSessionManager
PlatformCore o-- Leaderboard

GameSessionManager --> GameEngine : uses (per game)
GameSessionManager ..> EventBus : publish(GameStateUpdate)
EventBus --> GameStateUpdate : delivers >
GameStateUpdate ..> PlatformCore : consumed by >

MatchMaking ..> PartySystem
GameSessionManager --> Networking
Leaderboard --> Database

note right of GameSessionManager
Owns the move pipeline.
Emits GameStateUpdate on:
- move accepted/rejected
- turn changes
- game ended
end note

note right of MatchMaking
Finds matches and assigns rooms/sessions.
Avoid direct UI coupling.
end note

@enduml
```
