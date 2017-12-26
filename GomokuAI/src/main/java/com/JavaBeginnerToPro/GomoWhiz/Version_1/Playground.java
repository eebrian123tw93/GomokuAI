package com.JavaBeginnerToPro.GomoWhiz.Version_1;

import com.JavaBeginnerToPro.GomoWhiz.ConwayAI_V2.Conway_V2_base;
import com.JavaBeginnerToPro.GomoWhiz.ConwayAI_V2.PatternDetect;
import com.JavaBeginnerToPro.GomoWhiz.ConwayAI_V2.QMapIO;
import com.JavaBeginnerToPro.GomoWhiz.ConwayAI_V2.QTable_AI;
import com.JavaBeginnerToPro.GomoWhiz.minMax.SmartAgent;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.encog.persist.EncogDirectoryPersistence.loadObject;


public class Playground {
    AI player1, player2;
    public static final int GAMES_TO_PLAY = 1000;
    public static final int BOARD_WIDTH = 15;
    public static final int BOARD_SIZE = BOARD_WIDTH * BOARD_WIDTH;
    public static final int WIN_REQUIRE = 5;
    public int player1Win = 0;
    public int player2Win = 0;
    public int tie = 0;
    private int [] state;
    private java.util.Random rand;
    boolean displayBoard = true;
    GomokuGUI gui;


    Playground(){
        player2 = new ConwayAI_V2_QTable(2);
        player1 = new MinMax_smartAgent();
        rand = new java.util.Random();
        state = new int [BOARD_SIZE];
        if (displayBoard) gui = new GomokuGUI(state);
    }

    public static void main(String[] args) {
        int gamesPlayed = 0;
        Playground playground = new Playground();
        long startTime = System.currentTimeMillis();
        while (gamesPlayed < GAMES_TO_PLAY){
            playground.play();
            ++gamesPlayed;

            if (gamesPlayed % 1000 == 0) {
                System.out.println("Player 1 wins: " + playground.player1Win + " Player 2 wins: " + playground.player2Win + " ties: " + playground.tie);
            }

            //System.out.println("Player 1 wins: " + playground.player1Win + " Player 2 wins: " + playground.player2Win + " ties: " + playground.tie);
        }
        System.out.println("run time = " + (System.currentTimeMillis() - startTime) / 1000);
    }

    void play(){
        state = new int[BOARD_SIZE];
        int action;
        int movesRemaining = BOARD_SIZE;
        int currentPlayer;

        if (rand.nextBoolean()) currentPlayer = 1;
        else currentPlayer = 2;

        while (true){
            if (currentPlayer == 1) action = player1.move(state);
            else action = player2.move(state);
            state[action] = currentPlayer;
            --movesRemaining;

            if (displayBoard) showGUI(state);

            if (DetectWin_2.detectWin(state, BOARD_WIDTH, WIN_REQUIRE, 1)){
                ++player1Win;
                break;
            }
            else if (DetectWin_2.detectWin(state, BOARD_WIDTH, WIN_REQUIRE, 2)){
                ++player2Win;
                break;
            }
            else if (movesRemaining == 0){
                ++tie;
                break;
            }

            if (currentPlayer == 1) currentPlayer = 2;
            else currentPlayer = 1;
        }

    }

