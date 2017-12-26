package com.JavaBeginnerToPro.GomoWhiz.Version_1;

public class DetectWin {

    static int [][] convert1Dto2D(int [] state, int gameBoardWidth) {
        //int gameBoardWidthPlus2 = gameBoardWidth + 2;
        int[][] state2D = new int[gameBoardWidth + 2][gameBoardWidth + 2];
        int stateIndex = 0;

        for (int row = 0; row < gameBoardWidth + 2; ++row) {
            for (int col = 0; col < gameBoardWidth + 2; ++col) {
                if (row == 0 || row == gameBoardWidth + 1) state2D[row][col] = 2;
                else if (col == 0 || col == gameBoardWidth + 1) state2D[row][col] = 2;
                else {
                    state2D[row][col] = state[stateIndex];
                    ++stateIndex;
                }
            }
        }

        return state2D;
    }

    static boolean detectWin(int[] state, int gameBoardWidth, int winRequire, int player) {
        int [][] state2D = convert1Dto2D(state, gameBoardWidth);

        if (checkVertical(state2D, gameBoardWidth, winRequire, player) ||
                checkHorizontal(state2D, gameBoardWidth, winRequire, player) ||
                checkUpLeftToDownRight(state2D, gameBoardWidth, winRequire, player) ||
                checkUpRightToDownLeft(state2D, gameBoardWidth, winRequire, player))
            return true;
        else return false;
    }

