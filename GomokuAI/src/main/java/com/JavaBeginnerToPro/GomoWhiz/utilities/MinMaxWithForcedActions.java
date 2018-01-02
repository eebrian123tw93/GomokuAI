package com.JavaBeginnerToPro.GomoWhiz.utilities;

import com.JavaBeginnerToPro.GomoWhiz.QLearning.PatternDetect;
import com.JavaBeginnerToPro.GomoWhiz.QLearning.QTable_AI;
import com.JavaBeginnerToPro.GomoWhiz.minMax.SmartAgent;

public class MinMaxWithForcedActions extends AI {
    private SmartAgent smartAgent;
    private QTable_AI qTableAi = new QTable_AI();
    private int ourPlayerNum;

    public MinMaxWithForcedActions(int ourPlayerNum) {
        this.ourPlayerNum = ourPlayerNum;
        smartAgent = new SmartAgent(15, 5, ourPlayerNum);
        smartAgent.setMinimax(1);
    }
    public int move(int[] state) {
        if (PatternDetect.isEmpty(state)) return state.length / 2;
        int [] player1Patterns = PatternDetect.detect(state, 1);
        int [] player2Patterns = PatternDetect.detect(state, 2);
        if (qTableAi.obviousActionNeeded(player1Patterns, player2Patterns)){
            return qTableAi.forcedAction(state, qTableAi.scanObviousPatternTypes(player1Patterns, player2Patterns, ourPlayerNum), ourPlayerNum);
        }
        else return smartAgent.move(state);
    }
}
