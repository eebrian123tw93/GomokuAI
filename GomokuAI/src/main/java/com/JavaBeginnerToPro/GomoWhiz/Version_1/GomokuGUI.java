package com.JavaBeginnerToPro.GomoWhiz.Version_1;
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
        setSize(1280, 720);

        String osName=System.getProperty("os.name").toLowerCase();
        if(osName.indexOf("mac")>=0){
            enableOSXFullscreen();
        }
//        else {
//            setExtendedState(JFrame.MAXIMIZED_BOTH);
//            setUndecorated(true);
//        }
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

