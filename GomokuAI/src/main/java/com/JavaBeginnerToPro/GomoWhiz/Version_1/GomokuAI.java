package com.JavaBeginnerToPro.GomoWhiz.Version_1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GomokuAI {

    public static final int gameBoardWidth = 15;
    public static final int winRequire = 5;
    public static int GAMES_TO_TRAIN = 1000;
    public static int GAMES_TO_PLAY = 0;
    public boolean displayBoard = false;
    public boolean isPlaying = false;

    static final int GAMEBOARD_SIZE = gameBoardWidth * gameBoardWidth;
    static final double GAMMA = 0.9; //the decay rate of future rewards
    static final double LEARNING_RATE = 0.5; //the "ALPHA"
    Random rand;
    int[] currentState;
    boolean gameEnded = false;
    boolean learningMode = true;
    GomokuGUI gui;
    int wins = 0;
    int losses = 0;
    int ties = 0;
    int gamesPlayed = 0;
    static Map<String, HashMap<Integer, Double>> qMap; //Map<State, HashMap<Action, Q value>>

    public static void main(String[] args) {
        GomokuAI gomokuAI = new GomokuAI();

        long startTime = System.currentTimeMillis();
        gomokuAI.train();
        gomokuAI.play();
        System.out.println("run time = " + (System.currentTimeMillis() - startTime) / 1000);
        //QTableDAO.save("qmap15_5_1000games_original.txt", qMap);
    }

    GomokuAI() {
        rand = new Random(); //seed random number generator
        qMap = new HashMap<String, HashMap<Integer, Double>>();
        currentState = new int[GAMEBOARD_SIZE];
        gamesPlayed = 0;
        if (displayBoard) {
            gui = new GomokuGUI(currentState);
        }

        //train();
        //play();
        //System.out.println(qMap.size());
        //QTableDAO.save("qmapTicTacToe.txt", qMap);
        //System.out.println(LocalDateTime.now());
    }

    void train(){
        //training phrase
        while (gamesPlayed < GAMES_TO_TRAIN) {
            playOneGame();
            ++gamesPlayed;
            //System.out.println(Arrays.toString(currentState));
            //System.out.println(gamesPlayed);
            //System.out.println("qMap size: " + qMap.size());

            //display stats every 1000 games
            if (gamesPlayed % 100 == 0) {
                System.out.println("wins: " + wins + " losses: " + losses + " ties: " + ties);
                wins=losses=ties=0;
            }
        }
        gamesPlayed = 0;
    }
    void play(){
        isPlaying = true;
        //playing phrase
        while (gamesPlayed < GAMES_TO_PLAY){
            learningMode = false;
            playOneGame();
            ++gamesPlayed;

            if (displayBoard) {
                try {
                    ((BoardPanel)gui.getContentPane()).initRandomTable();
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

        //choose first AI
        if (rand.nextBoolean()) currentPlayer = 1;
        else currentPlayer = -1;

        while (!gameEnded) {
            if (currentPlayer == 1) nextPlayer = -1;
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
                    ++wins; //AI 1 win
                    //System.out.println("AI 1 win");
                }
                else if (reward == -1){
                    ++losses; //AI -1 win
                    //System.out.println("AI -1 win");
                }
                else if (movesRemaining <= 0){
                    ++ties;//tie
                    //System.out.println("tie");
                }
            }

            nextStateKey = makeStateKey(nextState, nextPlayer);
            //nextStateKey = makeStateKey_Better(nextState, nextPlayer);
            updateQValues(currentStateKey, currentPlayer, action, nextStateKey, reward);
            currentState = nextState.clone(); //change currentState to the state after the action

            //swap players
            if (currentPlayer == 1) currentPlayer = -1;
            else currentPlayer = 1;
            //my test
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            //
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
//        String key = Arrays.toString(state) + Integer.toString(currentPlayer); //key = state + AI
//        //if this game state hasn't happened before
//        if (qMap.get(key) == null) qMap.put(key, createActionQValueMap(state));
//        return key;
        StringBuilder sb = new StringBuilder();
        for (int i: state) sb.append(Integer.toString(i));
        sb.append(Integer.toString(currentPlayer));
        String key = sb.toString();

        //if this game state hasn't happened before
        if (qMap.get(key) == null) qMap.put(key, createActionQValueMap(state));
        return key;
    }

//    String makeStateKey_Better(int [] state, int currentPlayer){
//        //String key = Arrays.toString(state) + Integer.toString(currentPlayer); //key = state + AI
////        String key = new String();
////        for (int i: state) key += Integer.toString(i);
////        key += Integer.toString(currentPlayer);
//
//        StringBuilder sb = new StringBuilder();
//        for (int i: state) sb.append(Integer.toString(i));
//        String key = sb.toString();
//
//        //if this game state hasn't happened before
//        if (qMap.get(key) == null) qMap.put(key, createActionQValueMap(state));
//        return key;
//    }

    HashMap<Integer, Double> createActionQValueMap(int[] state) {
        // create hashmap of all valid actions in this state and random initial qvalues
        HashMap<Integer, Double> vals = new HashMap<Integer, Double>();
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0) //if the spot board is empty (will only make actions for empty places on the board)
                vals.put(i, (rand.nextDouble() * 0.3) - 0.15); // random [-0.15, 0.15]
        }
        return vals;
    }

    //chooses an action for the AI
    //int[] currentState is for choosing random moves
    int chooseAction(String stateKey, int currentPlayer, int[] currentState) {

        if (learningMode == false) {
            //AI 1 looks for the maximum Q values (because it gets a positive reward when winning)
            if (currentPlayer == 1) return getMaxQValueAction(stateKey);
            //AI -1 looks for the minimum Q values (because it gets a negative reward when winning)
            else if (currentPlayer == -1) return getMinQValueAction(stateKey);
            //else if (currentPlayer == -1) return getMaxQValueAction(stateKey);
        }

        else {
            if (rand.nextDouble() < 0.5) {
                if (currentPlayer == 1)
                    return getMaxQValueAction(stateKey);
                else
                    return getMinQValueAction(stateKey);
            }
            // 50% time choose random action
            else {
                return randomMove(currentState);
            }
        }

        //else if (currentPlayer == -1) return getMaxQValueAction(stateKey);
        return -1; //just in case. should cause an error
    }
    int getMaxQValueAction(String stateKey) {
        double maxQValue = -1.0;
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
        double minQValue = 1.0;
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
    int randomMove(int[] state) {
        ArrayList<Integer> moveList = new ArrayList<Integer>();
        for (int i = 0; i < state.length; i++)
            if (state[i] == 0)
                moveList.add(i);
        return moveList.get(rand.nextInt(moveList.size()));
    }

    double evalReward(int[] state) {
        if (DetectWin.detectWin(state, gameBoardWidth, winRequire, 1)) return 1;
        else if (DetectWin.detectWin(state, gameBoardWidth, winRequire, -1)) return -1;
        else return 0;
    }

    void updateQValues(String currentStateKey, int currentPlayer, int action, String nextStateKey, double reward) {
        double expectedQValue = 0; //the expected Q value

        if (gameEnded) expectedQValue = reward;
        else {
            // assumes that the opponent always makes the best move (similar to minimax)
            if (currentPlayer == 1) expectedQValue = reward + (GAMMA * getMinQValue(nextStateKey));
            else expectedQValue = reward + (GAMMA * getMaxQValue(nextStateKey));
        }

        double q = getQValue(currentStateKey, action);
        q += LEARNING_RATE * (expectedQValue - q);
        setQValue(currentStateKey, action, q);
    }

    //returns the minimum/maximum Q value of the corresponding stateKey
    double getMinQValue(String stateKey) {
        int minQAction = getMinQValueAction(stateKey);
        return qMap.get(stateKey).get(minQAction);
    }
    double getMaxQValue(String stateKey) {
        int maxQAction = getMaxQValueAction(stateKey);
        return qMap.get(stateKey).get(maxQAction);
    }
    double getQValue(String stateKey, int action) {
        return qMap.get(stateKey).get(action);
    }
    void setQValue(String stateKey, int action, double newQValue) {
        qMap.get(stateKey).put(action, newQValue);
    }
}
