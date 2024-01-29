package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer extends Thread{
    private static final int PORT = 1234;
    private ServerSocket serverSocket;

    public ChatServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    public synchronized void startServer(){
        try {
            System.out.println("Chat server is running on port " + PORT);
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){
            closeServerSocket();
        }
    }
    public synchronized void closeServerSocket(){
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error while closing server socket");
        }
    }

    public static synchronized void main(String[] args){
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            ChatServer chatServer = new ChatServer(serverSocket);
            chatServer.startServer();
        } catch (Exception e) {
            System.out.println("Error while creating server socket");
        }
    }
}
