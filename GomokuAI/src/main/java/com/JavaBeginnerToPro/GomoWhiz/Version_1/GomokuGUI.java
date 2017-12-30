package com.JavaBeginnerToPro.GomoWhiz.Version_1;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

//import java.util.Timer;

public class GomokuGUI extends JFrame implements KeyListener {
    BoardPanel panel;


    Playground playground;
    Mode mode;
    Player playerOne;
    Player playerTwo;
    JLabel statusLabel;
    Map<String, String> statusString;
    Timer timerTime;

    enum Mode {
        Human_VS_Human, AI_VS_AI, AI_VS_Human;
    }

    enum Player {
        Random, human, Minmax;
    }

    public static void main(String[] args) {
        new GomokuGUI(new int[225]);
    }

    public GomokuGUI(int[] gameState) {
        setTitle("Gomoku");

        setIconImage(new ImageIcon("five.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setLayout(new BorderLayout());
        panel = new BoardPanel(gameState);
//       panel = new PlayWithHumanBoardPanel(gameState);
        //getContentPane().add(panel, BorderLayout.CENTER);

        setSize(1280, 720);

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("mac") >= 0) {
            enableOSXFullscreen();
        }

        playground = new Playground();
        addKeyListener(this);

        mode = Mode.AI_VS_Human;
        playerOne = Player.Random;
        playerTwo = Player.human;

        timerTime = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStatusBar();
            }
        });
        timerTime.start();
        createMenuBar();
        createStatusBar();
        update();
        setVisible(true);
    }

    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        ////////////////GAME///////////////////
        JMenu game = new JMenu("Game");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        exitMenuItem.setToolTipText("Exit application");
        exitMenuItem.addActionListener((ActionEvent event) -> {
            System.exit(0);
        });
        JMenuItem restartMenuItem = new JMenuItem("Restart");
        restartMenuItem.setMnemonic(KeyEvent.VK_R);
        restartMenuItem.setToolTipText("Restart Game");
        restartMenuItem.addActionListener((ActionEvent event) -> {
            int[] gameState;
//            if(mode==Mode.AI_VS_Human){
//                gameState=panel.getGameState();
//            }else {
                gameState = playground.getState();
                panel.setGameState(gameState);
//            }
            for (int i = 0; i < gameState.length; i++) {
                gameState[i] = 0;
            }
            panel.playing = true;
            repaint();
            update();
        });
        game.add(restartMenuItem);
        game.add(exitMenuItem);
        ////////////////Mode///////////////////

        JMenu modeMenu = new JMenu("Mode");
        JMenuItem AI_VS_AI_MenuItem = new JMenuItem("AI_VS_AI");
        AI_VS_AI_MenuItem.setToolTipText("AI_VS_AI");
        AI_VS_AI_MenuItem.addActionListener((ActionEvent event) -> {
            if (mode != Mode.AI_VS_AI) {
                mode = Mode.AI_VS_AI;
                updateStatusBar();
                update();
            }
        });
        JMenuItem AI_VS_Human_MenuItem = new JMenuItem("AI_VS_Human");
        AI_VS_Human_MenuItem.setToolTipText("AI_VS_Human");
        AI_VS_Human_MenuItem.addActionListener((ActionEvent event) -> {
            if (mode != Mode.AI_VS_Human) {
                mode = Mode.AI_VS_Human;
                playerTwo=Player.human;
                updateStatusBar();
                update();
            }
        });
        JMenuItem Human_VS_Human_MenuItem = new JMenuItem("Human_VS_Human");
        Human_VS_Human_MenuItem.setToolTipText("Human_VS_Human");
        Human_VS_Human_MenuItem.addActionListener((ActionEvent event) -> {
            if(mode!=Mode.Human_VS_Human) {
                mode = Mode.Human_VS_Human;
                updateStatusBar();
                update();
            }
        });
        modeMenu.add(AI_VS_AI_MenuItem);
        modeMenu.add(AI_VS_Human_MenuItem);
        modeMenu.add(Human_VS_Human_MenuItem);

        ////////////////Player1///////////////////
        JMenu player1Menu = new JMenu("Player1");
        JMenuItem randomAI = new JMenuItem("Random");
        randomAI.addActionListener((ActionEvent event) -> {
            playerOne = Player.Random;
            update();
        });
        JMenuItem conwayQTableAI = new JMenuItem("Pure QTable");
        conwayQTableAI.addActionListener((ActionEvent event) -> {
            update();
        });
        JMenuItem forcedActionAI = new JMenuItem("Forced actions");
        forcedActionAI.addActionListener((ActionEvent event) -> {
            update();
        });
        JMenuItem minMaxAI = new JMenuItem("MinMax");
        minMaxAI.addActionListener((ActionEvent event) -> {
            playerOne = Player.Minmax;
            update();
        });
        JMenuItem minMaxForcedAI = new JMenuItem("MinMax with forced actions");
        minMaxForcedAI.addActionListener((ActionEvent event) -> {
            update();
        });
        JMenuItem qTableForcedAI = new JMenuItem("QTable with forced actions");
        qTableForcedAI.addActionListener((ActionEvent event) -> {
            update();
        });
        player1Menu.add(randomAI);
        player1Menu.add(conwayQTableAI);
        player1Menu.add(forcedActionAI);
        player1Menu.add(minMaxAI);
        player1Menu.add(minMaxForcedAI);
        player1Menu.add(qTableForcedAI);


        ////////////////Player2///////////////////
        JMenu player2Menu = new JMenu("Player2");
        JMenuItem randomAI2 = new JMenuItem("Random");
        JMenuItem conwayQTableAI2 = new JMenuItem("Pure QTable");
        JMenuItem forcedActionAI2 = new JMenuItem("Forced actions");
        JMenuItem minMaxAI2 = new JMenuItem("MinMax");
        JMenuItem minMaxForcedAI2 = new JMenuItem("MinMax with forced actions");
        JMenuItem qTableForcedAI2 = new JMenuItem("QTable with forced actions");

        JMenuItem human2 = new JMenuItem("Human");
        human2.addActionListener((ActionEvent event) -> {
            if (mode == Mode.AI_VS_Human) {
                playerTwo = Player.human;
            }
        });

