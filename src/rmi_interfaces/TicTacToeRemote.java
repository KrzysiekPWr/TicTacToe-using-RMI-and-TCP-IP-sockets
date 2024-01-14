package rmi_interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicTacToeRemote extends Remote{
    void registerPlayer(String nick, TicTacToeCallback callback) throws RemoteException;
    void startGame(String nick) throws RemoteException;
    void sendMove(String nick, int x, int y) throws RemoteException;
    boolean checkWin() throws RemoteException;
}
