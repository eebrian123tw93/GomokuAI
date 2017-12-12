package com.JavaBeginnerToPro.GomoWhiz;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import static org.encog.persist.EncogDirectoryPersistence.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
GomokuAI_NN:
    training methods: 1. train the network every step (bad)
                      2. train the network every X amount of games
                      3. randomly record games into a replay memory, then everytime
                      the replay memory fills up to a certain amount, train the network with it


    NN input is gameBoard state + current player
*/

/*
* if this state is new (not recorded in qMap), retrieve q values from NN
* get min q, max q and actions are all from qMaps
*
* the NN reads a double array
*
* */


public class GomokuAI_NN extends GomokuAI_2{

    static BasicNetwork network1;
    static BasicNetwork network2;

   // private static final int
    private static final int recordAmountBeforeTrain = 100000; //the amount of games to record before training it in NN
    private static final int trainingEpochs = 1;
    private static final int hiddenLayerCount = 10;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        GomokuAI_NN gomokuAI_NN = new GomokuAI_NN();
        gomokuAI_NN.trainAndPlay();

        saveObject(new File("network_9_3p1.eg"), network1); //save network1

        System.out.println("run time = " + (System.currentTimeMillis() - startTime) / 1000);
        Encog.getInstance().shutdown();
    }

    GomokuAI_NN(){
       createNN();
       GAMES_TO_TRAIN = 1000000;
       GAMES_TO_PLAY = 10000;
    }

    void createNN(){
        // create a neural network
        //input is GAMEBOARD_SIZE + 1 (GAMEBOARD_SIZE + current player)
        //output is GAMEBOARD_SIZE's Q values

//        network1 = new BasicNetwork();
//        for (int i = 0; i < )


        network1 = new BasicNetwork();
        network1.addLayer(new BasicLayer(null,true, GAMEBOARD_SIZE));

        for (int i = 0; i < hiddenLayerCount; ++i){
            network1.addLayer(new BasicLayer(new ActivationSigmoid(), true, GAMEBOARD_SIZE));
        }

        network1.addLayer(new BasicLayer(new ActivationSigmoid(),false, GAMEBOARD_SIZE));
        network1.getStructure().finalizeStructure();
        network1.reset(); //randomize weights

        network2 = new BasicNetwork();
        network2.addLayer(new BasicLayer(null,true, GAMEBOARD_SIZE));

        for (int i = 0; i < hiddenLayerCount; ++i){
            network2.addLayer(new BasicLayer(new ActivationSigmoid(), true, GAMEBOARD_SIZE));
        }

        network2.addLayer(new BasicLayer(new ActivationSigmoid(),false, GAMEBOARD_SIZE));
        network2.getStructure().finalizeStructure();
        network2.reset(); //randomize weights


        //        network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 2));
//        network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 3));
//        network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 4));
//        network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 5));
//        network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 6));
        //network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 7));
        //network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 8));
        //network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 9));
        //network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 8));
        //network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 7));
//        network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 6));
//        network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 5));
//        network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 4));
//        network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 3));
//        network1.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 2));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 2));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 3));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 4));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 5));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 6));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 7));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 8));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 9));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 8));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 7));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 6));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 5));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 4));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 3));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, GAMEBOARD_SIZE * 2));
    }

    void trainAndPlay(){
        while (gamesPlayed < GAMES_TO_TRAIN + GAMES_TO_PLAY){
            //if played more than GAMES_TO_TRAIN, switch to play mode
            if (gamesPlayed >= GAMES_TO_TRAIN && learningMode == true){
                learningMode = false;
                isPlaying = true;
                System.out.println("Switching to playing mode");
            }



            playOneGame();
            ++gamesPlayed;

            if (gamesPlayed % recordAmountBeforeTrain == 0){
                //System.out.println(qMap.size());
                //System.out.println(qMap2.size());
                trainNeuralNetworks();
                clearQMaps();
                //System.out.println(qMap.size());
                //System.out.println(qMap2.size());
                //System.out.println(network1.dumpWeights());
                //System.out.println(network2.dumpWeights());
            }

            if (gamesPlayed % 1000 == 0) {
                System.out.println("wins: " + wins + " losses: " + losses + " ties: " + ties);
                wins=losses=ties=0;
            }

            //System.out.println(gamesPlayed);
        }
    }

    String makeStateKey(int[] state, int currentPlayer) {
        StringBuilder sb = new StringBuilder();
        for (int i: state) sb.append(Integer.toString(i));
        String key = sb.toString();

        //if this game state isn't recorded in the qMaps
        if (currentPlayer == 1){
            if (qMap.get(key) == null) qMap.put(key, createActionQValueMap(stringKeyToDoubleKey(key), 1));
        }
        else if (currentPlayer == 2) {
            if (qMap2.get(key) == null) qMap2.put(key, createActionQValueMap(stringKeyToDoubleKey(key), 2));
        }

        return key;
    }
    HashMap<Integer, Double> createActionQValueMap(double [] stateKey, int player) {
        double [] nnOutputs;
        if (player == 1) nnOutputs = network1.compute(new BasicMLData(stateKey)).getData();
        else nnOutputs = network2.compute(new BasicMLData(stateKey)).getData();
        HashMap<Integer, Double> vals = new HashMap<>();

        for (int i = 0; i < stateKey.length; i++) {
            if (stateKey[i] == 0) //if the spot board is empty (will only make actions for empty places on the board)
                vals.put(i, nnOutputs[i]); // action, Q value generated by NN
        }
        return vals;
    }

