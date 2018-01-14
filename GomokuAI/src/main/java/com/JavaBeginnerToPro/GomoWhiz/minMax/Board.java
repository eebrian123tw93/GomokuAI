package com.JavaBeginnerToPro.GomoWhiz.minMax;

import java.util.*;

/**
 * Board class that contains all board manipulation and look around methods
 * first player / black == 'x'
 * second player / white == 'o'
 * empty space == '.'
 * Moves are formatted as "row column" (without quotes) where row and column are integers
 */
public class Board implements Comparator<Board.ActionValue> {
    char[][] board;
    int n;
    int m;
    int winRequire;
    public static List<boolean[][]> winStates = new ArrayList<>();
    final static long[][] zobrist = createZobrist();
    static Map<Long, Integer> zobristHash;
    public static int numplayer;
    public static int numOppenent;

    public char[][] getBoard() {
        return board;
    }

    int currentPlayer;
    char nextPlayer;
    char prevPlayer;
    char winner;
    String lastMove;

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public Board(int n, int m) {
        this.n = n;
        this.m = m;
        this.winRequire = m;
        this.board = new char[n][n];
        // fill char[][] with empty spaces
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.board[i][j] = '.';
            }
        }
        this.winner = '.';
    }

    public Board(Board other) {
        this.n = other.n;
        this.m = other.m;
        this.winRequire = other.winRequire;
        this.board = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.board[i][j] = other.board[i][j];
            }
        }
        this.nextPlayer = other.nextPlayer;
        this.prevPlayer = other.prevPlayer;
    }

    List<Integer> getEmpties() {
        List<Integer> ems = new ArrayList<>();
        List<Integer> five = new ArrayList<>();
        List<Integer> four = new ArrayList<>();
        List<Integer> three = new ArrayList<>();
        List<Integer> two = new ArrayList<>();
        List<Integer> el = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == '.' && hasNeighbor(i, j)) {
                    ems.add(transfer(strMove(i, j)));
                }
            }
        }
        if (currentPlayer == 2) {
            return ems;
        }

        for (int i = 0; i < ems.size(); i++) {
            int action = ems.get(i);
            int pointPlayer = evaluate(action, numplayer);
            int pointOpponent = evaluate(action, numOppenent);
            if (pointPlayer == Score.FIVE) {
                five.add(action);
                return five;
            } else if (pointOpponent == Score.FIVE) {
                five.add(action);
            } else if (pointPlayer == Score.FOUR) {
                four.add(0, action);
            } else if (pointOpponent == Score.FOUR) {
                four.add(action);
            } else if (pointPlayer == Score.THREE) {
                three.add(0, action);
            } else if (pointOpponent == Score.THREE) {
                three.add(action);
            } else if (pointPlayer == Score.TWO) {
                two.add(0, action);
            } else if (pointPlayer == Score.TWO) {
                two.add(action);
            } else {
                el.add(action);
            }
        }
        if (five.size() != 0) {
            return five;
        }
        if (four.size() != 0) {
            return four;
        }
        if (three.size() != 0) {
            return three;
        }

        if (two.size() != 0) {
            two = two.subList(0, two.size() > 3 ? 3 : two.size() - 1);
            return two;
        }

        if (el.size() != 0) {
            el = el.subList(0, el.size() > 3 ? 3 : el.size() - 1);
            return el;
        }
        return ems;
    }

    List<Integer> getEmpties(int currentPlayer) {

        this.currentPlayer = currentPlayer;
        List<Integer> ems = getEmpties();

//        if (currentPlayer == 2) {
//            List<ActionValue>sort=new ArrayList<>();
//            for(int i=0;i<ems.size();i++){
//                sort.add(new ActionValue(ems.get(i),evaluate(ems.get(i),currentPlayer)));
//            }
//            Collections.sort(sort, this);
//            if(sort.size()>0){
//                ems=new ArrayList<>();
//                ems.add(sort.get(0).action);
//            }
//
//        }

        return ems;
    }

    List<Integer> getEmpties(int currentPlayer, long stateKey) {
        this.currentPlayer = currentPlayer;
        List<Integer> ems = new ArrayList<>();
        List<Integer> five = new ArrayList<>();
        List<Integer> four = new ArrayList<>();
        List<Integer> liveFour = new ArrayList<>();
        List<Integer> three = new ArrayList<>();
        List<Integer> liveThree = new ArrayList<>();
        List<Integer> two = new ArrayList<>();
        List<Integer> el = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == '.' && hasNeighbor(i, j)) {
                    ems.add(transfer(strMove(i, j)));
                }
            }
        }
