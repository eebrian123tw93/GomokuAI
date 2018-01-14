package com.JavaBeginnerToPro.GomoWhiz.Version_1;

import com.JavaBeginnerToPro.GomoWhiz.minMax.Board;
import com.JavaBeginnerToPro.GomoWhiz.minMax.Score;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLComokuAI_II extends SQLGomokuAI {


    @Override
    String makeStateKey(int[] state, int currentPlayer) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < state.length; i++) {
            sb.append(state[i] + 2);
        }
        sb.append("]");
        String key = sb.append(currentPlayer + 2).toString(); //key = state + AI
        //if this game state hasn't happened before
        //[222222222222222222221322222222222222212312222222222323232222222222111321222222222233133222222221223123222222222132332122222223311122322222222112332222222222112322222222221322222222222221222222222222222222222222222222222222222]1
        //[222222222222222222222322222222222222212222222222222231222222222221232122222222213132331222222222331323232223331311313322222211131113222222223311322222222213212222222222111222222222222322222222222222212222222222222222222222222]1
        QTableRow qTableRow;
        if ((qTableRow = findByStateKey(key)) == null) {
            qTableRow = new QTableRow();
            qTableRow.setStateKey(key);
            qTableRow.setActions(createActionQValueMap(state));
            add(qTableRow);
        }
        qMap.put(qTableRow.getStateKey(), new HashMap<>(qTableRow.getActions()));
        return key;
    }

    public Board transfer(int[] state) {
        Board board = new Board(gameBoardWidth, winRequire);
        int j = 0, k = 0;
        for (int i = 0; i < state.length; i++) {
//            board[j][k]=
            if (state[i] == 1) board.getBoard()[j][k] = 'o';
            if (state[i] == -1) board.getBoard()[j][k] = 'x';
            if (i % (int) Math.sqrt(state.length) == (int) Math.sqrt(state.length) - 1 && i != 0) {
                j++;
                k = 0;
            } else {
                k++;
            }

        }
        return board;
    }

    HashMap<Integer, Double> createActionQValueMap(int[] state) {
        Board board = transfer(state);

        // create hashmap of all valid actions in this state and random initial qvalues
        HashMap<Integer, Double> vals = new HashMap<Integer, Double>();
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0 && board.hasNeighbor(i / gameBoardWidth, i % gameBoardWidth)) //if the spot board is empty (will only make actions for empty places on the board)
                vals.put(i, (rand.nextDouble() * 0.3) - 0.15); // random [-0.15, 0.15]
        }
        if (vals.size() == 0) {
            vals.put(state.length / 2, (rand.nextDouble() * 0.3) - 0.15);
        }
        return vals;
    }

    @Override
    int getMaxQValueAction(String stateKey) {
        double maxQValue = -1.0;
        int maxQValueAction = -1;
        Map<Integer, Double> vals = qMap.get(stateKey);
        for (Map.Entry<Integer, Double> entry : vals.entrySet()) {
            if (entry.getValue() > maxQValue) {
                maxQValue = entry.getValue();
                maxQValueAction = entry.getKey();
            }
        }
        return maxQValueAction;
    }

    @Override
    int getMinQValueAction(String stateKey) {
        double minQValue = 1.0;
        int minQValueAction = -1;
        Map<Integer, Double> vals = qMap.get(stateKey);
        for (Map.Entry<Integer, Double> entry : vals.entrySet()) {
            if (entry.getValue() < minQValue) {
                minQValue = entry.getValue();
                minQValueAction = entry.getKey();
            }
        }
        return minQValueAction;
    }

    @Override
    double getMinQValue(String stateKey) {
        int minQAction = getMinQValueAction(stateKey);
        return qMap.get(stateKey).get(minQAction);
    }

    @Override
    double getMaxQValue(String stateKey) {
        int maxQAction = getMaxQValueAction(stateKey);
        return qMap.get(stateKey).get(maxQAction);
    }

    @Override
    double getQValue(String stateKey, int action) {
        return qMap.get(stateKey).get(action);
    }

    @Override
    void setQValue(String stateKey, int action, double newQValue) {
        qMap.get(stateKey).put(action, newQValue);
    }


    public synchronized void updateValues(Map<String, HashMap<Integer, Double>> qTable) {

        List<QTableRow> updateValues = new LinkedList<>();
        for (Map.Entry<String, HashMap<Integer, Double>> entry : qTable.entrySet()) {
            QTableRow qTableRow = new QTableRow();
            qTableRow.setStateKey(entry.getKey());
            qTableRow.setActions(entry.getValue());
            updateValues.add(qTableRow);
        }
        qTable.clear();
        Statement statement = null;
        try {
            if (conn.isClosed()) conn = getConnection(DBName);
//            conn.setAutoCommit(false);
            statement = conn.createStatement();
            statement.clearBatch();
            for (QTableRow qTableRow : updateValues) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("update ").append(tableName).append(" set  ");

                String sql;
                Map<Integer, Double> actions = qTableRow.getActions();
                if (actions.size() == 0) continue;
                for (int i = 0; i < length; i++) {
                    if (actions.get(i) != null) {
                        stringBuilder.append("action" + i + "='" + actions.get(new Integer(i)) + "',");
                    }
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                stringBuilder.append("   WHERE   statekey = '" + qTableRow.getStateKey() + "' ;");
                sql = stringBuilder.toString();
                statement.addBatch(sql);
//                System.out.println(sql);
//                statement.executeUpdate(sql);
            }
            updateValues.clear();
            statement.executeBatch();
//            conn.commit();
//            conn.setAutoCommit(true);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statements.add(statement);
            }
        }

    }

    @Override
    void playOneGame() {
        super.playOneGame();
        updateValues(qMap);
    }


    int chooseAction(String stateKey, int currentPlayer, int[] currentState) {
//        if (currentPlayer == 1) {
        if(true) {
            List<Integer> five = new ArrayList<>();
            List<Integer> liveFour = new ArrayList<>();
            List<Integer> liveThree = new ArrayList<>();
            Board board = transfer(currentState);
            HashMap<Integer, Double> actions = qMap.get(stateKey);
            int player, numOppenent;
            if (currentPlayer == 1) {
                player = 1;
                numOppenent = 2;
            } else {
                numOppenent = 1;
                player = 2;
            }
            for (Map.Entry<Integer, Double> entry : actions.entrySet()) {
                int action = entry.getKey();
                int pointPlayer = board.evaluate(action, player);
                int pointOpponent = board.evaluate(action, numOppenent);
                if (pointPlayer == Score.FIVE) {
                    five.add(action);
                    return action;
                } else if (pointOpponent == Score.FIVE) {
                    five.add(action);
                } else if (pointPlayer == Score.LIVE_FOUR) {
                    liveFour.add(action);
                } else if (pointOpponent == Score.LIVE_FOUR) {
                    liveFour.add(action);
                } else if (pointPlayer == Score.LIVE_THREE) {
                    liveThree.add(0, action);
                } else if (pointOpponent == Score.THREE) {
                    liveThree.add(action);
                }
            }
            if (five.size() != 0) {
                return five.get(rand.nextInt(five.size()));
            }
            if (liveFour.size() != 0) {
                return liveFour.get(rand.nextInt(liveFour.size()));
            }
            if (liveThree.size() != 0) {
                return liveThree.get(rand.nextInt(liveThree.size()));
            }
        }

        if (learningMode == false) {
            //AI 1 looks for the maximum Q values (because it gets a positive reward when winning)
            if (currentPlayer == 1) return getMaxQValueAction(stateKey);
                //AI -1 looks for the minimum Q values (because it gets a negative reward when winning)
            else if (currentPlayer == -1) return getMinQValueAction(stateKey);
            //else if (currentPlayer == -1) return getMaxQValueAction(stateKey);
        } else {
            if (rand.nextDouble() > 0.75) {
                if (currentPlayer == 1)
                    return getMaxQValueAction(stateKey);
                else
                    return getMinQValueAction(stateKey);
            }else{
                HashMap<Integer, Double> actionsAndValue = qMap.get(stateKey);
                List<Integer>actions=new ArrayList<>();
                for (Map.Entry<Integer, Double> entry : actionsAndValue.entrySet()) {
                    actions.add(entry.getKey());
                }
                return actions.get(rand.nextInt(actions.size()));
            }
        }

        //else if (currentPlayer == -1) return getMaxQValueAction(stateKey);
        return -1; //just in case. should cause an error
    }


    public static Connection getConnection(String DBName) {
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


    public static void main(String[] args) {
        int th = Runtime.getRuntime().availableProcessors() * 2 - 1;
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 1; i++) {
            Thread thread = new SQLThread();
            service.execute(thread);
        }

    }
}

class SQLThread extends Thread {

    @Override
    public void run() {
        try {
            while (true) {

                SQLComokuAI_II sqlGomokuAI = new SQLComokuAI_II();
                System.out.println(new Date());
//                sqlGomokuAI.displayBoard=true;
//                sqlGomokuAI.learningMode=true;
                sqlGomokuAI.train();
//                sqlGomokuAI.play();
                sqlGomokuAI.conn.close();
                System.out.println(new Date());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
