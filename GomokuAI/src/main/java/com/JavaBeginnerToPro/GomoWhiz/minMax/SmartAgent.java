package com.JavaBeginnerToPro.GomoWhiz.minMax;


import com.JavaBeginnerToPro.GomoWhiz.QLearning.AI_Base;

import java.util.HashMap;

/**
 * SmartAgent class that uses minimax algorithm and heuristic evaluation to pick next move
 */
public class SmartAgent extends Agent {

    Minimax minimax;
    int deepMax;

    public SmartAgent(int boardWidth, int winRequire, int numPlayer) {
        super(boardWidth, winRequire, numPlayer);
//        minimax = new Minimax(numplayer);
//        minimax=new BrianConway(numplayer);
//        minimax=new MinimaxAlphaBetaPruning(numplayer);
//        minimax=new Fast(numplayer);
        setMinimax(1);
        Board.winStatesInit();
        Board.zobristHash = new HashMap<>();
        deepMax = 4;
    }

    public String firstTurn() {
        // pick default first move
        int center = board.n / 2;
        String move = center + " " + center;
        board.placeMove(me, move, true);
        return move;
    }

    public String takeTurn() {
        String move = pickMove();
        board.placeMove(me, move, true);
        return move;
    }

    public String pickMove() {


        int move = minimax.mm(board, AI_Base.WIN_REQUIRE);

        return board.transfer(move);
    }

    public int move(Board board) {
        board.nextPlayer = 'o';
        int action = 0;
//        for(int i=2;i<=deepMax;i+=2){
        action = minimax.mm(board, deepMax);
//            if(board.evaluate(action,numplayer)==Score.FIVE){
//                return action;
//            }
//        }
        return action;
//        return minimax.computerMove;
    }

    public int move(int[] state) {
        return move(minimax.transfer(state));
    }

    public void setMinimax(int i) {
        switch (i) {
            case 1:
                minimax = new MoreFast(numplayer);
                break;
            case 3:
                break;
            default:
                break;
        }
    }
}