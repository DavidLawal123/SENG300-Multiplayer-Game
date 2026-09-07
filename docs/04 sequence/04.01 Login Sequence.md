# Login and reach dashboard #

Description: The player submits credentials in the GUI. The platform core validates them using the stubbed credentials store. On success, the GUI transitions to the game dashboard; on failure, the GUI displays an error.

```uml
@startuml
title Sequence - Login -> Game Dashboard

actor Player
participant "GUI (Welcome/Login)" as GUI
participant "PlatformCore" as Core
database "CredentialsStore (stub)" as Store

Player -> GUI : enter username/password\nclick Login
GUI -> Core : login(username, password)
Core -> Store : verifyCredentials(username, password)
Store --> Core : valid/invalid

alt valid
  Core --> GUI : loginSuccess(sessionToken)
  GUI -> GUI : show Game Dashboard
else invalid
  Core --> GUI : loginFailed(message)
  GUI -> GUI : show error message
end

@enduml
```
