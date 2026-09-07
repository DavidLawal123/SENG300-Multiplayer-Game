package ca.ucalgary.seng300.gamelogic;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.gamelogic.context.ClientMoveRequest;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.context.MoveContextFactory;
import ca.ucalgary.seng300.gamelogic.gameengine.GameEngine;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateView;
import ca.ucalgary.seng300.statistics.MatchSummary;
import ca.ucalgary.seng300.statistics.MatchSummaryFactory;
import ca.ucalgary.seng300.gamelogic.moveresponse.MoveResponse;
import ca.ucalgary.seng300.gamelogic.moveresponse.MoveResponseFactory;
import ca.ucalgary.seng300.statistics.StatisticsUpdater;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class GameSession {
    private final UUID uuid;
    private final GameType gameType;
    private final Player p1;
    private final Player p2;

    private final GameEngine engine;
    private final GameState gameState;

    private final MoveContextFactory ctxFactory = new MoveContextFactory();
    private final MoveResponseFactory responseFactory = new MoveResponseFactory();
    private final MatchSummaryFactory matchSummaryFactory = new MatchSummaryFactory();

    protected GameSession(GameType gameType, List<Player> players, GameEngine engine, GameState gameState) {
        Objects.requireNonNull(gameType);
        Objects.requireNonNull(players);
        Objects.requireNonNull(engine);
        Objects.requireNonNull(gameState);

        if (players.size() != 2) {
            throw new IllegalArgumentException("GameSession requires exactly 2 players.");
        }

        this.uuid = UUID.randomUUID();
        this.gameType = gameType;
        this.p1 = players.get(0);
        this.p2 = players.get(1);
        this.engine = engine;
        this.gameState = gameState;
    }

    public UUID getUUID() {
        return uuid;
    }

    public GameType getGameType() {
        return gameType;
    }

    public GameStateView getGameStateView() {
        return gameState;
    }

    public Player getP1() {
        return p1;
    }

    public Player getP2() {
        return p2;
    }

    /**
     * Main entry point for UI/integration to submit a move.
     */
    public MoveResponse submitMoveRequest(ClientMoveRequest req) {
        Objects.requireNonNull(req);

        // Basic identity guard: only session players can submit.
        if (!req.getPlayer().getUUID().equals(p1.getUUID()) && !req.getPlayer().getUUID().equals(p2.getUUID())) {
            // gameState should not be returned; exposes setters methods which makes it mutable by the client
            return responseFactory.buildRejectedResponse(ValidationResult.ILLEGAL_MOVE, gameState);
        }
        Player requester = req.getPlayer();
        Player opposition = requester.getUUID() == p1.getUUID() ? p2 : p1;

        MoveContext ctx = ctxFactory.buildCTX(req.getRow(), req.getCol(), req.getPlayer(), gameState);

        ValidationResult vr = engine.validateMove(ctx);
        if (vr != ValidationResult.VALID) {
            return responseFactory.buildRejectedResponse(vr, gameState);
        }

        engine.applyMove(ctx);

        GameOutcome outcome = engine.evaluateGameOutcome(ctx);

        engine.applyGameOutcomeTransition(ctx, outcome);
        engine.applyPlayerTurnStateTransition(ctx, opposition);

        Optional<MatchSummary> matchSummary = buildMatchSummaryIfFinished();

        if (matchSummary.isPresent()){
            StatisticsUpdater.recordMatch(matchSummary.get());
        }

        return responseFactory.buildAcceptedResponse(gameState);
    }

    private Optional<MatchSummary> buildMatchSummaryIfFinished() {
        if (gameState.getGameOutcome() == GameOutcome.ONGOING) {
            return Optional.empty();
        }
        if (gameState.getGameOutcome() == GameOutcome.DRAW) {
            // Draw case
            return Optional.of(matchSummaryFactory.buildDrawMatchSummary(List.of(p1,p2), gameType));
        }
        return Optional.of(matchSummaryFactory.buildWinMatchSummary(gameState.getWinner(), gameState.getLoser(), gameType));
    }

}