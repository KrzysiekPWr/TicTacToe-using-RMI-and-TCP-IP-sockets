package rmi_interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicTacToeRemote extends Remote{
    int registerPlayer(String nick) throws RemoteException;

    int joinRoom(String roomName, String playerName, TicTacCallback playerCallback) throws RemoteException;

    void waitForPlayers(String roomName) throws RemoteException;

    boolean isGameEnded(String roomName) throws RemoteException;

    String getPlayerTurn(String room) throws RemoteException;

    void waitForTurn(String room, String nick) throws RemoteException;

    void move(String room, String nick, int x, int y) throws RemoteException;

    String getBoard(String room) throws RemoteException;

    void closeGameRoom(String room, String nick) throws RemoteException;

    String getGameWinner(String room) throws RemoteException;

    String getPlayerSessionStats(String room, String nick) throws RemoteException;

    void savePlayerStates(String room) throws RemoteException;

    String getOpponent(String nick, String room) throws RemoteException;
}
