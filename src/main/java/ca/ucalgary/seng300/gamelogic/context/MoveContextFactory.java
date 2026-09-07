package ca.ucalgary.seng300.gamelogic.context;

import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;

public class MoveContextFactory {
    public MoveContextFactory() {
    }
    public MoveContext buildCTX(int row, int col, Player player, GameState gs){
        return new MoveContext(row, col, player, gs);
    }
}
