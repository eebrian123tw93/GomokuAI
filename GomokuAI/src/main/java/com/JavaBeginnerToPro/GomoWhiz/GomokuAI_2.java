/*
* in GomokuAI_2, it is player 1 and player 2 instead of 1 and -1
* player 1 and player 2 have their Q tables stored in different Maps
* */

package com.JavaBeginnerToPro.GomoWhiz;

import java.util.HashMap;
import java.util.Map;

public class GomokuAI_2 extends GomokuAI{
    static Map<String, HashMap<Integer, Double>> qMap2; //qMap for player 2

    public static void main(String[] args) {
        GomokuAI_2 gomokuAI_2 = new GomokuAI_2();
        long startTime = System.currentTimeMillis();
        gomokuAI_2.train();
        gomokuAI_2.play();
        System.out.println("run time = " + (System.currentTimeMillis() - startTime) / 1000);
        //QTableDAO.save("qMap225_5p1.txt", qMap);
        //QTableDAO.save("qMap225_5p2.txt", qMap2);
    }

    GomokuAI_2(){
        qMap2 = new HashMap<>();
    }

    void play(){
        isPlaying = true;
        //playing phrase
        while (gamesPlayed < GAMES_TO_PLAY){
            learingMode = false;
            playOneGame();
            ++gamesPlayed;

            if (displayBoard) {
                try {
                    ((BoardPanel)gui.getContentPane().getComponent(0)).initRandomTable();
                    //gui.repaint();
                    Thread.sleep(500);
                }

                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (gamesPlayed % 1000 == 0) {
                System.out.println("wins: " + wins + " losses: " + losses + " ties: " + ties);
                wins=losses=ties=0;
            }
        }
        gamesPlayed = 0;
    }
    void playOneGame() {
        //int [] currentState = new int[GAMEBOARD_SIZE]; //initialize gameboard. all elements in state[] are 0 when declared
        for (int i = 0; i < GAMEBOARD_SIZE; ++i) currentState[i] = 0; //reset current state to all 0
        int[] nextState; //will be used between turns
        int currentPlayer;
        int nextPlayer;
        gameEnded = false;
        int movesRemaining = GAMEBOARD_SIZE;
        double reward;
        String currentStateKey;
        String nextStateKey;

        //choose first player
        if (rand.nextBoolean()) currentPlayer = 1;
        else currentPlayer = 2;

        while (!gameEnded) {
            if (currentPlayer == 1) nextPlayer = 2;
            else nextPlayer = 1;

            currentStateKey = makeStateKey(currentState, currentPlayer);
            //currentStateKey = makeStateKey_Better(currentState, currentPlayer);
            nextState = currentState.clone();
            int action = chooseAction(currentStateKey, currentPlayer, currentState);
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
                    ++losses; //player 2 win
                    //System.out.println("player 2 win");
                }
                else if (movesRemaining <= 0){
                    ++ties;//tie
                    //System.out.println("tie");
                }
            }

            nextStateKey = makeStateKey(nextState, nextPlayer);
            updateQValues(currentStateKey, currentPlayer, action, nextStateKey, reward);
            currentState = nextState.clone(); //change currentState to the state after the action

            //swap players
            if (currentPlayer == 1) currentPlayer = 2;
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

    String makeStateKey(int[] state, int currentPlayer) {
        StringBuilder sb = new StringBuilder();
        for (int i: state) sb.append(Integer.toString(i));
        //sb.append(Integer.toString(currentPlayer));
        String key = sb.toString();

        //if this game state hasn't happened before
        if (currentPlayer == 1){
         if (qMap.get(key) == null) qMap.put(key, createActionQValueMap(state));
        }
        else if (currentPlayer == 2) {
            if (qMap2.get(key) == null) qMap2.put(key, createActionQValueMap(state));
        }

        return key;
    }

    int chooseAction(String stateKey, int currentPlayer, int[] currentState) {

        if (learingMode == false) {
            //player 1 looks for the maximum Q values (because it gets a positive reward when winning)
            if (currentPlayer == 1) return getMaxQValueAction(stateKey, currentPlayer);
                //player 2 looks for the minimum Q values (because it gets a negative reward when winning)
            else if (currentPlayer == 2) return getMinQValueAction(stateKey, currentPlayer);
            //else if (currentPlayer == 2) return getMaxQValueAction(stateKey, currentPlayer);
        }

        else {
            if (rand.nextDouble() < 0.5) {
                if (currentPlayer == 1)
                    return getMaxQValueAction(stateKey, currentPlayer);
                else
                    return getMinQValueAction(stateKey, currentPlayer);
            }
            // 50% time choose random action
            else {
                return randomMove(currentState);
            }
        }

        return -1; //just in case. should cause an error
    }
    int getMaxQValueAction(String stateKey, int currentPlayer) {
        double maxQValue = -Double.MAX_VALUE;
        int maxQValueAction = -1;
        Map<Integer, Double> vals;

        if (currentPlayer == 1) vals = qMap.get(stateKey);
        else vals = qMap2.get(stateKey);

        for (Map.Entry<Integer, Double> entry : vals.entrySet()) {
            if (entry.getValue() > maxQValue) {
                maxQValue = entry.getValue();
                maxQValueAction = entry.getKey();
            }
        }
        return maxQValueAction;
    }
    int getMinQValueAction(String stateKey, int currentPlayer) {
        double minQValue = Double.MAX_VALUE;
        int minQValueAction = -1;
        Map<Integer, Double> vals;

        if (currentPlayer == 1) vals = qMap.get(stateKey);
        else vals = qMap2.get(stateKey);

        for (Map.Entry<Integer, Double> entry : vals.entrySet()) {
            if (entry.getValue() < minQValue) {
                minQValue = entry.getValue();
                minQValueAction = entry.getKey();
            }
        }
        return minQValueAction;
    }

    double evalReward(int[] state) {
        if (DetectWin_2.detectWin(state, gameBoardWidth, winRequire, 1)) return 1;
        else if (DetectWin_2.detectWin(state, gameBoardWidth, winRequire, 2)) return -1;
        else return 0;
    }

    void updateQValues(String currentStateKey, int currentPlayer, int action, String nextStateKey, double reward) {
        double expectedQValue = 0; //the expected Q value

        if (gameEnded) expectedQValue = reward;
        else {
            // assumes that the opponent always makes the best move
            if (currentPlayer == 1) expectedQValue = reward + (GAMMA * getMinQValue(nextStateKey, 2));
            else expectedQValue = reward + (GAMMA * getMaxQValue(nextStateKey, 1));
        }

        double q = getQValue(currentStateKey, action, currentPlayer);
        q += LEARNING_RATE * (expectedQValue - q);
        setQValue(currentStateKey, action, q, currentPlayer);
    }

    double getMinQValue(String stateKey, int currentPlayer) {
        double minQValue = Double.MAX_VALUE;
        Map<Integer, Double> vals;
        if (currentPlayer == 1) vals = qMap.get(stateKey);
        else vals = qMap2.get(stateKey);

        for (Map.Entry<Integer, Double> entry : vals.entrySet()) {
            if (entry.getValue() < minQValue) minQValue = entry.getValue();
        }
        return minQValue;

//        int minQAction = getMinQValueAction(stateKey, currentPlayer);
////        if (currentPlayer == 1) return qMap.get(stateKey).get(minQAction);
////        else return qMap2.get(stateKey).get(minQAction);
//        return qMap2.get(stateKey).get(minQAction);
    }
    double getMaxQValue(String stateKey, int currentPlayer) {
        double maxQValue = -Double.MAX_VALUE;
        Map<Integer, Double> vals;
        if (currentPlayer == 1) vals = qMap.get(stateKey);
        else vals = qMap2.get(stateKey);

        for (Map.Entry<Integer, Double> entry : vals.entrySet()) {
            if (entry.getValue() > maxQValue) maxQValue = entry.getValue();
        }
        return maxQValue;

//        int maxQAction = getMaxQValueAction(stateKey, currentPlayer);
//        //if (currentPlayer == 1) return qMap.get(stateKey).get(maxQAction);
//        //else return qMap2.get(stateKey).get(maxQAction);
//        return qMap.get(stateKey).get(maxQAction);
    }
    double getQValue(String stateKey, int action, int currentPlayer) {
        if (currentPlayer == 1) return qMap.get(stateKey).get(action);
        else return qMap2.get(stateKey).get(action);
    }
    void setQValue(String stateKey, int action, double newQValue, int currentPlayer) {
        if (currentPlayer == 1) qMap.get(stateKey).put(action, newQValue);
        else qMap2.get(stateKey).put(action, newQValue);
    }

}
