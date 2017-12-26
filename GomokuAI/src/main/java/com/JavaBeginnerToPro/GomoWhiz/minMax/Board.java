package com.JavaBeginnerToPro.GomoWhiz.minMax;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Board class that contains all board manipulation and look around methods
 * first player / black == 'x'
 * second player / white == 'o'
 * empty space == '.'
 * Moves are formatted as "row column" (without quotes) where row and column are integers
 */
public class Board {
    char[][] board;
    int n;
    int m;
    int winRequire;
    static List<Boolean[][]> winStates = new LinkedList<>();
    public char[][] getBoard() {
        return board;
    }

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

    /**
     * Board constructor
     *
     * @param n board dimension
     * @param m winning chain length
     */
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

    /**
     * Copy constructor for Board
     *
     * @param other board to copy
     */
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

    /**
     * Get set of empty spots on board
     *
     * @return ems set of empty locations
     */
    List<Integer> getEmpties() {
        List<Integer> ems = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == '.' && hasNeighbor(i, j)) {
                    ems.add(transfer(strMove(i, j)));
                }

            }
        }
        return ems;
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

    /**
     * Place a move on the board
     *
     * @param p    player's char representation
     * @param move location
     * @param out  if true, the move gets printed to System.out
     * @return move
     */
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

    /**
     * Get set of all player's pieces
     *
     * @param p player
     * @return ArrayList of locations
     */
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

    /**
     * Look for empty spots around a location
     *
     * @param pos location to look around
     * @return ArrayList of empty locations
     */
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

    /**
     * Set the board's winner
     *
     * @param p  player
     * @param ij location
     * @return a player char or 'd' if the board is in a draw state (no empty spots)
     */
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

    /**
     * Check row of last move for win
     *
     * @param p
     * @param ij
     * @return true if row has win
     */
    boolean isRowWin(char p, int[] ij) {
        String row = new String(board[ij[0]]);
        if (row.contains(strMatch(p)))
            return true;
        return false;
    }

    /**
     * Check column of last move for win
     *
     * @param p
     * @param ij
     * @return true if column has win
     */
    boolean isColWin(char p, int[] ij) {
        String column = "";
        for (int i = 0; i < n; i++) {
            column += board[i][ij[1]];
        }
        if (column.contains(strMatch(p)))
            return true;
        return false;
    }

    /**
     * Check diagonal of last move for win, looking top left to bottom right
     *
     * @param p
     * @param ij
     * @return true if diagonal has win
     */
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

    /**
     * Check diagonal of last move for win, looking top right to bottom left
     *
     * @param p
     * @param ij
     * @return true if diagonal has win
     */
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

    /**
     * Calculate number of near win chains
     *
     * @param p
     * @param away from winning chain
     * @return sum of all nearWin methods
     */
    int nearWins(char p, int away) {
        return nearWinRows(p, away) + nearWinCols(p, away);
    }

    /**
     * Calculate number of near win chains in rows
     *
     * @param p
     * @param away
     * @return count
     */
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

    /**
     * Calculate number of near win chains in columns
     *
     * @param p
     * @param away
     * @return count
     */
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

    /**
     * Generate string to match for win and nearWin methods
     *
     * @param p
     * @return match
     */
    String strMatch(char p) {
        String match = "";
        for (int i = 0; i < m; i++) {
            match += Character.toString(p);
        }
        return match;
    }

    /**
     * Generate string to match for win and nearWin methods
     *
     * @param p
     * @param length of match string
     * @return match
     */
    String strMatch(char p, int length) {
        String match = "";
        for (int i = 0; i < length; i++) {
            match += Character.toString(p);
        }
        return match;
    }

    /**
     * Parse move from a string into int[]
     */
    int[] parseMove(String s) {
        String[] ss = s.split(" ");
        int[] ij = {Integer.parseInt(ss[0]), Integer.parseInt(ss[1])};
        return ij;
    }

    /**
     * Create string move from coordinates
     *
     * @param i row index
     * @param j column index
     * @return move string
     */
    String strMove(int i, int j) {
        return i + " " + j;
    }

    /**
     * Create string move from int[] coordinates
     *
     * @param ij coordinates
     * @return move string
     */
    String strMove(int[] ij) {
        return ij[0] + " " + ij[1];
    }

    /**
     * Print a nice board
     *
     * @return String representation of board
     */
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

    public Board clone() {
        Board board = new Board(this);
        return board;
    }

    public static  void winStatesInit(){
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
}