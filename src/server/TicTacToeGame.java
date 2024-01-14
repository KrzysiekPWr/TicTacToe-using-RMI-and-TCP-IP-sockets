package server;

public class TicTacToeGame {
    private char[][] board;
    private String player1;
    private String player2;
    private String currentPlayer;

    public TicTacToeGame(String player1, String player2) {
        this.board = new char[3][3];
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
    }

    private void initializeBoard() {
        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board.length; col++) {
                board[row][col] = ' ';
            }
        }
    }
    private boolean checkRows() {
        for(int row = 0; row < board.length; row++) {
            if(board[row][0] != ' ' && board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                return true;
            }
        }
        return false;
    }
    private boolean checkColumns() {
        for(int col = 0; col < board.length; col++) {
            if(board[0][col] != ' ' && board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
                return true;
            }
        }
        return false;
    }
    private boolean checkDiagonals() {
        if(board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return true;
        }
        if(board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return true;
        }
        return false;
    }

    public void makeMove(int x, int y) {
        if(board[x][y] == ' ') {
            board[x][y] = currentPlayer.equals(player1) ? 'X' : 'O';
            currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
        }
    }

    public boolean checkWin() {
        return checkRows() || checkColumns() || checkDiagonals();
    }

    public boolean isBoardFull(){
        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board.length; col++) {
                if(board[row][col] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}
