package com.JavaBeginnerToPro.GomoWhiz;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
GomokuAI_NN:
    store training data in qTable
    every 100 games, train the NN with qTable
*/

public class GomokuAI_NN extends GomokuAI{
    //private static double [][] stateKeyPlusAction;
    //private static double [][] qValue;
    //Map<double[][] stateKeyPlusAction, double [][] qValue> qTable_NN = new
    Map <double [], Double> qTable = new HashMap<double[], Double>();

    MLDataSet trainingSet;
    BasicNetwork network;

    GomokuAI_NN(){

    }

    void createNN(){
        // create a neural network, without using a factory
        //input is GAMEBOARD_SIZE + 2 (GAMEBOARD_SIZE + current player + action)
        //output is 1 Q value
        network = new BasicNetwork();
        network.addLayer(new BasicLayer(null,true, GAMEBOARD_SIZE + 2));
        network.addLayer(new BasicLayer(new ActivationReLU(),true, 5));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
        network.getStructure().finalizeStructure();
        network.reset();
    }


    void playOneGame(){
        for (int i = 0; i < GAMEBOARD_SIZE; ++i) currentState[i] = 0; //reset current state to all 0
        int[] nextState; //will be used between turns
        int currentPlayer;
        int nextPlayer;
        gameEnded = false;
        int movesRemaining = GAMEBOARD_SIZE;
        double reward;
        int [] validActions;
        //String currentStateKey;
        //String nextStateKey;
        double [] currentStateKey;
        double [] nextStateKey;

        //choose first player
        if (rand.nextBoolean()) currentPlayer = 1;
        else currentPlayer = -1;

        while (!gameEnded) {
            if (currentPlayer == 1) nextPlayer = -1;
            else nextPlayer = 1;

            currentStateKey = makeStateKey_NN(currentState, currentPlayer);
            nextState = currentState.clone();

            validActions = getValidActions(currentState);
            int action = chooseAction(currentStateKey, currentPlayer, currentState, validActions);

            nextState[action] = currentPlayer; //place the piece
            --movesRemaining;
            reward = evalReward(nextState);
            if (reward == 1 || reward == -1 || movesRemaining <= 0) {
                gameEnded = true;
                if (reward == 1) {
                    ++wins; //player 1 win
                    //System.out.println("player 1 win");
                }
                else if (reward == -1){
                    ++losses; //player -1 win
                    //System.out.println("player -1 win");
                }
                else if (movesRemaining <= 0){
                    ++ties;//tie
                    //System.out.println("tie");
                }
            }

            nextStateKey = makeStateKey_NN(nextState, nextPlayer);
            //updateQValues(currentStateKey, currentPlayer, action, nextStateKey, reward);
            currentState = nextState.clone(); //change currentState to the state after the action

            //swap players
            if (currentPlayer == 1) currentPlayer = -1;
            else currentPlayer = 1;


            if (displayBoard && isPlaying) {
                try {
                    ((BoardPanel) gui.getContentPane().getComponent(0)).setGameState(currentState);
                    gui.repaint();
                    Thread.sleep(700);
                }

                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    double [] makeStateKey_NN(int [] state, int currentPlayer){
        double [] stateKey = new double[state.length + 1];
        for (int i = 0; i < stateKey.length; ++i) {
            if (i == stateKey.length - 1) stateKey[i] = currentPlayer;
            else stateKey[i] = state[i];
        }
        return stateKey;
    }

    //returns all valid actions according to the state
    int [] getValidActions(int [] state){
        ArrayList<Integer> validActionsList = new ArrayList<Integer>();
        for (int i = 0; i < state.length; ++i){
            if (state[i] == 0) validActionsList.add(i);
        }

        int [] validActions = new int[validActionsList.size()];
        for (int i = 0; i < validActions.length; ++i) validActions[i] = validActionsList.get(i);

        return validActions;
        //Integer [] validActions = validActionsList.toArray(new Integer[validActionsList.size()]);
        //return validActions;
//        double [] validActionsDouble = new double[validActions.size()];
//
//        for(int i = 0; i < validActionsDouble.length; ++i){
//            validActionsDouble[i] = validActions.get(i);
//        }
//        return validActionsDouble;
    }
    int chooseAction(double [] stateKey, int currentPlayer, int [] currentState, int [] validActions) {
        if (learingMode == false) {
            //player 1 looks for the maximum Q values (because it gets a positive reward when winning)
            if (currentPlayer == 1) return getMaxQValueAction(stateKey, validActions);
                //player -1 looks for the minimum Q values (because it gets a negative reward when winning)
            else if (currentPlayer == -1) return getMinQValueAction(stateKey, validActions);
        }

        else {
            if (rand.nextDouble() < 0.5) {
                if (currentPlayer == 1)
                    return getMaxQValueAction(stateKey, validActions);
                else
                    return getMinQValueAction(stateKey, validActions);
            }
            // 50% time choose random action
            else {
                return randomMove(currentState);
            }
        }

        //else if (currentPlayer == -1) return getMaxQValueAction(stateKey);
        return -1; //just in case. should cause an error
    }
    int getMaxQValueAction(double [] stateKey, int [] validActions) {
        double [][] stateKey_action = new double[1][1];
        //pass in the stateKey + a valid action to the NN to get a single Q value. find the action with
        //the largest Q value and return it
        double maxQValue = -1.0;
        int maxQValueAction = -1;
        double [] nnOutput;

        for (int i = 0; i < validActions.length; ++i){
            stateKey_action[0] = combineStateKeyWithAction(stateKey, validActions[i]);
            //get output from NN
            nnOutput = network.compute(new BasicMLData(stateKey_action[0])).getData();
            if (nnOutput[0] > maxQValue){
                maxQValue = nnOutput[0];
                maxQValueAction = validActions[i];
            }
        }


        return maxQValueAction;
    }
    int getMinQValueAction(double [] stateKey, int [] validActions) {
        double [][] stateKey_action = new double[1][1];
        double minQValue = 1.0;
        int minQValueAction = -1;
        double [] nnOutput;

        for (int i = 0; i < validActions.length; ++i){
            stateKey_action[0] = combineStateKeyWithAction(stateKey, validActions[i]);
            //get output from NN
            nnOutput = network.compute(new BasicMLData(stateKey_action[0])).getData();
            if (nnOutput[0] < minQValue){
                minQValue = nnOutput[0];
                minQValueAction = validActions[i];
            }
        }

        return minQValueAction;
    }
    double [] combineStateKeyWithAction(double [] stateKey, double action){
        double [] stateKey_action = new double[stateKey.length + 1];
        for (int i = 0; i < stateKey_action.length; ++i){
            if (i == stateKey_action.length - 1) stateKey_action[i] = action;
            else stateKey_action[i] = stateKey[i];
        }
        return stateKey_action;
    }

}
