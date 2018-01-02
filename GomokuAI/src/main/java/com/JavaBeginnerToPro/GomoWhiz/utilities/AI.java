package com.JavaBeginnerToPro.GomoWhiz.utilities;

import com.JavaBeginnerToPro.GomoWhiz.QLearning.PatternDetect;

import java.util.ArrayList;

// state: what the board looks like, in a 1D array. 1 and 2 for players, 0 for empty
public abstract class AI {
    java.util.Random rand = new java.util.Random();

    abstract public int move(int[] state);

    int randomMove(int[] state) {
        if (PatternDetect.isEmpty(state)) return state.length / 2;
        ArrayList<Integer> moveList = new ArrayList<Integer>();
        for (int i = 0; i < state.length; i++)
            if (state[i] == 0)
                moveList.add(i);
        return moveList.get(rand.nextInt(moveList.size()));
    }
}
