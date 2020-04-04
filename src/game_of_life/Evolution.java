package game_of_life;

import java.util.Random;

/**
 * Evolution is a utility class with static methods that perform
 * operations on the universe data structure such as its initial
 * creation and advancing its state following Conway's rules.
 * */

public class Evolution implements GameOfLifeConstants {

    /**takes a universe and advances it through the simulation by a step amount of
     * generations.
     * Note: in the main program, the step argument is just set to 1*/
    public static char[][] progressUniverse(int step, char[][] initialState) {
        char[][] result = deepCopy(initialState);
        for(int i = 0; i < step; i++) {
            result = advanceOneGeneration(result);
        }
        return result;
    }
    /**takes a matrix representing the current state, checks each cell for what
     * should be its next state and stores the results in a new matrix representing
     * the new state, which it then returns to the caller*/
    private static char[][] advanceOneGeneration(char[][] currentState) {
        char[][] nextState = deepCopy(currentState);
        for(int i = 0; i < currentState.length; i++) {
            for(int j = 0; j < currentState[0].length; j++) {
                if (cellIsDying(i,j,currentState)) {
                    nextState[i][j] = ' ';
                } else {
                    nextState[i][j] = 'O';
                }
            }
        }
        return nextState;
    }

    /** returns true if cell is dead or dying, and false if cell is alive or being revived*/
    private static boolean cellIsDying(int y, int x, char[][] universe) {
        int numberOfNeighbors = countNeighbors(y,x,universe);
        if (universe[y][x] == ' ') {
            if (numberOfNeighbors == 3) {
                return false;
            } else {
                return true;
            }
        } else {
            if(numberOfNeighbors == 2 || numberOfNeighbors == 3) {
                return false;
            } else {
                return true;
            }
        }
    }
    /**
     * counts the number of alive cells around a certain cell (x,y) in a universe
     * matrix, neighboring cells surround our cell in 8 directions (if our cell was
     * the number 5 on your keypad, the neighboring cells are digits 1-9 except for 5)
     * and we treat the universe as being periodic (the top left most corner cell's
     * neighbors pop up from the bottom right corner)
     * */
    private static int countNeighbors(int y, int x, char[][] universe) {
        int neighborsAlive = 0;
        for(int i = y -1; i < y + 2; i++) {
            for(int j = x - 1; j < x + 2; j++) {
                if( i == y && j == x) continue;
                int a = Math.floorMod(i,universe.length);
                int b = Math.floorMod(j,universe[0].length);
                if (universe[a][b] == 'O') {
                    neighborsAlive++;
                }
            }
        }
        return neighborsAlive;
    }
    /**simple method to perform deep copies of a two dimensional char array*/
    private static char[][] deepCopy(char[][] array) {
        char[][] result = new char[array.length][array[0].length];
        for (int i = 0; i < result.length; i++) {
            result[i] = array[i].clone();
        }
        return result;
    }

    /**
     * returns a matrix representing the initial state of a newly created
     * universe, the state of each cell is determined pseudo-randomly with
     * Java's random class */

    public static char[][] createNewUniverse(int size)   {
        Random rng = new Random();
        char[][] result = new char[size][size];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                if (rng.nextBoolean())    {
                    result[i][j] = 'O';

                } else {
                    result[i][j] = ' ';
                }
            }
        }
        return result;
    }
}
