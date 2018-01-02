package com.JavaBeginnerToPro.GomoWhiz.QLearning;

import com.JavaBeginnerToPro.GomoWhiz.utilities.DetectWin;

public class PatternDetect {
    private static int [][] patterns = {
            {1, 0, 0, 0, 0}, //0
            {0, 1, 0, 0, 0, 0}, //1
            {0, 0, 1, 0, 0, 0}, //2
            {1, 0, 1, 0, 0}, //3
            {0, 1, 0, 1, 0, 0}, //4
            {1, 0, 1, 1, 0}, //5
            {0, 1, 0, 1, 1, 0}, //6
            {0, 1, 0, 1, 1}, //7
            {1, 0, 1, 1, 1}, //8
            {0, 1, 0, 1, 1, 1}, //9
            {1, 0, 1, 1, 1, 1}, //10
            {1, 0, 0, 1, 0}, //11
            {0, 1, 0, 0, 1, 0}, //12
            {1, 0, 0, 1, 1, 1}, //13
            {1, 0, 0, 0, 1}, //14
            {1, 0, 0, 0, 1, 1}, //15
            {1, 0, 0, 0, 0, 1}, //16
            {1, 1, 0, 0, 0}, //17
            {0, 1, 1, 0, 0, 0}, //18
            {0, 0, 1, 1, 0, 0}, //19
            {1, 1, 0, 1, 1}, //20
            {1, 1, 0, 1, 1, 1}, //21
            {1, 1, 0, 0, 1, 1}, //22
            {1, 1, 1, 0, 0}, //23
            {0, 1, 1, 1, 0, 0}, //24
            {1, 1, 1, 1, 0}, //25
            {0, 1, 1, 1, 1, 0} //26
    };

    public static int[] detect(int [] state, int player){
        int [] patternsDetected = new int[patterns.length];

        //state2D is a 2D array representation of the board. The borders around the board are marked by the number 3.
        int [][] state2D = DetectWin.convert1Dto2D(state, AI_Base.BOARD_WIDTH);

        for (int i = 0; i < patterns.length; ++i){
            if (checkPattern(state2D, patterns[i], player)) patternsDetected[i] = 1;
        }

        return patternsDetected;
    }

    private static boolean checkPattern(int [][] state2D, int [] pattern, int player){
        int correct;
        int rowIndex;
        int colIndex;
        int patternArrayIndex;

        if (player == 2){
            for (int i = 0; i < pattern.length; ++i){
                if (pattern[i] == 1) pattern[i] = player;
            }
        }

        else if (player == 1){
            for (int i = 0; i < pattern.length; ++i){
                if (pattern[i] == 2) pattern[i] = player;
            }
        }

        for(int row = 1; row < AI_Base.BOARD_WIDTH + 1; ++row){
            for (int col = 1; col < AI_Base.BOARD_WIDTH + 1; ++col){
                if (state2D[row][col] == pattern[0]){
                    correct = 1;
                    patternArrayIndex = 1;

                    //check upwards
                    rowIndex = row - 1;
                    colIndex = col;
                    while (true){
                        if (correct == pattern.length) return true;
                        if (state2D[rowIndex][colIndex] == pattern[patternArrayIndex]){
                            ++correct;
                            --rowIndex;
                            ++patternArrayIndex;
                        }
                        else {
                            correct = 1;
                            patternArrayIndex = 1;
                            break;
                        }
                    }

                    //check downwards
                    rowIndex = row + 1;
                    colIndex = col;
                    while (true){
                        if (correct == pattern.length) return true;
                        if (state2D[rowIndex][colIndex] == pattern[patternArrayIndex]){
                            ++correct;
                            ++rowIndex;
                            ++patternArrayIndex;
                        }
                        else {
                            correct = 1;
                            patternArrayIndex = 1;
                            break;
                        }
                    }

                    //check left side
                    rowIndex = row;
                    colIndex = col - 1;
                    while (true){
                        if (correct == pattern.length) return true;
                        if (state2D[rowIndex][colIndex] == pattern[patternArrayIndex]){
                            ++correct;
                            --colIndex;
                            ++patternArrayIndex;
                        }
                        else {
                            correct = 1;
                            patternArrayIndex = 1;
                            break;
                        }
                    }

                    //check right side
                    rowIndex = row;
                    colIndex = col + 1;
                    while (true){
                        if (correct == pattern.length) return true;
                        if (state2D[rowIndex][colIndex] == pattern[patternArrayIndex]){
                            ++correct;
                            ++colIndex;
                            ++patternArrayIndex;
                        }
                        else {
                            correct = 1;
                            patternArrayIndex = 1;
                            break;
                        }
                    }

                    //check up left
                    rowIndex = row - 1;
                    colIndex = col - 1;
                    while (true){
                        if (correct == pattern.length) return true;
                        if (state2D[rowIndex][colIndex] == pattern[patternArrayIndex]){
                            ++correct;
                            --rowIndex;
                            --colIndex;
                            ++patternArrayIndex;
                        }
                        else {
                            correct = 1;
                            patternArrayIndex = 1;
                            break;
                        }
                    }

                    //check up right
                    rowIndex = row - 1;
                    colIndex = col + 1;
                    while (true){
                        if (correct == pattern.length) return true;
                        if (state2D[rowIndex][colIndex] == pattern[patternArrayIndex]){
                            ++correct;
                            --rowIndex;
                            ++colIndex;
                            ++patternArrayIndex;
                        }
                        else {
                            correct = 1;
                            patternArrayIndex = 1;
                            break;
                        }
                    }

                    //check down left
                    rowIndex = row + 1;
                    colIndex = col - 1;
                    while (true){
                        if (correct == pattern.length) return true;
                        if (state2D[rowIndex][colIndex] == pattern[patternArrayIndex]){
                            ++correct;
                            ++rowIndex;
                            --colIndex;
                            ++patternArrayIndex;
                        }
                        else {
                            correct = 1;
                            patternArrayIndex = 1;
                            break;
                        }
                    }

                    //check down right
                    rowIndex = row + 1;
                    colIndex = col + 1;
                    while (true){
                        if (correct == pattern.length) return true;
                        if (state2D[rowIndex][colIndex] == pattern[patternArrayIndex]){
                            ++correct;
                            ++rowIndex;
                            ++colIndex;
                            ++patternArrayIndex;
                        }
                        else break;
                    }
                }
            }
        }
        return false;
    }
    public static boolean isEmpty(int [] state){
        for (int i = 0; i < state.length; ++i){
            if (state[i] != 0) return false;
        }
        return true;
    }
}
