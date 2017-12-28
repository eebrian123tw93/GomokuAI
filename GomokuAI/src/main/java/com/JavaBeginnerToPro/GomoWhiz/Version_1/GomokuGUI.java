package com.JavaBeginnerToPro.GomoWhiz.Version_1;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import java.util.Random;

public class GomokuGUI extends JFrame implements KeyListener {
    PlayWithHumanBoardPanel panel;
    public GomokuGUI(int[] gameState) {
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
        getContentPane().setLayout(new BorderLayout());
//        getContentPane().add(new BoardPanel(gameState),BorderLayout.CENTER);
        panel=new PlayWithHumanBoardPanel(gameState);
        getContentPane().add(panel,BorderLayout.CENTER);
        setSize(1280, 720);

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("mac") >= 0) {
            enableOSXFullscreen();
        }
//        else {
//            setExtendedState(JFrame.MAXIMIZED_BOTH);
//            setUndecorated(true);
//        }
        addKeyListener(this);

        createMenuBar();
        createStatusBar();

        setVisible(true);
    }

    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu game = new JMenu("Game");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        exitMenuItem.setToolTipText("Exit application");
        exitMenuItem.addActionListener((ActionEvent event) -> {
            System.exit(0);
        });
        JMenuItem restartMenuItem=new JMenuItem("Restart");
        restartMenuItem.setMnemonic(KeyEvent.VK_R);
        restartMenuItem.setToolTipText("Restart Game");
        restartMenuItem.addActionListener((ActionEvent event) -> {
            int [] gameState= panel.getGameState();
            for(int i=0;i<gameState.length;i++){
                gameState[i]=0;
            }
           panel.playing=true;
            repaint();
        });
        game.add(restartMenuItem);
        game.add(exitMenuItem);
        menuBar.add(game);
        setJMenuBar(menuBar);
    }

    public void createStatusBar() {
        JFrame jFrame = this;
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(Color.YELLOW);
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        jFrame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(jFrame.getWidth(), 20));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        JLabel statusLabel = new JLabel("status");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
//        jFrame.setVisible(true);
    }

    public static void main(String[] args) {


        Random random = new Random();
        int[] state = new int[225];
        for (int i = 0; i < state.length; i++) {
//            state[i] = random.nextInt(3) - 1;
            state[i] = 0;
        }
        GomokuGUI gui = new GomokuGUI(state);


    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void enableOSXFullscreen() {
        try {
            Class fullScreenUtil = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = fullScreenUtil.getMethod("setWindowCanFullScreen", params);
            method.invoke(fullScreenUtil, this, true);

            Class application = Class.forName("com.apple.eawt.Application");
            Class params1[] = new Class[]{};
            Method method1 = application.getMethod("getApplication", params1);
            method1.invoke(application);
            Class params2[] = new Class[]{Image.class};
            Object o = application.newInstance();
            Method method2 = application.getMethod("setDockIconImage", params2);
            method2.invoke(o, new ImageIcon("five.png").getImage());

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

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);

            return;
        }

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("mac") >= 0) {
            if (e.getKeyCode() == KeyEvent.VK_F5)
                requestToggleFullScreen();
        } else {
            if (e.getKeyCode() == KeyEvent.VK_F11) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                setUndecorated(true);
            }
        }


    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void requestToggleFullScreen() {
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

