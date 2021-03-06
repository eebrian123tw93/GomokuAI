package com.JavaBeginnerToPro.GomoWhiz.QLearning;

import com.JavaBeginnerToPro.GomoWhiz.GUI.BoardPanel;
import com.JavaBeginnerToPro.GomoWhiz.utilities.DetectWin;
import com.JavaBeginnerToPro.GomoWhiz.GUI.PureGUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
    All moves are based on the qMap and forceAction() function
    The input (key) of the qMap are the current patterns (for both player 1 and 2) on the game board.
    The output (value) of the qMap is the winning probability of Player 1.
    Player 1 chooses actions that lead to states with highest winning probability
    Player 2 chooses actions that lead to states with the lowest winning probability (for player 1)
    If player 1 wins the reward is 1
    If player 2 wins the reward is -1
    If tie the reward is 0
*/

public class QTable_AI extends AI_Base {

    private static Map<String, Double> qMap = new HashMap<>();

    double LEARNING_RATE = 0.35;
    final double DECAY = 1;
    double RANDOM_MOVE_PROBABILITY = 0.1;
    private static boolean displayBoard = false;
    private static int GAMES_TO_TRAIN = 20000;
    private static int qMapSaveInterval = 5000;
    private PureGUI gui;
    private String brainFileName;
    private int player1Wins = 0;
    private int player2Wins = 0;
    private int ties = 0;

    public static void main(String[] args) {
        QTable_AI qTable_AI = new QTable_AI();
        qTable_AI.setQMap("qMap_20k");
        if (displayBoard) qTable_AI.gui = new PureGUI(new int [AI_Base.BOARD_SIZE]);
        qTable_AI.train();
        qTable_AI.saveQMap();
    }

    public QTable_AI(){
//        int [][] state2D = {
//                {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3}, //0
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //1
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //2
//                {3,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,3}, //3
//                {3,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,3}, //4
//                {3,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,3}, //5
//                {3,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,3}, //6
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //7
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //8
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //9
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //10
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //11
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //12
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //13
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //14
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //15
//                {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3} //16
//        };
//
//        int counter = 0;
//        int[] state = new int[225];
//        for (int i = 1; i < 16; ++i){
//            for (int j = 1; j < 16; ++j){
//                state[counter] = state2D[i][j];
//                ++counter;
//            }
//        }
//
//        int [] player1Pattern = PatternDetect.detect(state, 1);
//        int [] player2Pattern = PatternDetect.detect(state, 2);
//
//        System.out.println(obviousActionNeeded(player1Pattern, player2Pattern));
//        //System.out.println(forcedAction(state, scanObviousPatternTypes(player1Pattern, player2Pattern, 1), 1));
//        System.out.println("Player 1 obvious pattern types: " + scanObviousPatternTypes(player1Pattern, player2Pattern, 1));
//        System.out.println("Player 2 obvious pattern types: " + scanObviousPatternTypes(player1Pattern, player2Pattern, 2));
//
//
//        int forced_action = forcedAction(state, scanObviousPatternTypes(player1Pattern, player2Pattern, 1), 1);
//        System.out.println("Forced action: " + forced_action);
//
//        state[forced_action] = 1;
        //if (displayBoard) gui = new GomokuGUI(new int [225]);
//        showGUI(state);
//        showGUI(state);
//
//        //System.out.println(Arrays.toString(state));
//        System.out.println(Arrays.toString(getValidActions(state)));
//        System.out.println("num of valid actions = " + getValidActions(state).length);
//        if (displayBoard){
//            gui = new GomokuGUI(new int[BOARD_SIZE]);
//        }
    }
    public void setQMap(String fileName){
        brainFileName = fileName;
        qMap = QMapIO.load(fileName);
    }
    public void saveQMap(){
        QMapIO.save(brainFileName, qMap);
    }

