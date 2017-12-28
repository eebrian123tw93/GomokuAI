package com.JavaBeginnerToPro.GomoWhiz.Version_1;

import com.JavaBeginnerToPro.GomoWhiz.minMax.Board;
import com.JavaBeginnerToPro.GomoWhiz.minMax.SmartAgent;

import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLvsMini extends SQLComokuAI_II {

    @Override
    void playOneGame() {
        SmartAgent smartAgent = new SmartAgent(gameBoardWidth, winRequire,1);
        for (int i = 0; i < GAMEBOARD_SIZE; ++i) currentState[i] = 0; //reset current state to all 0
        int[] nextState; //will be used between turns
        int currentPlayer;
        int nextPlayer;
        gameEnded = false;
        int movesRemaining = GAMEBOARD_SIZE;
        double reward;
        String currentStateKey;
        String nextStateKey;

        //choose first player
        if (rand.nextBoolean()) currentPlayer = 1;
        else currentPlayer = -1;
//        currentPlayer = -1;

        while (!gameEnded) {
            if (currentPlayer == 1) nextPlayer = -1;
            else nextPlayer = 1;

            currentStateKey = makeStateKey(currentState, currentPlayer);
            nextState = currentState.clone();

            int action;
            if (currentPlayer == -1) {
                action = chooseAction(currentStateKey, currentPlayer, currentState);
            } else {
                action = smartAgent.move(minusOneToOne(currentState));
            }


            nextState[action] = currentPlayer; //place the piece
            --movesRemaining;
            reward = evalReward(nextState);
            if (reward == 1 || reward == -1 || movesRemaining <= 0) {
                gameEnded = true;
                if (reward == 1) {
                    ++wins; //player 1 win
                    //System.out.println("player 1 win");
                } else if (reward == -1) {
                    ++losses; //player -1 win
                    //System.out.println("player -1 win");
                } else if (movesRemaining <= 0) {
                    ++ties;//tie
                    //System.out.println("tie");
                }
            }

            nextStateKey = makeStateKey(nextState, nextPlayer);
            updateQValues(currentStateKey, currentPlayer, action, nextStateKey, reward);
            currentState = nextState.clone(); //change currentState to the state after the action

            //swap players
            if (currentPlayer == 1) currentPlayer = -1;
            else currentPlayer = 1;


            if (displayBoard && isPlaying) {
                try {
                    ((BoardPanel) gui.getContentPane().getComponent(0)).setGameState(currentState);
                    gui.repaint();
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
        updateValues(qMap);
        if (statements.size() > 100) {
            closeObject();
        }
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
            if (state[i] == 0 && board.hasNeighbor(i / gameBoardWidth,i % gameBoardWidth)) //if the spot board is empty (will only make actions for empty places on the board)
                vals.put(i, (rand.nextDouble() * 0.3) - 0.15); // random [-0.15, 0.15]
        }
        if(vals.size()==0){
            vals.put(state.length/2,(rand.nextDouble() * 0.3) - 0.15);
        }
        return vals;
    }

    @Override
    String makeStateKey(int[] state, int currentPlayer) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < state.length; i++) {
            sb.append(state[i] + 2);
        }
        sb.append("]");
        String key = sb.append(currentPlayer + 2).toString(); //key = state + player
        //if this game state hasn't happened before
        QTableRow qTableRow;
        if ((qTableRow = findByStateKey(key)) == null) {
            qTableRow = new QTableRow();
            qTableRow.setStateKey(key);
            qTableRow.setActions(createActionQValueMap(state));
            if (currentPlayer == -1) {
                add(qTableRow);
            }
        }
        qMap.put(qTableRow.getStateKey(), new HashMap<>(qTableRow.getActions()));
        return key;
    }

    public synchronized void updateValues(Map<String, HashMap<Integer, Double>> qTable) {

        List<QTableRow> updateValues = new LinkedList<>();
        for (Map.Entry<String, HashMap<Integer, Double>> entry : qTable.entrySet()) {
            if (entry.getKey().charAt(entry.getKey().length() - 1) == '1') {
                continue;
            }
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

    public String transfer(int action) {
        int x = action / gameBoardWidth;
        int y = action % gameBoardWidth;
//        return  action;
        return x + " " + y;
    }

    public int transfer(String move) {
        String[] xy = move.split(" ");
        return Integer.parseInt(xy[0]) * gameBoardWidth + Integer.parseInt(xy[1]);
    }

    public int[] minusOneToOne(int[] state) {
        for (int i = 0; i < state.length; i++) {
            if (state[i] == -1) state[i] = 2;
        }
        return state;
    }

    public static void main(String[] args) {
        int th = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i <1; i++) {
            Thread thread = new SQLvsMiniThread();
            service.execute(thread);
        }
    }
}

class SQLvsMiniThread extends Thread {

    @Override
    public void run() {
        try {
            while (true) {

                SQLvsMini sqlGomokuAI = new SQLvsMini();
                System.out.println(new Date());
//                sqlGomokuAI.train();
                sqlGomokuAI.play();
                sqlGomokuAI.conn.close();
                System.out.println(new Date());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
