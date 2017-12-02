package com.JavaBeginnerToPro.GomoWhiz;

public class DetectWin_2 extends DetectWin {

    static boolean detectWin(int[] state, int gameBoardWidth, int winRequire, int player) {
        int [][] state2D = convert1Dto2D(state, gameBoardWidth);

        if (checkVertical(state2D, gameBoardWidth, winRequire, player) ||
                checkHorizontal(state2D, gameBoardWidth, winRequire, player) ||
                checkUpLeftToDownRight(state2D, gameBoardWidth, winRequire, player) ||
                checkUpRightToDownLeft(state2D, gameBoardWidth, winRequire, player))
            return true;
        else return false;
    }

    static int [][] convert1Dto2D(int [] state, int gameBoardWidth) {
        int[][] state2D = new int[gameBoardWidth + 2][gameBoardWidth + 2];
        int stateIndex = 0;

        for (int row = 0; row < gameBoardWidth + 2; ++row) {
            for (int col = 0; col < gameBoardWidth + 2; ++col) {
                if (row == 0 || row == gameBoardWidth + 1) state2D[row][col] = 3;
                else if (col == 0 || col == gameBoardWidth + 1) state2D[row][col] = 3;
                else {
                    state2D[row][col] = state[stateIndex];
                    ++stateIndex;
                }
            }
        }

        return state2D;
    }
}