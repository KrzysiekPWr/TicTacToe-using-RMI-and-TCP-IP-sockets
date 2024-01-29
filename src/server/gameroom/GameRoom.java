package server.gameroom;

import data.Player;
import rmi_interfaces.TicTacCallback;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GameRoom {
    private final int MAX_PLAYERS = 2;
    private int playerCount = 0;
    private Character[][] board;
    private Player player1;
    private Player player2;
    private HashMap<String, TicTacCallback> callbacks = new HashMap<>();
    private String roomName;
    private String currentPlayerTurn;
    CountDownLatch player1Latch;
    CountDownLatch player2Latch;
    CountDownLatch gameStartLatch = new CountDownLatch(MAX_PLAYERS);
    boolean bothPlayersConnected = false;
    boolean hasGameEnded = false;
    ArrayList<String> chatMessages;

    public GameRoom(String roomName) {
        this.board = new Character[3][3];
        this.roomName = roomName;
        player1Latch = new CountDownLatch(1);
        player2Latch = new CountDownLatch(1);
        initializeBoard();
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
    public boolean canJoin() {
        return playerCount < MAX_PLAYERS;
    }
    public synchronized void addPlayer(Player player, TicTacCallback callback) {
        if(playerCount == 0) {
            player1 = player;
            currentPlayerTurn = player1.getName();
            callbacks.put(player1.getName(), callback);
        } else {
            player2 = player;
            callbacks.put(player2.getName(), callback);
        }
        playerCount++;
        gameStartLatch.countDown();
        if(playerCount == 2) bothPlayersConnected = true;
    }
    public void waitForPlayers() {
        try {
            gameStartLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public boolean isGameEnded() {
        return checkWin() || isBoardFull() || !bothPlayersConnected;
    }

    public String getPlayerTurn() {
        return currentPlayerTurn;
    }

    public void waitForTurn(String nick) {
        try {
            if(nick.equals(player1.getName())) {
                player1Latch.await();
            } else {
                player2Latch.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void move(String nick, int x, int y){
        if(board[x][y].equals(' ') && nick.equals(currentPlayerTurn)){
            board[x][y] = currentPlayerTurn.equals(player1.getName()) ? 'X' : 'O';
            currentPlayerTurn = currentPlayerTurn.equals(player1.getName()) ? player2.getName() : player1.getName();
            if(nick.equals(player1.getName())){
                player1Latch = new CountDownLatch(1);
                player2Latch.countDown();
            }
            else{
                player2Latch = new CountDownLatch(1);
                player1Latch.countDown();
            }
        }
        else{
            System.out.println("Invalid move");
        }
    }

    public String getBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == null) {
                    sb.append(" ");
                } else {
                    sb.append(board[i][j]);
                }
                if (j < 2) {
                    sb.append(" | ");
                }
            }
            sb.append("\n");
            if (i < 2) {
                sb.append("---------\n");
            }
        }
        return sb.toString();
    }

    public void removePlayer(String nick) throws RemoteException {
        //TODO: i dont know whether this should or will work like that
        if(nick.equals(player1.getName())){
            callbacks.get(player2.getName()).playerDisconnected(player1.getName(), roomName);
            player2Latch.countDown();
        }
        else{
            callbacks.get(player1.getName()).playerDisconnected(player2.getName(), roomName);
            player1Latch.countDown();
        }
    }

    public String getGameWinner() {
        if(checkWin()){
            Player winner = currentPlayerTurn.equals(player1.getName()) ? player2 : player1;
            return winner.getName();
        }
        else{
            return "Tie";
        }
    }

    synchronized public void addStats(String winner){
        if (!hasGameEnded) {
            player1.setGamesPlayed(player1.getGamesPlayed() + 1);
            player2.setGamesPlayed(player2.getGamesPlayed() + 1);
            if (winner.equals(player1.getName())) {
                player1.setWins(player1.getWins() + 1);
                player2.setLosses((player2.getLosses() + 1));
            }
            else if (winner.equals(player2.getName())) {
                player2.setWins(player2.getWins() + 1);
                player1.setLosses((player1.getLosses() + 1));
            }
            else{
                player1.setDraws(player1.getDraws() + 1);
                player2.setDraws(player2.getDraws() + 1);
            }
            hasGameEnded = true;
        }
    }

    public synchronized List<String> getChatMessages() {
        return new ArrayList<>(chatMessages);
    }

    public String getPlayerSessionStats(String nick) {
        if(nick.equals(player1.getName())){
            return "Wins: " + player1.getWins() + "\n"
                    + "Loses: " + player1.getLosses() + "\n"
                    + "Ties: " + player1.getDraws() + "\n"
                    + "Total games played: " + player1.getGamesPlayed() + "\n";
        }
        else{
            return "Wins: " + player2.getWins() + "\n"
                    + "Loses: " + player2.getLosses() + "\n"
                    + "Ties: " + player2.getDraws() + "\n"
                    + "Total games played: " + player2.getGamesPlayed() + "\n";
        }
    }

    public String getOpponent(String nick) {
        if(nick.equals(player1.getName())){
            return player2.getName();
        }
        else{
            return player1.getName();
        }
    }
}
