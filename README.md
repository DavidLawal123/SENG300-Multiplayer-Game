# SENG300 Multiplayer Game Platform

A JavaFX-based multiplayer game platform developed as part of a software engineering course at the University of Calgary.

The platform provides a desktop environment where players can create accounts, manage profiles, enter matchmaking queues, join game lobbies, challenge other players, communicate with opponents, and play supported games.

## Features

- User registration and login
- Player profiles and profile pictures
- Player statistics and leaderboards
- Matchmaking queues
- Skill-based matchmaking
- Game lobbies
- Direct player challenges
- Opponent chat interface
- Game result tracking
- Tic-Tac-Toe
- Connect Four
- Modular move validation
- JUnit test coverage

## Technologies

- Java 25
- JavaFX 25
- Maven
- FXML
- JUnit
- Git

## Architecture

The application separates the user interface, game logic, validation system, matchmaking, statistics, and persistence layers.

```text
JavaFX User Interface
        |
        v
Controllers
        |
        v
Game Session
        |
        v
Game Engine
        |
        v
Move Validation Manager
   |        |        |
   v        v        v
Turn     Bounds   Game Rules
Validator Validator Validator
        |
        v
Game State
        |
        v
Outcome Evaluation
```

## Supported Games

### Tic-Tac-Toe

The platform supports two-player Tic-Tac-Toe with turn validation, board boundary validation, occupied-position validation, and game-specific rule validation.

### Connect Four

The platform also supports two-player Connect Four with validation for legal moves and game-specific board rules.

## Matchmaking

Players can enter matchmaking queues for supported games. The matchmaking system uses player skill ratings to identify compatible opponents.

The skill matcher prioritizes players with the smallest skill difference while expanding the acceptable skill range as players wait.

## Multiplayer Implementation

The application provides multiplayer game functionality within the desktop application, including matchmaking, lobbies, challenges, and opponent interactions.

The current implementation runs locally and in-process. Network synchronization is represented by synchronization stubs rather than a deployed Internet-based server. This repository therefore demonstrates the application's multiplayer architecture and client-side functionality without claiming that it currently provides live Internet multiplayer.

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── ca/ucalgary/seng300/
│   │       ├── gamelogic/
│   │       ├── statistics/
│   │       └── controllers/
│   └── resources/
│       └── ca/ucalgary/seng300/
│
└── test/
    └── java/

docs/
    Architecture and design documentation

pom.xml
    Maven project configuration

mvnw
mvnw.cmd
    Maven Wrapper scripts
```

## Requirements

- JDK 25
- Git
- Windows, macOS, or Linux with JavaFX-compatible graphics support

Maven does not need to be installed separately because the project includes the Maven Wrapper.

## Setup

Clone the repository:

```bash
git clone https://github.com/DavidLawal123/SENG300-Multiplayer-Game.git
cd SENG300-Multiplayer-Game
```

The application uses a local CSV file for user persistence.

Create:

```text
src/database/data.csv
```

using `src/database/data.template.csv` as a starting point.

The local `data.csv` file is intentionally excluded from version control.

## Running the Application

On Windows:

```powershell
.\mvnw.cmd javafx:run
```

On macOS or Linux:

```bash
./mvnw javafx:run
```

## Running Tests

Run the test suite with:

```powershell
.\mvnw.cmd clean test
```

or on macOS/Linux:

```bash
./mvnw clean test
```

## My Contribution — Move Validation System

My primary contribution to this project was the design and implementation of the move-validation subsystem.

I developed a modular validation pipeline that verifies player moves before they are applied to the game state. The system separates validation responsibilities into reusable components:

- `MoveValidationManager` — coordinates the validation pipeline
- `MoveValidator` — defines the validation interface
- `TurnValidator` — verifies that the correct player is making the move
- `BoundsValidator` — ensures moves are within the board boundaries
- `TTTRuleValidator` — validates Tic-Tac-Toe-specific rules
- `FourCRuleValidator` — validates Connect Four-specific rules
- `RuleValidator` — provides the structure for game-specific validation

The validation pipeline checks:

1. Whether it is the correct player's turn
2. Whether the requested position is within the board boundaries
3. Whether the requested position is available
4. Whether the move follows the rules of the current game

The system was designed to separate general validation from game-specific rules, making the architecture easier to maintain and extend when adding additional games.

I also contributed to integrating the validation components into the broader game logic and resolving package and merge issues during development.

Other features in the repository were developed as part of the overall course project.

## Testing

The project includes automated JUnit tests covering components of the game logic and validation system.

Run the full test suite with:

```bash
./mvnw clean test
```

## Documentation

Additional project documentation is available in the `docs/` directory, including architecture, structure, use case, and sequence documentation.

## Course Project

This project was developed as part of a software engineering course at the University of Calgary.

The repository has been prepared as a personal portfolio project to demonstrate Java, JavaFX, object-oriented design, software architecture, testing, and Git-based development.