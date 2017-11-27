package com.JavaBeginnerToPro.GomoWhiz;

import java.util.HashMap;
import java.util.Map;

public class GomokuAI_2 extends GomokuAI{
    static Map<String, HashMap<Integer, Double>> qMap2; //qMap for player -1

    public static void main(String[] args) {
        GomokuAI_2 gomokuAI_2 = new GomokuAI_2();
        long startTime = System.currentTimeMillis();
        gomokuAI_2.train();
        gomokuAI_2.play();
        System.out.println("run time = " + (System.currentTimeMillis() - startTime) / 1000);
    }

    GomokuAI_2(){
        qMap2 = new HashMap<>();
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
        else if (currentPlayer == -1){
            if (qMap2.get(key) == null) qMap2.put(key, createActionQValueMap(state));
        }

        return key;
    }

    int chooseAction(String stateKey, int currentPlayer, int[] currentState) {

        if (learingMode == false) {
            //player 1 looks for the maximum Q values (because it gets a positive reward when winning)
            if (currentPlayer == 1) return getMaxQValueAction(stateKey, currentPlayer);
                //player -1 looks for the minimum Q values (because it gets a negative reward when winning)
            else if (currentPlayer == -1) return getMinQValueAction(stateKey, currentPlayer);
            //else if (currentPlayer == -1) return getMaxQValueAction(stateKey, currentPlayer);
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

        //else if (currentPlayer == -1) return getMaxQValueAction(stateKey);
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

    void updateQValues(String currentStateKey, int currentPlayer, int action, String nextStateKey, double reward) {
        double expectedQValue = 0; //the expected Q value
        int nextPlayer;

        if (currentPlayer == 1) nextPlayer = -1;
        else nextPlayer = 1;

        if (gameEnded) expectedQValue = reward;
        else {
            // assumes that the opponent always makes the best move (similar to minimax)
            if (currentPlayer == 1) expectedQValue = reward + (GAMMA * getMinQValue(nextStateKey, -1));
            else expectedQValue = reward + (GAMMA * getMaxQValue(nextStateKey, 1));
        }

        double q = getQValue(currentStateKey, action, currentPlayer);
        q += LEARNING_RATE * (expectedQValue - q);
        setQValue(currentStateKey, action, q, currentPlayer);
    }

    double getMinQValue(String stateKey, int currentPlayer) {
        int minQAction = getMinQValueAction(stateKey, currentPlayer);
//        if (currentPlayer == 1) return qMap.get(stateKey).get(minQAction);
//        else return qMap2.get(stateKey).get(minQAction);
        return qMap2.get(stateKey).get(minQAction);
    }
    double getMaxQValue(String stateKey, int currentPlayer) {
        int maxQAction = getMaxQValueAction(stateKey, currentPlayer);
        //if (currentPlayer == 1) return qMap.get(stateKey).get(maxQAction);
        //else return qMap2.get(stateKey).get(maxQAction);
        return qMap.get(stateKey).get(maxQAction);
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
