package com.JavaBeginnerToPro.GomoWhiz;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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

public class GomokuGUI extends JFrame implements KeyListener {
   public GomokuGUI(int[] gameState) {
        Point point = new Point(this.getBounds().width, this.getBounds().height);
        setTitle("Gomoku");


        setIconImage(new ImageIcon("five.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//       GridBagConstraints bag3 = new GridBagConstraints();
//       bag3.gridx = 0;
//       bag3.gridy = 1;
//       bag3.gridwidth = 2;
//       bag3.gridheight = 1;
//       bag3.weightx = 0;
//       bag3.weighty = 0;
//       bag3.fill = GridBagConstraints.CENTER;
//       bag3.anchor = GridBagConstraints.WEST;
       setLayout(new BorderLayout());
       ((JPanel)getContentPane()).setBorder(new EmptyBorder(30,30,30,30));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new BoardPanel(gameState));
        setSize(1200, 800);

        String osName=System.getProperty("os.name").toLowerCase();
        if(osName.indexOf("mac")>=0){
            enableOSXFullscreen();
        }else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setUndecorated(true);
        }
        addKeyListener(this);

       try {
           Thread.sleep(1000);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }

       setVisible(true);
    }
    public static void main(String[] args) {


        Random random = new Random();
        int[] state = new int[36];
        for (int i = 0; i < state.length; i++) {
//            state[i] = random.nextInt(3) - 1;
            state[i]=0;
        }
        GomokuGUI gui = new GomokuGUI(state);


        while (true) {
            try {
                Thread.sleep(1000);
                for (int i = 0; i < state.length; i++) {
                        state[i] = random.nextInt(3) - 1;
                }
                gui.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void enableOSXFullscreen() {
        try {
            Class fullScreenUtil = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = fullScreenUtil.getMethod("setWindowCanFullScreen", params);
            method.invoke(fullScreenUtil, this, true);

            Class  application=Class.forName("com.apple.eawt.Application");
            Class params1[] = new Class[]{};
            Method method1=application.getMethod("getApplication",params1);
            method1.invoke(application);
            Class params2[] = new Class[]{Image.class};
            Object o=  application.newInstance();
            Method method2=application.getMethod("setDockIconImage",params2);
            method2.invoke(o,new ImageIcon("five.png").getImage());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
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
    public  void requestToggleFullScreen()
    {
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
class BoardPanel extends JXPanel {
    private Rectangle[][] rectangles;
    private int[] gameState;
    private int [] randomTable;
    private int rectSize;
    private int chessR;
    private int blackPlayer = 1;
    private int whitePlayer = 2;

    private  BufferedImage blackChessImage;
    private   BufferedImage whiteChessImage;
    private  BufferedImage woodImage;
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
            }

        });
        init();
//        setBorder(BorderFactory.createRaisedBevelBorder());
        setBorder(new EmptyBorder(10,10,10,10));
        setOpaque(false);
        DropShadowBorder shadow = new DropShadowBorder();
        shadow.setShadowColor(Color.BLACK);
        shadow.setShowLeftShadow(true);
        shadow.setShowRightShadow(false);
        shadow.setShowBottomShadow(false);
        shadow.setShowTopShadow(true);

        shadow.setShadowSize(20);

        setBorder(shadow);
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
       // g.drawRect(p.x-rectSize-2, p.y-rectSize-2, rectSize*((int) Math.sqrt(gameState.length)+1)+3, rectSize*((int) Math.sqrt(gameState.length)+1)+3);
        int shadowSize=((DropShadowBorder)getBorder()).getShadowSize();
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
//                g.setColor(Color.BLACK);
//                g.fillOval(p.x - chessR / 2, p.y - chessR / 2, chessR, chessR);
//                blackChessImage

            } else if (gameState[i] == whitePlayer) {
                AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
                g.drawImage(op.filter(whiteChessImage,null),p.x - chessR / 2, p.y - chessR / 2, chessR, chessR,this);
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

    public void initRandomTable(){
        Random random=new Random();
        randomTable=new int[gameState.length];
        for(int i=0;i<randomTable.length;i++){
            randomTable[i]=random.nextInt(4);
        }
    }
}

