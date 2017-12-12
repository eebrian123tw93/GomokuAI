package com.JavaBeginnerToPro.GomoWhiz;

import com.JavaBeginnerToPro.GomoWhiz.minMax.SmartAgent;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.encog.persist.EncogDirectoryPersistence.loadObject;


public class Playground {
    AI player1, player2;
    public static final int GAMES_TO_PLAY = 1000000;
    public static final int BOARD_WIDTH = 3;
    public static final int BOARD_SIZE = BOARD_WIDTH * BOARD_WIDTH;
    public static final int WIN_REQUIRE = 3;
    public int player1Win = 0;
    public int player2Win = 0;
    public int tie = 0;
    private int [] state;
    private Random rand;
    boolean displayBoard = true;
    GomokuGUI gui;


    Playground(){
        player2 = new TableBased2_Conway();
        player1 = new randomMoves();
        rand = new Random();
        state = new int [BOARD_SIZE];
        if (displayBoard) gui = new GomokuGUI(state);
    }

    public static void main(String[] args) {
        int gamesPlayed = 0;
        Playground playground = new Playground();

        while (gamesPlayed < GAMES_TO_PLAY){
            playground.play();
            ++gamesPlayed;
            if (gamesPlayed % 1000 == 0) {
                System.out.println("Player 1 wins: " + playground.player1Win + " Player 2 wins: " + playground.player2Win + " ties: " + playground.tie);
//                playground.player1Win = 0;
//                playground.player2Win = 0;
//                playground.tie = 0;
            }




        }
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


            if (displayBoard) {
                try {
                    ((BoardPanel) gui.getContentPane().getComponent(0)).setGameState(state);
                    gui.repaint();
                    Thread.sleep(700);
                }

                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }



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
}

// state: what the board looks like, in a 1D array. 1 and 2 for players, 0 for empty
abstract class AI {
    Random rand = new Random();
    abstract public int move(int [] state);
    int randomMove(int [] state){
        ArrayList<Integer> moveList = new ArrayList<Integer>();
        for (int i = 0; i < state.length; i++)
            if (state[i] == 0)
                moveList.add(i);
        return moveList.get(rand.nextInt(moveList.size()));
    }
}

class randomMoves extends AI{
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

class NN_Conway extends AI{
    //GomokuAI_NN gomokuAI_NN = new GomokuAI_NN();
    private String fileName = "network_9_3p1.eg";
    BasicNetwork network;

    NN_Conway(){
        network = (BasicNetwork)loadObject(new File(fileName));
    }

    public int move(int [] state){
        return getMaxQValueAction(getNNOutputs(stateToDoubleArray(state)), state);
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
}

//class minMax_smartAgent extends AI {
////    public int move (int [] state){
////        return new SmartAgent(3,3).move(state);
////    }
////}