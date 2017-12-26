package com.JavaBeginnerToPro.GomoWhiz.minMax;

import com.JavaBeginnerToPro.GomoWhiz.Version_1.GomokuAI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Minimax class has evaluation function and minimax algorithm with alpha-beta pruning to determine best moves.
 */
public class Minimax {

    /**
     * Evaluation function
     *
     * @param board node to evaluate
     * @return score of board
     */
    //'o'=me
    //'x'=them
//    public double evaluate(Board board) {
//        int oneAway = board.nearWins(board.prevPlayer, 1);
//        int twoAway = board.nearWins(board.prevPlayer, 2);
//        int threeAway = board.nearWins(board.prevPlayer, 3);
//        double score = oneAway * 100.0 + twoAway * 5.0 + threeAway * 1.0;
//        return score;
//    }

    public Minimax(){
        Board.winStates.clear();
        Board.winStatesInit();
    }
    public int mm(Board board, int d) {
        List<Integer> bestAction = new ArrayList<>();
        List<Integer> availableCells = board.getEmpties();
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < availableCells.size(); i++) {
            int action = availableCells.get(i);
            Board b = board.clone();
            b.placeMove('o', board.transfer(action), false);
            int best = min(b, d - 1);
            if (best == max) {
                bestAction.add(action);
            }
            if (best > max) {
                max = best;
                bestAction.clear();
                bestAction.add(action);
            }
        }
        if (bestAction.size() == 0) {
            if (availableCells.size() == 0) return board.transfer(board.strMove(board.n / 2, board.n / 2));
            return availableCells.get(new Random().nextInt(availableCells.size()));
        }
        return bestAction.get(new Random().nextInt(bestAction.size()));
    }

    public int min(Board board, int deep) {
        if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire, 1)) {
            return 1;
        } else if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire, -1)) {
            return -1;
        }
        if (deep == 0) {
            return Integer.MIN_VALUE;
        }
        List<Integer> availableCells = board.getEmpties();
        if (availableCells.size() == 0) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < availableCells.size(); i++) {
            int action = availableCells.get(i);
            Board b = board.clone();
            b.placeMove('x', board.transfer(action), false);
            int currentScore = max(b, deep - 1);
            min = Math.min(currentScore, min);
        }
        return min;
    }

    public int max(Board board, int deep) {
        if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire, 1)) {
            return 1;
        } else if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire, -1)) {
            return -1;
        }
        if (deep == 0) {
            return Integer.MAX_VALUE;
        }
        List<Integer> availableCells = board.getEmpties();
        if (availableCells.size() == 0) {
            return 0;
        }
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < availableCells.size(); i++) {
            int action = availableCells.get(i);
            Board b = board.clone();
            b.placeMove('o', board.transfer(action), false);
            int currentScore = min(b, deep - 1);
            max = Math.max(currentScore, max);
        }
        return max;
    }

    public int[] transfer(Board board) {
        int[] status = new int[board.n * board.n];
        int k = 0;
        for (int i = 0; i < board.board.length; i++) {
            for (int j = 0; j < board.board[i].length; j++) {
                if (board.board[i][j] == 'o') {
                    status[k++] = 1;
                } else if (board.board[i][j] == 'x') {
                    status[k++] = -1;
                } else {
                    status[k++] = 0;
                }
            }
        }
        return status;
    }

    //'o'=min
    public Board transfer(int[] state) {
        Board board = new Board(GomokuAI.gameBoardWidth, GomokuAI.winRequire);
        int j = 0, k = 0;
        for (int i = 0; i < state.length; i++) {
//            board[j][k]=
            if (state[i] == 1) board.board[j][k] = 'o';
            if (state[i] == 2) board.board[j][k] = 'x';
            if (i % (int) Math.sqrt(state.length) == (int) Math.sqrt(state.length)-1 && i != 0) {
                j++;
                k = 0;
            } else {
                k++;
            }

        }
        return board;
    }
}