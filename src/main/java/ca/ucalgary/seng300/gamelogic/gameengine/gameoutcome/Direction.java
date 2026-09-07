package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

public enum Direction {
    UP(-1, 0),
    UP_RIGHT(-1, 1),
    RIGHT(0, 1),
    DOWN_RIGHT(1, 1),
    DOWN(1, 0),
    DOWN_LEFT(1, -1),
    LEFT(0, -1),
    UP_LEFT(-1, -1);

    private final int rowIncrement;
    private final int colIncrement;

    Direction(final int rowIncrement, final int colIncrement) {
        this.rowIncrement = rowIncrement;
        this.colIncrement = colIncrement;
    }

    public int getRowIncrement() {
        return rowIncrement;
    }

    public int getColIncrement() {
        return colIncrement;
    }
}