package com.JavaBeginnerToPro.GomoWhiz.GUI;

import com.JavaBeginnerToPro.GomoWhiz.utilities.*;

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
    Timer screenUpdate;
    private int screenDelay = 100;
    String brainTextPath;
    JFileChooser chooser;

    enum Mode {
        Human_VS_Human, AI_VS_AI, AI_VS_Human;
    }

    enum Player {
        Random, human, Minmax, PureQTable, ForcedActions, MinMaxWithForcedActions, QTableWithForcedActions;
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
//        panel = new BoardPanel(gameState);
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
        screenUpdate = new Timer(screenDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getContentPane().repaint();
            }
        });
        screenUpdate.start();
        timerTime.start();
        createMenuBar();
        createStatusBar();
        updatePlayer();
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
            gameState = playground.getState();
            panel.setGameState(gameState);
            for (int i = 0; i < gameState.length; i++) {
                gameState[i] = 0;
            }
            panel.playing = true;
            repaint();
            update();
            if (mode == Mode.AI_VS_AI) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread() {
                    @Override
                    public void run() {
                        playground.play();
                    }

                }.start();
            }
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
                playerTwo = Player.Random;
                updatePlayer();
                updateStatusBar();
                update();
            }
        });
        JMenuItem AI_VS_Human_MenuItem = new JMenuItem("AI_VS_Human");
        AI_VS_Human_MenuItem.setToolTipText("AI_VS_Human");
        AI_VS_Human_MenuItem.addActionListener((ActionEvent event) -> {
            if (mode != Mode.AI_VS_Human) {
                mode = Mode.AI_VS_Human;
                playerTwo = Player.human;
                updateStatusBar();
                update();
            }
        });
        JMenuItem Human_VS_Human_MenuItem = new JMenuItem("Human_VS_Human");
        Human_VS_Human_MenuItem.setToolTipText("Human_VS_Human");
        Human_VS_Human_MenuItem.addActionListener((ActionEvent event) -> {
            if (mode != Mode.Human_VS_Human) {
                mode = Mode.Human_VS_Human;
                playerTwo = Player.human;
                playerOne = Player.human;
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
            updatePlayer();
        });
        JMenuItem conwayQTableAI = new JMenuItem("Pure QTable");
        conwayQTableAI.addActionListener((ActionEvent event) -> {
            if (jFileChooserDialog()) {
                playerOne = Player.PureQTable;
                updatePlayer();
            }
        });
        JMenuItem forcedActionAI = new JMenuItem("Forced actions");
        forcedActionAI.addActionListener((ActionEvent event) -> {
            playerOne = Player.ForcedActions;
            updatePlayer();
        });
        JMenuItem minMaxAI = new JMenuItem("MinMax");
        minMaxAI.addActionListener((ActionEvent event) -> {
            playerOne = Player.Minmax;
            updatePlayer();
        });
        JMenuItem minMaxForcedAI = new JMenuItem("MinMax with forced actions");
        minMaxForcedAI.addActionListener((ActionEvent event) -> {
            playerOne = Player.MinMaxWithForcedActions;
            updatePlayer();
        });
        JMenuItem qTableForcedAI = new JMenuItem("QTable with forced actions");
        qTableForcedAI.addActionListener((ActionEvent event) -> {
            if (jFileChooserDialog()) {
                playerOne = Player.QTableWithForcedActions;
                updatePlayer();
            }

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
        randomAI2.addActionListener((ActionEvent event) -> {
            if (mode == Mode.AI_VS_Human) return;
            playerTwo = Player.Random;
            updatePlayer();
        });
        JMenuItem conwayQTableAI2 = new JMenuItem("Pure QTable");
        conwayQTableAI2.addActionListener((ActionEvent event) -> {
            if (mode == Mode.AI_VS_Human) return;

            if (jFileChooserDialog()) {
                playerTwo = Player.PureQTable;
                updatePlayer();
            }

        });
        JMenuItem forcedActionAI2 = new JMenuItem("Forced actions");
        forcedActionAI2.addActionListener((ActionEvent event) -> {
            if (mode == Mode.AI_VS_Human) return;
            playerTwo = Player.ForcedActions;
            updatePlayer();
        });
        JMenuItem minMaxAI2 = new JMenuItem("MinMax");
        minMaxAI2.addActionListener((ActionEvent event) -> {
            if (mode == Mode.AI_VS_Human) return;
            playerTwo = Player.Minmax;
            updatePlayer();
        });
        JMenuItem minMaxForcedAI2 = new JMenuItem("MinMax with forced actions");
        minMaxForcedAI2.addActionListener((ActionEvent event) -> {
            if (mode == Mode.AI_VS_Human) return;
            playerTwo = Player.MinMaxWithForcedActions;
            updatePlayer();
        });
        JMenuItem qTableForcedAI2 = new JMenuItem("QTable with forced actions");
        qTableForcedAI2.addActionListener((ActionEvent event) -> {
            if (mode == Mode.AI_VS_Human) return;
            if (jFileChooserDialog()) {
                playerTwo = Player.QTableWithForcedActions;
                updatePlayer();
            }

        });

        JMenuItem human2 = new JMenuItem("Human");
        human2.addActionListener((ActionEvent event) -> {
            if (mode == Mode.AI_VS_Human) {
                playerTwo = Player.human;
            }
        });

        player2Menu.add(randomAI2);
        player2Menu.add(conwayQTableAI2);
        player2Menu.add(forcedActionAI2);
        player2Menu.add(minMaxAI2);
        player2Menu.add(minMaxForcedAI2);
        player2Menu.add(qTableForcedAI2);
        player2Menu.add(human2);

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
        statusString.put("Player1 | BLACK :", playerOne.toString());
        statusString.put("Player2 | WHITE:", playerTwo.toString());
        statusString.put("Time:", new Date().toString());
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : statusString.entrySet()) {
            stringBuilder.append("\t[").append(entry.getKey()).append(" ").append(entry.getValue()).append("]\t");
        }
        statusLabel = new JLabel(stringBuilder.toString());
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel);
//        jFrame.setVisible(true);
    }

    public void updateStatusBar() {
        statusString.replace("Mode:", mode.toString());
        statusString.replace("Player1 | BLACK :", playerOne.toString());
        statusString.replace("Player2 | WHITE:", playerTwo.toString());
        statusString.replace("Time:", new Date().toString());
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : statusString.entrySet()) {
            stringBuilder.append("\t[").append(entry.getKey()).append(" ").append(entry.getValue()).append("]\t");
        }
        statusLabel.setText(stringBuilder.toString());
    }

    public void updatePlayer() {
        switch (playerOne) {
            case Random:
                playground.setAI1(new Random());
                break;
            case Minmax:
                playground.setAI1(new MinMax(1));
                break;
            case PureQTable:
                playground.setAI1(new PureQTable(1, brainTextPath));
                break;
            case MinMaxWithForcedActions:
                playground.setAI1(new MinMaxWithForcedActions(1));
                break;
            case QTableWithForcedActions:
                playground.setAI1(new QTableWithForcedActions(1, brainTextPath));
                break;
            case ForcedActions:
                playground.setAI1(new ForcedActions(1));
                break;
            default:
                break;
        }
        switch (playerTwo) {
            case Random:
                playground.setAI2(new Random());
                break;
            case Minmax:
                playground.setAI2(new MinMax(2));
                break;
            case PureQTable:
                playground.setAI2(new PureQTable(2, brainTextPath));
                break;
            case MinMaxWithForcedActions:
                playground.setAI2(new MinMaxWithForcedActions(2));
                break;
            case QTableWithForcedActions:
                playground.setAI2(new QTableWithForcedActions(2, brainTextPath));
                break;
            case ForcedActions:
                playground.setAI2(new ForcedActions(2));
                break;
            default:
                break;
        }
    }

    public void update() {
        if (mode == Mode.AI_VS_Human) {
            panel = new PlayWithHumanBoardPanel(playground.getState(), playground);
            ((PlayWithHumanBoardPanel) panel).setAI();
            if (getContentPane().getComponentCount() > 1) {
                getContentPane().remove(1);
            }
            getContentPane().add(panel, BorderLayout.CENTER, 1);
            repaint();
        } else if (mode == Mode.AI_VS_AI) {
            panel = new BoardPanel(playground.getState());
            if (getContentPane().getComponentCount() > 1) {
                getContentPane().remove(1);
            }
            getContentPane().add(panel, BorderLayout.CENTER, 1);
            repaint();
        }
    }

    public boolean jFileChooserDialog() {
        if (chooser == null) {
            chooser = new JFileChooser(System.getProperty("user.home")+"/Desktop");
//            FileNameExtensionFilter filter = new FileNameExtensionFilter(
//                    "文字檔", "txt");
//            chooser.setFileFilter(filter);
        }
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());
            brainTextPath = chooser.getSelectedFile().getName();
            return true;
        }
        return false;
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

