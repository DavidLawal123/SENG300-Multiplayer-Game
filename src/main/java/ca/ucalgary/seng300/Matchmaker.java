package ca.ucalgary.seng300;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Matchmaker is the single entry point for creating and joining lobbies.
 * Other parts of the system should NOT instantiate lobbies directly.
 */
public final class Matchmaker implements Sync {
    private final List<Lobby> lobbies;
    private final HashMap<GameType, MatchmakingQueue> queues;
    private final SkillMatcher skillMatcher;
    private final EventBus eventBus;

    /**
     * Initializes the Matchmaker and registers event-driven matchmaking handlers.
     *
     * - Sets up internal storage for lobbies and queues
     * - Initializes SkillMatcher and EventBus
     * - Registers handlers to retry matchmaking:
     *     • when a user enters the queue
     *     • periodically via MATCHMAKING_TICK
     * - Starts a lightweight background timer to trigger periodic matchmaking
     *
     * This ensures matchmaking is re-evaluated without blocking the system.
     */
    public Matchmaker() {
        this.lobbies = new ArrayList<>();
        this.queues = new HashMap<>();
        this.skillMatcher = new SkillMatcher();
        this.eventBus = new EventBus();

        // Retry matchmaking when users join
        eventBus.registerHandler(this::tryMatchAllQueues, Event.USER_DID_QUEUE);

        // Retry matchmaking periodically
        eventBus.registerHandler(this::tryMatchAllQueues, Event.MATCHMAKING_TICK);

        // Register handlers for the networking stub.
        eventBus.registerHandler(this::attemptSynchronizationWithServer, Event.USER_DID_QUEUE);
        eventBus.registerHandler(this::attemptSynchronizationWithServer, Event.USER_DID_EXIT_LOBBY);
        eventBus.registerHandler(this::attemptSynchronizationWithServer, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(this::attemptSynchronizationWithServer, Event.USER_DID_CREATE_LOBBY);
        eventBus.registerHandler(this::attemptSynchronizationWithServer, Event.USER_DID_CANCEL_QUEUE);
        eventBus.registerHandler(this::attemptSynchronizationWithServer, Event.LOBBY_DESTROYED);
        eventBus.registerHandler(this::attemptSynchronizationWithServer, Event.LOBBY_CREATED_FROM_QUEUE);

        startMatchmakingTimer();
    }

    /**
     * Starts a lightweight background timer that periodically triggers matchmaking.
     *
     * The timer publishes a MATCHMAKING_TICK event at a fixed interval,
     * allowing SkillMatcher to re-evaluate queued players as tolerance
     * expands over time.
     *
     * Runs on a daemon thread and does not block the main application.
     */
    private void startMatchmakingTimer() {
        Timer timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                eventBus.publishEvent(Event.MATCHMAKING_TICK);
            }
        }, 0, 3000);
    }

    /**
     * Attempts matchmaking across all active queues.
     *
     * Iterates through each game type and invokes tryMatch(...) to
     * find compatible player pairs. Used by event handlers to
     * re-evaluate matchmaking when triggered.
     */
    private void tryMatchAllQueues() {
        for (GameType gameType : queues.keySet()) {
            tryMatch(gameType);
        }
    }

    /**
     * Adds a user to the matchmaking queue and triggers matchmaking.
     *
     * Validates that the user is eligible to queue, creates a matchmaking
     * ticket, inserts it into the appropriate queue, and publishes a
     * USER_DID_QUEUE event to trigger matchmaking handlers.
     *
     * @param user the user entering the matchmaking queue
     * @param gameType the game type queue to join
     * @return the matchmaking ticket created for the queued user
     * @throws IllegalArgumentException if the user is not eligible to queue
     */
    public MatchmakingTicket queueForMatch(User user, GameType gameType) {
        // Check if the user status is online in order to queue matchmaker (May need to remove this if user can change willingly)
        if (user.getStatus() != UserStatus.ONLINE) {
            throw new IllegalArgumentException("User must be ONLINE to join matchmaking queue.");
        }

        // Check if there already exists a queue, creating one if necessary.
        MatchmakingQueue queue =
            queues.getOrDefault(gameType, new MatchmakingQueue(gameType));
        queues.put(gameType, queue);

        // Create a ticket for the user.
        MatchmakingTicket ticket = new MatchmakingTicket(user, gameType);
        // Enqueue them, passing their ticket as a kind of token.
        queue.enqueue(ticket);
        
        user.setStatus(UserStatus.IN_QUEUE);
        // Pubslish event: user entering queue
        eventBus.publishEvent(Event.USER_DID_QUEUE);

        return ticket;
    }

    /**
     * Remove the given ticket from its queue.
     *
     * @param ticketID, id of ticket to be cancelled from queue
     * @returns A boolean, indicating whether the
     * ticket was successfully found and removed.
     */
    public boolean cancelQueue(UUID ticketID) {
        for (MatchmakingQueue queue: queues.values()) {
            // Check if this queue contains a ticket by the ID given.
            if (queue.getWaitingTickets()
                    .stream()
                    .anyMatch(t -> t.getUUID().equals(ticketID))) {
                // Found! Remove the ticket as instructed.
                queue.remove(ticketID);
                // Publish eventBus User cancel queue
                eventBus.publishEvent(Event.USER_DID_CANCEL_QUEUE);
                return true;
            }
        }

        // In this event, no ticket by the ID given was found in a queue.
        // We indicate as such with a false return.
        return false;
    }

    /**
     * Remove the given user from the lobby.
     *
     * @param player The player to remove from the lobby given.
     * @param lobby The lobby to remove the player from; owner MUST NOT be the player given.
     */
    public void leaveLobby(User player, Lobby lobby) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(lobby);

        // Check that we this lobby is known to the class.
        if (!this.lobbies.contains(lobby)) {
            // We require that this is a real, owned lobby.
            throw new IllegalArgumentException("The given lobby is unknown!");
        }

        // Actually remove the player.
        lobby.removePlayer(player);

        // Notify subscribers.
        this.eventBus.publishEvent(Event.USER_DID_EXIT_LOBBY);
    }

    /**
     * Creates a new lobby for the given owner and game type.
     * The owner is placed in the lobby and marked as IN_LOBBY.
     * 
     * @param owner, user to be assigned as owner of the lobby
     * @param gameType, type of game the lobby will be
     */
    private Lobby createLobby(User owner, GameType gameType) {
        // Checks for if owner and gametype are empty
        Objects.requireNonNull(owner, "owner must not be null");
        Objects.requireNonNull(gameType, "gameType must not be null");

        Lobby lobby;

        // Creates new lobby based on gameType
        switch (gameType) {
            case TIC_TAC_TOE -> lobby = new TicTacToeLobby(owner);
            case CONNECT_FOUR -> lobby = new ConnectFourLobby(owner);
            default -> throw new IllegalArgumentException("Unsupported game type: " + gameType);
        }

        lobbies.add(lobby);
        owner.setStatus(UserStatus.IN_LOBBY); // owner is now in a lobby
        eventBus.publishEvent(Event.USER_DID_CREATE_LOBBY); // notify listeners that a lobby was created

        return lobby;
    }

    /**
     * Returns all open (not full, not locked) lobbies for the given game type.
     * A later SkillMatcher can choose the best lobby from this list.
     * 
     * @param gameType, filter for specific game type
     */
    public List<Lobby> findOpenLobbies(GameType gameType) {
        Objects.requireNonNull(gameType, "gameType must not be null");

        return lobbies.stream()
                // filter by game type via subclass
                .filter(l -> (gameType == GameType.TIC_TAC_TOE && l instanceof TicTacToeLobby)
                        || (gameType == GameType.CONNECT_FOUR && l instanceof ConnectFourLobby))
                // only lobbies that can still accept players
                .filter(l -> !l.isFull() && !l.getLocked())
                .toList();   // return all available lobbies which can be further filtered based on skillLevel
    }

    /**
     * Temporary matchmaking: picks the first open lobby for this game type.
     * If none exists, creates a new lobby with the user as owner.
     * Later, SkillMatcher can replace the selection logic.
     */
    public Lobby joinOrCreateLobby(User user, GameType gameType) {
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(gameType, "gameType must not be null");

        List<Lobby> openLobbies = findOpenLobbies(gameType);
        Lobby lobby;

        if (!openLobbies.isEmpty()) {
            lobby = openLobbies.getFirst();   // simple strategy for now

            lobby.addPlayer(user);
            user.setStatus(UserStatus.IN_LOBBY);

            eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);
        } else {
            // no open lobby: create one and put user as owner
            lobby = createLobby(user, gameType);
        }

        if (lobby.isFull()) {
            lobby.setLocked(true);
            eventBus.publishEvent(Event.LOBBY_IS_FULL);   // add to Event enum
        }

        return lobby;
    }

    /**
     * Removes a lobby from tracking (e.g., when game ends or lobby is cancelled).
     * Also resets the user status indicators back to ONLINE (or whatever you prefer).
     *
     * @param lobby, lobby to be destroyed
     * @returns A boolean, indicating whether the lobby was removed successfully.
     * Returns false if the lobby was not known to matchmaker (i.e., we did not create it).
     */
    private boolean destroyLobby(Lobby lobby) {
        Objects.requireNonNull(lobby, "lobby must not be null");

        // Remove the lobby and detect errors.
        if (!lobbies.remove(lobby)) {
            return false;
        }

        for (User user: lobby.getUsers()) {
            if (user.getStatus() == UserStatus.IN_LOBBY) {
                user.setStatus(UserStatus.ONLINE);
            } else {
                // Why does this user's status not match the
                // fact they are in a lobby? We will propogate this
                // error now instead of letting it silently cause issues
                // with a log to stderr.
                System.err.println("User in lobby did not have an IN_LOBBY status! This was detected during removal. User ID: " + user.getUUID());
            }
        }

        // Publish lobby destruction for listeners.
        eventBus.publishEvent(Event.LOBBY_DESTROYED);
        return true;
    }

    /**
     * Returns an immutable snapshot of all tracked lobbies.
     */
    public List<Lobby> getLobbies() {
        return Collections.unmodifiableList(lobbies);
    }

    /**
     * Get the matchmaker's event bus.
     * Subscribe to it with any closure or function to receive event updates.
     */
    public EventBus getEventBus() {
        return this.eventBus;
    }

    /**
     * Attempts to match two compatible players from the queue using SkillMatcher.
     * On success, removes their tickets, creates a lobby, and assigns both players.
     * Does nothing if no valid match exists.
     *
     * Only supports 2-player matchmaking and should be triggered externally.
     * 
     * @param gameType, type of game
     */
    private void tryMatch(GameType gameType) {
        Objects.requireNonNull(gameType, "gameType must not be null");

        // Get queue for gameType
        MatchmakingQueue queue = queues.get(gameType);

        if (queue == null) {
            // No match can be made if there is no queue.
            return;
        }

        // Get requiredPlayers
        int requiredPlayers = 2;

        // Query skillMatcher to find two tickets, return if none are found (replace with error message?)
        Optional<List<MatchmakingTicket>> maybeMatchedTickets =
        skillMatcher.selectMatchGroup(queue, requiredPlayers);

        // return (fail) if list is empty or less than required amount
        if (maybeMatchedTickets.isEmpty() || maybeMatchedTickets.get().size() < requiredPlayers) {
            return;
        }

        // Unwrap optional.
        List<MatchmakingTicket> matchedTickets = maybeMatchedTickets.get();

        // Get player tickets
        MatchmakingTicket ownerTicket = matchedTickets.get(0);
        MatchmakingTicket secondTicket = matchedTickets.get(1);

        // Mark the tickets as matched
        ownerTicket.markMatched();
        secondTicket.markMatched();

        // Remove tickets from queue
        queue.remove(ownerTicket.getUUID());
        queue.remove(secondTicket.getUUID());

        // Get players from tickets
        User owner = ownerTicket.getPlayer();
        User secondPlayer = secondTicket.getPlayer();

        // Create lobby
        Lobby lobby = createLobby(owner, gameType);
        lobby.addPlayer(secondPlayer);
        eventBus.publishEvent(Event.LOBBY_CREATED_FROM_QUEUE);

        // Locks lobby
        if (lobby.isFull()) {
            lobby.setLocked(true);
            eventBus.publishEvent(Event.LOBBY_IS_FULL);
        }
    }

    /**
     * Fake populate the matchmaker by queueing up all users
     * in the database. Of course, not all these users are
     * playable.
     *
     * <b>This should be used for testing or demo purposes only,
     * and most certainly not in production.</b>
     */
    // Randomly picks 2 users that are not the current users and tries to add them into the 2 game queues if they
    // are not already waiting there
    public void fakePopulateLobby(User currentUser) {
        // Get all users from the database
        List<User> users = Database.getUsers();

        // If there are no users, stop here
        if (users.isEmpty()) {
            return;
        }

        Random random = new Random();
        User user1;
        User user2;
        User user3;
        User user4;

        // Pick the first random user, but not the current user
        do {
            user1 = users.get(random.nextInt(users.size()));
        } while (user1.equals(currentUser));

        // Pick the second random user, but not the current user and not the same as the first user
        do {
            user2 = users.get(random.nextInt(users.size()));
        } while (user2.equals(currentUser) || user2.equals(user1));

        // Pick the third random user, unique from current/user1/user2
        do {
            user3 = users.get(random.nextInt(users.size()));
        } while (
                user3.equals(currentUser) ||
                        user3.equals(user1) ||
                        user3.equals(user2)
        );

        // Pick the fourth random user, unique from current/user1/user2/user3
        do {
            user4 = users.get(random.nextInt(users.size()));
        } while (
                user4.equals(currentUser) ||
                        user4.equals(user1) ||
                        user4.equals(user2) ||
                        user4.equals(user3)
        );


        try {
            // Check who is already in each queue.
            HashMap<GameType, List<User>> currentQueues = this.getCurrentQueues();

            // Add user1 to Connect Four if the queue is empty
            List<User> connectFourQueue = currentQueues.getOrDefault(GameType.CONNECT_FOUR, new ArrayList<>());
            if (connectFourQueue.isEmpty()) {
                this.queueForMatch(user1, GameType.CONNECT_FOUR);
            }

            // Add user2 to Tic Tac Toe if the queue is empty
            List<User> ticTacToeQueue = currentQueues.getOrDefault(GameType.TIC_TAC_TOE, new ArrayList<>());
            if (ticTacToeQueue.isEmpty()) {
                this.queueForMatch(user2, GameType.TIC_TAC_TOE);
            }

            // Add an open Connect Four lobby only if none already exist
            boolean hasOpenConnectFourLobby = false;
            for (Lobby lobby : this.getLobbies()) {
                if (lobby instanceof ConnectFourLobby && !lobby.getLocked() && !lobby.isFull()) {
                    hasOpenConnectFourLobby = true;
                    break;
                }
            }

            if (!hasOpenConnectFourLobby) {
                this.joinOrCreateLobby(user3, GameType.CONNECT_FOUR);
            }

            // Add an open Tic Tac Toe lobby only if none already exist
            boolean hasOpenTicTacToeLobby = false;
            for (Lobby lobby : this.getLobbies()) {
                if (lobby instanceof TicTacToeLobby && !lobby.getLocked() && !lobby.isFull()) {
                    hasOpenTicTacToeLobby = true;
                    break;
                }
            }

            if (!hasOpenTicTacToeLobby) {
                this.joinOrCreateLobby(user4, GameType.TIC_TAC_TOE);
            }
        } catch (Exception err) {
        }
    }

    /**
     * Get the current list of matchmaking queues current in-progress
     * by the matchmaker.
     *
     * @returns A new hash map giving the current list of
     * users in each of the matchmaking queues.
     */
    public HashMap<GameType, List<User>> getCurrentQueues() {
        // The new, fresh and resultant hash map we'll
        // return back.
        HashMap<GameType, List<User>> map = new HashMap<>();

        // Convert all the queues to a list of users.
        for (Entry<GameType, MatchmakingQueue> entry: this.queues.entrySet()) {
            GameType gameType = (GameType) entry.getKey();

            // Convert the queue to a list of 
            // its current users.
            MatchmakingQueue queue = (MatchmakingQueue) entry.getValue();
            List<User> users = queue.getWaitingTickets().stream().map(t -> t.getPlayer()).toList();

            map.put(gameType, users);
        }

        return map;
    }

    // New method to createLobby with the direct challenge buttons
    public Lobby createDirectChallengeLobby(User owner, User opponent, GameType gameType) {
        // Create a new lobby with the owner as the first player
        Lobby lobby = createLobby(owner, gameType);

        // Add the challenged opponent to the lobby
        lobby.addPlayer(opponent);

        // If the lobby is now full, lock it so no more players can join
        if (lobby.isFull()) {
            lobby.setLocked(true);
        }

        // Return the completed direct-challenge lobby
        return lobby;
    }

    //
    // Networking Stubs
    //

    /**
     * Synchronize the local matchmaking data with a remote centralized server
     * (or perhaps a proxy for a more sophisticated implementation).
     * This method will upload local user data to the server database, which
     * will accept changes on a first-come, first-serve basis. If a local change
     * with the server, it is rejected.
     *
     * @returns A boolean indicating whether the local changes relative
     * to the server matchmaking state were accepted or rejected.
     * @throws An IOException if there was an error connecting to the
     * server.
     */
    public boolean synchronizeWithServer() throws IOException {
        // Internal functionality stub...
        return true; // Assume success for stub.
    }

    /**
     * A version of the synchronization method which discards all
     * errors related to IO (the server connection). See the docs
     * for `synchronizeWithServer` for more detail on what
     * this method does.
     */
    public boolean attemptSynchronizationWithServer() {
        try {
            return this.synchronizeWithServer();
        } catch (IOException err) {
            // Suppress the error for our temporary stub.
            // This could be fleshed out in the future.
        }

        return false;
    }
}
