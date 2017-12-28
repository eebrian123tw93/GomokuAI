package com.JavaBeginnerToPro.GomoWhiz.minMax;

public class Agent {
    Board board;
    char me;
    char them;
    boolean isFirst;
    int numplayer;
    int numOpponent;

    public Agent(int n, int m, int numplayer) {
        this.board = new Board(n, m);
        this.me = 'o';
        this.them = 'x';
        this.isFirst = isFirst;
        this.numplayer = numplayer;
        this.numOpponent = numplayer == 1 ? 2 : 1;
    }

    public String receiveTurn(String move) {
        board.placeMove(them, move, false);
        return move;
    }

    public String takeTurn(String move) {
        return null;
    }

    public char getWinner() {
        return board.winner;
    }
}