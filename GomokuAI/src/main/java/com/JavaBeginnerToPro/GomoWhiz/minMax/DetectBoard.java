package com.JavaBeginnerToPro.GomoWhiz.minMax;

public class DetectBoard {
    public static boolean detectWin(int[] state, int gameBoardWidth, int winRequire, int player) {
        int[][] state2D = convert1Dto2D(state, gameBoardWidth);

        if (checkVertical(state2D, gameBoardWidth, winRequire, player) ||
                checkHorizontal(state2D, gameBoardWidth, winRequire, player) ||
                checkUpLeftToDownRight(state2D, gameBoardWidth, winRequire, player) ||
                checkUpRightToDownLeft(state2D, gameBoardWidth, winRequire, player))
            return true;
        else return false;
    }

    public static int[][] convert1Dto2D(int[] state, int gameBoardWidth) {
        /*
        Board borders are marked with the number 3!
        e.g. 33333
             30003
             30003
             30003
             33333

            is a completely empty 3*3 board
        */
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

    static boolean checkVertical(int[][] state2D, int gameBoardWidth, int winRequire, int player) {
        int inARow = 0;
        int rowIndex;

        for (int row = 1; row <= gameBoardWidth + 1 - winRequire; ++row) {
            for (int col = 1; col < gameBoardWidth + 1; ++col) {
                if (state2D[row][col] == player) {
                    rowIndex = row;
                    while (true) {
                        if (inARow >= winRequire&&(state2D[rowIndex++][col] == 0 ||  state2D[rowIndex-=winRequire][col] == 0) ) return true;
                        if (state2D[rowIndex][col] == player) {
                            ++inARow;
                            ++rowIndex;
                        } else {
                            inARow = 0;
                            break;
                        }
                    }
                }
            }
        }

        return false;
    }

    static boolean checkHorizontal(int[][] state2D, int gameBoardWidth, int winRequire, int player) {
        int inARow = 0;
        int colIndex;

        for (int row = 1; row < gameBoardWidth + 1; ++row) {
            for (int col = 1; col <= gameBoardWidth + 1 - winRequire; ++col) {
                if (state2D[row][col] == player) {
                    colIndex = col;

                    while (true) {
                        if (inARow >= winRequire&&(state2D[row][colIndex++] == 0 ||  state2D[row][colIndex-=winRequire] == 0) ) {
                            return true;
                        }
                        if (state2D[row][colIndex] == player) {
                            ++inARow;
                            ++colIndex;
                        } else {
                            inARow = 0;
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }

    static boolean checkUpLeftToDownRight(int[][] state2D, int gameBoardWidth, int winRequire, int player) {
        int inARow = 0;
        int rowIndex;
        int colIndex;

        for (int row = 1; row <= gameBoardWidth + 1 - winRequire; ++row) {
            for (int col = 1; col <= gameBoardWidth + 1 - winRequire; ++col) {
                if (state2D[row][col] == player) {
                    colIndex = col;
                    rowIndex = row;
                    while (true) {
                        if (inARow >= winRequire &&(state2D[rowIndex++][colIndex++] == 0 ||  state2D[rowIndex-=winRequire][colIndex-=winRequire] == 0)) return true;
                        if (state2D[rowIndex][colIndex] == player) {
                            ++inARow;
                            ++colIndex;
                            ++rowIndex;
                        } else {
                            inARow = 0;
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }

    static boolean checkUpRightToDownLeft(int[][] state2D, int gameBoardWidth, int winRequire, int player) {
        int inARow = 0;
        int rowIndex;
        int colIndex;

        for (int row = 1; row <= gameBoardWidth + 1 - winRequire; ++row) {
            for (int col = winRequire; col < gameBoardWidth + 1; ++col) {
                if (state2D[row][col] == player) {
                    colIndex = col;
                    rowIndex = row;
                    while (true) {
                        if (inARow >= winRequire&&(state2D[rowIndex++][colIndex--] == 0 ||  state2D[rowIndex-=winRequire][colIndex+=winRequire] == 0)) return true;
                        if (state2D[rowIndex][colIndex] == player) {
                            ++inARow;
                            --colIndex;
                            ++rowIndex;
                        } else {
                            inARow = 0;
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }

}
class DetectBoard_2 {
    public static boolean detectWin(int[] state, int gameBoardWidth, int winRequire, int player,int action) {
        int[][] state2D = convert1Dto2D(state, gameBoardWidth);
        int x = action / 15;
        int y = action % 15;
        if (checkVertical(state2D, gameBoardWidth, winRequire, player,y) ||
                checkHorizontal(state2D, gameBoardWidth, winRequire, player,x) ||
                checkUpLeftToDownRight(state2D, gameBoardWidth, winRequire, player,x,y) ||
                checkUpRightToDownLeft(state2D, gameBoardWidth, winRequire, player,x,y))
            return true;
        else return false;
    }

    public static int[][] convert1Dto2D(int[] state, int gameBoardWidth) {
        /*
        Board borders are marked with the number 3!
        e.g. 33333
             30003
             30003
             30003
             33333

            is a completely empty 3*3 board
        */
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

    static boolean checkVertical(int[][] state2D, int gameBoardWidth, int winRequire, int player,int y) {
        int inARow = 0;
        int rowIndex;

        for (int row = 1; row <= gameBoardWidth + 1 - winRequire; ++row) {
            for (int col = y; col < y + 1; ++col) {
                if (state2D[row][col] == player) {
                    rowIndex = row;
                    while (true) {
                        if (inARow >= winRequire&&(state2D[rowIndex++][col] == 0 ||  state2D[rowIndex-=winRequire][col] == 0) ) return true;
                        if (state2D[rowIndex][col] == player) {
                            ++inARow;
                            ++rowIndex;
                        } else {
                            inARow = 0;
                            break;
                        }
                    }
                }
            }
        }

        return false;
    }

    static boolean checkHorizontal(int[][] state2D, int gameBoardWidth, int winRequire, int player,int x) {
        int inARow = 0;
        int colIndex;

        for (int row = x; row < x + 1; ++row) {
            for (int col = 1; col <= gameBoardWidth + 1 - winRequire; ++col) {
                if (state2D[row][col] == player) {
                    colIndex = col;

                    while (true) {
                        if (inARow >= winRequire&&(state2D[row][colIndex++] == 0 ||  state2D[row][colIndex-=winRequire] == 0) ) {
                            return true;
                        }
                        if (state2D[row][colIndex] == player) {
                            ++inARow;
                            ++colIndex;
                        } else {
                            inARow = 0;
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }

    static boolean checkUpLeftToDownRight(int[][] state2D, int gameBoardWidth, int winRequire, int player,int x,int y) {
        int inARow = 0;
        int rowIndex;
        int colIndex;
        while (true){
            if(state2D[x][y]!=3){
                x--;
                y--;
            }else{
                break;
            }
        }
        for(int row=x,col=y;row <= gameBoardWidth + 1 - winRequire&&col <= gameBoardWidth + 1 - winRequire;row++,col++){
            if (state2D[row][col] == player) {
                colIndex = col;
                rowIndex = row;
                while (true) {
                    if (inARow >= winRequire &&(state2D[rowIndex++][colIndex++] == 0 ||  state2D[rowIndex-=winRequire][colIndex-=winRequire] == 0)) return true;
                    if (state2D[rowIndex][colIndex] == player) {
                        ++inARow;
                        ++colIndex;
                        ++rowIndex;
                    } else {
                        inARow = 0;
                        break;
                    }
                }
            }
        }
//        for (int row = 1; row <= gameBoardWidth + 1 - winRequire; ++row) {
//            for (int col = 1; col <= gameBoardWidth + 1 - winRequire; ++col) {
//                if (state2D[row][col] == player) {
//                    colIndex = col;
//                    rowIndex = row;
//                    while (true) {
//                        if (inARow >= winRequire &&(state2D[rowIndex++][colIndex++] == 0 ||  state2D[rowIndex-=winRequire][colIndex-=winRequire] == 0)) return true;
//                        if (state2D[rowIndex][colIndex] == player) {
//                            ++inARow;
//                            ++colIndex;
//                            ++rowIndex;
//                        } else {
//                            inARow = 0;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
        return false;
    }

    static boolean checkUpRightToDownLeft(int[][] state2D, int gameBoardWidth, int winRequire, int player,int x,int y) {
        int inARow = 0;
        int rowIndex;
        int colIndex;
        while (true){
            if(state2D[x][y]!=3){
                x--;
                y++;
            }else{
                break;
            }
        }
        for(int row=x,col=y;state2D[row][col]!=3&&row <= gameBoardWidth + 1 - winRequire&&col <= gameBoardWidth + 1 - winRequire;row++,col--){
            if (state2D[row][col] == player) {
                colIndex = col;
                rowIndex = row;
                while (true) {
                    if (inARow >= winRequire&&(state2D[rowIndex++][colIndex--] == 0 ||  state2D[rowIndex-=winRequire][colIndex+=winRequire] == 0)) return true;
                    if (state2D[rowIndex][colIndex] == player) {
                        ++inARow;
                        --colIndex;
                        ++rowIndex;
                    } else {
                        inARow = 0;
                        break;
                    }
                }
            }
        }
//        for (int row = 1; row <= gameBoardWidth + 1 - winRequire; ++row) {
//            for (int col = winRequire; col < gameBoardWidth + 1; ++col) {
//                if (state2D[row][col] == player) {
//                    colIndex = col;
//                    rowIndex = row;
//                    while (true) {
//                        if (inARow >= winRequire&&(state2D[rowIndex++][colIndex--] == 0 ||  state2D[rowIndex-=winRequire][colIndex+=winRequire] == 0)) return true;
//                        if (state2D[rowIndex][colIndex] == player) {
//                            ++inARow;
//                            --colIndex;
//                            ++rowIndex;
//                        } else {
//                            inARow = 0;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
        return false;
    }

}
