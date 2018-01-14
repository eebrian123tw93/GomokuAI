package com.JavaBeginnerToPro.GomoWhiz.Version_1;

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
    public static final int GAMES_TO_PLAY = 100;
    public static final int BOARD_WIDTH = 15;
    public static final int BOARD_SIZE = BOARD_WIDTH * BOARD_WIDTH;
    public static final int WIN_REQUIRE = 5;

    private int player1Win = 0;
    private int player2Win = 0;
    private int tie = 0;

    private boolean displayBoard = false;
    private static int gamesPlayed = 0;
    private static float player1WinPercent = 0;
    private static float player2WinPercent = 0;

    private AI AI1;
    private AI AI2;
    private int[] state;
    private java.util.Random rand;
    private PureGUI gui;
    private int guiDelayMillis = 0;

    public static void main(String[] args) {
        Playground playground = new Playground();
        if (playground.displayBoard == true)
            playground.gui = new PureGUI(playground.state, "Player 1 win percent", "Player 2 win percent", "Games played");

        playground.setAI1(new QTableWithForcedActions(1, "qMap_20k"));
        //playground.setAI2(new QTableWithForcedActions(2, "qMap_20k"));
        playground.setAI2(new MinMaxWithForcedActions(2));

        long startTime = System.currentTimeMillis();

        while (gamesPlayed < GAMES_TO_PLAY) {
            playground.play();
            ++gamesPlayed;
            System.out.println("AI 1 wins: " + playground.player1Win + " AI 2 wins: " + playground.player2Win + " ties: " + playground.tie);
        }

        System.out.println("run time = " + (System.currentTimeMillis() - startTime) / 1000);
    }

    Playground() {
        rand = new java.util.Random();
        state = new int[BOARD_SIZE];
    }

    public AI getAI1() {
        return AI1;
    }

    public void setAI1(AI AI1) {
        this.AI1 = AI1;
    }

    public void setAI2(AI AI2) {
        this.AI2 = AI2;
    }

    public int[] getState() {
        return state;
    }

    void play() {
        for (int i = 0; i < state.length; i++) state[i] = 0;
        int action;
        int movesRemaining = BOARD_SIZE;
        int currentPlayer;

        if (rand.nextBoolean()) currentPlayer = 1;
        else currentPlayer = 2;

        while (true) {
            if (currentPlayer == 1) action = AI1.move(state);
            else action = AI2.move(state);
            state[action] = currentPlayer;
            --movesRemaining;

            if (displayBoard) {
                showGUI(state);
                try {
                    Thread.sleep(guiDelayMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (DetectWin_2.detectWin(state, BOARD_WIDTH, WIN_REQUIRE, 1)) {
                ++player1Win;
                break;
            } else if (DetectWin_2.detectWin(state, BOARD_WIDTH, WIN_REQUIRE, 2)) {
                ++player2Win;
                break;
            } else if (movesRemaining == 0) {
                ++tie;
                break;
            }

            if (currentPlayer == 1) currentPlayer = 2;
            else currentPlayer = 1;

            if (gamesPlayed > 0) {
                player1WinPercent = (float) player1Win / gamesPlayed * 100;
                player2WinPercent = (float) player2Win / gamesPlayed * 100;
            }

            if (gui == null) {
                try {
                    Thread.sleep(200);
                } catch (Exception e) {

                }
            }
        }

    }

    void showGUI(int[] state) {
        try {
            ((BoardPanel) gui.getContentPane().getComponent(0)).setGameState(state);
            gui.updateStatusBar(Float.toString(player1WinPercent) + "%", Float.toString(player2WinPercent) + "%", Integer.toString(gamesPlayed));
            gui.repaint();
            Thread.sleep(guiDelayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// state: what the board looks like, in a 1D array. 1 and 2 for players, 0 for empty
abstract class AI {
    java.util.Random rand = new java.util.Random();

    abstract public int move(int[] state);

    int randomMove(int[] state) {
        if (PatternDetect.isEmpty(state)) return state.length / 2;
        ArrayList<Integer> moveList = new ArrayList<Integer>();
        for (int i = 0; i < state.length; i++)
            if (state[i] == 0)
                moveList.add(i);
        return moveList.get(rand.nextInt(moveList.size()));
    }
}

class Random extends AI {
    public int move(int[] state) {
        return randomMove(state);
    }
}

//verion 1 AI's
class TableBased2_Conway extends AI {
    private Map<String, HashMap<Integer, Double>> qMap;
    private String brainFileName;
    private int ourPlayerNum;

    TableBased2_Conway(int ourPlayerNum, String brainFileName) {
        this.ourPlayerNum = ourPlayerNum;
        this.brainFileName = brainFileName;
        qMap = QTableDAO.load(brainFileName);
    }

    public int move(int[] state) {
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

    int getMinQValueAction(String stateKey) {
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

    String stateToString(int[] state) {
        StringBuilder sb = new StringBuilder();
        for (int i : state) sb.append(Integer.toString(i));
        return sb.toString();
    }
}

class NN_ConwayP1 extends AI {
    //GomokuAI_NN gomokuAI_NN = new GomokuAI_NN();
    private String fileName = "network_9_3p1.eg";
    BasicNetwork network;

    NN_ConwayP1() {
        network = (BasicNetwork) loadObject(new File(fileName));
    }

    public int move(int[] state) {
        return getMaxQValueAction(getNNOutputs(stateToDoubleArray(state)), state);
        //return getMinQValueAction(getNNOutputs(stateToDoubleArray(state)), state);
    }

    double[] stateToDoubleArray(int[] state) {
        double[] state_double = new double[state.length];
        for (int i = 0; i < state_double.length; ++i) state_double[i] = state[i];
        return state_double;
    }

    double[] getNNOutputs(double[] stateKey) {
        return network.compute(new BasicMLData(stateKey)).getData();
    }

    int getMaxQValueAction(double[] nnOutputs, int[] state) {
        double maxQValue = Double.NEGATIVE_INFINITY;
        int action = 0;

        for (int i = 0; i < nnOutputs.length; ++i) {
            if (nnOutputs[i] > maxQValue && state[i] == 0) {
                maxQValue = nnOutputs[i];
                action = i;
            }
        }

        return action;
    }

    int getMinQValueAction(double[] nnOutputs, int[] state) {
        double minQValue = Double.POSITIVE_INFINITY;
        int action = 0;

        for (int i = 0; i < nnOutputs.length; ++i) {
            if (nnOutputs[i] < minQValue && state[i] == 0) {
                minQValue = nnOutputs[i];
                action = i;
            }
        }

        return action;
    }
}

class NN_ConwayP2 extends AI {
    //GomokuAI_NN gomokuAI_NN = new GomokuAI_NN();
    private String fileName = "network_9_3p2.eg";
    BasicNetwork network;

    NN_ConwayP2() {
        network = (BasicNetwork) loadObject(new File(fileName));
    }

    public int move(int[] state) {
        //return getMaxQValueAction(getNNOutputs(stateToDoubleArray(state)), state);
        return getMinQValueAction(getNNOutputs(stateToDoubleArray(state)), state);
    }

    double[] stateToDoubleArray(int[] state) {
        double[] state_double = new double[state.length];
        for (int i = 0; i < state_double.length; ++i) state_double[i] = state[i];
        return state_double;
    }

    double[] getNNOutputs(double[] stateKey) {
        return network.compute(new BasicMLData(stateKey)).getData();
    }

    int getMaxQValueAction(double[] nnOutputs, int[] state) {
        double maxQValue = Double.NEGATIVE_INFINITY;
        int action = 0;

        for (int i = 0; i < nnOutputs.length; ++i) {
            if (nnOutputs[i] > maxQValue && state[i] == 0) {
                maxQValue = nnOutputs[i];
                action = i;
            }
        }

        return action;
    }

    int getMinQValueAction(double[] nnOutputs, int[] state) {
        double minQValue = Double.POSITIVE_INFINITY;
        int action = 0;

        for (int i = 0; i < nnOutputs.length; ++i) {
            if (nnOutputs[i] < minQValue && state[i] == 0) {
                minQValue = nnOutputs[i];
                action = i;
            }
        }

        return action;
    }
}

class SQL_AI extends AI {
    int player;
    SQLComokuAI_II sqlComokuAI_ii;

    public SQL_AI(int player) {
        if (player == 1) {
            this.player = 1;
        } else if (player == 2) {
            this.player = -1;
        }

        sqlComokuAI_ii = new SQLComokuAI_II();
    }

    public int move(int[] state) {
        int[] copyState = state.clone();
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 1) {
                copyState[i] = player;
            } else if (state[i] == 2) {
                copyState[i] = -player;
            }
        }
        String key = sqlComokuAI_ii.makeStateKey(copyState, player);
        return sqlComokuAI_ii.chooseAction(key, player, copyState);
    }
}

//version 2 AI's
class MinMax extends AI {
    private SmartAgent smartAgent;

    MinMax(int ourPlayerNum) {
        smartAgent = new SmartAgent(15, 5, ourPlayerNum);
    }

    public int move(int[] state) {
        return smartAgent.move(state);
    }
}

class MinMaxWithForcedActions extends AI {
    private SmartAgent smartAgent;
    private QTable_AI qTableAi = new QTable_AI();
    private int ourPlayerNum;

    MinMaxWithForcedActions(int ourPlayerNum) {
        this.ourPlayerNum = ourPlayerNum;
        smartAgent = new SmartAgent(15, 5, ourPlayerNum);
        smartAgent.setMinimax(1);
    }

    public int move(int[] state) {
        if (PatternDetect.isEmpty(state)) return state.length / 2;
        int[] player1Patterns = PatternDetect.detect(state, 1);
        int[] player2Patterns = PatternDetect.detect(state, 2);
        if (qTableAi.obviousActionNeeded(player1Patterns, player2Patterns)) {
            return qTableAi.forcedAction(state, qTableAi.scanObviousPatternTypes(player1Patterns, player2Patterns, ourPlayerNum), ourPlayerNum);
        } else return smartAgent.move(state);
    }
}

class QTableWithForcedActions extends AI {
    private int ourPlayerNum;
    private QTable_AI qTable_ai;

    QTableWithForcedActions(int ourPlayerNum, String brainName) {
        this.ourPlayerNum = ourPlayerNum;
        qTable_ai = new QTable_AI();
        qTable_ai.setQMap(brainName);
    }

    public int move(int[] state) {
        return qTable_ai.chooseAction(state, ourPlayerNum);
    }
}

class PureQTable extends AI {
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

    private int getMaxQValueAction(int[] state, int currentPlayer) {
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

    private int getMinQValueAction(int[] state, int currentPlayer) {
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

    private double evalState(String stateKey) {
        //if state has not happened before
        if (qMap.get(stateKey) == null)
            qMap.put(stateKey, (rand.nextDouble() * 0.3) - 0.15); //-0.15 ~ +0.15

        return qMap.get(stateKey);
    }

    private static int[] getValidActions(int[] state) {
        int[][] state2D = DetectWin_2.convert1Dto2D(state, Playground.BOARD_WIDTH);
        int[] validActions;
        int upperBound = 0;
        int lowerBound = state2D.length;
        int leftBound = 0;
        int rightBound = state2D.length;

        if (PatternDetect.isEmpty(state)) {
            validActions = new int[1];
            validActions[0] = state.length / 2;
            return validActions;
        } else {
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
            for (int row = upperBound; row <= lowerBound; ++row) {
                for (int col = leftBound; col <= rightBound; ++col) {
                    if (state2D[row][col] == 0) ++emptySpacesInsideValidRegion;
                }
            }

            validActions = new int[emptySpacesInsideValidRegion];

            int counter = 0;
            int index = 0;
            for (int row = 1; row < state2D.length - 1; ++row) {
                for (int col = 1; col < state2D.length - 1; ++col) {
                    if (row >= upperBound && row <= lowerBound && col >= leftBound && col <= rightBound) {
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

class ForcedActions extends AI {
    //pure forced actions, if no obvious actions detected, random play
    private int ourPlayerNum;
    private QTable_AI qTableAi = new QTable_AI();

    public ForcedActions(int ourPlayerNum) {
        this.ourPlayerNum = ourPlayerNum;
    }

    public int move(int[] state) {
        if (PatternDetect.isEmpty(state)) return state.length / 2;
        int[] player1Patterns = PatternDetect.detect(state, 1);
        int[] player2Patterns = PatternDetect.detect(state, 2);
        if (qTableAi.obviousActionNeeded(player1Patterns, player2Patterns)) {
            return qTableAi.forcedAction(state, qTableAi.scanObviousPatternTypes(player1Patterns, player2Patterns, ourPlayerNum), ourPlayerNum);
        } else return randomMove(state);
    }
}