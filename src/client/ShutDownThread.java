package client;

import rmi_interfaces.TicTacToeRemote;

public class ShutDownThread extends Thread{
    TicTacToeRemote server;
    String room;
    String nick;

    public ShutDownThread(TicTacToeRemote server, String room, String nick){
        this.server = server;
        this.room = room;
        this.nick = nick;
    }

    @Override
    public void run(){
        try{
            server.savePlayerStates(room);
            server.closeGameRoom(room, nick);
        }
        catch(Exception e){
            System.out.println("Shutting down...");
        }
    }

}