    void train(){
        int gamesPlayed = 0;
        long startCountTime = System.currentTimeMillis();
        int gamesPerSecond = 0;
        int gamesPerSecondCounter = 0;

        while(gamesPlayed < GAMES_TO_TRAIN){

            if (System.currentTimeMillis() - startCountTime >= 1000){
                gamesPerSecond = gamesPerSecondCounter;
                gamesPerSecondCounter = 0;
                startCountTime = System.currentTimeMillis();
            }

            trainOneGame();
            ++gamesPlayed;
            ++gamesPerSecondCounter;

            if (gamesPlayed % 5000 == 0 && RANDOM_MOVE_PROBABILITY > 0) RANDOM_MOVE_PROBABILITY -= 0.02;
            if (gamesPlayed % qMapSaveInterval == 0){
                System.out.println("Saved qMap");
                QMapIO.save(brainFileName, qMap);
            }

            System.out.println("Win/Loss ratio: " + (float) player2Wins / (float) player1Wins + " Games played: " + gamesPlayed + ", " + gamesPerSecond + " games/s");
        }
    }
    void trainOneGame(){
        int [] currentState = new int[BOARD_SIZE];
        int [] nextState;
        int currentPlayer = rand.nextInt(2) + 1;
        int movesRemaining = BOARD_SIZE;
        double nextStateReward;
        boolean gameEnded = false;
        int action;

        while (gameEnded == false){
            nextState = currentState.clone();
            action = chooseAction(currentState, currentPlayer);
            nextState[action] = currentPlayer;
            --movesRemaining;
            nextStateReward = evalReward(nextState, movesRemaining);
            if (nextStateReward == 1 || nextStateReward == -1 || movesRemaining == 0) gameEnded = true;
            updateQValue(makeStateKey(currentState), makeStateKey(nextState), nextStateReward, gameEnded);
            currentState = nextState.clone();

            //swap players
            if (currentPlayer == 1) currentPlayer = 2;
            else currentPlayer = 1;

            if (displayBoard) showGUI(currentState);
        }
    }

    public int chooseAction(int [] state, int currentPlayer){
        if (PatternDetect.isEmpty(state)) return state.length / 2;

        int [] player1Patterns = PatternDetect.detect(state, 1);
        int [] player2Patterns = PatternDetect.detect(state, 2);

        if (obviousActionNeeded(player1Patterns, player2Patterns)) {
            return forcedAction(state, scanObviousPatternTypes(player1Patterns, player2Patterns, currentPlayer), currentPlayer);
        }

        else if (rand.nextDouble() > RANDOM_MOVE_PROBABILITY) {
            if (currentPlayer == 1) {
                return getMaxQValueAction(state, currentPlayer);
                //return randomMove(state);
            }
            else {
                return getMinQValueAction(state, currentPlayer);
                //return randomMove(state);
            }
        }
        else return randomMove(state);
    }
    int getMaxQValueAction(int [] state, int currentPlayer){
        int[] nextState = state.clone();
        int[] validActions = getValidActions(state);
        double [] validActionsQValues = new double [validActions.length];
        ArrayList<Integer> bestActions = new ArrayList<>();
        double stateMaxQValue = Double.NEGATIVE_INFINITY;
        double stateQValue;

        for (int i = 0; i < validActions.length; ++i) {
            nextState[validActions[i]] = currentPlayer;
            stateQValue = evalState(makeStateKey(nextState));
            if (stateQValue > stateMaxQValue) {
                stateMaxQValue = stateQValue;
                //maxQAction = validActions[i];
            }
            validActionsQValues[i] = stateQValue;
            nextState[validActions[i]] = 0;
        }

        for (int i = 0; i < validActions.length; ++i){
            if (validActionsQValues[i] == stateMaxQValue){
                bestActions.add(validActions[i]);
            }
        }

        return bestActions.get(rand.nextInt(bestActions.size()));
    }
    int getMinQValueAction(int [] state, int currentPlayer){
        int [] nextState = state.clone();
        int [] validActions = getValidActions(state);
        double [] validActionsQValues = new double [validActions.length];
        ArrayList<Integer> bestActions = new ArrayList<>();
        double stateMinQValue = Double.POSITIVE_INFINITY;
        double stateQValue;

        for (int i = 0; i < validActions.length; ++i) {
            nextState[validActions[i]] = currentPlayer;
            stateQValue = evalState(makeStateKey(nextState));
            if (stateQValue < stateMinQValue) {
                stateMinQValue = stateQValue;
                //minQAction = validActions[i];
            }
            validActionsQValues[i] = stateQValue;
            nextState[validActions[i]] = 0;
        }

        for (int i = 0; i < validActions.length; ++i){
            if (validActionsQValues[i] == stateMinQValue){
                bestActions.add(validActions[i]);
            }
        }

        return bestActions.get(rand.nextInt(bestActions.size()));
    }
    double evalReward(int [] state, int movesRemaining){
        if (DetectWin.detectWin(state, BOARD_WIDTH, WIN_REQUIRE, 1)){
            ++player1Wins;
            return  1;
        }
        else if (DetectWin.detectWin(state, BOARD_WIDTH, WIN_REQUIRE, 2)){
            ++player2Wins;
            return -1;
        }
        else if (movesRemaining == 0) {
            ++ties;
            return 0;
        }
        return 0;
    }