//        if (currentPlayer == 2) {
//            return ems;
//        }

        for (int i = 0; i < ems.size(); i++) {
            int action = ems.get(i);
            int pointPlayer = evaluate(action, numplayer, stateKey);
            int pointOpponent = evaluate(action, numOppenent, stateKey);
            if (pointPlayer == Score.FIVE) {
                five.add(action);
                return five;
            } else if (pointOpponent == Score.FIVE) {
                five.add(action);
            } else if (pointPlayer == Score.LIVE_FOUR) {
                liveFour.add(0, action);
            } else if (pointOpponent == Score.LIVE_FOUR) {
                liveFour.add(action);
            } else if (pointPlayer == Score.FOUR) {
                four.add(0, action);
            } else if (pointOpponent == Score.FOUR) {
                four.add(action);
            } else if (pointPlayer == Score.LIVE_THREE) {
                liveThree.add(0, action);
            } else if (pointOpponent == Score.THREE) {
                liveThree.add(action);
            } else if (pointPlayer == Score.THREE) {
                three.add(0, action);
            } else if (pointOpponent == Score.THREE) {
                three.add(action);
            } else if (pointPlayer == Score.TWO) {
                two.add(0, action);
            } else if (pointPlayer == Score.TWO) {
                two.add(action);
            } else {
                el.add(action);
            }
        }
        if (five.size() != 0) {
            return five;
        }
        if (liveFour.size() != 0) {
            return liveFour;
        }
        if (four.size() != 0) {
            return four;
        }
        if (liveThree.size() != 0) {
            return liveThree;
        }
        if (three.size() != 0) {
            three = three.subList(0, three.size() > 1 ? 1 : three.size() - 1);
            return three;
        }
        if (two.size() != 0) {
            two = two.subList(0, two.size() > 1 ? 1 : two.size() - 1);
            return two;
        } else {
            if (el.size() > 0)
                el = el.subList(0, el.size() > 1 ? 1 : el.size() - 1);
            return el;
        }
    }

    public int evaluate(int player) {
        int point = 0;
        if (player == 1) {
            if (DetectWin_2.detectWin(transfer(this), n, winRequire, player)) {
                point = Score.FIVE;
                return point;
            }
            if (DetectBoard.detectWin(transfer(this), n, winRequire - 1, player)) {
                point = Score.FOUR;
                return point;
            }
            if (DetectBoard.detectWin(transfer(this), n, winRequire - 2, player)) {
                point = Score.THREE;
                return point;
            }
            if (DetectBoard.detectWin(transfer(this), n, winRequire - 3, player)) {
                point = Score.TWO;
                return point;
            }
        } else if (player == 2) {
            if (DetectWin_2.detectWin(transfer(this), n, winRequire, player)) {
                point = Score.FIVE;
                return point;
            }
            if (DetectBoard.detectWin(transfer(this), n, winRequire - 1, player)) {
                point = Score.FOUR;
                return point;
            }
            if (DetectBoard.detectWin(transfer(this), n, winRequire - 2, player)) {
                point = Score.THREE;
                return point;
            }
            if (DetectBoard.detectWin(transfer(this), n, winRequire - 3, player)) {
                point = Score.TWO;
                return point;
            }
        }
        return point;
    }

    public int evaluate(int action, int player) {
        int x1 = action / n;
        int y1 = action % n;
        board[x1][y1] = player == 1 ? 'o' : 'x';
        int point = 0;
        for (int i = 0; i < winStates.size(); i++) {
            point = Math.max(evl(this, winStates.get(i), player), point);
        }
//        point=evaluate(player);
        board[x1][y1] = '.';
        return point;
    }

    public int evaluate(int action, int player, long stateKey) {
        stateKey = stateKey ^ Board.zobrist[player][action];
        Integer point = 0;
        if ((point = zobristHash.get(stateKey)) == null) {
            int evl = evaluate(action, player);
            zobristHash.put(stateKey, evl);
            point = evl;

            ;
        }
        return point;
    }

    public boolean hasNeighbor(int i, int j) {
        if (i == 0) {
            if (j == 0) {
                if (board[i][j + 1] != '.') {
                    return true;
                }
                if (board[i + 1][j + 1] != '.') {
                    return true;
                }
                if (board[i + 1][j] != '.') {
                    return true;
                }
                return false;
            } else if (j == n - 1) {
                if (board[i][j - 1] != '.') {
                    return true;
                }
                if (board[i + 1][j - 1] != '.') {
                    return true;
                }
                if (board[i + 1][j] != '.') {
                    return true;
                }
                return false;
            } else {
                if (board[i + 1][j] != '.') {
                    return true;
                }
                if (board[i][j + 1] != '.') {
                    return true;
                }
                if (board[i][j - 1] != '.') {
                    return true;
                }
                return false;
            }
        }
        if (i == n - 1) {
            if (j == 0) {
                if (board[i][j + 1] != '.') {
                    return true;
                }
                if (board[i - 1][j + 1] != '.') {
                    return true;
                }
                if (board[i - 1][j] != '.') {
                    return true;
                }
                return false;
            } else if (j == n - 1) {
                if (board[i][j - 1] != '.') {
                    return true;
                }
                if (board[i - 1][j - 1] != '.') {
                    return true;
                }
                if (board[i - 1][j] != '.') {
                    return true;
                }
                return false;
            } else {
                if (board[i - 1][j] != '.') {
                    return true;
                }
                if (board[i][j + 1] != '.') {
                    return true;
                }
                if (board[i][j - 1] != '.') {
                    return true;
                }
                return false;
            }
        }
        if (j == 0) {
            if (board[i - 1][j] != '.') {
                return true;
            }
            if (board[i + 1][j] != '.') {
                return true;
            }
            if (board[i][j + 1] != '.') {
                return true;
            }
            return false;
        }
        if (j == n - 1) {
            if (board[i - 1][j] != '.') {
                return true;
            }
            if (board[i + 1][j] != '.') {
                return true;
            }
            if (board[i][j - 1] != '.') {
                return true;
            }
            return false;
        }

        if (board[i][j + 1] != '.') {
            return true;
        }
        if (board[i - 1][j - 1] != '.') {
            return true;
        }
        if (board[i][j - 1] != '.') {
            return true;
        }
        if (board[i + 1][j] != '.') {
            return true;
        }
        if (board[i - 1][j] != '.') {
            return true;
        }
        if (board[i + 1][j + 1] != '.') {
            return true;
        }
        if (board[i + 1][j - 1] != '.') {
            return true;
        }
        if (board[i - 1][j + 1] != '.') {
            return true;
        }
        return false;
    }

    String placeMove(char p, String move, boolean out) {
        int[] ij = parseMove(move);
        board[ij[0]][ij[1]] = p;
        if (out)
//			System.out.println(move);
            prevPlayer = p;
        // sets next and prev player
        if (p == 'x')
            nextPlayer = 'o';
        else
            nextPlayer = 'x';
//        winner = setWinner(p, ij);
        return move;
    }

    ArrayList<String> getPlayerPlaces(char p) {
        ArrayList<String> places = new ArrayList<String>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == p) {
                    places.add(i + " " + j);
                }
            }
        }
        return places;
    }

    ArrayList<String> lookAround(String pos) {
        ArrayList<String> adjacent = new ArrayList<String>();
        int[] coords = parseMove(pos);
        int i = coords[0];
        int j = coords[1];
        String x;
        if (i - 1 >= 0) {
            if (board[i - 1][j] == '.') {
                x = strMove(i - 1, j);
                adjacent.add(x);
            }
            if (j - 1 >= 0) {
                if (board[i - 1][j - 1] == '.') {
                    x = strMove(i - 1, j - 1);
                    adjacent.add(x);
                }
            }
        }
        if (j + 1 < n) {
            if (board[i][j + 1] == '.') {
                x = strMove(i, j + 1);
                adjacent.add(x);
            }
            if (i - 1 >= 0) {
                if (board[i - 1][j + 1] == '.') {
                    x = strMove(i - 1, j + 1);
                    adjacent.add(x);
                }
            }
        }
        if (i + 1 < n) {
            if (board[i + 1][j] == '.') {
                x = strMove(i + 1, j);
                adjacent.add(x);
            }
            if (j + 1 < n) {
                if (board[i + 1][j + 1] == '.') {
                    x = strMove(i + 1, j - 1);
                    adjacent.add(x);
                }
            }
        }
        return adjacent;
    }

