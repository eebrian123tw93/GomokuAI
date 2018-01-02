package com.JavaBeginnerToPro.GomoWhiz.utilities;

import com.JavaBeginnerToPro.GomoWhiz.GUI.BoardPanel;
import com.JavaBeginnerToPro.GomoWhiz.GUI.PureGUI;
import com.JavaBeginnerToPro.GomoWhiz.QLearning.PatternDetect;
import com.JavaBeginnerToPro.GomoWhiz.QLearning.QMapIO;
import com.JavaBeginnerToPro.GomoWhiz.QLearning.QTable_AI;
import com.JavaBeginnerToPro.GomoWhiz.minMax.SmartAgent;

import java.util.ArrayList;
import java.util.Map;

public class Playground {
    public static final int GAMES_TO_PLAY = 100;
    public static final int BOARD_WIDTH = 15;
    public static final int BOARD_SIZE = BOARD_WIDTH * BOARD_WIDTH;
    public static final int WIN_REQUIRE = 5;

    private int player1Win = 0;
    private int player2Win = 0;
    private int tie = 0;

    private boolean displayBoard = true;
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
        if (playground.displayBoard == true) playground.gui = new PureGUI(playground.state, "Player 1 win percent", "Player 2 win percent", "Games played");

        playground.setAI1(new QTableWithForcedActions(1, "qMap_10k"));
        playground.setAI2(new Random());
        //playground.setAI2(new ForcedActions(2));
        //playground.setAI2(new Random());
        long startTime = System.currentTimeMillis();

        while (gamesPlayed < GAMES_TO_PLAY) {
            playground.play();
            ++gamesPlayed;
            System.out.println("AI 1 wins: " + playground.player1Win + " AI 2 wins: " + playground.player2Win + " ties: " + playground.tie);
        }

        System.out.println("run time = " + (System.currentTimeMillis() - startTime) / 1000);
    }

    public Playground() {
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

    public void play() {
        for(int i=0;i<state.length;i++)state[i]=0;
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
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (DetectWin.detectWin(state, BOARD_WIDTH, WIN_REQUIRE, 1)) {
                ++player1Win;
                break;
            }
            else if (DetectWin.detectWin(state, BOARD_WIDTH, WIN_REQUIRE, 2)) {
                ++player2Win;
                break;
            }
            else if (movesRemaining == 0) {
                ++tie;
                break;
            }

            if (currentPlayer == 1) currentPlayer = 2;
            else currentPlayer = 1;

            if (gamesPlayed > 0) {
                player1WinPercent = (float) player1Win / gamesPlayed * 100;
                player2WinPercent = (float) player2Win / gamesPlayed * 100;
            }

            if(gui == null){
                try {
                    Thread.sleep(200);
                }
                catch (Exception e){

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

