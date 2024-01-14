package client;
import java.rmi.Naming;
import java.util.Scanner;
import rmi_interfaces.TicTacToeRemote;

public class TicTacToeClient {
    public static void main(String[] args) {
        try {
            String serverURL = "rmi://localhost/TicTacToeServer";
            TicTacToeRemote server = (TicTacToeRemote) Naming.lookup(serverURL);

            // Register player
            System.out.print("Nick: ");
            Scanner scanner = new Scanner(System.in);
            String nick = scanner.nextLine();

            // Stwórz callback dla klienta
            TicTacToeCallbackImpl callback = new TicTacToeCallbackImpl();
            server.registerPlayer(nick, callback);

            // Logika gry i chatu
            // ...

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
