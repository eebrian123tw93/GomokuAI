package com.JavaBeginnerToPro.GomoWhiz.Version_1;

import com.JavaBeginnerToPro.GomoWhiz.Version_1.GomokuAI;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.error.ErrorCalculationMode;
import org.encog.mathutil.randomize.XaiverRandomizer;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.neural.error.ErrorFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.persist.EncogDirectoryPersistence;
import org.nd4j.linalg.util.ArrayUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

//import org.encog.neural.networks.training.propagation.


public class DQNBrain extends GomokuAI {
    int inputSize;
    static int memorySize = 1000;//100
    static int batchSize = 1000;//50
    static int replaceTargetIter = 10;//10
    BasicNetwork qTargetNetwork;
    BasicNetwork qEvalNetwork;
    Backpropagation backpropagation;
    private TrainingContinuation trainingState;
    Memory[] memories;
    int step;
    int memoryCount;

    public DQNBrain() {

        try {
            qEvalNetwork = loadNN("a.eg");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (qEvalNetwork == null) {
            buildNet();
        } else {
            System.out.println("load successfully");
            qTargetNetwork = loadNN("a.eg");
        }
        memories = new Memory[memorySize];

    }

    double[] reverseKey(String stateKey) {
        String item[] = stateKey.replaceAll("\\[", "").replaceAll("\\]", ",").split(",");
        double reverse[] = new double[item.length];
        for (int i = 0; i < reverse.length; i++) {
            reverse[i] = Double.parseDouble(item[i]);
        }
        return reverse;
    }

    int getMinQValueAction(int state[], double[] actions) {
        double minQValue =Double.MAX_VALUE;
        int minQValueAction = -1;
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] < minQValue && state[i] == 0) {
                minQValue = actions[i];
                minQValueAction = i;
            }
        }

        return minQValueAction;
    }

    int getMaxQValueAction(int state[], double[] actions) {
        double maxQValue = -Double.MAX_VALUE;
        int maxQValueAction = -1;
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] > maxQValue && state[i] == 0) {
                maxQValue = actions[i];
                maxQValueAction = i;
            }
        }

        return maxQValueAction;
    }

    @Override
    int getMinQValueAction(String stateKey) {
        double input[] = reverseKey(stateKey);
        MLData data = new BasicMLData(input);
        final double[] output = qEvalNetwork.compute(data).getData();
        return getMinQValueAction(Arrays.copyOf(ArrayUtil.toInts(input),input.length-1), output);
    }

    @Override
    int getMaxQValueAction(String stateKey) {
        //0,0,0,0,0,0,-1
        double input[] = reverseKey(stateKey);
        MLData data = new BasicMLData(input);
        final double[] output = qEvalNetwork.compute(data).getData();
        return getMaxQValueAction(Arrays.copyOf(ArrayUtil.toInts(input),input.length-1), output);
    }

    @Override
    String makeStateKey(int[] state, int currentPlayer) {
        String key = Arrays.toString(state) + Integer.toString(currentPlayer); //key = state + player
        //if this game state hasn't happened before
        return key;
    }

    @Override
    void updateQValues(String currentStateKey, int currentPlayer, int action, String nextStateKey, double reward) {
//        if (true)return;
        if (GAMES_TO_TRAIN % replaceTargetIter == 0) {
            replaceTargetParams();
        }
//        if(!gameEnded){
        storeTransition(reverseKey(currentStateKey), action, reward, reverseKey(nextStateKey));

//        }


        if (step < 1000) {
            step++;
            return;
        } else {
            step++;
        }
    if(step%100!=0){
            return;
    }
        ;


        int range = memoryCount > memorySize ? memorySize : memoryCount;
        LinkedList<Integer> iter = new LinkedList<>();
        for (int i = 0; i < range; i++) {
            iter.add(new Integer(i));
        }
        ArrayList<Integer> randomChoice = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            int randomIndex = rand.nextInt(iter.size());
            randomChoice.add(iter.get(randomIndex));
            iter.remove(randomIndex);
        }

        Memory[] batchMemory = new Memory[batchSize];
        for (int i = 0; i < batchMemory.length; i++) {
            batchMemory[i] = memories[randomChoice.get(i)];
        }

        MLData[] nextStateInputSet = new BasicMLData[batchSize];
        MLData[] currentStateIntputSet = new BasicMLData[batchSize];
        for (int i = 0; i < batchMemory.length; i++) {
            nextStateInputSet[i] = new BasicMLData(batchMemory[i].getNextState());
            currentStateIntputSet[i] = new BasicMLData(batchMemory[i].getCurrentState());
        }

        MLData[] nextStateOutputSet = new BasicMLData[batchSize];
        MLData[] currentStateOutputSet = new BasicMLData[batchSize];

        for (int i = 0; i < batchSize; i++) {
            nextStateOutputSet[i] = qTargetNetwork.compute(nextStateInputSet[i]);
            currentStateOutputSet[i] = qEvalNetwork.compute(currentStateIntputSet[i]);
        }

        MLData qTarget[] = new BasicMLData[batchSize];
        for (int i = 0; i < batchSize; i++) {
            qTarget[i] = currentStateOutputSet[i].clone();
        }

        for (int i = 0; i < batchSize; i++) {
            double updateValue;
            if (batchMemory[i].getCurrentState()[batchMemory[i].getCurrentState().length - 1] == 1) {
                int index = getMinQValueAction(ArrayUtil.toInts(batchMemory[i].getNextState()), nextStateOutputSet[i].getData());
                if (index == -1) {
                    updateValue = batchMemory[i].getReward();
                } else {
                    updateValue = batchMemory[i].getReward() + GAMMA * nextStateOutputSet[i].getData(index);
                }
            } else {
                int index = getMaxQValueAction(ArrayUtil.toInts(batchMemory[i].getNextState()), nextStateOutputSet[i].getData());
                if (index == -1) {
                    updateValue = batchMemory[i].getReward();
                } else {
                    updateValue = batchMemory[i].getReward() + GAMMA * nextStateOutputSet[i].getData(index);
                }

            }
            qTarget[i].setData(batchMemory[i].getAction(), updateValue);
        }

        //train net

        MLDataSet trainDate = new BasicMLDataSet();
        for (int i = 0; i < batchSize; i++) {
            trainDate.add(currentStateIntputSet[i], qTarget[i]);
        }


        CalculateScore score = new TrainingSetScore(trainDate);
        backpropagation = new Backpropagation(qEvalNetwork, trainDate, 0.000001, 0.0);
        final MLTrain trainAlt = new NeuralSimulatedAnnealing(
                qEvalNetwork, score, 10, 2, 100);
        backpropagation.addStrategy(new Greedy());
        backpropagation.addStrategy(new HybridStrategy(trainAlt));
        backpropagation.addStrategy(new StopTrainingStrategy());


        backpropagation.setThreadCount(Runtime.getRuntime().availableProcessors());
        backpropagation.setNesterovUpdate(true);
        if (trainingState == null) {
            System.out.println("Null");
        } else {
            backpropagation.resume(trainingState);
        }
        backpropagation.iteration();


        trainingState = backpropagation.pause();
        backpropagation = null;

    }

    public void storeTransition(double[] currentState, int action, double reward, double[] nextState) {
        Memory memory = new Memory(currentState, action, reward, nextState);
        int index = memoryCount % memorySize;
        memories[index] = memory;
        memoryCount += 1;
    }

    public void buildNet() {
        inputSize = currentState.length + 1;
        qEvalNetwork = new BasicNetwork();
        qEvalNetwork.addLayer(new BasicLayer(null, true, inputSize));
        int neuralCount = (int) Math.sqrt(inputSize - 1);
        for (int i = 0; i < Math.sqrt(inputSize - 1); i++) {
            qEvalNetwork.addLayer(new BasicLayer(new ActivationTANH(), true, inputSize));
        }
        qEvalNetwork.addLayer(new BasicLayer(new ActivationLinear(), false, inputSize - 1));
        qEvalNetwork.getStructure().finalizeStructure();
        new XaiverRandomizer(42).randomize(qEvalNetwork);

        qTargetNetwork = new BasicNetwork();
        qTargetNetwork.addLayer(new BasicLayer(null, true, inputSize));

        for (int i = 0; i < Math.sqrt(inputSize - 1); i++) {
            qTargetNetwork.addLayer(new BasicLayer(new ActivationTANH(), true, inputSize*2));
        }
        qTargetNetwork.addLayer(new BasicLayer(new ActivationLinear(), false, inputSize - 1));
        qTargetNetwork.getStructure().finalizeStructure();
        replaceTargetParams();

//        JordanPattern

        ElmanPattern pattern = new ElmanPattern();
        pattern.setActivationFunction(new ActivationSigmoid());
        pattern.setInputNeurons(inputSize);
        pattern.addHiddenLayer(inputSize*2);
        pattern.setOutputNeurons(inputSize-1);
        qEvalNetwork= (BasicNetwork)pattern.generate();

        new XaiverRandomizer(42).randomize(qEvalNetwork);

        ElmanPattern pattern2 = new ElmanPattern();
        pattern2.setActivationFunction(new ActivationSigmoid());
        pattern2.setInputNeurons(inputSize);
        pattern2.addHiddenLayer(inputSize*2);
        pattern2.setOutputNeurons(inputSize-1);
        qTargetNetwork= (BasicNetwork)pattern2.generate();

        replaceTargetParams();


    }

    public void replaceTargetParams() {
        qTargetNetwork.getFlat().setWeights(qEvalNetwork.getFlat().getWeights());
        qTargetNetwork.getFlat().setBiasActivation(qEvalNetwork.getFlat().getBiasActivation());
        //  saveNN("temp.eg");
        //  qTargetNetwork=loadNN("temp.eg");

    }

    public void saveNN(String path) {
        EncogDirectoryPersistence.saveObject(new File(path), qEvalNetwork);
    }

    public static BasicNetwork loadNN(String path) {
        BasicNetwork network =
                (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(path));
        return network;
    }

    @Override
    void playOneGame() {
        super.playOneGame();
        System.out.println(gamesPlayed);
    }

    public static void main(String[] args) {


            DQNBrain dqnBrain = new DQNBrain();

            dqnBrain.train();
            dqnBrain.play();
//            break;
//            dqnBrain.saveNN("a.eg");
//      l      System.out.println("save successfully");

    }
}

class Memory {
    private double[] currentState;
    private double reward;
    private int action;
    private double[] nextState;

    public Memory(double[] currentState, int action, double reward, double[] nextState) {
        setCurrentState(currentState);
        setAction(action);
        setReward(reward);
        setNextState(nextState);
    }

    public double[] getCurrentState() {
        return currentState;
    }

    public void setCurrentState(double[] currentState) {
        this.currentState = currentState;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public double[] getNextState() {
        return nextState;
    }

    public void setNextState(double[] nextState) {
        this.nextState = nextState;
    }
}

class MyErrorFunction implements ErrorFunction {
    @Override
    public void calculateError(ActivationFunction af, double[] b, double[] a, double[] ideal, double[] actual, double[] error, double derivShift, double significance) {
        ErrorCalculation errorCalculation = new ErrorCalculation();
        ErrorCalculation.setMode(ErrorCalculationMode.RMS);


        for (int j = 0; j < ideal.length; j++) {
            errorCalculation.updateError(ideal[j], actual[j]);
            error[j] = errorCalculation.calculate();
        }

    }


}