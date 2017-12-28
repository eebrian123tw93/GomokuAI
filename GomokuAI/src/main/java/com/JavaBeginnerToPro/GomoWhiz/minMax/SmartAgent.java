package com.JavaBeginnerToPro.GomoWhiz.minMax;

import com.JavaBeginnerToPro.GomoWhiz.Version_1.GomokuAI;

/**
 * SmartAgent class that uses minimax algorithm and heuristic evaluation to pick next move
 */
public class SmartAgent extends Agent {

    Minimax minimax;

    public SmartAgent(int boardWidth, int winRequire,int numPlayer) {
        super(boardWidth, winRequire,numPlayer);
//        minimax = new Minimax(numplayer);
//        minimax=new BrianConway(numplayer);
//        minimax=new MinimxAlphaBetaPruning();
        minimax=new Fast(numplayer);
    }

    public String firstTurn() {
        // pick default first move
        int center=board.n/2;
        String move = center+" "+center;
        board.placeMove(me, move, true);
        return move;
    }

    public String takeTurn() {
        String move = pickMove();
        board.placeMove(me, move, true);
        return move;
    }

    public String pickMove() {


        int move=minimax.mm(board, GomokuAI.winRequire);

        return board.transfer(move);
    }
    public int move(Board board){
        board.nextPlayer='o';
        return  minimax.mm(board,4);
//        return minimax.computerMove;
    }
    public int move(int [] state){
        return move(minimax.transfer(state));
    }
}