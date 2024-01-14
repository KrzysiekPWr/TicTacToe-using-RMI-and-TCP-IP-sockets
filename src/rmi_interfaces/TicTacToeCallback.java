package rmi_interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicTacToeCallback extends Remote{
    void getMove(int x, int y) throws RemoteException;
    void endGame(String result) throws RemoteException;
}
