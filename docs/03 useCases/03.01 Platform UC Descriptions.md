# Use Case Descriptions #

## About ##

A use case diagram shows what a system does from the user’s perspective, not how it is implemented.

### Purpose ##

Identify who interacts with the system (actors).
Define what goals those actors can accomplish (use cases).
Clarify the system boundary—what is inside the system and what is external.
Provide a shared, high-level understanding for stakeholders (developers, instructors, clients).
Serve as a starting point for requirements, user stories, and test scenarios.

### What it avoids ###

Internal logic, algorithms, data structures.
Execution order or control flow.
UI design details.

### Summary ###

A use case diagram answers “Who can do what with this system?” and is used to scope and validate requirements before design or implementation begins.

## Platform Use Case Descriptions ##

### UC-01: View game list ###

Primary actor: Player
Goal: See available games (browse-only).
Preconditions: None (no authentication required)
Trigger: Player visits the welcome page.

Main success scenario:
System displays a list of games.
Player optionally selects a game to view basic info.

Postconditions: None (no session created)

### UC-02: Create account ###

Primary actor: Player
Goal: Register a new account.
Preconditions: Player does not already have an account (or chooses to create a new one).

Trigger: Player selects “Create account”.

Main success scenario:
System displays registration form.
Player enters required details.
System validates input and creates the account.
System confirms account creation (optionally signs them in).

Postconditions: Account exists; player is authenticated if auto-login is used.

### UC-03: Log in ###

Primary actor: Player
Goal: Authenticate and access dashboard features.
Preconditions: Player has an account.

Trigger: Player selects “Log in”.

Main success scenario:
System displays login form.
Player enters credentials.
System validates credentials.
System establishes an authenticated session.
System displays the dashboard.

Postconditions: Player is authenticated.

### UC-04: View dashboard ###

Primary actor: Player
Goal: Access authenticated options (lobby, queue, chat, join game when ready).
Preconditions: Player is authenticated.

Trigger: Player logs in or navigates to dashboard.

Main success scenario:
System displays dashboard options and current status (in lobby, in queue, game ready, etc.).

Postconditions: None.

### UC-05: Join game lobby ###

Primary actor: Player
Goal: Enter a lobby for a specific game (typically to coordinate and chat).
Preconditions: Player is authenticated; target game exists.

Trigger: Player selects a game and chooses “Join lobby”.

Main success scenario:
System displays available lobbies (or creates/assigns one).
Player joins the lobby.
System shows lobby participants and lobby chat.

Postconditions: Player is in the selected lobby.

### UC-06: Enqueue for next available game ###

Primary actor: Player
Goal: Wait to be matched/assigned into the next available game session.
Preconditions: Player is authenticated; game supports queueing.

Trigger: Player selects “Enqueue” from the dashboard (optionally choosing a game).

Main success scenario:
System places player into the queue.
System shows queue status (position, estimated wait, etc. if applicable).
When a session is available, the system notifies the player that the game is ready.

Postconditions: Player is either still queued or has a “game ready” status.

### UC-07: Chat ###

Primary actor: Player
Goal: Send/receive chat messages (in a lobby or outside it, if your design allows).
Preconditions: Player is authenticated.

Trigger: Player opens chat UI (from dashboard or lobby).

Main success scenario:
Player enters a message.
System broadcasts the message to the appropriate channel/context.
Player receives messages from others.

Postconditions: Chat history updated (per your design).

### UC-08: Join game ###

Primary actor: Player
Goal: Enter the active game session once it starts.
Preconditions: Player is authenticated; a game session is available for the player (invited/matched/ready).

Trigger: Player selects “Join game” after being notified or seeing “game ready”.

Main success scenario:
System verifies the player is assigned to the session.
System connects the player to the game session.
System displays the game.

Postconditions: Player is in an active game session.