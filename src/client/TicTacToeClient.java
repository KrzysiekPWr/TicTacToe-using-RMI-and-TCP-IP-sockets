package client;
import java.io.*;
import java.net.Socket;
import java.rmi.Naming;
import java.util.Scanner;

import rmi_interfaces.TicTacCallback;
import rmi_interfaces.TicTacToeRemote;

public class TicTacToeClient {
    private String nick;
    private String room;
    TicTacToeRemote server;
    TicTacCallback callback;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    public TicTacToeClient(String nick, String room){
        this.nick = nick;
        this.room = room;
    }

    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("Invalid number of arguments\nUsage: java TicTacToeClient <nick> <room>");
            return;
        }

        String nick = args[0];
        String room = args[1];

        TicTacToeClient client = new TicTacToeClient(nick, room);
        String playerTurn;
        // STARTUP ------------------------------------
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            client.sendNick();

            String serverURL = "rmi://localhost/TicTacToeServer";

            client.server = (TicTacToeRemote) Naming.lookup(serverURL);
            System.out.println("Connected to client.server");

            Runtime.getRuntime().addShutdownHook(new ShutDownThread(client.server, room, nick));


            // Create callback
            client.callback = new TicTacCallbackImpl();

            // Register player
            int serverRegisterFeedback = client.server.registerPlayer(nick);
            if(serverRegisterFeedback == -1){
                System.out.println("Welcome back, " + nick + "!");
            }
            else{
                System.out.println("Welcome to TicTacToe " + nick + "!");
            }

            // Join room
            int serverJoinRoomFeedback = client.server.joinRoom(room, nick, client.callback);
            if(serverJoinRoomFeedback < 0){
                System.out.println("Room is full, disconnecting...");
                client.server.closeGameRoom(room, nick);
                System.exit(-1);
            }
            else{
                System.out.println("Joined room: " + room);
            }
            System.out.println("Please wait for another player to join the room...");
            client.server.waitForPlayers(room);


            //main loop
            client.listenForMessage();
            while(!client.server.isGameEnded(room)) {
                playerTurn = client.server.getPlayerTurn(room);
                if(!playerTurn.equals(nick) && !client.server.isGameEnded(room)){
                    System.out.println("Waiting for " + playerTurn + " to make a move...");
                    client.server.waitForTurn(room, nick);
                }
                else{
                    System.out.println("Your turn!");
                }

                if(client.server.isGameEnded(room)){
                    break;
                }

                System.out.println(client.server.getBoard(room));

                String[] coordinates = new String[0];
                Scanner input = new Scanner(System.in);
                System.out.println("Select field: (x,y coordinates like: 0 0) or use /msg <message> to chat");
                while (!areCordsValid(coordinates)) {
                    String playerInput = input.nextLine();
                    coordinates = playerInput.split(" ");
                    if (playerInput.matches("(?i)^(\\/msg).*")){
                        client.sendMessage(playerInput.split("/msg")[1], client.server.getOpponent(nick, room));
                    }else if (!areCordsValid(coordinates)) {
                        System.out.println("Invalid coordinates, try again");
                    }
                }

                client.server.move(room, nick, Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
            }

            String winner = client.server.getGameWinner(room);
            System.out.println(client.server.getBoard(room));
            System.out.println("The winner is: " + winner + "\n");
            System.out.println("Game stats: \n" + client.server.getPlayerSessionStats(room, nick));
            System.exit(0);
        }
        catch (Exception e){
            System.out.println("Error. Server is not responding");
            closeChat(bufferedWriter, bufferedReader, socket);
            System.exit(-1);
        }
    }
    private static boolean areCordsValid(String[] coordinates) {
        if(coordinates.length != 2){
            return false;
        }
        else if(!coordinates[0].matches("[0-2]") || !coordinates[1].matches("[0-2]")){
            return false;
        }
        return true;
    }

    public void sendMessage(String message, String opponentsNick){
        try {
            bufferedWriter.write(opponentsNick + "|" + nick + ": " + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch (Exception e) {
            System.out.println("Error sending message");
        }
    }

    public void sendNick(){
        try {
            bufferedWriter.write(nick);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            System.out.println("Error sending nick");
        }
    }

    public void listenForMessage(){
        new Thread(() -> {
            try {
                while (true) {
                    String messageFromChat = bufferedReader.readLine();
                    System.out.println(messageFromChat);
                }
            } catch (Exception e) {
                closeChat(bufferedWriter, bufferedReader, socket);
            }
        }).start();
    }

    public static void closeChat(BufferedWriter bufferedWriter, BufferedReader bufferedReader, Socket socket){
        try {
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (Exception e) {
            System.out.println("Chat error");
        }
    }
}
