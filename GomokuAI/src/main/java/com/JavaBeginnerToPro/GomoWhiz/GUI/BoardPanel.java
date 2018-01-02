package com.JavaBeginnerToPro.GomoWhiz.GUI;


import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class BoardPanel extends JPanel {
    boolean playing = false;

    public Rectangle[][] getRectangles() {
        return rectangles;
    }

    private Rectangle[][] rectangles;

    public int[] getGameState() {
        return gameState;
    }

    private int[] gameState;
    private int[] randomTable;
    private int rectSize;
    private int chessR;

    public int getBlackPlayer() {
        return blackPlayer;
    }


    public int getWhitePlayer() {
        return whitePlayer;
    }

    private int blackPlayer = 1;
    private int whitePlayer = 2;
    //RightPanel rightPanel;
    private BufferedImage blackChessImage;
    private BufferedImage whiteChessImage;
    private BufferedImage woodImage;

    public void setGameState(int[] gameState) {
        this.gameState = gameState;
    }

    public BoardPanel(int[] gameState) {
        this.gameState = gameState;
        setDoubleBuffered(true);
        setBackground(Color.white);
        try {
            woodImage = ImageIO.read(new File("wood.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                init();
                repaint();
            }

        });

        init();
//        creatRightPanel();
//        setBorder(BorderFactory.createRaisedBevelBorder());
//        setBorder(new EmptyBorder(10,10,10,10));
        setOpaque(false);
//        DropShadowBorder shadow = new DropShadowBorder();
//        shadow.setShadowColor(Color.BLACK);
//        shadow.setShowLeftShadow(true);
//        shadow.setShowRightShadow(false);
//        shadow.setShowBottomShadow(false);
//        shadow.setShowTopShadow(true);
//
//        shadow.setShadowSize(20);
        setLayout(new BorderLayout());
//        setBorder(shadow);

        initRandomTable();

    }

    public void init() {
        try {
            int withSize = (this.getBounds().width * 4 / 5) / (int) Math.sqrt(gameState.length);
            int heightSize = (this.getBounds().height * 4 / 5) / (int) Math.sqrt(gameState.length);
            this.rectSize = (int) Math.min(withSize, heightSize);
            Point point = new Point(this.getBounds().width / 2 - rectSize * (int) Math.sqrt(gameState.length) / 2, this.getBounds().height / 2 - rectSize * (int) Math.sqrt(gameState.length) / 2);
            rectangles = new Rectangle[(int) Math.sqrt(gameState.length) - 1][(int) Math.sqrt(gameState.length) - 1];
            for (int i = 0; i < rectangles.length; i++) {
                for (int j = 0; j < rectangles[i].length; j++) {
                    rectangles[i][j] = new Rectangle(point.x, point.y, rectSize, rectSize);
                    point.x += rectSize;
                }
                point.x = this.getBounds().width / 2 - rectSize * (int) Math.sqrt(gameState.length) / 2;
                point.y += rectSize;
            }
            chessR = rectSize * 3 / 5;
        } catch (Exception e) {

        }
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        Point p = new Point(this.getBounds().width / 2 - rectSize * (int) Math.sqrt(gameState.length) / 2, this.getBounds().height / 2 - rectSize * (int) Math.sqrt(gameState.length) / 2);
        g.drawImage(woodImage, 20, 20, getBounds().width - 10, getBounds().height - 10, this);
        super.paintChildren(g);
        g.setColor(Color.darkGray);
        ((Graphics2D) g).setStroke(new BasicStroke(3));
        // g.drawRect(p.x-rectSize-2, p.y-rectSize-2, rectSize*((int) Math.sqrt(gameState.length)+1)+3, rectSize*((int) Math.sqrt(gameState.length)+1)+3);
        //int shadowSize = ((DropShadowBorder) getBorder()).getShadowSize();
        g.drawImage(woodImage, p.x - rectSize , p.y - rectSize , rectSize * ((int) Math.sqrt(gameState.length) + 1), rectSize * ((int) Math.sqrt(gameState.length) + 1), this);
        g.setColor(Color.BLACK);
        for (int i = 0; i < rectangles.length; i++) {
            for (int j = 0; j < rectangles[i].length; j++) {

                g.drawRect(rectangles[i][j].x, rectangles[i][j].y, rectangles[i][j].width, rectangles[i][j].height);
            }
        }
        if (gameState == null) return;
        AffineTransform transform = new AffineTransform();

        for (int i = 0; i < gameState.length; i++) {
            transform.rotate(randomTable[i] * Math.PI / 2, blackChessImage.getWidth() / 2, blackChessImage.getHeight() / 2);
            if (gameState[i] == blackPlayer) {


                AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);

                g.drawImage(op.filter(blackChessImage, null), p.x - chessR / 2, p.y - chessR / 2, chessR, chessR, this);
//                g.setColor(Color.BLACK);
//                g.fillOval(p.x - chessR / 2, p.y - chessR / 2, chessR, chessR);
//                blackChessImage

            } else if (gameState[i] == whitePlayer) {
                AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
                g.drawImage(op.filter(whiteChessImage, null), p.x - chessR / 2, p.y - chessR / 2, chessR, chessR, this);
//                g.setColor(Color.WHITE);
//                g.fillOval(p.x - chessR / 2, p.y - chessR / 2, chessR, chessR);
            }

            if (i % (int) Math.sqrt(gameState.length) == (int) Math.sqrt(gameState.length) - 1) {
                p.x = this.getBounds().width / 2 - rectSize * (int) Math.sqrt(gameState.length) / 2;
                p.y += rectSize;
            } else {
                p.x += rectSize;
            }
        }
    }

    public void initRandomTable() {
        Random random = new Random();
        randomTable = new int[gameState.length];
        for (int i = 0; i < randomTable.length; i++) {
            randomTable[i] = random.nextInt(4);
        }
    }
}
