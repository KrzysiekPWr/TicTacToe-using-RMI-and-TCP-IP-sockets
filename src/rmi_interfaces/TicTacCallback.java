package rmi_interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicTacCallback extends Remote {
    void playerDisconnected(String roomName, String nick) throws RemoteException;
}