    //forced action functions
    public boolean obviousActionNeeded(int [] player1Patterns, int [] player2Patterns){
        if (player1Patterns[25] == 1 || player1Patterns[6] == 1 || player1Patterns[20] == 1 || player1Patterns[24] == 1 || player1Patterns[9] == 1) return true;
        if (player2Patterns[25] == 1 || player2Patterns[6] == 1 || player2Patterns[20] == 1 || player2Patterns[24] == 1 || player2Patterns[9] == 1) return true;

        return false;
    }
    public ArrayList<Integer> scanObviousPatternTypes(int [] player1Patterns, int [] player2Patterns, int currentPlayer){
        ArrayList<Integer> patterns = new ArrayList<>();
        /*
        check for:
            oooo-   #25(current) #52(opponent)
            -oooo-  #26(current) #53(opponent)
            oo-oo   #20(current) #47(opponent)
            -ooo--  #24(current) #51(opponent)
            -o-oo-  #6(current)  #33
            -o-ooo  #9           #36
            o-ooo   #8           #35


            Priority: If player can win immediately, win immediately
                      If enemy will win next turn, block
                      If enemy has pattern 24, block
                      If we have pattern 24, make it into pattern 26
        */

        if (currentPlayer == 1){
            if (player1Patterns[25] == 1) patterns.add(25);
            if (player1Patterns[6] == 1) patterns.add(6);
            if (player1Patterns[26] == 1) patterns.add(26);
            if (player1Patterns[20] == 1) patterns.add(20);
            if (player1Patterns[9] == 1) patterns.add(9);
            if (player1Patterns[8] == 1) patterns.add(8);

            if (player2Patterns[25] == 1 ) patterns.add(52);
            if (player2Patterns[9] == 1) patterns.add(36);
            if (player2Patterns[6] == 1) patterns.add(33);
            if (player2Patterns[20] == 1) patterns.add(47);
            if (player2Patterns[24] == 1) patterns.add(51);
            if (player2Patterns[8] == 1) patterns.add(35);

            if (player2Patterns[26] == 1) patterns.add(53);

            if (player1Patterns[24] == 1) patterns.add(24);
        }

        else {
            if (player2Patterns[25] == 1) patterns.add(25);
            if (player2Patterns[6] == 1) patterns.add(6);
            if (player2Patterns[26] == 1) patterns.add(26);
            if (player2Patterns[20] == 1) patterns.add(20);
            if (player2Patterns[9] == 1) patterns.add(9);
            if (player2Patterns[8] == 1) patterns.add(8);

            if (player1Patterns[25] == 1 ) patterns.add(52);
            if (player1Patterns[6] == 1) patterns.add(33);
            if (player1Patterns[20] == 1) patterns.add(47);
            if (player1Patterns[24] == 1) patterns.add(51);
            if (player1Patterns[9] == 1) patterns.add(36);
            if (player1Patterns[8] == 1) patterns.add(35);

            if (player1Patterns[26] == 1) patterns.add(53);

            if (player2Patterns[24] == 1) patterns.add(24);
        }
//        if (currentPlayer == 1){
//            if (player1Patterns[25] == 1) return 25;
//            else if (player1Patterns[26] == 1) return 26;
//            else if (player1Patterns[20] == 1) return 25;
//
//            if (player2Patterns[25] == 1 ) return 52;
//            else if (player2Patterns[20] == 1) return 47;
//            else if (player2Patterns[24] == 1) return 51;
//
//            if (player1Patterns[24] == 1) return 24;
//        }
//
//        else {
//            if (player2Patterns[25] == 1) return 25;
//            else if (player2Patterns[26] == 1) return 26;
//            else if (player2Patterns[20] == 1) return 25;
//
//            if (player1Patterns[25] == 1) return 52;
//            else if (player1Patterns[20] == 1) return 47;
//            else if (player1Patterns[24] == 1) return 51;
//
//            if (player2Patterns[24] == 1) return 24;
//        }
        return patterns;
    }
    public int forcedAction(int [] state, ArrayList<Integer> types, int currentPlayer){
        int [] validActions = getValidActions(state);
        int [] stateCopy = state.clone();
        int [] pattern;
        int opponent;
        if (currentPlayer == 1) opponent = 2;
        else opponent = 1;


        if (types.contains(25) || types.contains(26) || types.contains(20) || types.contains(9) || types.contains(8)) {
                for (int i : validActions){
                    stateCopy[i] = currentPlayer;
                    if (DetectWin.detectWin(stateCopy, BOARD_WIDTH, WIN_REQUIRE, currentPlayer)) return i;
                    stateCopy[i] = 0;
                }
        }

        else if (types.contains(52) || types.contains(47) || types.contains(51) || types.contains(33) || types.contains(36) || types.contains(35)){
            for (int i : validActions){
                stateCopy[i] = currentPlayer;
                pattern = PatternDetect.detect(stateCopy, opponent);
                if (!patternContainsType(pattern, 25) && !patternContainsType(pattern, 20)
                        && !patternContainsType(pattern, 24) && !patternContainsType(pattern, 6)
                        && !patternContainsType(pattern, 9) && !patternContainsType(pattern, 8)) return i;
                stateCopy[i] = 0;
            }
        }

        else if (types.contains(53)){
            for (int i : validActions){
                stateCopy[i] = currentPlayer;
                pattern = PatternDetect.detect(stateCopy, opponent);
                if (!patternContainsType(pattern, 26)) return i;
                stateCopy[i] = 0;
            }
        }

        else if (types.contains(24) || types.contains(6)){
            for (int i : validActions){
                stateCopy[i] = currentPlayer;
                pattern = PatternDetect.detect(stateCopy, currentPlayer);
                if (patternContainsType(pattern, 26)) return i;
                stateCopy[i] = 0;
            }
        }

        //last resort
        return randomMove(state);
    }
    public boolean patternContainsType(int [] pattern, int type){
        if (pattern[type] == 1) return true;
        return false;
    }

