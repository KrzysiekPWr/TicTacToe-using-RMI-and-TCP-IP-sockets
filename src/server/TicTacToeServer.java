package server;

import rmi_interfaces.*;
import data.Player;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class TicTacToeServer extends UnicastRemoteObject implements TicTacToeRemote {
    private HashMap<String, TicTacToeCallback> callbacks;
    private HashMap<String, ChatCallBack> chatCallbacks;
    //TODO idk if this is needed
    private HashMap<String, Player> players;

    public TicTacToeServer() throws RemoteException {
        super();
        callbacks = new HashMap<>();
        chatCallbacks = new HashMap<>();
    }
    @Override
    public void registerPlayer(String nick, TicTacToeCallback callback) throws RemoteException {
        if (callbacks.containsKey(nick)) {

        }
    }

    @Override
    public void startGame(String nick) throws RemoteException {

    }

    @Override
    public void sendMove(String nick, int x, int y) throws RemoteException {

    }

    @Override
    public boolean checkWin() throws RemoteException {
        return false;
    }

    public static void main(String[] args) {
        try {
            TicTacToeServer server = new TicTacToeServer();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("TicTacToeServer", server);
            System.out.println("Serwer RMI uruchomiony.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
