package client;

import rmi_interfaces.TicTacCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TicTacCallbackImpl extends UnicastRemoteObject implements TicTacCallback {
    protected TicTacCallbackImpl() throws RemoteException {
        super();
    }

    @Override
    public void playerDisconnected(String nick, String roomName) throws RemoteException {
        System.out.println("Player " + nick + " disconnected from room " + roomName + ". Game ended.");
    }
}
