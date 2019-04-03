package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Player {
    private String name;
    private int score;
    private int deathCount;

    private List<Player> damage;
    private List<Player> markings;

    private List<Weapon> weapons;
    private List<PowerUp> powerUps;
    private AmmoCubes ammoCubes;

    private Cell position;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.deathCount = 0;
        this.damage = new ArrayList<>();
        this.markings = new ArrayList<>();
        this.weapons = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.ammoCubes = new AmmoCubes();
    }

    // returns the total amount of damage points the player has taken
    public boolean isKilled() {
        return this.damage.size() > 10;
    }

    public boolean isOverKilled() {
        return this.damage.size() > 11;
    }

    // returns the amount of damage points the player has taken by a given opponent
    public int getDamageByAuthor(Player author) {
        int count = 0;
        for(Player auth : this.damage)
            if(auth == author)
                count ++;
        return count;
    }

    // returns the amount of markings the player has taken by a given opponent
    public int getMarkingsByAuthor(Player author) {
        int count = 0;
        for(Player auth : this.markings)
            if(auth == author)
                count ++;
        return count;
    }

    // inflicts the player with a damage point
    public void applyDamage(Player author) {
        this.damage.add(author);

        // if the damage's author has markings on the targeted player ...
        int awaitingMarkings = this.getMarkingsByAuthor(author);
        for(int i = 0; i < awaitingMarkings; i ++) {
            // ... each marking is turned into a damage point
            this.markings.remove(author);
            this.damage.add(author);
        }
    }

    // inflicts the player with a marking, but ONLY if the player has received fewer than 3 markings from that same author
    public void applyMarking(Player author) {
        if(this.getMarkingsByAuthor(author) < 3)
            this.markings.add(author);
    }

    public void giveScore(int amount) {
        this.score += amount;
    }

    // this is used immediately before respawning
    // the player hands out points to its damagers, ranked by total damage inflicted
    // ties are broken in favor of chronological earliness (lowest minimum index)
    // first blood dealer is awareded 1 extra point
    // TODO check for finalFrenzy
    public void scoreUponDeath() {

        final int scoreboard[] = {8, 6, 4, 2, 1, 1};
        final int scoreboardSize = scoreboard.length;

        // this comparator sorts players from the MOST to the LEAST damaging
        // if p1 is better than p2, a negative number should be returned
        Comparator<Player> better = (p1, p2) -> {
            // if p1 dealt more damage, this expression evaluates to a negative
            int damageDelta = this.getDamageByAuthor(p2) - this.getDamageByAuthor(p1);
            if(damageDelta != 0)
                return damageDelta;

            // in case of a tie: if p1 came before p2, then p1 has a lower index and the expression evaluates to a negative
            return this.damage.indexOf(p1) - this.damage.indexOf(p2);
        };

        // this predicate will be used to filter out harmless players
        Predicate<Player> atLeastOneDamage = p -> this.getDamageByAuthor(p) > 0;

        // this stream defines the ranking of the players about to earn points
        ArrayList<Player> ranking = new ArrayList<>(
                this.damage.stream()
                        .filter(atLeastOneDamage)
                        .sorted(better)
                        .distinct()
                        .collect(Collectors.toList())
        );

        // the initial index of the scoreboard should be equal to the player's death count
        // this will skip the first (most valuable) elements if the player has already died
        int i = this.deathCount;

        // points of the scoreboard are given to the players in ranking order
        for(Player p : ranking) {
            if(i < scoreboardSize)
                p.giveScore(scoreboard[i]);
            i ++;
        }

        // one extra point to the player who drew first blood
        this.damage.get(0).giveScore(1);
    }

    public void spawn(Cell cell) {
        //TODO
    }
}
