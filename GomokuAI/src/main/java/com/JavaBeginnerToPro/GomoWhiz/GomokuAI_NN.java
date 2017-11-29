//package com.JavaBeginnerToPro.GomoWhiz;
//
//import org.encog.Encog;
//import org.encog.engine.network.activation.ActivationReLU;
//import org.encog.engine.network.activation.ActivationSigmoid;
//import org.encog.ml.data.MLData;
//import org.encog.ml.data.MLDataPair;
//import org.encog.ml.data.MLDataSet;
//import org.encog.ml.data.basic.BasicMLData;
//import org.encog.ml.data.basic.BasicMLDataSet;
//import org.encog.neural.networks.BasicNetwork;
//import org.encog.neural.networks.layers.BasicLayer;
//import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
///*
//GomokuAI_NN:
//    training methods: 1. train the network every step (bad)
//                      2. train the network every X amount of games
//                      3. randomly record games into a replay memory, then everytime
//                      the replay memory fills up to a certain amount, train the network with it
//
//
//    NN input is gameBoard state + current player
//*/
//
///*
//* things todo: make stateKey and state the same thing, tweak NN
//*
//*
//* */
//
//
//public class GomokuAI_NN extends GomokuAI_2{
//    //Map <state, q values>
//    Map <ArrayList<Integer>, ArrayList<Double>> qTable = new HashMap<>();
//    Map <ArrayList<Integer>, ArrayList<Double>> qTable2 = new HashMap<>();
//
//    //MLDataSet trainingSet;
//    BasicNetwork network1;
//    BasicNetwork network2;
//
//    public static void main(String[] args) {
//        long startTime = System.currentTimeMillis();
//        GomokuAI_NN gomokuAI_NN = new GomokuAI_NN();
//        gomokuAI_NN.train();
//        gomokuAI_NN.play();
//        System.out.println("run time = " + (System.currentTimeMillis() - startTime) / 1000);
//    }
//
//    GomokuAI_NN(){
//       createNN();
//    }
//
//    void createNN(){
//        // create a neural network
//        //input is GAMEBOARD_SIZE + 1 (GAMEBOARD_SIZE + current player)
//        //output is GAMEBOARD_SIZE's Q values
//        network1 = new BasicNetwork();
//        network1.addLayer(new BasicLayer(null,true, GAMEBOARD_SIZE));
//        network1.addLayer(new BasicLayer(new ActivationReLU(),true, 225));
//        network1.addLayer(new BasicLayer(new ActivationReLU(),true, 100));
//        network1.addLayer(new BasicLayer(new ActivationReLU(), true, 12));
//        network1.addLayer(new BasicLayer(new ActivationSigmoid(),false, GAMEBOARD_SIZE));
//        network1.getStructure().finalizeStructure();
//        network1.reset(); //randomize weights
//
//        network2 = new BasicNetwork();
//        network2.addLayer(new BasicLayer(null,true, GAMEBOARD_SIZE ));
//        network2.addLayer(new BasicLayer(new ActivationReLU(),true, 225));
//        network2.addLayer(new BasicLayer(new ActivationReLU(),true, 100));
//        network2.addLayer(new BasicLayer(new ActivationReLU(), true, 12));
//        network2.addLayer(new BasicLayer(new ActivationSigmoid(),false, GAMEBOARD_SIZE));
//        network2.getStructure().finalizeStructure();
//        network2.reset(); //randomize weights
//
//    }
//
//
//    void playOneGame(){
//        for (int i = 0; i < GAMEBOARD_SIZE; ++i) currentState[i] = 0; //reset current state to all 0
//        int[] nextState; //will be used between turns
//        int currentPlayer;
//        int nextPlayer;
//        gameEnded = false;
//        int movesRemaining = GAMEBOARD_SIZE;
//        double reward;
//        int [] validActions;
//        //String currentStateKey;
//        //String nextStateKey;
//        double [] currentStateKey;
//        double [] nextStateKey;
//
//        //choose first player
//        if (rand.nextBoolean()) currentPlayer = 1;
//        else currentPlayer = 2;
//
//        while (!gameEnded) {
//            if (currentPlayer == 1) nextPlayer = 2;
//            else nextPlayer = 1;
//
//            currentStateKey = makeStateKey_NN(currentState, currentPlayer);
//            nextState = currentState.clone();
//
//            validActions = getValidActions(currentState);
//            int action = chooseAction(currentStateKey, currentPlayer, currentState, validActions);
//
//            nextState[action] = currentPlayer; //place the piece
//            --movesRemaining;
//            reward = evalReward(nextState);
//            if (reward == 1 || reward == -1 || movesRemaining <= 0) {
//                gameEnded = true;
//                if (reward == 1) {
//                    ++wins; //player 1 win
//                    //System.out.println("player 1 win");
//                }
//                else if (reward == -1){
//                    ++losses; //player 2 win
//                    //System.out.println("player 2 win");
//                }
//                else if (movesRemaining <= 0){
//                    ++ties;//tie
//                    //System.out.println("tie");
//                }
//            }
//
//            nextStateKey = makeStateKey_NN(nextState, nextPlayer);
//            updateQValues(currentStateKey, currentPlayer, action, nextStateKey, reward, nextState);
//            currentState = nextState.clone(); //change currentState to the state after the action
//
//            //swap players
//            if (currentPlayer == 1) currentPlayer = 2;
//            else currentPlayer = 1;
//
//
//            if (displayBoard && isPlaying) {
//                try {
//                    ((BoardPanel) gui.getContentPane().getComponent(0)).setGameState(currentState);
//                    gui.repaint();
//                    Thread.sleep(700);
//                }
//
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//        }
//    }
//
//    double [] makeStateKey_NN(int [] state){
////        double [] stateKey = new double[state.length + 1];
////        for (int i = 0; i < stateKey.length; ++i) {
////            if (i == stateKey.length - 1) stateKey[i] = currentPlayer;
////            else stateKey[i] = state[i];
////        }
//        double [] stateKey = new double[state.length];
//        for (int i = 0; i < stateKey.length; ++i) stateKey[i] = state[i];
//        return stateKey;
//    }
//
//    //returns all valid actions according to the state
//    int [] getValidActions(int [] state){
//        ArrayList<Integer> validActionsList = new ArrayList<Integer>();
//        for (int i = 0; i < state.length; ++i){
//            if (state[i] == 0) validActionsList.add(i);
//        }
//
//        int [] validActions = new int[validActionsList.size()];
//        for (int i = 0; i < validActions.length; ++i) validActions[i] = validActionsList.get(i);
//
//        return validActions;
//        //Integer [] validActions = validActionsList.toArray(new Integer[validActionsList.size()]);
//        //return validActions;
////        double [] validActionsDouble = new double[validActions.size()];
////
////        for(int i = 0; i < validActionsDouble.length; ++i){
////            validActionsDouble[i] = validActions.get(i);
////        }
////        return validActionsDouble;
//    }
//    int chooseAction(double [] stateKey, int currentPlayer, int [] currentState, int [] validActions) {
//        if (learingMode == false) {
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
//
//        //boolean somethingwentwrong = true;
//        //else if (currentPlayer == -1) return getMaxQValueAction(stateKey);
//        //return -1; //just in case. should cause an error
//    }
//    int getMaxQValueAction(double [] stateKey, int [] validActions) {
//        double maxQValue = -Double.MAX_VALUE;
//        int maxQValueAction = -1;
//        double [] nnOutputs = network1.compute(new BasicMLData(stateKey)).getData();
//
//        for (int i = 0; i < validActions.length; ++i) {
//            if (nnOutputs[validActions[i]] > maxQValue) {
//                maxQValue = nnOutputs[validActions[i]];
//                maxQValueAction = validActions[i];
//            }
//        }
////        for (int i = 0; i < validActions.length; ++i){
////            stateKey_action[0] = combineStateKeyWithAction(stateKey, validActions[i]);
////            //get output from NN
////            nnOutput = network.compute(new BasicMLData(stateKey_action[0])).getData();
////            if (nnOutput[0] > maxQValue){
////                maxQValue = nnOutput[0];
////                maxQValueAction = validActions[i];
////            }
//        return maxQValueAction;
//    }
//    int getMinQValueAction(double [] stateKey, int [] validActions) {
//        double minQValue = Double.MAX_VALUE;
//        int minQValueAction = -1;
//        double [] nnOutputs = network2.compute(new BasicMLData(stateKey)).getData();
//
//        for (int i = 0; i < validActions.length; ++i){
//            if (nnOutputs[validActions[i]] < minQValue){
//                minQValue = nnOutputs[validActions[i]];
//                minQValueAction = validActions[i];
//            }
//        }
////        for (int i = 0; i < validActions.length; ++i){
////            stateKey_action[0] = combineStateKeyWithAction(stateKey, validActions[i]);
////            //get output from NN
////            nnOutput = network.compute(new BasicMLData(stateKey_action[0])).getData();
////            if (nnOutput[0] < minQValue){
////                minQValue = nnOutput[0];
////                minQValueAction = validActions[i];
////            }
////        }
//        return minQValueAction;
//    }
////    double [] combineStateKeyWithAction(double [] stateKey, double action){
////        double [] stateKey_action = new double[stateKey.length + 1];
////        for (int i = 0; i < stateKey_action.length; ++i){
////            if (i == stateKey_action.length - 1) stateKey_action[i] = action;
////            else stateKey_action[i] = stateKey[i];
////        }
////        return stateKey_action;
////    }
//
//    void updateQValues(double [] currentStateKey, int currentPlayer, int action, double [] nextStateKey, double reward, int[] nextState) {
//        //MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);
//        double expectedQValue = 0; //the expected Q value
//        if (gameEnded) expectedQValue = reward;
//        else {
//            // assumes that the opponent always makes the best move
//            if (currentPlayer == 1) expectedQValue = reward + (GAMMA * getMinQValue(nextStateKey, nextState));
//            else expectedQValue = reward + (GAMMA * getMaxQValue(nextStateKey, nextState));
//        }
//
////        double q = getQValue(currentStateKey, action);
////        q += LEARNING_RATE * (expectedQValue - q);
//        double[] qValues = network.compute(new BasicMLData(currentStateKey)).getData();
//        if (qValues[action] != expectedQValue) {
//            qValues[action] += LEARNING_RATE * (expectedQValue - qValues[action]);
//            trainNN(currentStateKey, qValues);
//        }
//    }
//    double getMinQValue(double [] stateKey, int [] state){
//        double [] nnOutputs = network.compute(new BasicMLData(stateKey)).getData();
//        int [] validActions = getValidActions(state);
//        double minQValue = -Double.MAX_VALUE;
//        for (int i = 0; i < validActions.length; ++i){
//            if (nnOutputs[validActions[i]] < minQValue) minQValue = nnOutputs[validActions[i]];
//        }
//        return  minQValue;
//    }
//    double getMaxQValue(double [] stateKey, int [] state){
//        double [] nnOutputs = network.compute(new BasicMLData(stateKey)).getData();
//        int [] validActions = getValidActions(state);
//        double maxQValue = Double.MAX_VALUE;
//        for (int i = 0; i < validActions.length; ++i){
//            if (nnOutputs[validActions[i]] > maxQValue) maxQValue = nnOutputs[validActions[i]];
//        }
//        return  maxQValue;
//    }
//    double getQValue(double [] stateKey, int action){
//        return network.compute(new BasicMLData(stateKey)).getData(action);
//    }
//
//    //train the NN with a single state key and qValues
//    void trainNN(double [] stateKey, double [] qValues){
//        double [][] trainingInput = {stateKey};
//        double [][] trainingIdeal = {qValues};
//        MLDataSet trainingSet = new BasicMLDataSet(trainingInput, trainingIdeal);
//        ResilientPropagation train = new ResilientPropagation(network, trainingSet);
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
//    void trainNN(double [][] stateKeys, double [][] qValues){
//        double [][] trainingInput = stateKeys.clone();
//        double [][] trainingIdeal = qValues.clone();
//        MLDataSet trainingSet = new BasicMLDataSet(trainingInput, trainingIdeal);
//        ResilientPropagation train = new ResilientPropagation(network, trainingSet);
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
//}
