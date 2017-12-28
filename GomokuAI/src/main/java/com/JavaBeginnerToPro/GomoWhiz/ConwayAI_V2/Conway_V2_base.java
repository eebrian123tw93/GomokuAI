package com.JavaBeginnerToPro.GomoWhiz.ConwayAI_V2;

import com.JavaBeginnerToPro.GomoWhiz.Version_1.DetectWin_2;

import java.util.Arrays;
import java.util.Random;

public abstract class Conway_V2_base {
    public static final int BOARD_WIDTH = 15;
    public static final int BOARD_SIZE = BOARD_WIDTH * BOARD_WIDTH;
    public static final int WIN_REQUIRE = 5;
    protected Random rand = new Random();

    public static void main(String[] args) {
        int [][] state2D = {
                {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3}, //0
                {3,2,2,1,0,0,0,0,0,0,0,0,0,0,0,0,3}, //1
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //2
                {3,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,3}, //3
                {3,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,3}, //4
                {3,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,3}, //5
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //6
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //7
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //8
                {3,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,3}, //9
                {3,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,3}, //10
                {3,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,3}, //11
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //12
                {3,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,3}, //13
                {3,0,0,0,1,0,0,0,0,0,1,0,0,1,0,0,3}, //14
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3}, //15
                {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3} //16
        };

        int counter = 0;
        int[] state = new int[225];
        for (int i = 1; i < 16; ++i){
            for (int j = 1; j < 16; ++j){
                if (state2D[i][j] == 0) state[counter] = 1;
                else state[counter] = state2D[i][j];
                ++counter;
            }
        }

        System.out.println(Arrays.toString(getValidActions(state)));

        }

    abstract int chooseAction(int [] state, int currentPlayer);
    static int [] getValidActions(int [] state){
        int [][] state2D = DetectWin_2.convert1Dto2D(state, BOARD_WIDTH);
        int [] validActions;
        //boolean empty = true;
        int upperBound = 0;
        int lowerBound = state2D.length;
        int leftBound = 0;
        int rightBound = state2D.length;

        if (PatternDetect.isEmpty(state)) {
            validActions = new int[1];
            validActions[0] = state.length / 2;
            return validActions;
        }

        else {
            boolean found = false;

            //0, 1, 2, 3...
            //int[] validActionNums = new int[Conway_V2_base.BOARD_WIDTH * Conway_V2_base.BOARD_WIDTH];
            //for (int i = 0; i < validActionNums.length; ++i) validActionNums[i] = i;
            //int[][] validActionNums2D = DetectWin_2.convert1Dto2D(validActionNums, Conway_V2_base.BOARD_WIDTH);

            //find upper bound
            for (int row = 0; row < state2D.length - 2; ++row) {
                for (int col = 1; col < state2D.length - 1; ++col) {
                    if (state2D[row + 1][col] != 0) {
                        upperBound = row;
                        found = true;
                        break;
                    }
                }
                if (found == true) break;
            }

            found = false;
            //find lower bound
            for (int row = state2D.length - 1; row > 1; --row) {
                for (int col = 1; col < state2D.length - 1; ++col) {
                    if (state2D[row - 1][col] != 0) {
                        lowerBound = row;
                        found = true;
                        break;
                    }
                }
                if (found == true) break;
            }

            found = false;
            //find left bound
            for (int col = 0; col < state2D.length - 2; ++col) {
                for (int row = 1; row < state2D.length - 1; ++row) {
                    if (state2D[row][col + 1] != 0) {
                        leftBound = col;
                        found = true;
                        break;
                    }
                }
                if (found == true) break;
            }

            found = false;
            //find right bound
            for (int col = state2D.length - 1; col > 1; --col) {
                for (int row = 1; row < state2D.length - 1; ++row) {
                    if (state2D[row][col - 1] != 0) {
                        rightBound = col;
                        found = true;
                        break;
                    }
                }
                if (found == true) break;
            }

            //validActions = new int[(lowerBound - upperBound + 1) * (rightBound - leftBound + 1)];

            int emptySpacesInsideValidRegion = 0;
            for (int row = upperBound; row <= lowerBound; ++row){
                for (int col = leftBound; col <= rightBound; ++col){
                    if (state2D[row][col] == 0) ++emptySpacesInsideValidRegion;
                }
            }

            validActions = new int[emptySpacesInsideValidRegion];


            int counter = 0;
            int index = 0;
            for (int row = 1; row < state2D.length - 1; ++row) {
                for (int col = 1; col < state2D.length - 1; ++col) {
                    if (row >= upperBound && row <= lowerBound && col >= leftBound && col <= rightBound){
                        if (state2D[row][col] == 0) {
                            validActions[index] = counter;
                            ++index;
                        }
                    }
                    ++counter;
                }
            }

//            System.out.println("upper " + upperBound);
//            System.out.println("lower " + lowerBound);
//            System.out.println("left " + leftBound);
//            System.out.println("right " + rightBound);
            return validActions;
        }
    }
    int randomMove(int[] state) {
        //ArrayList<Integer> moveList = new ArrayList<>();
        int [] validActions = getValidActions(state);
        return validActions[rand.nextInt(validActions.length)];
    }

}
