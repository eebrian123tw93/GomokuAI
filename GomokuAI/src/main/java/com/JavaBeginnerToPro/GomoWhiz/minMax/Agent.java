package com.JavaBeginnerToPro.GomoWhiz.minMax;

/**
 * Agent class that is extended by other players
 */
public class Agent {
    Board board;
    char me;
    char them;
    boolean isFirst;

    public Agent(int n, int m) {
        this.board = new Board(n, m);
        this.me =  'o';
        this.them = 'x';
        this.isFirst = isFirst;
    }

    /**
     * Get opponent's turn and update board
     *
     * @param move opponent's move
     * @return opponent's move
     */
    public String receiveTurn(String move) {
        board.placeMove(them, move, false);
//        System.out.println(board);
        return move;
    }

    /**
     * Execute a turn
     *
     * @param move to execute
     * @return agent's move
     */
    public String takeTurn(String move) {
        return null;
    }

    /**
     * Get the board's winner
     *
     * @return player's char representation
     */
    public char getWinner() {
        return board.winner;
    }
}