package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectionTests {

    @Test
    public void upDirection() {
        assertEquals(-1, Direction.UP.getRowIncrement());
        assertEquals(0, Direction.UP.getColIncrement());
    }

    @Test
    public void upRightDirection() {
        assertEquals(-1, Direction.UP_RIGHT.getRowIncrement());
        assertEquals(1, Direction.UP_RIGHT.getColIncrement());
    }

    @Test
    public void rightDirection() {
        assertEquals(0, Direction.RIGHT.getRowIncrement());
        assertEquals(1, Direction.RIGHT.getColIncrement());
    }

    @Test
    public void downRightDirection() {
        assertEquals(1, Direction.DOWN_RIGHT.getRowIncrement());
        assertEquals(1, Direction.DOWN_RIGHT.getColIncrement());
    }

    @Test
    public void downDirection() {
        assertEquals(1, Direction.DOWN.getRowIncrement());
        assertEquals(0, Direction.DOWN.getColIncrement());
    }

    @Test
    public void downLeftDirection() {
        assertEquals(1, Direction.DOWN_LEFT.getRowIncrement());
        assertEquals(-1, Direction.DOWN_LEFT.getColIncrement());
    }

    @Test
    public void leftDirection() {
        assertEquals(0, Direction.LEFT.getRowIncrement());
        assertEquals(-1, Direction.LEFT.getColIncrement());
    }

    @Test
    public void upLeftDirection() {
        assertEquals(-1, Direction.UP_LEFT.getRowIncrement());
        assertEquals(-1, Direction.UP_LEFT.getColIncrement());
    }

    @Test
    public void directionEnumHasEight() {
        assertEquals(8, Direction.values().length);
    }
}