    void showGUI(int [] state){
        try {
            ((BoardPanel) gui.getContentPane().getComponent(0)).setGameState(state);
            gui.repaint();
            Thread.sleep(300);
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// state: what the board looks like, in a 1D array. 1 and 2 for players, 0 for empty
abstract class AI {
    java.util.Random rand = new java.util.Random();
    abstract public int move(int [] state);
    int randomMove(int [] state){
        ArrayList<Integer> moveList = new ArrayList<Integer>();
        for (int i = 0; i < state.length; i++)
            if (state[i] == 0)
                moveList.add(i);
        return moveList.get(rand.nextInt(moveList.size()));
    }
}

class Random extends AI{
    public int move(int [] state){
        return randomMove(state);
    }
}

class TableBased2_Conway extends AI{
    private Map<String, HashMap<Integer, Double>> qMap;
    private String brainFileName = "qMap9_3p2.txt";

    TableBased2_Conway(){
        qMap = QTableDAO.load(brainFileName);
    }

    public int move(int [] state){
        String stateKey = stateToString(state);
        if (qMap.get(stateKey) == null) return randomMove(state);
       //else return getMaxQValueAction(stateKey);
       else return getMinQValueAction(stateKey);
    }

    int getMaxQValueAction(String stateKey) {
        double maxQValue = -Double.MAX_VALUE;
        int maxQValueAction = -1;
        Map<Integer, Double> vals = qMap.get(stateKey);

        for (Map.Entry<Integer, Double> entry : vals.entrySet()) {
            if (entry.getValue() > maxQValue) {
                maxQValue = entry.getValue();
                maxQValueAction = entry.getKey();
            }
        }
        return maxQValueAction;
    }
    int getMinQValueAction(String stateKey){
        double minQValue = Double.MAX_VALUE;
        int minQValueAction = -1;
        Map<Integer, Double> vals = qMap.get(stateKey);

        for (Map.Entry<Integer, Double> entry : vals.entrySet()) {
            if (entry.getValue() < minQValue) {
                minQValue = entry.getValue();
                minQValueAction = entry.getKey();
            }
        }
        return minQValueAction;
    }
    String stateToString(int [] state){
        StringBuilder sb = new StringBuilder();
        for (int i: state) sb.append(Integer.toString(i));
        return sb.toString();
    }

//    GomokuAI_2 gomokuAI_2 = new GomokuAI_2("playVs");
//
//    TableBased2_Conway(){
//        gomokuAI_2.qMap = QTableDAO.load(brainFileName);
//    }
//
//    public int move(int [] state){
//        return gomokuAI_2.chooseAction(gomokuAI_2.makeStateKey(state, 1), 1, state);
//    }
}

class NN_ConwayP1 extends AI{
    //GomokuAI_NN gomokuAI_NN = new GomokuAI_NN();
    private String fileName = "network_9_3p1.eg";
    BasicNetwork network;

    NN_ConwayP1(){
        network = (BasicNetwork)loadObject(new File(fileName));
    }

    public int move(int [] state){
        return getMaxQValueAction(getNNOutputs(stateToDoubleArray(state)), state);
        //return getMinQValueAction(getNNOutputs(stateToDoubleArray(state)), state);
    }

    double [] stateToDoubleArray(int [] state){
        double [] state_double = new double[state.length];
        for (int i = 0; i < state_double.length; ++i) state_double[i] = state[i];
        return state_double;
    }
    double [] getNNOutputs (double [] stateKey){
        return network.compute(new BasicMLData(stateKey)).getData();
    }
    int getMaxQValueAction(double [] nnOutputs, int [] state){
        double maxQValue = Double.NEGATIVE_INFINITY;
        int action = 0;

        for (int i = 0; i < nnOutputs.length; ++i){
            if (nnOutputs[i] > maxQValue && state[i] == 0){
                maxQValue = nnOutputs[i];
                action = i;
            }
        }

        return action;
    }
    int getMinQValueAction(double [] nnOutputs, int [] state){
        double minQValue = Double.POSITIVE_INFINITY;
        int action = 0;

        for (int i = 0; i < nnOutputs.length; ++i){
            if (nnOutputs[i] < minQValue && state[i] == 0){
                minQValue = nnOutputs[i];
                action = i;
            }
        }

        return action;
    }
}

class NN_ConwayP2 extends AI{
    //GomokuAI_NN gomokuAI_NN = new GomokuAI_NN();
    private String fileName = "network_9_3p2.eg";
    BasicNetwork network;

    NN_ConwayP2(){
        network = (BasicNetwork)loadObject(new File(fileName));
    }

    public int move(int [] state){
        //return getMaxQValueAction(getNNOutputs(stateToDoubleArray(state)), state);
        return getMinQValueAction(getNNOutputs(stateToDoubleArray(state)), state);
    }

    double [] stateToDoubleArray(int [] state){
        double [] state_double = new double[state.length];
        for (int i = 0; i < state_double.length; ++i) state_double[i] = state[i];
        return state_double;
    }
    double [] getNNOutputs (double [] stateKey){
        return network.compute(new BasicMLData(stateKey)).getData();
    }
    int getMaxQValueAction(double [] nnOutputs, int [] state){
        double maxQValue = Double.NEGATIVE_INFINITY;
        int action = 0;

        for (int i = 0; i < nnOutputs.length; ++i){
            if (nnOutputs[i] > maxQValue && state[i] == 0){
                maxQValue = nnOutputs[i];
                action = i;
            }
        }

        return action;
    }
    int getMinQValueAction(double [] nnOutputs, int [] state){
        double minQValue = Double.POSITIVE_INFINITY;
        int action = 0;

        for (int i = 0; i < nnOutputs.length; ++i){
            if (nnOutputs[i] < minQValue && state[i] == 0){
                minQValue = nnOutputs[i];
                action = i;
            }
        }

        return action;
    }
}

class MinMax_smartAgent extends AI {
    public int move (int [] state){
        return new SmartAgent(15,5).move(state);
    }
}

class ConwayAI_V2_QTable extends AI {
    private int ourPlayerNum;
    private Map<String, Double> qMap;

    ConwayAI_V2_QTable(int ourPlayerNum){
        this.ourPlayerNum = ourPlayerNum;
        qMap = QMapIO.load("QTable_AI_V2_brain.txt");
    }

    public int move(int [] state){
        if (ourPlayerNum == 1) return getMaxQValueAction(state, 1);
        else return getMaxQValueAction(state, 2);
    }

    int getMaxQValueAction(int [] state, int currentPlayer){
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
    int getMinQValueAction(int [] state, int currentPlayer){
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

    String makeStateKey(int [] state){
        StringBuilder sb = new StringBuilder();
        for (int i: PatternDetect.detect(state, 1)) sb.append(Integer.toString(i));
        for (int i: PatternDetect.detect(state, 2)) sb.append(Integer.toString(i));
        return sb.toString();
    }

    double evalState(String stateKey){
        //if state has not happened before
        if (qMap.get(stateKey) == null)
            qMap.put(stateKey, (rand.nextDouble() * 0.3) - 0.15); //-0.15 ~ +0.15

        return qMap.get(stateKey);
    }

    int [] getValidActions(int [] state){
        int [][] state2D = DetectWin_2.convert1Dto2D(state, Conway_V2_base.BOARD_WIDTH);
        int [] validActions;
        //boolean empty = true;
        int upperBound = 0;
        int lowerBound = state2D.length;
        int leftBound = 0;
        int rightBound = state2D.length;

        if (PatternDetect.checkEmpty(state)) {
            validActions = new int[1];
            validActions[0] = state.length / 2;
            return validActions;
        }

        else {
            boolean found = false;

            //0, 1, 2, 3...
            //int[] validActionNums = new int[Conway_V2_base.BOARD_WIDTH * Conway_V2_base.BOARD_WIDTH];
            //for (int i = 0; i < validActionNums.length; ++i) validActionNums[i] = i;
            //int[][] validActionNums2D = DetectWin_2.convert1Dto2D(validActionNums, Conway_V2_base.BOARD_WIDTH);

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

            //validActions = new int[(lowerBound - upperBound + 1) * (rightBound - leftBound + 1)];

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

//            System.out.println("upper " + upperBound);
//            System.out.println("lower " + lowerBound);
//            System.out.println("left " + leftBound);
//            System.out.println("right " + rightBound);
            return validActions;
        }
    }
}