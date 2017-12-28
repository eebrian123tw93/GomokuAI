package com.JavaBeginnerToPro.GomoWhiz.minMax;

import java.util.*;

/**
 * Board class that contains all board manipulation and look around methods
 * first player / black == 'x'
 * second player / white == 'o'
 * empty space == '.'
 * Moves are formatted as "row column" (without quotes) where row and column are integers
 */
public class Board implements Comparator<Integer> {
    char[][] board;
    int n;
    int m;
    int winRequire;
    static List<Boolean[][]> winStates = new LinkedList<>();

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

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == '.' && hasNeighbor(i, j)) {
                    ems.add(transfer(strMove(i, j)));
                }
            }
        }
//        Collections.sort(ems, this);
        return ems;
    }
    List<Integer> getEmpties(int currentPlayer) {
        List<Integer>ems=getEmpties();
        this.currentPlayer=currentPlayer;
//        Collections.sort(ems, this);
        return ems;
    }

    public int evaluate(int player) {
        int point = 0;
        if (player == 1) {
            if (DetectWin_2.detectWin(transfer(this), n, winRequire, player)) {
                point = 105000;
                return point;
            }
            if (DetectWin_2.detectWin(transfer(this), n, winRequire - 1, player)) {
                point = 1500;
                return point;
            }
            if (DetectWin_2.detectWin(transfer(this), n, winRequire - 2, player)) {
                point = 15;
                return point;
            }
            if (DetectWin_2.detectWin(transfer(this), n, winRequire - 3, player)) {
                point = 5;
                return point;
            }
        } else if (player == -1) {
            if (DetectWin_2.detectWin(transfer(this), n, winRequire, player)) {
                point = -100000;
                return point;
            }
            if (DetectWin_2.detectWin(transfer(this), n, winRequire - 1, player)) {
                point = -1000;
                return point;
            }
            if (DetectWin_2.detectWin(transfer(this), n, winRequire - 2, player)) {
                point = -10;
                return point;
            }
            if (DetectWin_2.detectWin(transfer(this), n, winRequire - 3, player)) {
                point = -1;
                return point;
            }
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
        winner = setWinner(p, ij);
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

    char setWinner(char p, int[] ij) {
        if (isRowWin(p, ij) || isColWin(p, ij) || isLtrWin(p, ij)
                || isRtlWin(p, ij)) {
            return p;
        }
        if (getEmpties().isEmpty()) {
            return 'd';
        }
        return '.';
    }

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
                    status[k++] = 1;
                } else if (board.board[i][j] == 'x') {
                    status[k++] = -1;
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
                Boolean[][] winState = newBooleanArray();

                for (int k = 0; k < 5; k++) {
                    winState[i][k + j] = true;
                }
                winStates.add(winState);
            }
        }
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 15; j++) {
                Boolean[][] winState = newBooleanArray();

                for (int k = 0; k < 5; k++) {
                    winState[i + k][j] = true;
                }
                winStates.add(winState);
            }
        }
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                Boolean[][] winState = newBooleanArray();

                for (int k = 0; k < 5; k++) {
                    winState[i + k][k + j] = true;
                }
                winStates.add(winState);
            }
        }
        for (int i = 0; i < 11; i++) {
            for (int j = 4; j < 15; j++) {
                Boolean[][] winState = newBooleanArray();

                for (int k = 0; k < 5; k++) {
                    winState[i + k][j - k] = true;
                }
                winStates.add(winState);
            }
        }
    }

    public static void main(String[] args) {

        winStatesInit();


//        Random random = new Random();

//        for (int i = 0; i < board.board.length; i++) {
//            for (int j = 0; j < board.board[i].length; j++) {
//                if (random.nextBoolean()) {
//                    board.board[i][j] = 'o';
//                }
//            }
//        }
//        for (int i = 0; i < board.board.length; i++) {
//            for (int j = 0; j < board.board[i].length; j++) {
//                System.out.print(board.board[i][j]);
//            }
//            System.out.println();
//        }
        Board board = new Board(15, 5);
        long point = 0;
        for (Boolean[][] winState : winStates) {
            point += evl(board, winState, 1);
        }
        System.out.println(point);

    }

    public static int evl(Board board, Boolean[][] winState, int player) {
        char symbol = '\0';
        int score = 0;
        int count = 0;
        if (player == 1) {
            symbol = 'o';
        } else {
            symbol = 'x';
        }
        for (int i = 0; i < winState.length; i++) {
            for (int j = 0; j < winState[i].length; j++) {
                if (winState[i][j] && board.board[i][j] == symbol) {
                    count++;
                }
            }
        }
        System.out.println(count);
        if (count == 2) {
            score = 1;
        }
        if (count == 3) {
            score = 10;
        }
        if (count == 4) {
            score = 1000;
        }
        if (count == 5) {
            score = 10000;
        }

        return score;
    }

    public static Boolean[][] newBooleanArray() {
        Boolean[][] boolArray = new Boolean[15][15];
        for (int i = 0; i < 15; i++) {
            boolArray[i] = new Boolean[15];
            for (int j = 0; j < 15; j++) {
                boolArray[i][j] = false;
            }
        }
        return boolArray;
    }


    @Override
    public int compare(Integer o1, Integer o2) {
        int x1 = o1 / n;
        int y1 = o1 % n;
        board[x1][y1] = currentPlayer==1?'o':'x';

        int o1Score = evaluate(currentPlayer);
        board[x1][y1] = '.';


        int x2 = o2 / n;
        int y2 = o2 % n;
        board[x2][y2] = currentPlayer==1?'o':'x';
        int o2Score = evaluate(currentPlayer);
        board[x2][y2] = '.';
        if (o1Score > o2Score) return 1;
        if (o1Score == o2Score) return 0;
        return -1;
    }
}