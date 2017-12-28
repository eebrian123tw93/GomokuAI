package com.JavaBeginnerToPro.GomoWhiz.ConwayAI_V2;

import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

public class NN_AI_V2 extends QTable_AI{
    static BasicNetwork network;
    private static final int trainingEpochs = 1;

    NN_AI_V2(){
        createNN();
        GAMES_TO_TRAIN = 100000;
        //GAMES_TO_PLAY = 10000;
    }

    void createNN(){
        // create a neural network
        network = new BasicNetwork();
        network.addLayer(new BasicLayer(null,true, Conway_V2_base.BOARD_SIZE));
        network.addLayer(new BasicLayer(new ActivationReLU(), true, 100));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false, 1));
        network.getStructure().finalizeStructure();
        network.reset(); //randomize weights
    }
}
