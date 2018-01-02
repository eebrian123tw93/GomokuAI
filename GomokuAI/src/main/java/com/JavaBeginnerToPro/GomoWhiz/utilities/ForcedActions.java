package com.JavaBeginnerToPro.GomoWhiz.utilities;

import com.JavaBeginnerToPro.GomoWhiz.QLearning.PatternDetect;
import com.JavaBeginnerToPro.GomoWhiz.QLearning.QTable_AI;

public class ForcedActions extends AI {
    //pure forced actions, if no obvious actions detected, random play
    private int ourPlayerNum;
    private QTable_AI qTableAi = new QTable_AI();

    public ForcedActions(int ourPlayerNum){
        this.ourPlayerNum = ourPlayerNum;
    }
    public int move(int [] state){
        if (PatternDetect.isEmpty(state)) return state.length / 2;
        int [] player1Patterns = PatternDetect.detect(state, 1);
        int [] player2Patterns = PatternDetect.detect(state, 2);
        if (qTableAi.obviousActionNeeded(player1Patterns, player2Patterns)){
            return qTableAi.forcedAction(state, qTableAi.scanObviousPatternTypes(player1Patterns, player2Patterns, ourPlayerNum), ourPlayerNum);
        }
        else return randomMove(state);
    }
}
