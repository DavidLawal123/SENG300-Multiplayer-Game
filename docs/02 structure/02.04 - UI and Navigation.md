# 04 - UI & Navigation #

## About this Diagram ##

This diagram represents the user interface and navigation flow. It shows screens, pages, and UI components and how users move between them. The UI layer is reactive: it displays information and sends user actions to the platform core, but it does not own game logic or session state. All updates shown to the user originate from platform notifications or platform queries, not from direct calls into game engines.

## PlantUML Editor Code ##

```uml
@startuml
title 04 - UI & Navigation Layer

hide empty members
skinparam classAttributeIconSize 0
skinparam linetype ortho

' Scope:
' - Screens/pages and their navigation
' - UI components
' Excludes:
' - Game-specific domain classes
' - Matchmaking/session logic internals

class PlatformCore <<service>> {
  + notifyPlayer(playerId: String, msg: String): void
  + submitMove(sessionId: String, move): void
  + joinQueue(gameType: String): void
}

note "UI Rule:\nUI subscribes to PlatformCore notifications.\nUI calls PlatformCore APIs.\nUI does NOT call GameEngine directly." as N_RULE
N_RULE .. PlatformCore

class LoginPage {
  + login(username: String, password: String): HomePage
  + register(username: String, password: String, email: String): State
}

class HomePage {
  + viewYourOwnRecords(): void
  + viewOtherPlayerRecords(): void
  + viewSettings(): Settings
  + chooseGame(): void
}

class Settings {
  + changeUsername(newUsername: String): boolean
  + changeEmail(newEmail: String): boolean
  + changePassword(oldPassword: String, \n newPassword: String): boolean
  + logout(): boolean
}

enum State {
  UsernameTaken
  EmailFormatWrong
  VerificationCodeWrong
}

class AuthenticationScreen {
  + displayLoginScreen(): void
  + submitLogin(): void
  + displayRegistrationScreen(): void
}

class WelcomePage {
  + displayMessage(msg: String): String
}

class MainGameDashboard {
  + displayGames(): void
  + chooseGame(): void
  + viewProfile(): void
  + showLeaderboard(): void
}

class ProfileDashboard {
  + displayProfileView(): void
  + displayGameStats(type): void
}

class PlayGame {
  + displayInstructions(): void
  + displayGameBoard(): void
  + updateBoard(): void
  + displayWinner(): void
}

class GameGUI {
  + setupUIWindow(): void
  + renderBoard(): void
  + updateUI(): void
}

class ChatboxGUI {
  + displayMessages(): void
  + displayChatbox(): void
}

' Navigation and composition
LoginPage --> HomePage
HomePage --> Settings
LoginPage --> State

AuthenticationScreen --> WelcomePage
MainGameDashboard --> ProfileDashboard
MainGameDashboard --> PlayGame
PlayGame --> GameGUI
PlayGame --> ChatboxGUI

' Interaction boundary
MainGameDashboard ..> PlatformCore : joinQueue()/start/join
PlayGame ..> PlatformCore : submitMove()
PlatformCore ..> MainGameDashboard : notify/refresh
PlatformCore ..> PlayGame : state updates/turn alerts

@enduml
```