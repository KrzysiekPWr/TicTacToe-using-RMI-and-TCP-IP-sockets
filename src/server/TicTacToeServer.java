package server;

import data.DataSaveManager;
import rmi_interfaces.*;
import data.Player;
import server.gameroom.GameRoom;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class TicTacToeServer extends UnicastRemoteObject implements TicTacToeRemote {
    private final HashMap<String, GameRoom> gameRooms;
    private final HashMap<String, Player> players;

    public TicTacToeServer() throws RemoteException {
        super();
        gameRooms = new HashMap<>();
        players = DataSaveManager.readPlayers();
    }
    @Override
    public int registerPlayer(String nick) throws RemoteException {
        if(doPlayerExist(nick)){
            System.out.println("Player " + nick + " has joined");
            return -1;
        }
        else{
            players.put(nick, new Player(nick));
            System.out.println("Player " + nick + " registered");
            return 1;
        }
    }

    @Override
    public int joinRoom(String roomName, String playerName, TicTacCallback playerCallback) throws RemoteException {
        if(gameRooms.containsKey(roomName)){
            if(gameRooms.get(roomName).canJoin()){
                gameRooms.get(roomName).addPlayer(players.get(playerName), playerCallback);
                System.out.println("Player " + playerName + " joined room: " + roomName);
                return 0;
            }
            else{
                System.out.println("Cannot join " + playerName + " to room " + roomName + " because it is full");
                return -1;
            }
        }
        else{
            gameRooms.put(roomName, new GameRoom(roomName));
            System.out.println("Room " + roomName + " created");
            gameRooms.get(roomName).addPlayer(players.get(playerName), playerCallback);
            return 1;
        }
    }

    @Override
    public void waitForPlayers(String roomName) {
        gameRooms.get(roomName).waitForPlayers();
    }

    @Override
    public synchronized boolean isGameEnded(String roomName) throws RemoteException {
        return gameRooms.get(roomName).isGameEnded();
    }

    @Override
    public synchronized String getPlayerTurn(String room) throws RemoteException {
        return gameRooms.get(room).getPlayerTurn();
    }

    @Override
    public void waitForTurn(String room, String playerName) throws RemoteException {
        gameRooms.get(room).waitForTurn(playerName);
    }

    @Override
    public synchronized void move(String room, String nick, int x, int y) {
        gameRooms.get(room).move(nick, x, y);
    }

    @Override
    public String getBoard(String room) throws RemoteException {
        return gameRooms.get(room).getBoard();
    }

    @Override
    public synchronized void closeGameRoom(String room, String nick) throws RemoteException {
        System.out.println("Player " + nick + " left room " + room);
        if (gameRooms.containsKey(room)) {
            gameRooms.get(room).removePlayer(nick);
            gameRooms.remove(room);
            System.out.println("Room: " + room + " closed");
        }
    }

    @Override
    public String getGameWinner(String room) {
        String winner = gameRooms.get(room).getGameWinner();
        gameRooms.get(room).addStats(winner);
        return winner;
    }

    @Override
    public String getPlayerSessionStats(String room, String nick) throws RemoteException {
        return gameRooms.get(room).getPlayerSessionStats(nick);
    }

    private boolean doPlayerExist(String nick) {
        return players.containsKey(nick);
    }

    public void savePlayerStates(String roomName){
        DataSaveManager.writePlayers(players);
    }

    @Override
    public String getOpponent(String nick, String room) {
        return gameRooms.get(room).getOpponent(nick);
    }

    public static void main(String[] args) {
        try {
            TicTacToeServer server = new TicTacToeServer();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("TicTacToeServer", server);
            System.out.println("RMI Server launched..");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}