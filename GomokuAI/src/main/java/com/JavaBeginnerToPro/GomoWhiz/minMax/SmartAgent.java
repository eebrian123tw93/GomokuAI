package com.JavaBeginnerToPro.GomoWhiz.minMax;

import com.JavaBeginnerToPro.GomoWhiz.Version_1.GomokuAI;

import java.util.HashMap;

/**
 * SmartAgent class that uses minimax algorithm and heuristic evaluation to pick next move
 */
public class SmartAgent extends Agent {

    Minimax minimax;
    int deepMax;
    public SmartAgent(int boardWidth, int winRequire,int numPlayer) {
        super(boardWidth, winRequire,numPlayer);
//        minimax = new Minimax(numplayer);
//        minimax=new BrianConway(numplayer);
//        minimax=new MinimxAlphaBetaPruning(numplayer);
//        minimax=new Fast(numplayer);
        minimax=new MoreFast(numplayer);
        Board.winStatesInit();
        Board.zobristHash=new HashMap<>();
        deepMax=4;
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
        board.nextPlayer='o';int action=0;
//        for(int i=2;i<=deepMax;i+=2){
            action=minimax.mm(board,deepMax);
//            if(board.evaluate(action,numplayer)==Score.FIVE){
//                return action;
//            }
//        }
        return  action;
//        return minimax.computerMove;
    }
    public int move(int [] state){
        return move(minimax.transfer(state));
    }
}