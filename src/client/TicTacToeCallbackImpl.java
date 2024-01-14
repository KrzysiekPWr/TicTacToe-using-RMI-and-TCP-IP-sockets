package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import rmi_interfaces.TicTacToeCallback;

public class TicTacToeCallbackImpl extends UnicastRemoteObject implements TicTacToeCallback {
    protected TicTacToeCallbackImpl() throws RemoteException {
        super();
    }

    @Override
    public void getMove(int x, int y) throws RemoteException {

    }

    @Override
    public void endGame(String result) throws RemoteException {

    }
}