//    int chooseAction(String stateKey, int currentPlayer, int[] currentState){
//        if (learningMode == false) {
//            //player 1 looks for the maximum Q values (because it gets a positive reward when winning)
//            if (currentPlayer == 1) return getMaxQValueAction(stateKey, currentPlayer);
//                //player 2 looks for the minimum Q values (because it gets a negative reward when winning)
//            else return getMinQValueAction(stateKey, currentPlayer);
//            //else if (currentPlayer == 2) return getMaxQValueAction(stateKey, currentPlayer);
//        }
//
//        else return randomMove(currentState);
//    }

    double [] stringKeyToDoubleKey(String stateKey) {
        double [] doubleStateKey = new double[stateKey.length()];
        for (int i = 0; i < doubleStateKey.length; ++i)
            doubleStateKey[i] = Double.parseDouble(String.valueOf(stateKey.charAt(i)));
        return  doubleStateKey;
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
}
//    int chooseAction(String stateKey, int currentPlayer, int [] currentState, int [] validActions) {
//        if (learningMode == false) {
//            //player 1 looks for the maximum Q values (because it gets a positive reward when winning)
//            if (currentPlayer == 1) return getMaxQValueAction(stateKey, validActions);
//                //player 2 looks for the minimum Q values (because it gets a negative reward when winning)
//            else return getMinQValueAction(stateKey, validActions);
//        }
//
//        else {
//            if (rand.nextDouble() < 0.5) {
//                if (currentPlayer == 1)
//                    return getMaxQValueAction(stateKey, validActions);
//                else
//                    return getMinQValueAction(stateKey, validActions);
//            }
//            // 50% time choose random action
//            else {
//                return randomMove(currentState);
//            }
//        }
//    }

//  void updateQValues(String currentStateKey, int currentPlayer, int action, String nextStateKey, double reward, int[] nextState) {
//        //MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);
//        double expectedQValue = 0; //the expected Q value
//        if (gameEnded) expectedQValue = reward;
//        else {
//            // assumes that the opponent always makes the best move
//            if (currentPlayer == 1) expectedQValue = reward + (GAMMA * getMinQValue(nextStateKey, nextState));
//            else expectedQValue = reward + (GAMMA * getMaxQValue(nextStateKey, nextState));
//        }
//
//        double qValue;
//        if (currentPlayer == 1) qValue = qTable.get(currentStateKey).
//
////        double q = getQValue(currentStateKey, action);
////        q += LEARNING_RATE * (expectedQValue - q);
//
////        double[] qValues;
////        if (currentPlayer == 1) qValues = network1.compute(new BasicMLData(currentStateKey)).getData();
////        else qValues = network2.compute(new BasicMLData(currentStateKey)).getData();
//
////        if (qValues[action] != expectedQValue) {
////            qValues[action] += LEARNING_RATE * (expectedQValue - qValues[action]);
////            trainNN(currentStateKey, qValues, currentPlayer);
////        }
//    }

