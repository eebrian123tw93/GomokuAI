package com.JavaBeginnerToPro.GomoWhiz.Version_1;

import org.jdesktop.swingx.JXPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public  class RightPanel extends JXPanel {
    private int blackPlayer = 1;
    private int whitePlayer = 2;

    private BufferedImage blackChessImage;
    private BufferedImage whiteChessImage;

    public RightPanel(){
        try {
            blackChessImage = ImageIO.read(new File("black_chess.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            whiteChessImage = ImageIO.read(new File("white_chess.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        JButton buttonBlack=new JButton(new ImageIcon(blackChessImage.getScaledInstance(100,100,10)));
////            buttonBlack.add(new JLabel("player1"));
//        add(buttonBlack);
//        JButton buttonWhite=new JButton(new ImageIcon(whiteChessImage.getScaledInstance(100,100,10)));
////            buttonWhite.add(new JLabel("player2"));
//        add(buttonWhite);
    }

    @Override
    public void print(Graphics g) {
        super.print(g);
    }
}