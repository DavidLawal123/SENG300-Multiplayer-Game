package ca.ucalgary.seng300.statistics;

import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;

import java.util.List;

public class MatchSummaryFactory {
    public MatchSummaryFactory() {
    }
    public MatchSummary buildWinMatchSummary(final Player winner, final Player loser, final GameType gameType){
        return new MatchSummary(gameType, GameOutcome.WIN, winner.getUUID(), loser.getUUID());
    }
    public MatchSummary buildDrawMatchSummary(List<Player> playerList, final GameType gameType){
        return new MatchSummary(gameType, GameOutcome.DRAW, playerList.get(0).getUUID(), playerList.get(1).getUUID());
    }
}
