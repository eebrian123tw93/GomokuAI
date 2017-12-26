package com.JavaBeginnerToPro.GomoWhiz.ConwayAI_V2;

import com.JavaBeginnerToPro.GomoWhiz.Version_1.BoardPanel;
import com.JavaBeginnerToPro.GomoWhiz.Version_1.DetectWin_2;
import com.JavaBeginnerToPro.GomoWhiz.Version_1.GomokuGUI;
import com.JavaBeginnerToPro.GomoWhiz.minMax.SmartAgent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public class QTable_AI extends Conway_V2_base{

    static Map<String, Double> qMap = new HashMap<String, Double>();

    double LEARNING_RATE = 0.5;
    final double DECAY = 0.9;
    double RANDOM_MOVE_PROBABILITY = 0.3;
    boolean displayBoard = false;
    static int GAMES_TO_TRAIN = 1000;

    GomokuGUI gui;
    int player1Wins = 0;
    int player2Wins = 0;
    int ties = 0;

    public static void main(String[] args) {
        Path path = Paths.get("QTable_AI_V2_brain.txt").toAbsolutePath();
        if (Files.exists(path)){
            qMap = QMapIO.load("QTable_AI_V2_brain.txt");
        }
        QTable_AI qTable_AI = new QTable_AI();
        qTable_AI.train();
        QMapIO.save("QTable_AI_V2_brain.txt", qMap);
    }

    QTable_AI(){
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
        if (displayBoard) gui = new GomokuGUI(new int [225]);
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

    void train(){
        int gamesPlayed = 0;
        while(gamesPlayed < GAMES_TO_TRAIN){
            //trainOneGame();
            trainOneGame_withMinMax();
            ++gamesPlayed;

            System.out.println("Win/Loss ratio: " + (float) player1Wins / (float) player2Wins + " Games played: " + gamesPlayed);

            //System.out.println("Player 1 wins: " + player1Wins + " Player 2 wins: " + player2Wins + " Ties: " + ties);
//
//             if (gamesPlayed % 1000 == 0){
//                System.out.println("Player 1 wins: " + player1Wins + " Player 2 wins: " + player2Wins + " Ties: " + ties);
//            }
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
    void trainOneGame_withMinMax(){
        int [] currentState = new int[BOARD_SIZE];
        int [] nextState;
        int currentPlayer = rand.nextInt(2) + 1;
        int movesRemaining = BOARD_SIZE;
        double nextStateReward;
        boolean gameEnded = false;
        int action;

        while (gameEnded == false){
            nextState = currentState.clone();

            if (currentPlayer == 1) {
                action = chooseAction(currentState, currentPlayer);
                nextState[action] = currentPlayer;
                --movesRemaining;
                nextStateReward = evalReward(nextState, movesRemaining);
                if (nextStateReward == 1 || nextStateReward == -1 || movesRemaining == 0) gameEnded = true;
                updateQValue(makeStateKey(currentState), makeStateKey(nextState), nextStateReward, gameEnded);
            }

            else {
                action = new SmartAgent(15,5).move(currentState);
                nextState[action] = currentPlayer;
                --movesRemaining;
            }

            currentState = nextState.clone();
            //swap players
            if (currentPlayer == 1) currentPlayer = 2;
            else currentPlayer = 1;

            if (displayBoard) showGUI(currentState);
        }
    }

    int chooseAction(int [] state, int currentPlayer){
        if (rand.nextDouble() > RANDOM_MOVE_PROBABILITY) {
            int [] player1Patterns = PatternDetect.detect(state, 1);
            int [] player2Patterns = PatternDetect.detect(state, 2);

            if (currentPlayer == 1){
                if (obviousActionNeeded(player1Patterns, player2Patterns)){
                    return forcedAction(state, scanObviousPatternTypes(player1Patterns, player2Patterns, currentPlayer), currentPlayer);
                }
                else return getMaxQValueAction(state, currentPlayer);
                //return getMaxQValueAction(state, currentPlayer);
            }
            //else return getMinQValueAction(state, currentPlayer);
            else {
                if (obviousActionNeeded(player1Patterns, player2Patterns)){
                    return forcedAction(state, scanObviousPatternTypes(player1Patterns, player2Patterns, currentPlayer), currentPlayer);
                }

                return getMinQValueAction(state, currentPlayer);
                 //return randomMove(state);
            }
            //else return randomMove(state);
        }

        else return randomMove(state);
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
    double evalReward(int [] state, int movesRemaining){
        if (DetectWin_2.detectWin(state, BOARD_WIDTH, WIN_REQUIRE, 1)){
            ++player1Wins;
            return  1;
        }
        else if (DetectWin_2.detectWin(state, BOARD_WIDTH, WIN_REQUIRE, 2)){
            ++player2Wins;
            return -1;
        }
        else if (movesRemaining == 0) {
            ++ties;
            return 0;
        }
        return 0;
    }

    boolean obviousActionNeeded(int [] player1Patterns, int [] player2Patterns){
        if (player1Patterns[25] == 1 || player1Patterns[26] == 1 ||
                player1Patterns[20] == 1 || player1Patterns[24] == 1) return true;
        if (player2Patterns[25] == 1 || player2Patterns[26] == 1 ||
                player2Patterns[20] == 1 || player2Patterns[24] == 1) return true;

        return false;
    }
    ArrayList<Integer> scanObviousPatternTypes(int [] player1Patterns, int [] player2Patterns, int currentPlayer){
        ArrayList<Integer> patterns = new ArrayList<>();
        /*
        check for:
            oooo-   #25(current) #52(opponent)
            -oooo-  #26(current) #53(opponent)
            oo-oo   #20(current) #47(opponent)
            -ooo--  #24(current) #51(opponent)

            Priority: If player can win immediately, win immediately
                      If enemy will win next turn, block
                      If enemy has pattern 24, block
                      If we have pattern 24, make it into pattern 26
        */

        if (currentPlayer == 1){
            if (player1Patterns[25] == 1) patterns.add(25);
            if (player1Patterns[26] == 1) patterns.add(26);
            if (player1Patterns[20] == 1) patterns.add(20);

            if (player2Patterns[25] == 1 ) patterns.add(52);
            if (player2Patterns[20] == 1) patterns.add(47);
            if (player2Patterns[24] == 1) patterns.add(51);

            if (player1Patterns[24] == 1) patterns.add(24);
        }

        else {
            if (player2Patterns[25] == 1) patterns.add(25);
            if (player2Patterns[26] == 1) patterns.add(26);
            if (player2Patterns[20] == 1) patterns.add(20);

            if (player1Patterns[25] == 1 ) patterns.add(52);
            if (player1Patterns[20] == 1) patterns.add(47);
            if (player1Patterns[24] == 1) patterns.add(51);

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
    int forcedAction(int [] state, ArrayList<Integer> types, int currentPlayer){
        int [] validActions = getValidActions(state);
        int [] stateCopy = state.clone();
        int [] pattern;
        int opponent;
        if (currentPlayer == 1) opponent = 2;
        else opponent = 1;


        if (types.contains(25) || types.contains(26) || types.contains(20)) {
                for (int i : validActions){
                    stateCopy[i] = currentPlayer;
                    if (DetectWin_2.detectWin(stateCopy, BOARD_WIDTH, WIN_REQUIRE, currentPlayer)) return i;
                    stateCopy[i] = 0;
                }
        }

        else if (types.contains(52) || types.contains(47) || types.contains(51)){
            for (int i : validActions){
                stateCopy[i] = currentPlayer;
                pattern = PatternDetect.detect(stateCopy, opponent);
                if (!patternContainsType(pattern, 25) || !patternContainsType(pattern, 20) || !patternContainsType(pattern, 24)) return i;
                stateCopy[i] = 0;
            }
        }

        else if (types.contains(24)){
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
    boolean patternContainsType(int [] pattern, int type){
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
            qMap.put(stateKey, (rand.nextDouble() * 0.3) - 0.15); //-0.15 ~ +0.15

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
            Thread.sleep(700);
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}