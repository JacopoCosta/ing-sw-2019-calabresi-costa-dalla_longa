package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.AppendedToAppendableActionException;
import it.polimi.ingsw.model.exceptions.AppendedUnappendableActionException;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.Action;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Player {
    private static final int KILL_THRESHOLD = 10; // maximum amount of damage before a Player is declared as "killed"
    private static final int OVERKILL_THRESHOLD = 11; // maximum amount of damage before a Player is declared as "overkilled"

    private static final int MAX_MARKINGS_PER_AUTHOR = 3; // maximum number of markings an "author" can give to another Player
    private static final int EXECUTIONS_PER_TURN = 2; // number of executions a player needs to perform on each non-frenzy turn
    private static final int EXECUTIONS_PER_TURN_FRENETIC = 1; // number of executions a player needs to perform on each frenzy turn

    private String name;
    private int score;
    private int deathCount;
    private boolean onFrenzy;
    private boolean onFrenzyBeforeStartingPlayer;
    private int remainingExecutions;

    private int scopesUsed;

    private List<Player> damage;
    private List<Player> markings;

    private List<Weapon> weapons;
    private List<PowerUp> powerUps;
    private AmmoCubes ammoCubes;

    private Cell position;
    private List<ActiveAction> activeActions;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.deathCount = 0;
        this.onFrenzy = false;
        this.onFrenzyBeforeStartingPlayer = false;
        this.remainingExecutions = EXECUTIONS_PER_TURN;
        this.damage = new ArrayList<>();
        this.markings = new ArrayList<>();
        this.weapons = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.ammoCubes = new AmmoCubes();
    }

    // returns the total amount of damage points the player has taken
    public boolean isKilled() {
        return this.damage.size() > KILL_THRESHOLD;
    }

    public boolean isOverKilled() {
        return this.damage.size() > OVERKILL_THRESHOLD;
    }

    public int getDamage() {
        return this.damage.size();
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

    public boolean isOnFrenzy() {
        return this.onFrenzy;
    }

    public boolean isOnFrenzyBeforeStartingPlayer() { // this is used to determine which executions the player should be allowed to pick from, after final frenzy has been triggered
        return this.onFrenzyBeforeStartingPlayer;
    }

    public int getRemainingExecutions() {
        return this.remainingExecutions;
    }

    public List<Weapon> getWeapons() {
        return this.weapons;
    }

    public List<PowerUp> getPowerUps() {
        return this.powerUps;
    }

    public Cell getPosition() {
        return this.position;
    }

    public void loadActionsFromWeapon(Weapon weapon) {
        this.activeActions = ActiveAction.createList(weapon.getActions());
    }

    public List<ActiveAction> getActiveActions() {
        return activeActions;
    }

    public void playAction(int id) throws AppendedToAppendableActionException, AppendedUnappendableActionException {
        // TODO appendability, consumability, constraintsbility CHECKS
        ActiveAction activeAction = activeActions.get(id);
        activeAction.getAction().accomplish();
        activeAction.consume();
    }

    public void setPosition(Cell cell) {
        this.position = cell;
    }

    public void activateFrenzy() {
        this.onFrenzy = true;
    }

    public void useScope() {
        this.scopesUsed ++;
    }

    public int exhaustScopes() {
        int scopes = this.scopesUsed;
        this.scopesUsed = 0;
        return scopes;
    }

    public void endExecution() {
        this.remainingExecutions --;
    }

    public void beginTurn() {
        this.remainingExecutions = this.onFrenzy ? EXECUTIONS_PER_TURN_FRENETIC : EXECUTIONS_PER_TURN;
    }

    // inflicts the player with a damage point
    public void applyDamage(Player author) {
        if(!this.isOverKilled()) // no more than the max amount of tokens can be stored, any excess tokens are ignored
            this.damage.add(author);

        // if the damage's author has markings on the targeted player ...
        int awaitingMarkings = this.getMarkingsByAuthor(author);
        for(int i = 0; i < awaitingMarkings; i ++) {
            // ... each marking is turned into a damage point
            if(!this.isOverKilled()) {
                this.markings.remove(author);
                this.damage.add(author);
            }
        }
    }

    // inflicts the player with a marking, but ONLY if the player has received fewer than MAX_MARKINGS_PER_AUTHOR markings from that same author
    public void applyMarking(Player author) {
        if(this.getMarkingsByAuthor(author) < MAX_MARKINGS_PER_AUTHOR)
            this.markings.add(author);
    }

    public void giveScore(int amount) {
        this.score += amount;
    }

    // this is used immediately before respawning
    // the player hands out points to its damagers, ranked by total damage inflicted
    // ties are broken in favor of chronological earliness (lowest minimum index)
    // first blood dealer is awarded 1 extra point
    public void scoreUponDeath() {

        final int[] scoreboardDefault = {8, 6, 4, 2, 1, 1};
        final int[] scoreboardFrenzy = {2, 1, 1, 1};
        final int[] scoreboard = this.onFrenzy ? scoreboardFrenzy : scoreboardDefault;
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
        List<Player> ranking = this.damage.stream()
                .filter(atLeastOneDamage)
                .sorted(better)
                .distinct()
                .collect(Collectors.toList());

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
        this.damage.clear();
    }
}
