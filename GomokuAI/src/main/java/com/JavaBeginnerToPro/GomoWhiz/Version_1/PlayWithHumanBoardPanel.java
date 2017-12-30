package com.JavaBeginnerToPro.GomoWhiz.Version_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class PlayWithHumanBoardPanel extends BoardPanel implements MouseListener {
    List<List<Rectangle>> humanClickAreas;
    AI ai;
    boolean aiMoving=false;
    @Override
    public void init() {
        super.init();
        humanClickAreas = new ArrayList<>();
        for (int i = 0; i < getRectangles().length; i++) {
            List<Rectangle> rectangles = new ArrayList<>();
            for (int j = 0; j < getRectangles()[i].length; j++) {
                Rectangle rectangle = new Rectangle();
                Point point = getRectangles()[i][j].getLocation();
                Dimension size = getRectangles()[i][j].getSize();
                point.x -= size.width / 2;
                point.y -= size.height / 2;
                rectangle.setLocation(point);
                rectangle.setSize(size);
                rectangles.add(rectangle);
            }
            Rectangle rectangle = new Rectangle();
            Rectangle r = getRectangles()[i][getRectangles()[i].length - 1];
            Dimension size = r.getSize();
            Point point = r.getLocation();
            point.x = point.x + size.width - size.width / 2;
            point.y -= size.height / 2;
            rectangle.setLocation(point);
            rectangle.setSize(size);
            rectangles.add(rectangle);
            humanClickAreas.add(rectangles);
        }
        List<Rectangle> rectangles = new ArrayList<>();
        int column = getRectangles().length - 1;
        for (int i = 0; i < getRectangles()[column].length; i++) {
            Rectangle rectangle = new Rectangle();
            Point point = getRectangles()[column][i].getLocation();
            Dimension size = getRectangles()[column][i].getSize();
            point.x -= size.width / 2;
            point.y += size.height / 2;
            rectangle.setLocation(point);
            rectangle.setSize(size);
            rectangles.add(rectangle);
        }
        Rectangle rectangle = new Rectangle();
        Rectangle r = getRectangles()[column][getRectangles()[column].length - 1];
        Dimension size = r.getSize();
        Point point = r.getLocation();
        point.x = point.x + size.width - size.width / 2;
        point.y += size.height / 2;
        rectangle.setSize(size);
        rectangle.setLocation(point);
        rectangles.add(rectangle);
        humanClickAreas.add(rectangles);
        System.out.println();
    }

    public PlayWithHumanBoardPanel(int[] gameState) {
        super(gameState);
        addMouseListener(this);
//        ai=new QTableWithForcedActions(1);
        ai=new MinMax(1);
        playing=true;
//        Button button=new Button("Restart");
//        button.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                for (int i = 0; i < gameState.length; i++) {
////            state[i] = random.nextInt(3) - 1;
//                    gameState[i] = 0;
//                }
//                ;button.getParent().repaint();
//                playing=true;
//            }
//        });
//        add(button);
    }

    public static void main(String[] args) {
//        java.util.Random random = new Random();
//        int[] state = new int[225];
//        for (int i = 0; i < state.length; i++) {
////            state[i] = random.nextInt(3) - 1;
//            state[i] = 0;
//        }
//        JFrame frame = new JFrame();
//        frame.setVisible(true);
//        frame.add(new PlayWithHumanBoardPanel(state));

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
//        for (int i = 0; i < humanClickAreas.size(); i++) {
//            for (int j = 0; j < humanClickAreas.get(i).size(); j++) {
//
//                g.drawRect(humanClickAreas.get(i).get(j).x, humanClickAreas.get(i).get(j).y, humanClickAreas.get(i).get(j).width, humanClickAreas.get(i).get(j).height);
//            }
//        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!playing)return;
        boolean move=false;
        if(aiMoving)return;
        for (int i = 0; i < humanClickAreas.size(); i++) {
            for (int j = 0; j < humanClickAreas.get(i).size(); j++) {
                if (humanClickAreas.get(i).get(j).contains(e.getPoint())) {
                    int pos = i * humanClickAreas.size() + j;
                    if (getGameState()[pos] == 0){
                        getGameState()[pos] = getWhitePlayer();
                        move=true;
                        break;
                    }
                }
            }
        }
        repaint();
        if(DetectWin.detectWin(getGameState(),15,5,getWhitePlayer())){
            playing=false;
        }
        //(playing=DetectWin.detectWin(getGameState(),15,5,1))
        if(move&&playing){

            new Thread(){
                @Override
                public void run() {
                    aiMoving=true;
                    getGameState()[ai.move(getGameState())]=getBlackPlayer();
                    aiMoving=false;
                }
            }.start();

        }

        if(DetectWin.detectWin(getGameState(),15,5,getBlackPlayer())){
            playing=false;
        }
        repaint();


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
