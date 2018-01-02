package com.JavaBeginnerToPro.GomoWhiz.GUI;


import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Random;

public class PureGUI extends JFrame implements KeyListener {
    private String info1, info2, info3;
    private JLabel statusLabel;

    public static void main(String[] args) {
        Random random = new Random();
        int[] state = new int[225];
        for (int i = 0; i < state.length; i++) {
//            state[i] = random.nextInt(3) - 1;
            state[i]=0;
        }

        PureGUI gui = new PureGUI(state);

        while (true) {
            try {
                Thread.sleep(1000);
                for (int i = 0; i < state.length; i++) {
                    state[i] = random.nextInt(4) - 1;
                }
                gui.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public PureGUI(int[] gameState) {
        setTitle("Gomoku");
        setIconImage(new ImageIcon("five.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(30,30,30,30));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new BoardPanel(gameState));
        setSize(1280, 720);
        addKeyListener(this);
        setVisible(true);
    }
    public PureGUI(int [] gameState, String info1, String info2, String info3){
        this.info1 = info1;
        this.info2 = info2;
        this.info3 = info3;
        setTitle("Gomoku");
        setIconImage(new ImageIcon("five.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(30,30,30,30));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new BoardPanel(gameState));
        setSize(1280, 720);
        addKeyListener(this);
        createStatusBar();
        setVisible(true);
    }

    private void createStatusBar() {
        JFrame jFrame = this;
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        jFrame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(jFrame.getWidth(), 60));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("Info");
        statusLabel.setFont(new Font(statusLabel.getFont().getFontName(), Font.PLAIN, 24));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
    }
    public void updateStatusBar(String value1, String value2, String value3){
        statusLabel.setText(info1 + ": " + value1 + ' ' + info2 + ": " + value2 + ' ' + info3 + ": " + value3);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
//    public void enableOSXFullscreen() {
//        try {
//            Class fullScreenUtil = Class.forName("com.apple.eawt.FullScreenUtilities");
//            Class params[] = new Class[]{Window.class, Boolean.TYPE};
//            Method method = fullScreenUtil.getMethod("setWindowCanFullScreen", params);
//            method.invoke(fullScreenUtil, this, true);
//
//            Class  application=Class.forName("com.apple.eawt.Application");
//            Class params1[] = new Class[]{};
//            Method method1=application.getMethod("getApplication",params1);
//            method1.invoke(application);
//            Class params2[] = new Class[]{Image.class};
//            Object o=  application.newInstance();
//            Method method2=application.getMethod("setDockIconImage",params2);
//            method2.invoke(o,new ImageIcon("five.png").getImage());
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {

    }
    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
            return;
        }

        String osName=System.getProperty("os.name").toLowerCase();
        if(osName.indexOf("mac")>=0){
            if(e.getKeyCode()==KeyEvent.VK_F5)
                requestToggleFullScreen();
        }else {
            if(e.getKeyCode()==KeyEvent.VK_F11) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                setUndecorated(true);
            }
        }


    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    public  void requestToggleFullScreen() {
        try {
            Class appClass = Class.forName("com.apple.eawt.Application");
            Class params[] = new Class[]{};

            Method getApplication = appClass.getMethod("getApplication", params);
            Object application = getApplication.invoke(appClass);
            Method requestToggleFulLScreen = application.getClass().getMethod("requestToggleFullScreen", Window.class);

            requestToggleFulLScreen.invoke(application, this);
        } catch (Exception e) {
            System.out.println("An exception occurred while trying to toggle full screen mode");
        }
    }
}

class BoardPanel_Pure extends JPanel {
    private Rectangle[][] rectangles;
    private int[] gameState;
    private int [] randomTable;
    private int rectSize;
    private int chessR;

    private  BufferedImage blackChessImage;
    private   BufferedImage whiteChessImage;
    private  BufferedImage woodImage;

    private int blackPlayer = 1;
    private int whitePlayer = 2;

    public void setGameState(int[] gameState) {
        this.gameState = gameState;
    }
    public BoardPanel_Pure (int[] gameState) {
        this.gameState = gameState;
        setDoubleBuffered(true);
        setBackground(Color.white);
        try {
            woodImage = ImageIO.read(new File("wood.jpg"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            blackChessImage = ImageIO.read(new File("black_chess.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            whiteChessImage = ImageIO.read(new File("white_chess.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                init();
            }
        });

        init();
        setBorder(new EmptyBorder(10,10,10,10));
        setOpaque(false);
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
        }catch (Exception e){

        }

    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Point p = new Point(this.getBounds().width / 2 - rectSize * (int) Math.sqrt(gameState.length) / 2, this.getBounds().height / 2 - rectSize * (int) Math.sqrt(gameState.length) / 2);
        g.drawImage(woodImage,20,20,getBounds().width-10,getBounds().height-10,this);
        g.setColor(Color.darkGray);
        ((Graphics2D)g).setStroke(new BasicStroke(3));
        int shadowSize=0;
        g.drawImage(woodImage, p.x-rectSize+shadowSize, p.y-rectSize+shadowSize, rectSize*((int) Math.sqrt(gameState.length)+1), rectSize*((int) Math.sqrt(gameState.length)+1), this);
        g.setColor(Color.BLACK);

        for (int i = 0; i < rectangles.length; i++) {
            for (int j = 0; j < rectangles[i].length; j++) {
                g.drawRect(rectangles[i][j].x, rectangles[i][j].y, rectangles[i][j].width, rectangles[i][j].height);
            }
        }
        if(gameState==null) return;
        AffineTransform transform = new AffineTransform();

        for (int i = 0; i < gameState.length; i++) {
            transform.rotate(randomTable[i]*Math.PI/2, blackChessImage.getWidth()/2, blackChessImage.getHeight()/2);
            if (gameState[i] == blackPlayer) {


                AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);

                g.drawImage(op.filter(blackChessImage,null),p.x - chessR / 2, p.y - chessR / 2, chessR, chessR,this);
            }

            else if (gameState[i] == whitePlayer) {
                AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
                g.drawImage(op.filter(whiteChessImage,null),p.x - chessR / 2, p.y - chessR / 2, chessR, chessR,this);
            }

            if (i % (int) Math.sqrt(gameState.length) == (int) Math.sqrt(gameState.length) - 1) {
                p.x = this.getBounds().width / 2 - rectSize * (int) Math.sqrt(gameState.length) / 2;
                p.y += rectSize;
            } else {
                p.x += rectSize;
            }
        }

    }

    public void initRandomTable(){
        Random random=new Random();
        randomTable=new int[gameState.length];
        for(int i=0;i<randomTable.length;i++){
            randomTable[i]=random.nextInt(4);
        }
    }
}
