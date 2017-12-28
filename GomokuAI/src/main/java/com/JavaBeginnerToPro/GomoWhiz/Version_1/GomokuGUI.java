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
    Playground playground;
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
        getContentPane().add(new BoardPanel(gameState),BorderLayout.CENTER);
     //   panel=new PlayWithHumanBoardPanel(gameState);
     //   getContentPane().add(panel,BorderLayout.CENTER);
        setSize(1280, 720);

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("mac") >= 0) {
            enableOSXFullscreen();
        }
//        else {
//            setExtendedState(JFrame.MAXIMIZED_BOTH);
//            setUndecorated(true);
//        }

        playground=new Playground();

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
        JMenu player1Menu = new JMenu("Player1");
        JMenu player2Menu = new JMenu("Player2");

        JMenuItem randomAI = new JMenuItem("Random");
        randomAI.addActionListener((ActionEvent event) -> {
            playground.setPlayer1(new com.JavaBeginnerToPro.GomoWhiz.Version_1.Random());
        });

        JMenuItem conwayQTableAI = new JMenuItem("Pure QTable");
//        conwayQTableAI.addActionListener((ActionEvent event) -> {
//            playground.setPlayer1(new com.JavaBeginnerToPro.GomoWhiz.Version_1.Random());
//        });

        JMenuItem forcedActionAI = new JMenuItem("Forced actions");
        JMenuItem minMaxAI = new JMenuItem("MinMax");
        JMenuItem minMaxForcedAI = new JMenuItem("MinMax with forced actions");
        JMenuItem qTableForcedAI = new JMenuItem("QTable with forced actions");
        JMenuItem randomAI2 = new JMenuItem("Random");
        JMenuItem conwayQTableAI2 = new JMenuItem("Pure QTable");
        JMenuItem forcedActionAI2 = new JMenuItem("Forced actions");
        JMenuItem minMaxAI2 = new JMenuItem("MinMax");
        JMenuItem minMaxForcedAI2 = new JMenuItem("MinMax with forced actions");
        JMenuItem qTableForcedAI2 = new JMenuItem("QTable with forced actions");

        player1Menu.add(randomAI);
        player1Menu.add(conwayQTableAI);
        player1Menu.add(forcedActionAI);
        player1Menu.add(minMaxAI);
        player1Menu.add(minMaxForcedAI);
        player1Menu.add(qTableForcedAI);

        player2Menu.add(randomAI2);
        player2Menu.add(conwayQTableAI2);
        player2Menu.add(forcedActionAI2);
        player2Menu.add(minMaxAI2);
        player2Menu.add(minMaxForcedAI2);
        player2Menu.add(qTableForcedAI2);

        game.add(restartMenuItem);
        game.add(exitMenuItem);
        menuBar.add(game);
        menuBar.add(player1Menu);
        menuBar.add(player2Menu);
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
        new GomokuGUI( new int[225]);
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

