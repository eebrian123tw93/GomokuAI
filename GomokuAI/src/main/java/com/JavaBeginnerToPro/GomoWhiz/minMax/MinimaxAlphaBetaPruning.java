package com.JavaBeginnerToPro.GomoWhiz.minMax;


import com.JavaBeginnerToPro.GomoWhiz.utilities.DetectWin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class MinimaxAlphaBetaPruning extends Minimax {

    public MinimaxAlphaBetaPruning(int numplayer) {
        super(numplayer);
    }

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

        boolean win = DetectWin.detectWin(transfer(board), board.n, board.winRequire, numplayer) || DetectWin.detectWin(transfer(board), board.n, board.winRequire, numOpponent);

        int point = 0;
//        for (Boolean[][] winState : Board.winStates) {
//            point += eval(board, winState, 1);
//
//        }
        point = evaluate(board, numplayer);
//        System.out.println(point);
        if (deep == 0 || win) {
            return point;
        }
        List<Integer> availableCells = board.getEmpties(numOpponent);
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
        if (player == numplayer) {
            if (DetectWin.detectWin(transfer(board), board.n, board.winRequire, player)) {
                point = 6;
                return point;
            }
//            if (DetectWin.detectWin(transfer(board), board.n, board.winRequire - 1, player)) {
//                point = 4;
//                return point;
//            }
//            if (DetectWin.detectWin(transfer(board), board.n, board.winRequire - 2, player)) {
//                point = 3;
//                return point;
//            }
//            if (DetectWin.detectWin(transfer(board), board.n, board.winRequire - 3, player)) {
//                point = 2;
//                return point;
//            }
        } else if (player == numOpponent) {
            if (DetectWin.detectWin(transfer(board), board.n, board.winRequire, player)) {
                point = -5;
                return point;
            }
//            if (DetectWin.detectWin(transfer(board), board.n, board.winRequire - 1, player)) {
//                point = -4;
//                return point;
//            }
//            if (DetectWin.detectWin(transfer(board), board.n, board.winRequire - 2, player)) {
//                point = -3;
//                return point;
//            }
//            if (DetectWin.detectWin(transfer(board), board.n, board.winRequire - 3, player)) {
//                point = -2;
//                return point;
//            }
        }

        return point;
    }

    public int eval(Board board, Boolean[][] winState, int player) {
        char symbol = '\0';
        int score = 0;
        int count = 0;
        boolean continues = false;
        if (player == 1) {
            symbol = 'o';
        } else {
            symbol = 'x';
        }
        for (int i = 0; i < winState.length; i++) {
            for (int j = 0; j < winState[i].length; j++) {
                if (winState[i][j] && board.board[i][j] == symbol) {

                    if (continues) {
                        count++;
                    }
                    continues = true;
                } else if (winState[i][j] && board.board[i][j] != symbol) {
                    continues = false;
                }
            }
        }
        if (player == 1) {
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
                score = 100000000;
            }
        } else {
            if (count == 2) {
                score = -10;
            }
            if (count == 3) {
                score = -1000;
            }
            if (count == 4) {
                score = -100000;
            }
            if (count == 5) {
                score = -10000000;
            }
        }

        return score;
    }

    public int maxAB(Board board, int deep, int alpha, int beta) {
        boolean win = DetectWin.detectWin(transfer(board), board.n, board.winRequire, numplayer) || DetectWin.detectWin(transfer(board), board.n, board.winRequire, numOpponent);
        int point = 0;
//        for (Boolean[][] winState : Board.winStates) {
//            point += eval(board, winState, -1);
//        }
        point = evaluate(board, numOpponent);
        if (deep == 0 || win) {
            return point;
        }
        List<Integer> availableCells = board.getEmpties(numplayer);
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

class Fast extends MinimaxAlphaBetaPruning {
    public Fast(int numplayer) {
        super(numplayer);
    }

    @Override
    public int mm(Board board, int d) {
        List<Integer> bestAction = new ArrayList<>();
        List<Integer> availableCells = board.getEmpties();
        int max = Integer.MIN_VALUE;
//        for (int i = 0; i < availableCells.size(); i++) {
//            int action = availableCells.get(i);
//            Board b = board.clone();
//            b.placeMove('o', board.transfer(action), false);
//            int best = minAB(b, d - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
//            if (best == max) {
//                bestAction.add(action);
//            }
//            if (best > max) {
//                max = best;
//                bestAction.clear();
//                bestAction.add(action);
//            }
//
//        }

//        ExecutorService es = Executors.newCachedThreadPool();
        ExecutorService es;
        if (availableCells.size() != 0)
            es = Executors.newFixedThreadPool(availableCells.size());
        else
            es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<FutureTask<ReturnObject>> list = new ArrayList<>();
        for (int i = 0; i < availableCells.size(); i++) {
            int action = availableCells.get(i);
            FutureTask<ReturnObject> futureTask = new FutureTask<ReturnObject>(new CallableTask(action, board, d));
            es.execute(futureTask);
            list.add(futureTask);
        }
        for (int i = 0; i < availableCells.size(); i++) {
            try {
                ReturnObject returnObject = list.get(i).get();
                if (returnObject.best == max) {
                    bestAction.add(returnObject.action);
                }
                if (returnObject.best > max) {
                    max = returnObject.best;
                    bestAction.clear();
                    bestAction.add(returnObject.action);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        es.shutdown();
        Runtime.getRuntime().gc();
        try {
            es.awaitTermination(5, TimeUnit.SECONDS);
        } catch (Exception e) {

        }
        if (bestAction.size() == 0) {
            if (availableCells.size() == 0) return board.transfer(board.strMove(board.n / 2, board.n / 2));
            return availableCells.get(new Random().nextInt(availableCells.size()));
        }

        return bestAction.get(new Random().nextInt(bestAction.size()));
//        return bestAction.get(bestAction.size()-1);
    }

    class CallableTask implements Callable<ReturnObject> {
        int action;
        Board board;
        int d;

        CallableTask(int action, Board board, int d) {
            this.action = action;
            this.board = board;
            this.d = d;
        }

        @Override
        public ReturnObject call() throws Exception {
            ReturnObject returnObject = new ReturnObject();
            returnObject.action = action;
            Board b = board.clone();
            b.placeMove('o', board.transfer(action), false);
            returnObject.best = minAB(b, d - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            return returnObject;
        }
    }

    class ReturnObject {
        int action;
        int best;
    }

}

class MoreFast extends Fast {
    long stateKey;

    public MoreFast(int numplayer) {
        super(numplayer);
        stateKey = Board.zobrist[0][0];
        for (int i = 1; i < 225; i++) {
            stateKey = stateKey ^ Board.zobrist[0][i];
        }
    }

    @Override
    public int mm(Board board, int d) {
        int state[] = transfer(board);
        for (int i = 0; i < state.length; i++) {
            if (state[i] != 0) {
                stateKey = stateKey ^ Board.zobrist[state[i]][i];
            }
        }

        List<Integer> bestAction = new ArrayList<>();
        List<Integer> availableCells = board.getEmpties(numplayer, stateKey);
        if(availableCells.size()>2){
            availableCells=availableCells.subList(0,2);
        }
        if(availableCells.size()==1){
            return availableCells.get(0);
        }

        int max = Integer.MIN_VALUE;
        ExecutorService es;

        es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);
        List<FutureTask<ReturnObject>> list = new ArrayList<>();
        for (int i = 0; i < availableCells.size(); i++) {
            int action = availableCells.get(i);
            FutureTask<ReturnObject> futureTask = new FutureTask<ReturnObject>(new CallableTask(action, board, d) {
                @Override
                public ReturnObject call() throws Exception {
                    ReturnObject returnObject = new ReturnObject();
                    returnObject.action = action;
                    Board b = board.clone();
                    b.placeMove('o', board.transfer(action), false);
                    stateKey = stateKey ^ Board.zobrist[numplayer][action];
                    returnObject.best = minAB(b, d - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, stateKey);
                    return returnObject;
                }
            });
            es.execute(futureTask);
            list.add(futureTask);
        }
        for (int i = 0; i < availableCells.size(); i++) {
            try {
                ReturnObject returnObject = list.get(i).get();
                if (returnObject.best == max) {
                    bestAction.add(returnObject.action);
                }
                if (returnObject.best > max) {
                    max = returnObject.best;
                    bestAction.clear();
                    bestAction.add(returnObject.action);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        es.shutdown();
        Runtime.getRuntime().gc();
        try {
            es.awaitTermination(5, TimeUnit.SECONDS);
        } catch (Exception e) {

        }
        if (bestAction.size() == 0) {
            if (availableCells.size() == 0) return board.transfer(board.strMove(board.n / 2, board.n / 2));
            return availableCells.get(new Random().nextInt(availableCells.size()));
        }

        return bestAction.get(new Random().nextInt(bestAction.size()));
//        return bestAction.get(bestAction.size()-1);
    }

    public int maxAB(Board board, int deep, int alpha, int beta, long stateKey) {
        boolean win = DetectWin.detectWin(transfer(board), board.n, board.winRequire, numplayer) || DetectWin.detectWin(transfer(board), board.n, board.winRequire, numOpponent);
        int point = 0;
//        for (Boolean[][] winState : Board.winStates) {
//            point += eval(board, winState, -1);
//        }
        point = evaluate(board, numOpponent);
        if (deep == 0 || win) {
            return point;
        }
        List<Integer> availableCells = board.getEmpties(numplayer, stateKey);
        if (availableCells.size() == 0) {
            return 0;
        }
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < availableCells.size(); i++) {
            int action = availableCells.get(i);
            Board b = board.clone();
            b.placeMove('o', board.transfer(action), false);
            stateKey = stateKey ^ Board.zobrist[numplayer][action];
            int currentScore = minAB(b, deep - 1, alpha, beta, stateKey);
            max = Math.max(currentScore, max);
            alpha = Math.max(max, alpha);
            if (beta <= alpha) {
                break;
            }
        }
        return max;
    }

    public int minAB(Board board, int deep, int alpha, int beta, long stateKey) {

        boolean win = DetectWin.detectWin(transfer(board), board.n, board.winRequire, numplayer) || DetectWin.detectWin(transfer(board), board.n, board.winRequire, numOpponent);

        int point = 0;
//        for (Boolean[][] winState : Board.winStates) {
//            point += eval(board, winState, 1);
//
//        }
        point = evaluate(board, numplayer);
//        System.out.println(point);
        if (deep == 0 || win) {
            return point;
        }
        List<Integer> availableCells = board.getEmpties(numOpponent, stateKey);
        if (availableCells.size() == 0) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < availableCells.size(); i++) {
            int action = availableCells.get(i);
            Board b = board.clone();
            b.placeMove('x', board.transfer(action), false);
            stateKey = stateKey ^ Board.zobrist[numOpponent][action];
            int currentScore = maxAB(b, deep - 1, alpha, beta, stateKey);
            min = Math.min(currentScore, min);
            beta = Math.min(beta, min);
            if (beta <= alpha) {
                break;
            }
        }
        return min;
    }
}
