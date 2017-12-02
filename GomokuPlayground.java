package com.JavaBeginnerToPro.GomoWhiz;

public class GomokuPlayground {
    static final int GAMES_TO_PLAY = 10;
    static final int gameBoardWidth = 15;
    static final int winRequire = 5;
    static final boolean displayBoard = false;
    static final boolean printStats = true;

    static final int GAMEBOARD_SIZE = gameBoardWidth*gameBoardWidth;
    static int player1Wins = 0;
    static int player2Wins =0;
    int[] currentState;
    static int gamesPlayed = 0;

    public static void main(String[] args) {
        Player player1;
        Player player2;

        do{
           playOneGame();
           ++gamesPlayed;
        }while (gamesPlayed < GAMES_TO_PLAY);
    }

    static void playOneGame(){

    }
}



class Player{
    //Table-based, Conway-NN, Brian-DQN or Human
    String brainType;

}
