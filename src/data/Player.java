package data;

public class Player {
    private String name;
    private int score;
    private int wins;
    private int losses;
    private int draws;
    private int status;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
        this.status = 0;
    }
}