    String makeStateKey(int [] state){
        StringBuilder sb = new StringBuilder();
        for (int i: PatternDetect.detect(state, 1)) sb.append(Integer.toString(i));
        for (int i: PatternDetect.detect(state, 2)) sb.append(Integer.toString(i));
        return sb.toString();
    }

    //pass in state, return Q value of the state
    double evalState(String stateKey){
        //if state has not happened before
        if (qMap.get(stateKey) == null)
            qMap.put(stateKey, (rand.nextDouble() - 0.5)); //-0.5 ~ +0.5

        return qMap.get(stateKey);
    }

    void updateQValue(String currentStateKey, String nextStateKey, double nextStateReward, boolean gameEnded){
        double expectedValue;
        double currentQValue = evalState(currentStateKey);

        if (gameEnded) expectedValue = nextStateReward - currentQValue;
        //r(t + 1) + GAMMA * V(t + 1) - V(t)
        else expectedValue = nextStateReward + DECAY * evalState(nextStateKey) - currentQValue;

        currentQValue += LEARNING_RATE * expectedValue;
        qMap.put(currentStateKey, currentQValue);
    }

    void showGUI(int [] state){
        try {
            ((BoardPanel) gui.getContentPane().getComponent(0)).setGameState(state);
            gui.repaint();
            Thread.sleep(000);
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}