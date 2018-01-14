package com.JavaBeginnerToPro.GomoWhiz.Version_1;

import com.JavaBeginnerToPro.GomoWhiz.minMax.Board;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;
import java.util.*;

public class SQLGomokuAI extends GomokuAI {
    public static Connection getConnection(String DBName) throws SQLException {
//        SQLiteConfig config = new SQLiteConfig();
//        config.setSharedCache(true);
//        config.enableRecursiveTriggers(true);
//        config.setOpenMode(SQLiteOpenMode.NOMUTEX);
//        SQLiteDataSource ds = new SQLiteDataSource(config);
////        ds.se
//        ds.setUrl("jdbc:sqlite:/Users/eebrian123tw93/Desktop/" + DBName + ".db");
//        Connection connection=ds.getConnection();
////        connection.set
//        return connection;

        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String datasource = "jdbc:mysql://localhost/chess?user=root&password=123456";
            conn = DriverManager.getConnection(datasource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    Connection conn;
    String tableName;
    String DBName;
    String sqlAdd;
    LinkedList<StringBuilder> updateValues;
    int length;
    List<Statement> statements;
    BoardPanel boardPanel;

    public SQLGomokuAI() {
        DBName = "chess";
        updateValues = new LinkedList<>();
        statements = new LinkedList<>();
        try {
            conn = getConnection(DBName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableName = gameBoardWidth + "_" + gameBoardWidth;
        if (!isTableExists()) {
            creatTable();
        }

        length = gameBoardWidth * gameBoardWidth;
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("insert into ").append(tableName).append("(statekey,");
        int length = gameBoardWidth * gameBoardWidth;
        for (int i = 0; i < length - 1; i++) {
            strBuilder.append("action" + i + ",");
        }
        strBuilder.append(("action" + (length - 1)) + ")values(?,");
        for (int i = 0; i < length - 1; i++) {
            strBuilder.append("?,");
        }
        strBuilder.append("?);");
        sqlAdd = strBuilder.toString();


//        boardPanel=new BoardPanel(currentState);
//
//        JFrame frame=new JFrame();
//        frame.add(boardPanel);
//        frame.setVisible(true);
//        frame.setSize(1920,1080);
//
//
//        Timer screenUpdate = new Timer(300, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int [] state=new int[currentState.length];
//                for (int i=0;i<currentState.length;i++){
//                    state[i]=currentState[i];
//                    if(state[i]==-1)state[i]+=2;
//                    else if(state[i]==1)state[i]+=1;
//                }
//                boardPanel.setGameState(state);
//               frame.repaint();
//
//            }
//        });
//        screenUpdate.start();
        if(Board.winStates.size()==0)
        Board.winStatesInit();
    }

    public void closeObject() {
        List<Statement> statementList = statements;
        statements=new LinkedList<>();
        new Thread() {
            @Override
            public void run() {
                for (Statement s : statementList) {
                    try {
                        if (!s.isClosed()) {
                            s.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                statementList.clear();
            }

        }.start();
    }

    public boolean isTableExists() {
        PreparedStatement pStatement = null;
        try {
            if (conn.isClosed()) conn = getConnection(DBName);
            String query = "show tables like \"" + tableName + "\";";
            pStatement = conn.prepareStatement(query);
            ResultSet resultSet = pStatement.executeQuery();

            if (!resultSet.next()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pStatement != null) {
                statements.add(pStatement);
            }
        }
        return true;
    }

    public synchronized void creatTable() {
        PreparedStatement statement = null;
        try {
            conn = SQLComokuAI_II.getConnection(DBName);
            String query = "DROP TABLE IF EXISTS " + this.tableName + ";";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("CREATE TABLE `chess`.`").append(this.tableName).append("`( `statekey`    varchar(260) NOT NULL, ");
            int length = gameBoardWidth * gameBoardWidth;
            for (int i = 0; i < length - 1; i++) {
                stringBuilder.append("`action" + i + "` varchar(30) NULL,");
            }

            stringBuilder.append("`action" + (length - 1) + "` varchar(30) NULL,");
            stringBuilder.append(" PRIMARY KEY (`statekey`));");
            statement = conn.prepareStatement(stringBuilder.toString());
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statements.add(statement);
            }
        }
    }

    public synchronized void add(QTableRow qTableRow) {
        PreparedStatement pst = null;
        try {
            if (conn.isClosed()) conn = getConnection(DBName);
            pst = conn.prepareStatement(sqlAdd);
            int idx = 1;
            pst.setString(idx++, qTableRow.getStateKey());
            Map<Integer, Double> actions = qTableRow.getActions();

            for (int i = 0; i < length; i++) {
                //actions.get()
                if (actions.get(i) != null) {
                    pst.setString(idx++, "" + actions.get(new Integer(i)));
                } else {
                    pst.setString(idx++, null);
                }
            }
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                statements.add(pst);
            }
        }
    }

    public synchronized void update(QTableRow qTableRow) {

        StringBuilder stringBuilder = new StringBuilder();
        PreparedStatement statement = null;
        try {
            if (conn.isClosed()) conn = getConnection(DBName);
            stringBuilder.append("update ").append(tableName).append(" set ");
            String sql;
            Map<Integer, Double> actions = qTableRow.getActions();
            for (int i = 0; i < length; i++) {
                if (actions.get(i) != null) {
                    stringBuilder.append("action" + i + "='" + actions.get(new Integer(i)) + "',");
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("  where statekey = '" + qTableRow.getStateKey() + "' ;");
            sql = stringBuilder.toString();
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statements.add(statement);
            }
        }
    }

    public synchronized QTableRow findByStateKey(String stateKey) {
        int length = gameBoardWidth * gameBoardWidth;
        PreparedStatement statement = null;
        try {
            if (conn.isClosed()) conn = getConnection(DBName);
            String sql = "SELECT * FROM " + this.tableName + " WHERE statekey =\"" + stateKey + "\";";
            statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                QTableRow qTableRow = new QTableRow();
                qTableRow.setStateKey(stateKey);
                Map<Integer, Double> action = new HashMap<>();
                for (int i = 0; i < length; i++) {
                    String a = resultSet.getString("action" + i);
                    if (a != null && a.length() != 0) {
                        action.put(i, Double.parseDouble(a));
                    }
                }
                qTableRow.setActions(action);
                return qTableRow;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statements.add(statement);
            }
        }
        return null;
    }

    String makeStateKey(int[] state, int currentPlayer) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < state.length; i++) {
            sb.append(state[i] + 2);
        }
        sb.append("]");
        String key = sb.append(currentPlayer + 2).toString(); //key = state + AI
        //if this game state hasn't happened before
        if (key.trim().length() == 1) {
            System.out.println();
        }
        if ((findByStateKey(key)) == null) {
            QTableRow qTableRow = new QTableRow();
            qTableRow.setStateKey(key);
            qTableRow.setActions(createActionQValueMap(state,currentPlayer));
            add(qTableRow);
        }
        return key;
    }


    HashMap<Integer, Double> createActionQValueMap(int[] state,int currentPlayer) {
        HashMap<Integer, Double> vals = new HashMap<Integer, Double>();
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0) //if the spot board is empty (will only make actions for empty places on the board)
                vals.put(i, (rand.nextDouble() * 0.3) - 0.15); // random [-0.15, 0.15]
        }
        return vals;
    }

    int getMaxQValueAction(String stateKey) {
        double maxQValue = -1.0;
        int maxQValueAction = -1;
        Map<Integer, Double> vals = findByStateKey(stateKey).getActions();
        for (Map.Entry<Integer, Double> entry : vals.entrySet()) {
            if (entry.getValue() > maxQValue) {
                maxQValue = entry.getValue();
                maxQValueAction = entry.getKey();
            }
        }
        return maxQValueAction;
    }

    int getMinQValueAction(String stateKey) {
        double minQValue = 1.0;
        int minQValueAction = -1;
        Map<Integer, Double> vals = findByStateKey(stateKey).getActions();
        for (Map.Entry<Integer, Double> entry : vals.entrySet()) {
            if (entry.getValue() < minQValue) {
                minQValue = entry.getValue();
                minQValueAction = entry.getKey();
            }
        }
        return minQValueAction;
    }

    double getMinQValue(String stateKey) {
        int minQAction = getMinQValueAction(stateKey);
        return findByStateKey(stateKey).getActions().get(minQAction);
    }

    double getMaxQValue(String stateKey) {
        int maxQAction = getMaxQValueAction(stateKey);
        return findByStateKey(stateKey).getActions().get(maxQAction);
    }

    double getQValue(String stateKey, int action) {

        if (findByStateKey(stateKey).getActions().get(new Integer(action)) == null) {
            Map<Integer, Double> a = findByStateKey(stateKey).getActions();
            System.out.println();
        }
        return findByStateKey(stateKey).getActions().get(action);
    }

    void setQValue(String stateKey, int action, double newQValue) {
        QTableRow qTableRow = findByStateKey(stateKey);
        qTableRow.getActions().put(action, newQValue);
        update(qTableRow);
//        System.out.println(gamesPlayed);
    }

    @Override
    void playOneGame() {
        super.playOneGame();
        if (statements.size() > 500) {
            closeObject();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
        SQLGomokuAI sqlGomokuAI = new SQLGomokuAI();
        System.out.println(new Date());
        sqlGomokuAI.train();
        sqlGomokuAI.play();
        System.out.println(new Date());
        Runtime.getRuntime().gc();
    }

    int chooseAction(String stateKey, int currentPlayer, int[] currentState) {

        if (learningMode == false) {
            //AI 1 looks for the maximum Q values (because it gets a positive reward when winning)
            if (currentPlayer == 1) return getMaxQValueAction(stateKey);
                //AI -1 looks for the minimum Q values (because it gets a negative reward when winning)
            else if (currentPlayer == -1) return getMinQValueAction(stateKey);
            //else if (currentPlayer == -1) return getMaxQValueAction(stateKey);
        }

        else {
                if (currentPlayer == 1)
                    return getMaxQValueAction(stateKey);
                else
                    return getMinQValueAction(stateKey);
        }

        //else if (currentPlayer == -1) return getMaxQValueAction(stateKey);
        return -1; //just in case. should cause an error
    }
}

class QTableRow {
    String stateKey;
    Map<Integer, Double> actions;

    public String getStateKey() {
        return stateKey;
    }

    public void setStateKey(String stateKey) {
        this.stateKey = stateKey;
    }

    public Map<Integer, Double> getActions() {
        return actions;
    }

    public void setActions(Map<Integer, Double> actions) {
        this.actions = actions;
    }
}
