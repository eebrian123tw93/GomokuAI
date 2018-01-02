package com.JavaBeginnerToPro.GomoWhiz.utilities;

import com.JavaBeginnerToPro.GomoWhiz.minMax.SmartAgent;

//version 2 AI's
public class MinMax extends AI {
    private SmartAgent smartAgent;

    public MinMax(int ourPlayerNum) {
        smartAgent = new SmartAgent(15, 5, ourPlayerNum);
    }
    public int move(int[] state) {
        return smartAgent.move(state);
    }
}