//    char setWinner(char p, int[] ij) {
//        if (isRowWin(p, ij) || isColWin(p, ij) || isLtrWin(p, ij)
//                || isRtlWin(p, ij)) {
//            return p;
//        }
//        if (getEmpties().isEmpty()) {
//            return 'd';
//        }
//        return '.';
//    }

    boolean isRowWin(char p, int[] ij) {
        String row = new String(board[ij[0]]);
        if (row.contains(strMatch(p)))
            return true;
        return false;
    }

    boolean isColWin(char p, int[] ij) {
        String column = "";
        for (int i = 0; i < n; i++) {
            column += board[i][ij[1]];
        }
        if (column.contains(strMatch(p)))
            return true;
        return false;
    }

    boolean isLtrWin(char p, int[] ij) {
        int i = ij[0];
        int j = ij[1];
        String match = strMatch(p, m);
        String diag = "";
        // going up and left
        while (i >= 0 && j >= 0) {
            diag = String.valueOf(board[i][j]) + diag;
            i--;
            j--;
        }
        i = ij[0] + 1;
        j = ij[1] + 1;
        // going down and right
        while (i < n && j < n) {
            diag += String.valueOf(board[i][j]);
            i++;
            j++;
        }
        if (diag.contains(match))
            return true;
        return false;
    }

    boolean isRtlWin(char p, int[] ij) {
        int i = ij[0];
        int j = ij[1];
        String match = strMatch(p, m);
        String diag = "";
        // going up and right
        while (i >= 0 && j < n) {
            diag = String.valueOf(board[i][j]) + diag;
            i--;
            j++;
        }
        i = ij[0] + 1;
        j = ij[1] - 1;
        // going down and left
        while (i < n && j >= 0) {
            diag += String.valueOf(board[i][j]);
            i++;
            j--;
        }
        if (diag.contains(match))
            return true;
        return false;
    }

    int nearWins(char p, int away) {
        return nearWinRows(p, away) + nearWinCols(p, away);
    }

    int nearWinRows(char p, int away) {
        int count = 0;
        int length = m - away;
        String match1 = strMatch(p, length);
        String match2 = '.' + match1;
        match1 += '.';
        for (int i = 0; i < n; i++) {
            String row = new String(board[i]);
            if (row.contains(match1)) {
                int x = row.indexOf(match1);
                while (x >= 0) {
                    count++;
                    x = row.indexOf(match1, match1.length() + x);
                }
            }
            if (row.contains(match2)) {
                int x = row.indexOf(match2);
                while (x >= 0) {
                    count++;
                    x = row.indexOf(match2, match2.length() + x);
                }
            }
        }
        return count;
    }

    int nearWinCols(char p, int away) {
        int count = 0;
        int length = m - away;
        String match1 = strMatch(p, length);
        String match2 = '.' + match1;
        match1 += '.';
        for (int j = 0; j < n; j++) {
            String column = "";
            for (int i = 0; i < n; i++) {
                column += board[i][j];
            }
            if (column.contains(match1)) {
                int x = column.indexOf(match1);
                while (x >= 0) {
                    count++;
                    x = column.indexOf(match1, match1.length() + x);
                }
            }
            if (column.contains(match2)) {
                int x = column.indexOf(match2);
                while (x >= 0) {
                    count++;
                    x = column.indexOf(match2, match2.length() + x);
                }
            }
        }

        return count;
    }

    String strMatch(char p) {
        String match = "";
        for (int i = 0; i < m; i++) {
            match += Character.toString(p);
        }
        return match;
    }

    String strMatch(char p, int length) {
        String match = "";
        for (int i = 0; i < length; i++) {
            match += Character.toString(p);
        }
        return match;
    }

    int[] parseMove(String s) {
        String[] ss = s.split(" ");
        int[] ij = {Integer.parseInt(ss[0]), Integer.parseInt(ss[1])};
        return ij;
    }

    String strMove(int i, int j) {
        return i + " " + j;
    }

    String strMove(int[] ij) {
        return ij[0] + " " + ij[1];
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                str += board[i][j] + " ";
            }
            str += "\n";
        }
        return str;
    }

    public String transfer(int action) {
        int x = action / n;
        int y = action % n;
//        return  action;
        return x + " " + y;
    }

    public int transfer(String move) {
        String[] xy = move.split(" ");
        return Integer.parseInt(xy[0]) * n + Integer.parseInt(xy[1]);
    }

    public int[] transfer(Board board) {
        int[] status = new int[board.n * board.n];
        int k = 0;
        for (int i = 0; i < board.board.length; i++) {
            for (int j = 0; j < board.board[i].length; j++) {
                if (board.board[i][j] == 'o') {
                    status[k++] = numplayer;
                } else if (board.board[i][j] == 'x') {
                    status[k++] = numOppenent;
                } else {
                    status[k++] = 0;
                }
            }
        }
        return status;
    }

    public Board clone() {
        Board board = new Board(this);
        return board;
    }

    public static void winStatesInit() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 11; j++) {
                boolean[][] winState = newBooleanArray();

                for (int k = 0; k < 5; k++) {
                    winState[i][k + j] = true;
                }
                winStates.add(winState);
            }
        }
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 15; j++) {
                boolean[][] winState = newBooleanArray();

                for (int k = 0; k < 5; k++) {
                    winState[i + k][j] = true;
                }
                winStates.add(winState);
            }
        }
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                boolean[][] winState = newBooleanArray();

                for (int k = 0; k < 5; k++) {
                    winState[i + k][k + j] = true;
                }
                winStates.add(winState);
            }
        }
        for (int i = 0; i < 11; i++) {
            for (int j = 4; j < 15; j++) {
                boolean[][] winState = newBooleanArray();

                for (int k = 0; k < 5; k++) {
                    winState[i + k][j - k] = true;
                }
                winStates.add(winState);
            }
        }
    }

    public static void main(String[] args) {
//        UUID a=UUID.randomUUID();
//        System.out.println(a.getLeastSignificantBits());
        long zobrist[][] = new long[3][15 * 15];
        long max = (long) Math.pow(2, 63) - 1;
        long min = (long) Math.pow(2, 62);
        System.out.println(Long.MAX_VALUE);
        System.out.println(max);
        System.out.println(min);
        Random random = new Random();
        for (int i = 0; i < zobrist.length; i++) {
            for (int j = 0; j < zobrist[i].length; j++) {
                zobrist[i][j] = (long) (Math.random() * (max - min + 1)) + min;
//                zobrist[i][j]=UUID.randomUUID().getMostSignificantBits();
                System.out.println(zobrist[i][j]);
            }
        }

    }

    public static long[][] createZobrist() {
        long zobrist[][] = new long[3][15 * 15];
        long max = (long) Math.pow(2, 63) - 1;
        long min = (long) Math.pow(2, 62);
        for (int i = 0; i < zobrist.length; i++) {
            for (int j = 0; j < zobrist[i].length; j++) {
                zobrist[i][j] = (long) (Math.random() * (max - min + 1)) + min;
            }
        }
        return zobrist;
    }

    public static int evl(Board board, boolean[][] winState, int player) {
        char symbol = '\0';
        boolean first = false;
        boolean last = false;
        int score = 0;
        int count = 0;
        int count2 = count;
        byte liveThreeCount = 0;
        if (player == 1) {
            symbol = 'o';
        } else {
            symbol = 'x';
        }
        for (int i = 0; i < winState.length; i++) {
            for (int j = 0; j < winState[i].length; j++) {
                if (winState[i][j] && board.board[i][j] == symbol) {
                    count++;
                    liveThreeCount++;
                } else if (winState[i][j] && board.board[i][j] == '.') {
                    liveThreeCount++;
                    if (liveThreeCount == 1) {
                        first = true;
                    } else if (liveThreeCount == 5) {
                        last = true;
                    }
                } else if (winState[i][j] && board.board[i][j] != symbol) {
                    count2 = count;
                    count = 0;
                }
            }
        }
        count = Math.max(count, count2);
        if (count == 2) {
            score = Score.TWO;
        } else if (count == 3 && first && last) {
            score = Score.LIVE_THREE;
        } else if (count == 3) {
            score = Score.THREE;
        } else if (count == 4 && (first || last)) {
            score = Score.LIVE_FOUR;
        } else if (count == 4) {
            score = Score.FOUR;
        } else if (count == 5) {
            score = Score.FIVE;
        }
        return score;
    }

    public static boolean[][] newBooleanArray() {
        boolean[][] boolArray = new boolean[15][15];
        for (int i = 0; i < 15; i++) {
            boolArray[i] = new boolean[15];
            for (int j = 0; j < 15; j++) {
                boolArray[i][j] = false;
            }
        }
        return boolArray;
    }


    @Override
    public int compare(ActionValue o1, ActionValue o2) {

        int o1Score = o1.value;
        int o2Score = o1.value;
        if (o1Score > o2Score) return -1;
        if (o1Score == o2Score) return 0;
        return 1;
    }

    class ActionValue {
        public ActionValue(int action, int value) {
            this.action = action;
            this.value = value;
        }

        int action;
        int value;
    }
}