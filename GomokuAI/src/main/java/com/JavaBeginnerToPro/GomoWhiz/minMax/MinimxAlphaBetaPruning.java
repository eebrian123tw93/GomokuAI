package com.JavaBeginnerToPro.GomoWhiz.minMax;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinimxAlphaBetaPruning extends Minimax {

    @Override
    public int mm(Board board, int d) {
        List<Integer> bestAction = new ArrayList<>();
        List<Integer> availableCells = board.getEmpties();
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < availableCells.size(); i++) {
            int action = availableCells.get(i);
            Board b = board.clone();
            b.placeMove('o', board.transfer(action), false);
            int best = minAB(b, d - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
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
//        return bestAction.get(bestAction.size()-1);
    }

    public int minAB(Board board, int deep, int alpha, int beta) {

        boolean win = DetectWin_2.detectWin(transfer(board), board.n, board.winRequire, 1) || DetectWin_2.detectWin(transfer(board), board.n, board.winRequire, -1);

        int point = 0;
        for (Boolean[][] winState : Board.winStates) {
            point += eval(board, winState, 1);
        }
//        System.out.println(point);
        if (deep == 0 || win) {
            return point;
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
            int currentScore = maxAB(b, deep - 1, alpha, beta);
            min = Math.min(currentScore, min);
            beta = Math.min(beta, min);
            if (beta <= alpha) {
                break;
            }
        }
        return min;
    }

    public int evaluate(Board board, int player) {
        int point = 0;
        if (player == 1) {
            if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire, player)) {
                point += 105000;
            }
            if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire - 1, player)) {
                point += 1500;
            }
            if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire - 2, player)) {
                point += 15;
            }
            if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire - 3, player)) {
                point += 5;
            }
        } else if (player == -1) {
            if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire, player)) {
                point += 100000;
            }
            if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire - 1, player)) {
                point += 1000;
            }
            if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire - 2, player)) {
                point += 10;
            }
            if (DetectWin_2.detectWin(transfer(board), board.n, board.winRequire - 3, player)) {
                point += 1;
            }
        }

        return point;
    }

    public int eval(Board board, Boolean[][] winState, int player) {
        char symbol = '\0';
        int score = 0;
        int count = 0;
        if (player == 1) {
            symbol = 'o';
        } else {
            symbol = 'x';
        }
        for (int i = 0; i < winState.length; i++) {
            for (int j = 0; j < winState[i].length; j++) {
                if (winState[i][j] && board.board[i][j] == symbol) {
                    count++;
                }
            }
        }
        if (player == 1) {
            if (count == 2) {
                score = 2;
            }
            if (count == 3) {
                score = 200;
            }
            if (count == 4) {
                score = 20000;
            }
            if (count == 5) {
                score = 2000000;
            }
        }else{
            if (count == 2) {
                score = 1;
            }
            if (count == 3) {
                score = 100;
            }
            if (count == 4) {
                score = 10000;
            }
            if (count == 5) {
                score = 1000000;
            }
        }

        return score;
    }

    public int maxAB(Board board, int deep, int alpha, int beta) {
        boolean win = DetectWin_2.detectWin(transfer(board), board.n, board.winRequire, 1) || DetectWin_2.detectWin(transfer(board), board.n, board.winRequire, -1);
        int point = 0;
        for (Boolean[][] winState : Board.winStates) {
            point += eval(board, winState, -1);
        }
        if (deep == 0 || win) {
            return point;
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
            int currentScore = minAB(b, deep - 1, alpha, beta);
            max = Math.max(currentScore, max);
            alpha = Math.max(max, alpha);
            if (beta <= alpha) {
                break;
            }
        }
        return max;
    }
}
