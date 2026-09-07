package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.context.MoveContextFactory;
import ca.ucalgary.seng300.gamelogic.gamestate.Cell;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GameOutcomeEvaluator {
    private final int winLength;

    public GameOutcomeEvaluator(int winLength) {
        this.winLength = winLength;
    }

    public int getWinLength() {
        return winLength;
    }

    protected abstract GameOutcome evaluateGameOutcome(MoveContext ctx);

    protected static class Search {
        /**
         *
         * @param ctx
         * @param winLength
         * @return It returns Optional.empty(), i.e. null, if there is no win
         */
        protected static Optional<List<Cell>> winSearch(final MoveContext ctx, final int winLength) {
            Optional<List<Cell>> result = biDirectionalSearch(ctx, Direction.UP, Direction.DOWN, winLength);
            if (result.isPresent()){ return result; }

            result = biDirectionalSearch(ctx, Direction.UP_RIGHT, Direction.DOWN_LEFT, winLength);
            if (result.isPresent()){ return result; }

            result = biDirectionalSearch(ctx, Direction.RIGHT, Direction.LEFT, winLength);
            if (result.isPresent()){ return result; }

            result = biDirectionalSearch(ctx, Direction.DOWN_RIGHT, Direction.UP_LEFT, winLength);
//            if (result.isPresent()){ return result; }
//
//            result = biDirectionalSearch(ctx, Direction.DOWN_RIGHT, Direction.UP_LEFT, winLength);
            return result;
        }

        private static Optional<List<Cell>> biDirectionalSearch(final MoveContext ctx, final Direction dir1, final Direction dir2, final int winLength) {
            GameState gs = ctx.getGameState();
            Cell root = gs.getCell(ctx.getRow(), ctx.getCol());
            List<Cell> discoveredNodes = new ArrayList<>();

            discoveredNodes.add(root);
            int searchLength = winLength - 1;

            // searches in direction 1 first
            List<Cell> discoveredNodes1 = uniDirectionalSearch(ctx, dir1, searchLength);
            discoveredNodes.addAll(discoveredNodes1);

            // checks if the cells in that direction matches the amount of cells to win
            if (discoveredNodes.size() == winLength){
                return Optional.of(discoveredNodes);
            }

            searchLength = winLength - discoveredNodes.size();

            // searches in direction 2
            List<Cell> discoveredNodes2 = uniDirectionalSearch(ctx, dir2, searchLength);
            discoveredNodes.addAll(discoveredNodes2);

            // checks if the cells in that direction matches the amount of cells to win
            if (discoveredNodes.size() == winLength){
                return Optional.of(discoveredNodes);
            }

            return Optional.empty();
        }

        private static List<Cell> uniDirectionalSearch(final MoveContext ctx, final Direction dir, final int winLength) {
            MoveContextFactory mcf = new MoveContextFactory();
            GameState gs = ctx.getGameState();
            Player player = ctx.getPlayer();
            int row = ctx.getRow();
            int col = ctx.getCol();
            Cell root = gs.getCell(row, col);
            List<Cell> discoveredNodes = new ArrayList<>();

            Optional<Cell> node = Optional.ofNullable(root);

            // this allows to reflect the changes in current node by updating the row and column
            MoveContext searchContext = ctx;

            while(node.isPresent()){
                Optional<Cell> nextNode = oneStepSearch(searchContext, dir);

                // checks if the next cell is null
                if (nextNode.isPresent()){
                    Optional<Player> nextNodePlayer = nextNode.get().getOccupancy();

                    // checks whether the cell has a player; i.e., checks if there is a 'piece' in the cell
                    if (nextNodePlayer.isPresent()){

                        // checks if the player in the cell is the same as the player requesting the move
                        if (nextNodePlayer.get().getUUID() == player.getUUID()){
                            node = nextNode;
                            discoveredNodes.add(node.get());

                            // checks if there is enough pieces to win
                            if (discoveredNodes.size() == winLength) {
                                return discoveredNodes;
                            }

                            Cell cell = nextNode.get();
                            searchContext = mcf.buildCTX(cell.getRow(), cell.getCol(), ctx.getPlayer(), ctx.getGameState());
                        } else {
                            node = Optional.empty();
                        }
                    } else {
                        node = Optional.empty();
                    }
                } else {
                    node = Optional.empty();
                }
            }
            return discoveredNodes;
        }

        private static Optional<Cell> oneStepSearch(final MoveContext ctx, final Direction dir) {
            GameState gs = ctx.getGameState();
            int row = ctx.getRow();
            int col = ctx.getCol();
            Player requester = ctx.getPlayer();

            // get the coordinates of the cell being searched
            int nextCellRow = row + dir.getRowIncrement();
            int nextCellCol = col + dir.getColIncrement();

            // if cell is not within the bounds
            if (!gs.inBounds(nextCellRow, nextCellCol)) {
                return Optional.empty();
            }

            Cell cell = gs.getCell(nextCellRow, nextCellCol);
            Optional<Player> occupancy = cell.getOccupancy();

            // if there is no player
            if (occupancy.isEmpty()){
                return Optional.empty();
            }

            // if there is a player, but not the move requester
            if (requester.getUUID() != occupancy.get().getUUID()){
                return Optional.empty();
            }

            // returns cell if the cell's occupancy is the same as the requester, i.e., original cell's 'piece' is the same as the incremented cell's 'piece'
            return Optional.of(cell);
        }
    }
}