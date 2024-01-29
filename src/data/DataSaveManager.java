package data;

import java.io.*;
import java.util.HashMap;

public class DataSaveManager {
    private static String dataFileName = "players.txt";
    private static File playerFile;
    public static void writePlayers(HashMap<String, Player> players){
        try {
            FileOutputStream fileOut = new FileOutputStream("players.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(players);
            fileOut.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error while writing to file");
        }
    }

    public static HashMap<String, Player> readPlayers() {
        HashMap<String, Player> players = new HashMap<>();
        playerFile = new File(dataFileName);
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating file");
            }
        } else {

            try {
                FileInputStream fileIn = new FileInputStream(dataFileName);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                players = (HashMap<String, Player>) in.readObject();

                in.close();
                fileIn.close();
            } catch (IOException | ClassNotFoundException ioe) {
                System.out.println("Error reading file");
            }
        }
        return players;
    }
}
