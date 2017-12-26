package com.JavaBeginnerToPro.GomoWhiz.ConwayAI_V2;


//o----
//-o----
//--o---
//o-o--
//-o-o--
//o-oo-
//-o-oo-
//-o-oo
//o-ooo
//-o-ooo
//o-oooo
//o--o-
//-o--o-
//o--ooo
//o---o
//o---oo
//o----o
//oo---
//-oo---
//--oo--
//oo-oo
//oo-ooo
//oo--oo
//ooo--
//-ooo--
//oooo-
//-oooo-
//ooooo-

/*
* input a 1d array state, output the patterns detected (an int array)
* ONLY for 15*15, 5 in a row! (Gomoku rules only)
* */

import com.JavaBeginnerToPro.GomoWhiz.Version_1.DetectWin_2;

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
            //{1, 1, 1, 1, 1, 0},
    };



    public static void main(String[] args) {
//        int [] stateSample = new int[225];
//        String str = "120221202000200120002002021110212200012102101210000110201002101000202102100200020022120000000120011122120100120112222020010210000022211021200002012101000122012211120010001020010010010100200020221020211101020000000000010111120";
//        //System.out.println(patterns.length);
//        for (int i = 0; i < str.length(); ++i){
//            stateSample[i] = str.charAt(i) - '0';
//        }
//
//
//        //System.out.println(Arrays.toString(stateSample));
//        //System.out.println(str);
//        //System.out.println(Arrays.toString(detect(stateSample, 1)));
//
//        int [] detected = detect(stateSample, 1);
//        for (int i = 0; i < detected.length; ++i){
//            if (detected[i] == 1) System.out.print(i + ", ");
//        }

    }

    public static int[] detect(int [] state, int player){
        int [] patternsDetected = new int[patterns.length];

        //state2D is a 2D array representation of the board. The borders around the board are marked by the number 3.
        int [][] state2D = DetectWin_2.convert1Dto2D(state, Conway_V2_base.BOARD_WIDTH);
//        int [][] state2D = {
//                {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3}, //0
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //1
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //2
//                {3,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,3}, //3
//                {3,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,3}, //4
//                {3,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,3}, //5
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //6
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //7
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //8
//                {3,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,3}, //9
//                {3,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,3}, //10
//                {3,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,3}, //11
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //12
//                {3,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,3}, //13
//                {3,0,0,0,1,0,0,0,0,0,1,0,0,1,0,0,3}, //14
//                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //15
//                {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3} //16
//        };

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

        for(int row = 1; row < Conway_V2_base.BOARD_WIDTH + 1; ++row){
            for (int col = 1; col < Conway_V2_base.BOARD_WIDTH + 1; ++col){
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
    public static boolean checkEmpty(int [] state){
        for (int i = 0; i < state.length; ++i){
            if (state[i] != 0) return false;
        }
        return true;
    }
}
