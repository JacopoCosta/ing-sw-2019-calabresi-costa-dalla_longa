package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.exceptions.CannotDiscardFirstCardOfDeckException;
import it.polimi.ingsw.model.exceptions.FullHandException;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.powerups.PowerUpType;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.network.server.VirtualClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Player extends VirtualClient {
    private static final int KILL_THRESHOLD = 10; // maximum amount of damage before a Player is declared as "killed"
    private static final int OVERKILL_THRESHOLD = 11; // maximum amount of damage before a Player is declared as "overkilled"

    private static final int MAX_MARKINGS_PER_AUTHOR = 3; // maximum number of markings an "author" can give to another Player
    private static final int EXECUTIONS_PER_TURN = 2; // number of executions a player needs to perform on each non-frenzy turn
    private static final int EXECUTIONS_PER_TURN_FRENETIC = 1; // number of executions a player needs to perform on each frenzy turn

    private static final int MAX_CARDS_IN_HAND = 3;

    public static final int[] SCOREBOARD_DEFAULT = {8, 6, 4, 2, 1, 1};
    private static final int[] SCOREBOARD_FRENZY = {2, 1, 1, 1};

    private Game game;

    private int score;
    private int deathCount;
    private boolean onFrenzy;
    private boolean causedFrenzy;
    private int remainingExecutions;

    private List<Player> damage;
    private List<Player> markings;

    private List<Weapon> weapons;
    private List<PowerUp> powerUps;
    private AmmoCubes ammoCubes;

    private Cell position;
    private Cell savedPosition;

    public Player(String name) {
        super(name);

        this.score = 0;
        this.deathCount = 0;
        this.onFrenzy = false;
        this.causedFrenzy = false;
        this.remainingExecutions = EXECUTIONS_PER_TURN;
        this.damage = new ArrayList<>();
        this.markings = new ArrayList<>();
        this.weapons = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.ammoCubes = new AmmoCubes();
        this.position = null;
        this.savedPosition = null;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public int getId() {
        return this.game.getParticipants().indexOf(this) + 1; // non-programmer friendly, ids start from 1
    }

    public int getScore() {
        return this.score;
    }

    public int getDeathCount() {
        return this.deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public boolean isKilled() {
        return this.damage.size() > KILL_THRESHOLD;
    }

    public boolean isOverKilled() {
        return this.damage.size() > OVERKILL_THRESHOLD;
    }

    // returns the total amount of damage points the player has taken
    public int getDamage() {
        return this.damage.size();
    }

    public List<Player> getDamageAsList() {
        return this.damage;
    }

    // returns the amount of damage points the player has taken by a given opponent
    public int getDamageByAuthor(Player author) {
        int count = 0;
        for(Player auth : this.damage)
            if(auth == author)
                count ++;
        return count;
    }

    public List<Player> getMarkingsAsList() {
        return this.markings;
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

    public boolean causedFrenzy() { // this is used to determine which executions the player should be allowed to pick from, after final frenzy has been triggered
        return this.causedFrenzy;
    }

    boolean isOnFrenzyBeforeStartingPlayer() {
        return this.getId() > game.getParticipants()
                .stream()
                .filter(Player::causedFrenzy)
                .map(Player::getId)
                .findFirst()
                .orElse(-1);
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

    public AmmoCubes getAmmoCubes() {
        return ammoCubes;
    }

    public void giveWeapon(Weapon weapon) throws FullHandException {
        this.weapons.add(weapon);

        if(this.weapons.size() > MAX_CARDS_IN_HAND)
            throw new FullHandException("There can't be more than 3 weapons in a player's hand.");
    }

    public void discardWeapon(Weapon weapon) {
        weapons.remove(weapon);
        try {
            game.getBoard().getWeaponDeck().discard(weapon);
        } catch (CannotDiscardFirstCardOfDeckException ignored) { }
    }

    public void givePowerUp(PowerUp powerUp) throws FullHandException {
        this.powerUps.add(powerUp);

        if(this.powerUps.size() > MAX_CARDS_IN_HAND)
            throw new FullHandException("There can't be more than 3 power-ups in a player's hand.");
    }

    public void discardPowerUp(PowerUp powerUp) {
        powerUps.remove(powerUp);
        try {
            game.getBoard().getPowerUpDeck().discard(powerUp);
        } catch (CannotDiscardFirstCardOfDeckException ignored) { }
    }

    public void giveAmmoCubes(AmmoCubes ammoCubes) {
        this.ammoCubes = this.ammoCubes.sum(ammoCubes);
    }

    public void takeAmmoCubes(AmmoCubes ammoCubes) throws CannotAffordException {
        this.ammoCubes = this.ammoCubes.take(ammoCubes);
    }

    public void setPosition(Cell cell) {
        this.position = cell;
    }

    public Cell getPosition() {
        return this.position;
    }

    public void savePosition() {
        this.savedPosition = position;
    }

    public Cell getSavedPosition() {
        return this.savedPosition;
    }

    public boolean canAfford(AmmoCubes cost) {
        return ammoCubes.covers(cost);
    }

    public boolean canAffordWithPowerUps(AmmoCubes cost) {
        AmmoCubes augmentedBalance = ammoCubes.augment(powerUps);
        return augmentedBalance.covers(cost);
    }

    public void activateFrenzy() {
        this.onFrenzy = true;
    }

    public void causeFrenzy() {
        this.causedFrenzy = true;
    }

    public List<PowerUp> getScopes() {
        return powerUps.stream()
                .filter(p -> p.getType() == PowerUpType.SCOPE)
                .collect(Collectors.toList());
    }

    public List<PowerUp> getGrenades() {
        return powerUps.stream()
                .filter(p -> p.getType() == PowerUpType.GRENADE)
                .collect(Collectors.toList());
    }

    public void beginTurn() {
        this.remainingExecutions = this.onFrenzy ? EXECUTIONS_PER_TURN_FRENETIC : EXECUTIONS_PER_TURN;
    }

    public void endExecution() {
        this.remainingExecutions --;
    }

    // inflicts the player with a damage point
    public void applyDamage(Player author) {
        if(damage.size() <= OVERKILL_THRESHOLD) // no more than the max amount of tokens can be stored, any excess tokens are ignored
            this.damage.add(author);

        // if the damage's author has markings on the targeted player ...
        int awaitingMarkings = this.getMarkingsByAuthor(author);


        for(int i = 0; i < awaitingMarkings; i ++) {
            // ... each marking is turned into a damage point
            if(damage.size() <= OVERKILL_THRESHOLD) {
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

    public void giveScore(int score) {
        this.score += score;
    }

    // this is used immediately before respawning
    // the player hands out points to its damagers, ranked by total damage inflicted
    // ties are broken in favor of chronological earliness (lowest minimum index)
    // first blood dealer is awarded 1 extra point
    public void scoreUponDeath() {
        final int[] scoreboard = this.onFrenzy ? SCOREBOARD_FRENZY : SCOREBOARD_DEFAULT;
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

        game.getBoard().addKiller(damage.get(KILL_THRESHOLD));
        if(damage.size() > OVERKILL_THRESHOLD) {
            if (damage.get(KILL_THRESHOLD) == damage.get(OVERKILL_THRESHOLD)) { // the opposite should never happen
                game.getBoard().addKiller(null); // watch out
                damage.get(OVERKILL_THRESHOLD).applyMarking(this);
            }
            else
                throw new RuntimeException("This 'else' branch should never be taken.");
        }

        // one extra point to the player who drew first blood
        this.damage.get(0).giveScore(1);
    }

    public void die() {
        this.deathCount ++;
        this.position = null;
    }

    public void spawn(Cell cell) {
        this.damage.clear();
        this.position = cell;
    }

    @Override
    public String toString() {
        return getName();
    }
}