//        player1Menu.add(human);

        player2Menu.add(randomAI2);
        player2Menu.add(conwayQTableAI2);
        player2Menu.add(forcedActionAI2);
        player2Menu.add(minMaxAI2);
        player2Menu.add(minMaxForcedAI2);
        player2Menu.add(qTableForcedAI2);
        player2Menu.add(human2);

//


        menuBar.add(game);
        menuBar.add(modeMenu);
        menuBar.add(player1Menu);
        menuBar.add(player2Menu);
        setJMenuBar(menuBar);
    }

    public void createStatusBar() {

        JFrame jFrame = this;
        JPanel statusPanel = new JPanel();
        statusString = new TreeMap<>();
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        jFrame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(jFrame.getWidth(), 30));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        statusString.put("Mode:", mode.toString());
        statusString.put("Player1:", playerOne.toString());
        statusString.put("Player2:", playerTwo.toString());
        statusString.put("Time:", new Date().toString());
        String status = "";
        for (Map.Entry<String, String> entry : statusString.entrySet()) {
            status += "\t[" + entry.getKey() + " " + entry.getValue() + "]\t";
        }
        statusLabel = new JLabel(status);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
//        jFrame.setVisible(true);
    }

    public void updateStatusBar(){
        String status = "";
        statusString.replace("Mode:", mode.toString());
        statusString.replace("Player1:", playerOne.toString());
        statusString.replace("Player2:", playerTwo.toString());
        statusString.replace("Time:", new Date().toString());
        for (Map.Entry<String, String> entry : statusString.entrySet()) {
            status += "\t[" + entry.getKey() + " " + entry.getValue() + "]\t";
        }
        statusLabel.setText(status);

    }
    public void update() {

        if (mode == Mode.AI_VS_Human) {
            BoardPanel panel = new PlayWithHumanBoardPanel(playground.getState());
            if(getContentPane().getComponentCount()>1){
                getContentPane().remove(1);
            }
//            this.panel.setEnabled(false);
            getContentPane().add(panel,BorderLayout.CENTER,1);
            repaint();
        }else if(mode ==Mode.AI_VS_AI){
            BoardPanel panel = new BoardPanel(playground.getState());
            if(getContentPane().getComponentCount()>1){
                getContentPane().remove(1);
            }
//            this.panel.setEnabled(false);
            getContentPane().add(panel,BorderLayout.CENTER,1);
            repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

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

