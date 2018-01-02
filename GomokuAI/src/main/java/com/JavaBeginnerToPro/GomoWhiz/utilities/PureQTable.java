package com.JavaBeginnerToPro.GomoWhiz.utilities;

import com.JavaBeginnerToPro.GomoWhiz.QLearning.PatternDetect;
import com.JavaBeginnerToPro.GomoWhiz.QLearning.QMapIO;

import java.util.Map;

public class PureQTable extends AI {
    //only gets moves from qMap, otherwise random
    private Map<String, Double> qMap;
    private int ourPlayerNum;

    public PureQTable(int ourPlayerNum, String brainName) {
        qMap = QMapIO.load(brainName);
        this.ourPlayerNum = ourPlayerNum;
    }

    public int move(int[] state) {
        if (PatternDetect.isEmpty(state)) return state.length / 2;
        String stateKey = makeStateKey(state);
        if (ourPlayerNum == 1 && qMap.get(stateKey) != null) return getMaxQValueAction(state, ourPlayerNum);
        else if (ourPlayerNum == 2 && qMap.get(stateKey) != null) return getMinQValueAction(state, ourPlayerNum);
        else return randomMove(state);
    }

    private String makeStateKey(int[] state) {
        StringBuilder sb = new StringBuilder();
        for (int i : PatternDetect.detect(state, 1)) sb.append(Integer.toString(i));
        for (int i : PatternDetect.detect(state, 2)) sb.append(Integer.toString(i));
        return sb.toString();
    }
    private int getMaxQValueAction(int [] state, int currentPlayer){
        int[] nextState = state.clone();
        int[] validActions = getValidActions(state);
        int maxQAction = -1;
        double stateMaxQValue = Double.NEGATIVE_INFINITY;
        double stateQValue;

        for (int i = 0; i < validActions.length; ++i) {
            nextState[validActions[i]] = currentPlayer;
            stateQValue = evalState(makeStateKey(nextState));
            if (stateQValue > stateMaxQValue) {
                stateMaxQValue = stateQValue;
                maxQAction = validActions[i];
            }
            nextState[validActions[i]] = 0;
        }

        return maxQAction;
    }
    private int getMinQValueAction(int [] state, int currentPlayer){
        int[] nextState = state.clone();
        int[] validActions = getValidActions(state);
        int minQAction = -1;
        double stateMinQValue = Double.POSITIVE_INFINITY;
        double stateQValue;

        for (int i = 0; i < validActions.length; ++i) {
            nextState[validActions[i]] = currentPlayer;
            stateQValue = evalState(makeStateKey(nextState));
            if (stateQValue < stateMinQValue) {
                stateMinQValue = stateQValue;
                minQAction = validActions[i];
            }
            nextState[validActions[i]] = 0;
        }
        return minQAction;
    }
    private double evalState(String stateKey){
        //if state has not happened before
        if (qMap.get(stateKey) == null)
            qMap.put(stateKey, (rand.nextDouble() * 0.3) - 0.15); //-0.15 ~ +0.15

        return qMap.get(stateKey);
    }
    private static int [] getValidActions(int [] state){
        int [][] state2D = DetectWin.convert1Dto2D(state, Playground.BOARD_WIDTH);
        int [] validActions;
        int upperBound = 0;
        int lowerBound = state2D.length;
        int leftBound = 0;
        int rightBound = state2D.length;

        if (PatternDetect.isEmpty(state)) {
            validActions = new int[1];
            validActions[0] = state.length / 2;
            return validActions;
        }

        else {
            boolean found = false;

            //find upper bound
            for (int row = 0; row < state2D.length - 2; ++row) {
                for (int col = 1; col < state2D.length - 1; ++col) {
                    if (state2D[row + 1][col] != 0) {
                        upperBound = row;
                        found = true;
                        break;
                    }
                }
                if (found == true) break;
            }

            found = false;
            //find lower bound
            for (int row = state2D.length - 1; row > 1; --row) {
                for (int col = 1; col < state2D.length - 1; ++col) {
                    if (state2D[row - 1][col] != 0) {
                        lowerBound = row;
                        found = true;
                        break;
                    }
                }
                if (found == true) break;
            }

            found = false;
            //find left bound
            for (int col = 0; col < state2D.length - 2; ++col) {
                for (int row = 1; row < state2D.length - 1; ++row) {
                    if (state2D[row][col + 1] != 0) {
                        leftBound = col;
                        found = true;
                        break;
                    }
                }
                if (found == true) break;
            }

            found = false;
            //find right bound
            for (int col = state2D.length - 1; col > 1; --col) {
                for (int row = 1; row < state2D.length - 1; ++row) {
                    if (state2D[row][col - 1] != 0) {
                        rightBound = col;
                        found = true;
                        break;
                    }
                }
                if (found == true) break;
            }

            int emptySpacesInsideValidRegion = 0;
            for (int row = upperBound; row <= lowerBound; ++row){
                for (int col = leftBound; col <= rightBound; ++col){
                    if (state2D[row][col] == 0) ++emptySpacesInsideValidRegion;
                }
            }

            validActions = new int[emptySpacesInsideValidRegion];

            int counter = 0;
            int index = 0;
            for (int row = 1; row < state2D.length - 1; ++row) {
                for (int col = 1; col < state2D.length - 1; ++col) {
                    if (row >= upperBound && row <= lowerBound && col >= leftBound && col <= rightBound){
                        if (state2D[row][col] == 0) {
                            validActions[index] = counter;
                            ++index;
                        }
                    }
                    ++counter;
                }
            }

            return validActions;
        }
    }
}