    static boolean checkVertical(int [][] state2D, int gameBoardWidth, int winRequire, int player){
        int inARow = 0;
        int rowIndex;

        for (int row = 1; row <= gameBoardWidth + 1 - winRequire; ++row){
            for (int col = 1; col < gameBoardWidth + 1; ++col){
                if (state2D[row][col] == player){
                    rowIndex = row;
                    while (true){
                        if (inARow >= winRequire) return true;
                        if (state2D[rowIndex][col] == player) {
                            ++inARow;
                            ++rowIndex;
                        }
                        else {
                            inARow = 0;
                            break;
                        }
                    }
                }
            }
        }

        return false;
    }
    static boolean checkHorizontal(int [][] state2D, int gameBoardWidth, int winRequire, int player){
        int inARow = 0;
        int colIndex;

        for (int row = 1; row < gameBoardWidth + 1; ++row){
            for (int col = 1; col <= gameBoardWidth + 1 - winRequire; ++col){
                if (state2D[row][col] == player){
                    colIndex = col;
                    while (true){
                        if (inARow >= winRequire) return true;
                        if (state2D[row][colIndex] == player) {
                            ++inARow;
                            ++colIndex;
                        }
                        else {
                            inARow = 0;
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }
    static boolean checkUpLeftToDownRight(int [][] state2D, int gameBoardWidth, int winRequire, int player){
        int inARow = 0;
        int rowIndex;
        int colIndex;

        for (int row = 1; row <= gameBoardWidth + 1 - winRequire; ++row){
            for (int col = 1; col <= gameBoardWidth + 1 - winRequire; ++col){
                if (state2D[row][col] == player){
                    colIndex = col;
                    rowIndex = row;
                    while (true){
                        if (inARow >= winRequire) return true;
                        if (state2D[rowIndex][colIndex] == player) {
                            ++inARow;
                            ++colIndex;
                            ++rowIndex;
                        }
                        else {
                            inARow = 0;
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }
    static boolean checkUpRightToDownLeft(int [][] state2D, int gameBoardWidth, int winRequire, int player){
        int inARow = 0;
        int rowIndex;
        int colIndex;

        for (int row = 1; row <= gameBoardWidth + 1 - winRequire; ++row){
            for (int col = winRequire; col < gameBoardWidth + 1; ++col){
                if (state2D[row][col] == player){
                    colIndex = col;
                    rowIndex = row;
                    while (true){
                        if (inARow >= winRequire) return true;
                        if (state2D[rowIndex][colIndex] == player) {
                            ++inARow;
                            --colIndex;
                            ++rowIndex;
                        }
                        else {
                            inARow = 0;
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }


//    static boolean detectWin(int[] state, int gameBoardWidth, int winRequire, int player) {
//
////        if ((state[0] == state[1] && state[1] == state[2] && state[0] == player) // horizontal
////                || (state[3] == state[4] && state[4] == state[5] && state[3] == player)
////                || (state[6] == state[7] && state[7] == state[8] && state[6] == player)
////                || (state[0] == state[3] && state[3] == state[6] && state[0] == player) // vertical
////                || (state[1] == state[4] && state[4] == state[7] && state[1] == player)
////                || (state[2] == state[5] && state[5] == state[8] && state[2] == player)
////                || (state[0] == state[4] && state[4] == state[8] && state[0] == player) // diagonal
////                || (state[2] == state[4] && state[4] == state[6] && state[2] == player))
////            return true;
////        else return false;
//
//        if (checkVertical(state, gameBoardWidth, winRequire, player) ||
//                checkHorizontal(state, gameBoardWidth, winRequire, player) ||
//                checkUpLeftToDownRight(state, gameBoardWidth, winRequire, player) ||
//                checkUpRightToDownLeft(state, gameBoardWidth, winRequire, player))
//            return true;
//        else return false;
//    }
//
//    static boolean checkVertical(int [] state, int gameBoardWidth, int winRequire, int player){
//        int inARow = 0;
//        int currentCheckIndex = 0;
//        int inARowCheckIndex = 0;
//
//        while(currentCheckIndex < state.length){
//            inARow = 0;
//            if(state[currentCheckIndex] != player) ++currentCheckIndex;
//            else {
//                inARowCheckIndex = currentCheckIndex;
//                while(state[inARowCheckIndex] == player){
//                    ++inARow;
//                    inARowCheckIndex += gameBoardWidth;
//                    if (inARowCheckIndex >= state.length) break;
//                }
//                if (inARow == winRequire) return true;
//                else ++currentCheckIndex;
//            }
//        }
//
//        return false;
//    }
//    static boolean checkHorizontal(int [] state, int gameBoardWidth, int winRequire, int player){
//        int inARow = 0;
//        int currentCheckIndex = 0;
//        int checkCount = 0;
//
//        while(currentCheckIndex < state.length){
//            if (checkCount > gameBoardWidth - 1) {//switched rows
//                inARow = 0;
//                checkCount = 0;
//            }
//
//            if(state[currentCheckIndex] == player) ++inARow;
//            else inARow = 0;
//
//            ++currentCheckIndex;
//            ++checkCount;
//
//            if (inARow >= winRequire) return true;
//        }
//
//        return false;
//    }
//    static boolean checkUpLeftToDownRight(int [] state, int gameBoardWidth, int winRequire, int player){
//        int inARow = 0;
//        int currentCheckIndex = 0;
//        int inARowCheckIndex = 0;
//
//        while(currentCheckIndex < state.length){
//            inARow = 0;
//            if(state[currentCheckIndex] != player) ++currentCheckIndex;
//            else {
//                inARowCheckIndex = currentCheckIndex;
//                while(state[inARowCheckIndex] == player){
//                    ++inARow;
//                    inARowCheckIndex += (gameBoardWidth + 1);
//                    if (inARowCheckIndex >= state.length) break;
//                }
//                if (inARow == winRequire) return true;
//                else ++currentCheckIndex;
//            }
//        }
//
//        return false;
//    }
//    static boolean checkUpRightToDownLeft(int [] state, int gameBoardWidth, int winRequire, int player){
//        int inARow = 0;
//        int currentCheckIndex = 0;
//        int inARowCheckIndex = 0;
//
//        while(currentCheckIndex < state.length){
//            inARow = 0;
//            if(state[currentCheckIndex] != player) ++currentCheckIndex;
//            else {
//                inARowCheckIndex = currentCheckIndex;
//                while(state[inARowCheckIndex] == player){
//                    ++inARow;
//                    inARowCheckIndex += (gameBoardWidth - 1);
//                    if (inARowCheckIndex >= state.length) break;
//                }
//                if (inARow == winRequire) return true;
//                else ++currentCheckIndex;
//            }
//        }
//
//        return false;
//    }
}