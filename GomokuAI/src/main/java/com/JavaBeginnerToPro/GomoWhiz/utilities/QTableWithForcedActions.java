package com.JavaBeginnerToPro.GomoWhiz.utilities;

import com.JavaBeginnerToPro.GomoWhiz.QLearning.QTable_AI;

public class QTableWithForcedActions extends AI {
    private int ourPlayerNum;
    private QTable_AI qTable_ai;

    public QTableWithForcedActions(int ourPlayerNum, String brainName) {
        this.ourPlayerNum = ourPlayerNum;
        qTable_ai = new QTable_AI();
        qTable_ai.setQMap(brainName);
    }

    public int move(int[] state) {
        return qTable_ai.chooseAction(state, ourPlayerNum);
    }
}