//    //getMinQValue only used by player 2
//    double getMinQValue(String stateKey, int [] state){
//        double [] nnOutputs = network2.compute(new BasicMLData(stringKeyToDoubleKey(stateKey))).getData();
//        int [] validActions = getValidActions(state);
//        double minQValue = -Double.MAX_VALUE;
//        for (int i = 0; i < validActions.length; ++i){
//            if (nnOutputs[validActions[i]] < minQValue) minQValue = nnOutputs[validActions[i]];
//        }
//        return  minQValue;
//    }
//
//    //getMaxQValue only used by player 1
//    double getMaxQValue(String stateKey, int [] state){
//        double [] nnOutputs = network1.compute(new BasicMLData(stringKeyToDoubleKey(stateKey))).getData();
//        int [] validActions = getValidActions(state);
//        double maxQValue = Double.MAX_VALUE;
//        for (int i = 0; i < validActions.length; ++i){
//            if (nnOutputs[validActions[i]] > maxQValue) maxQValue = nnOutputs[validActions[i]];
//        }
//        return  maxQValue;
//    }
//    double getQValue(String stateKey, int action, ){
//        //return network.compute(new BasicMLData(stateKey)).getData(action);
//        if (currentPlayer == 1) return qMap.get(stateKey).get(action);
//        else return qMap2.get(stateKey).get(action);
//    }

    void trainNeuralNetworks(){
        //System.out.println("train NN");
        trainNN(1);
        trainNN(2);
        //System.out.println("training finished");
    }

    void clearQMaps(){
        qMap.clear();
        qMap2.clear();
    }

    //the NN input is what the game board looks like. the output is the q values for each action,
    //the NN has GAMEBOARD_SIZE inputs and outputs
    void trainNN(int player){
        double [][] stateKeys; //stateKeys[amount of stateKeys in qMap][the stateKey array]
        double [][] qValues; //actionsAndQValues[amount of stateKeys in qMap][array of q values]
        int index = 0;
        HashMap<Integer, Double> vals;

        MLDataSet trainingSet;
        ResilientPropagation train;
        int epoch = 0;

        //train player 1 NN
        if (player == 1) {
            stateKeys = new double[qMap.size()][GAMEBOARD_SIZE];
            qValues = new double[qMap.size()][GAMEBOARD_SIZE];

            //store data from qMap into double arrays
            for (Map.Entry<String, HashMap<Integer, Double>> entry : qMap.entrySet()) {
                stateKeys[index] = stringKeyToDoubleKey(entry.getKey());
                vals = qMap.get(entry.getKey());
                for (int i = 0; i < GAMEBOARD_SIZE; ++i) {
                    if (vals.get(i) != null) qValues[index][i] = vals.get(i);
                    //else qValues[index][i] = 0;
                }
                ++index;
            }

            trainingSet = new BasicMLDataSet(stateKeys, qValues);
            train = new ResilientPropagation(network1, trainingSet);


        }

        //train player 2 NN
        else {
            stateKeys = new double[qMap2.size()][GAMEBOARD_SIZE];
            qValues = new double[qMap2.size()][GAMEBOARD_SIZE];

            //store data from qMap into double arrays
            for (Map.Entry<String, HashMap<Integer, Double>> entry : qMap2.entrySet()) {
                stateKeys[index] = stringKeyToDoubleKey(entry.getKey());
                vals = qMap2.get(entry.getKey());
                for (int i = 0; i < GAMEBOARD_SIZE; ++i) {
                    if (vals.get(i) != null) qValues[index][i] = vals.get(i);
                    //else qValues[index][i] = 0;
                }
                ++index;
            }

            trainingSet = new BasicMLDataSet(stateKeys, qValues);
            train = new ResilientPropagation(network2, trainingSet);
        }

        train.addStrategy(new Greedy());

        while (epoch < trainingEpochs) {
            train.iteration();
            ++epoch;
        }
        train.finishTraining();
    }

//    //train the NN with a single state key and qValues
//    void trainNN(double [] stateKey, double [] qValues, int player){
//        double [][] trainingInput = {stateKey};
//        double [][] trainingIdeal = {qValues};
//        MLDataSet trainingSet = new BasicMLDataSet(trainingInput, trainingIdeal);
//
//        ResilientPropagation train;
//
//        if (player == 1) train = new ResilientPropagation(network1, trainingSet);
//        else train = new ResilientPropagation(network2, trainingSet);
//
//        //int epoch = 1;
//        do {
//            train.iteration();
//            //System.out.println("Epoch #" + epoch + " Error:" + train.getError());
//            //++epoch;
//        } while(train.getError() > 0.01);
//        train.finishTraining();
//        //System.out.println("-----finished training-----");
//    }
//
//    //train the NN with multiple state keys and qValues
//    void trainNN(double [][] stateKeys, double [][] qValues, int player){
//        double [][] trainingInput = stateKeys.clone();
//        double [][] trainingIdeal = qValues.clone();
//        MLDataSet trainingSet = new BasicMLDataSet(trainingInput, trainingIdeal);
//        ResilientPropagation train;
//        if (player == 1) train = new ResilientPropagation(network1, trainingSet);
//        else train = new ResilientPropagation(network2, trainingSet);
//
//        //int epoch = 1;
//        do {
//            train.iteration();
//            //System.out.println("Epoch #" + epoch + " Error:" + train.getError());
//            //++epoch;
//        } while(train.getError() > 0.01);
//        train.finishTraining();
//        //System.out.println("-----finished training-----");
//    }

}
