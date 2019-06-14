package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
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

/**
 * the
 */
public class Player extends VirtualClient {
    /**
     * The number of damage required in order to declare a player as dead.
     */
    private static final int KILL_THRESHOLD = 10; // maximum amount of damage before a Player is declared as "killed"

    /**
     * The number of damage required in order to declare a player as dead, and also overkilled.
     */
    private static final int OVERKILL_THRESHOLD = 11; // maximum amount of damage before a Player is declared as "overkilled"

    /**
     * The maximum allowed number of markings a player can have from the same opponent.
     */
    private static final int MAX_MARKINGS_PER_AUTHOR = 3; // maximum number of markings an "author" can give to another Player

    /**
     * The number of executions a player performs on each turn, in standard conditions.
     */
    private static final int EXECUTIONS_PER_TURN = 2; // number of executions a player needs to perform on each non-frenzy turn

    /**
     * The number of executions a player performs on each turn, once final frenzy is activated on said player.
     */
    private static final int EXECUTIONS_PER_TURN_FRENETIC = 1; // number of executions a player needs to perform on each frenzy turn

    /**
     * The maximum number of cards of each type a player is allowed in their hand.
     * This means a player must discard a weapon as soon as they have more than this threshold, and the same goes for power ups.
     */
    private static final int MAX_CARDS_IN_HAND = 3;

    /**
     * The game the player is playing.
     */
    private Game game;

    /**
     * The current score of the player.
     */
    private int score;

    /**
     * The number of times the player has died.
     */
    private int deathCount;

    /**
     * Whether or not the player activated frenetic actions or not.
     */
    private boolean onFrenzy;

    /**
     * Whether ot not the player was the first to trigger the final frenzy.
     */
    private boolean causedFrenzy;

    /**
     * The number of executions left to play on the player's current turn.
     */
    private int remainingExecutions;

    /**
     * The list of opponents who damaged the player.
     * Each opponent appears as many times as damage points they inflicted to the player, and in chronological order.
     */
    private List<Player> damage;

    /**
     * The list of opponents who marked the player.
     * Each opponent appears as many times as markings they dealt to the player, and in chronological order.
     */
    private List<Player> markings;

    /**
     * The weapon cards in the player's hand.
     */
    private List<Weapon> weapons;

    /**
     * The power up cards in the player's hand.
     */
    private List<PowerUp> powerUps;

    /**
     * The amount of ammo available to the player.
     */
    private AmmoCubes ammoCubes;

    /**
     * The cell corresponding to the player's current position on the board.
     */
    private Cell position;

    /**
     * The cell corresponding to the player's position on the board, at the beginning of the current execution.
     * @see Execution
     */
    private Cell savedPosition;

    /**
     * Whether or not the player resulted to be connected after the online player count.
     */
    private boolean connected;

    /**
     * This is the only constructor
     * @param name The name of the player.
     */
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

    /**
     * Binds the player to a game.
     * @param game The game the player needs to be bound to.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Returns the game the player belongs to.
     * @return the player's game.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Returns a number that, inside the game, is unique to this player.
     * @return the player's id.
     */
    public int getId() {
        return this.game.getParticipants().indexOf(this) + 1; // non-programmer friendly, ids start from 1
    }

    /**
     * Returns the player's score.
     * @return the player's score.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Returns how many times the player has died in the current game.
     * @return the player's death count.
     */
    public int getDeathCount() {
        return this.deathCount;
    }

    /**
     * Sets the player's death count to a given value. This is used when loading a game from a save state.
     * @param deathCount the death count to set on the player.
     */
    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    /**
     * Returns whether or not the player is currently dead.
     * @return whether or not the player is currently dead.
     * @see Player#KILL_THRESHOLD
     */
    public boolean isKilled() {
        return this.damage.size() > KILL_THRESHOLD;
    }

    /**
     * Returns whether or not the player is currently overkilled.
     * @return whether ot not the player is currently overkilled.
     * @see Player#OVERKILL_THRESHOLD
     */
    public boolean isOverKilled() {
        return this.damage.size() > OVERKILL_THRESHOLD;
    }

    /**
     * Returns the total amount of damage points the player has taken since they last spawned.
     * @return the total damage count.
     */
    public int getDamage() {
        return this.damage.size();
    }

    /**
     * Returns the list of opponents who dealt damage to the player.
     * @return the list of opponents who dealt damage to the player.
     */
    public List<Player> getDamageAsList() {
        return this.damage;
    }

    /**
     * Returns the amount of damage points the player has taken by a given opponent, since the player last spawned.
     * @param author The opponent for which to count damage points on the player.
     * @return The damage count from the fixed opponent.
     */
    public int getDamageByAuthor(Player author) {
        int count = 0;
        for(Player auth : this.damage)
            if(auth == author)
                count ++;
        return count;
    }

    /**
     * Returns the list of opponents who marked the player.
     * @return the list of opponents who marked the player.
     */
    public List<Player> getMarkingsAsList() {
        return this.markings;
    }

    /**
     * Returns the amount of markings the player has taken by a given opponent, since the player last spawned.
     * @param author The opponent for which to count markings on the player.
     * @return The marking count from the fixed opponent.
     */
    public int getMarkingsByAuthor(Player author) {
        int count = 0;
        for(Player auth : this.markings)
            if(auth == author)
                count ++;
        return count;
    }

    /**
     * Returns whether or not the player has activated frenetic actions on themselves.
     * @return whether ot not the player has activated frenetic actions on themselves.
     */
    public boolean isOnFrenzy() {
        return this.onFrenzy;
    }

    /**
     * Returns whether or not the player was the first to trigger the final frenzy stage of the game.
     * @return whether or not the player was the first to trigger the final frenzy stage of the game.
     */
    public boolean causedFrenzy() { // this is used to determine which executions the player should be allowed to pick from, after final frenzy has been triggered
        return this.causedFrenzy;
    }

    /**
     * Returns whether or not the player is on final frenzy and their turn both strictly precedes the starting player's turn
     * and strictly follows the turn of the player who caused the final frenzy.
     * @return whether or not the player is on final frenzy and their turn both strictly precedes the starting player's turn
     * and strictly follows the turn of the player who caused the final frenzy.
     */
    boolean isOnFrenzyBeforeStartingPlayer() {
        return this.getId() > game.getParticipants()
                .stream()
                .filter(Player::causedFrenzy)
                .map(Player::getId)
                .findFirst()
                .orElse(-1);
    }

    /**
     * Returns how many executions the player has to perform before the end of their turn.
     * @return how many executions the player has to perform before the end of their turn.
     */
    public int getRemainingExecutions() {
        return this.remainingExecutions;
    }

    /**
     * Returns a list containing the weapons in the player's hand.
     * @return the player's hand of weapons.
     */
    public List<Weapon> getWeapons() {
        return this.weapons;
    }

    /**
     * Returns a list containing the power up cards in the player's hand.
     * @return the player's hand of power ups.
     */
    public List<PowerUp> getPowerUps() {
        return this.powerUps;
    }

    /**
     * Returns the amount of ammo available to the player.
     * @return the amount of ammo available to the player.
     */
    public AmmoCubes getAmmoCubes() {
        return ammoCubes;
    }

    /**
     * Gives the player a weapon.
     * @param weapon the weapon to give to the player.
     * @throws FullHandException when the amount of weapons in the player's hand exceeds a fixed threshold.
     * @see Player#MAX_CARDS_IN_HAND
     */
    public void giveWeapon(Weapon weapon) throws FullHandException {
        this.weapons.add(weapon);

        if(this.weapons.size() > MAX_CARDS_IN_HAND)
            throw new FullHandException("There can't be more than 3 weapons in a player's hand.");
    }

    /**
     * Causes the player to discard a weapon and put it back in the weapon shop of the cell the player is currently standing on.
     * @param weapon the weapon to discard.
     */
    public void discardWeapon(Weapon weapon) {
        weapons.remove(weapon);
        if(!weapon.isLoaded())
            weapon.reload();
        ((SpawnCell) position).addToWeaponShop(weapon);
    }

    /**
     * Gives the player a power up.
     * @param powerUp the power up to give to the player.
     * @throws FullHandException when the amount of power ups in the player's hand exceeds a fixed threshold.
     */
    public void givePowerUp(PowerUp powerUp) throws FullHandException {
        this.powerUps.add(powerUp);

        if(this.powerUps.size() > MAX_CARDS_IN_HAND)
            throw new FullHandException("There can't be more than 3 power-ups in a player's hand.");
    }

    /**
     * Causes the player to discard a power up and put it in the discard pile of the power up deck.
     * @param powerUp The power up to discard.
     */
    public void discardPowerUp(PowerUp powerUp) {
        powerUps.remove(powerUp);
        try {
            game.getBoard().getPowerUpDeck().discard(powerUp);
        } catch (CannotDiscardFirstCardOfDeckException ignored) { }
    }

    /**
     * Gives the player an amount of ammo cubes that will be summed to the player's personal stash.
     * @param ammoCubes The amount of ammo to give.
     * @see AmmoCubes#sum(AmmoCubes)
     */
    public void giveAmmoCubes(AmmoCubes ammoCubes) {
        this.ammoCubes = this.ammoCubes.sum(ammoCubes);
    }

    /**
     * Pays an amount of ammo drawing from the player's personal stash of ammo.
     * @param ammoCubes the amount of ammo to take.
     * @throws CannotAffordException When the player cannot afford the cost.
     */
    public void takeAmmoCubes(AmmoCubes ammoCubes) throws CannotAffordException {
        this.ammoCubes = this.ammoCubes.take(ammoCubes);
    }

    /**
     * Sets the player's position to a given cell.
     * @param cell the player's new position.
     */
    public void setPosition(Cell cell) {
        this.position = cell;
    }

    /**
     * Gets the player's current position.
     * @return the player's current position.
     */
    public Cell getPosition() {
        return this.position;
    }

    /**
     * Sets the player's saved position to their current position.
     */
    public void savePosition() {
        this.savedPosition = position;
    }

    /**
     * Gets the player's saved position.
     * @return the player's saved postion.
     */
    public Cell getSavedPosition() {
        return this.savedPosition;
    }

    /**
     * Whether or not a player can afford an ammo cost, using only their personal stash of ammo.
     * @param cost The amount of ammo cubes the cost consists of.
     * @return whether or not the player can afford such cost.
     */
    public boolean canAfford(AmmoCubes cost) {
        return ammoCubes.covers(cost);
    }

    /**
     * Whether or not a player can afford an ammo cost, using their personal stash of ammo and possibly their power ups.
     * @param cost The amount of ammo cubes the cost consists of.
     * @return Whether or not the player can afford such cost.
     * @see AmmoCubes#augment(List)
     */
    public boolean canAffordWithPowerUps(AmmoCubes cost) {
        AmmoCubes augmentedBalance = ammoCubes.augment(powerUps);
        return augmentedBalance.covers(cost);
    }

    /**
     * Activates the player's frenetic actions.
     */
    public void activateFrenzy() {
        this.onFrenzy = true;
    }

    /**
     * Identifies the player as the first to trigger the final frenzy.
     */
    public void causeFrenzy() {
        this.causedFrenzy = true;
    }

    /**
     * Updates the connection status flag of the player.
     * @param connected The player's new online status.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Reports the player's connection status (online or offline).
     * @return true if and only if the player is online.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Returns the list of scope power ups in the player's hand.
     * This is used when checking whether or not the player can add one or more scopes to an attack,
     * in order to deal additional damage to their targets.
     * @return the list of scope power ups in the player's hand.
     */
    public List<PowerUp> getScopes() {
        return powerUps.stream()
                .filter(p -> p.getType() == PowerUpType.SCOPE)
                .collect(Collectors.toList());
    }

    /**
     * Returns the list of grenade power ups in the player's hand.
     * This is used when checking whether or not the player can respond to the fire with a tagback grenade.
     * @return the list of grenade power ups in the player's hand.
     */
    public List<PowerUp> getGrenades() {
        return powerUps.stream()
                .filter(p -> p.getType() == PowerUpType.GRENADE)
                .collect(Collectors.toList());
    }

    /**
     * Raises the amount of remaining executions of the player to the maximum allowed per turn, given the player's conditions.
     * @see Player#EXECUTIONS_PER_TURN
     * @see Player#EXECUTIONS_PER_TURN_FRENETIC
     */
    public void beginTurn() {
        this.remainingExecutions = this.onFrenzy ? EXECUTIONS_PER_TURN_FRENETIC : EXECUTIONS_PER_TURN;
    }

    /**
     * Decrements the amount of remaining executions for the player on the current turn.
     */
    public void endExecution() {
        this.remainingExecutions --;
    }

    /**
     * Inflicts the player with one damage point.
     * @param author the source of that damage point.
     */
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

    /**
     * Inflicts the player with one marking, unless the number of markings from the same source were to exceed the threshold.
     * @param author the source of the marking.
     * @see Player#MAX_MARKINGS_PER_AUTHOR
     */
    public void applyMarking(Player author) {
        if(this.getMarkingsByAuthor(author) < MAX_MARKINGS_PER_AUTHOR)
            this.markings.add(author);
    }

    /**
     * Adds a number of points to the player's total score.
     * @param score the number of points to add to the current total.
     */
    public void giveScore(int score) {
        this.score += score;
    }

    /**
     * This method is used immediately before respawning and at the end of the game before the final scoring.
     * The player hands out points to all those opponents they took damage from, ranked highest to lowest total damage inflicted.
     * Opponents who dealt no damage to the player are not considered.
     * Ties are broken in favour of the opponent who damaged the player the earliest.
     * If the player is not on final frenzy, the opponent who dealt the very first damage point is awarded one extra point.
     * @see ScoreList
     */
    public void scoreDamageTrack() {
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
            int points = ScoreList.get(i, onFrenzy);
            p.giveScore(points);
            game.getVirtualView().announceScore(this, p, points, false);
            i ++;
        }

        if(this.isKilled()) { // only when scoring upon death
            game.getBoard().addKiller(damage.get(KILL_THRESHOLD));
            if (this.isOverKilled()) {
                if (damage.get(KILL_THRESHOLD) == damage.get(OVERKILL_THRESHOLD)) { // the opposite should never happen
                    game.getBoard().addKiller(null); // watch out -- this will be read as an overkill from the previous entry
                    damage.get(OVERKILL_THRESHOLD).applyMarking(this);
                } else
                    throw new RuntimeException("This 'else' branch should never be taken.");
            }
        }

        // one extra point to the player who drew first blood (if the victim is not on frenzy)
        if(!onFrenzy) {
            this.damage.get(0).giveScore(1);
            game.getVirtualView().announceScore(this, damage.get(0), 1, true);
        }
    }

    /**
     * Increments the player's death count and removes them from the board (in order to allow for the respawning mechanic to take place).
     */
    public void die() {
        this.deathCount ++;
        this.position = null;
    }

    /**
     * Clears the damage track of the player and sets them on a spawn point.
     * @param cell the spawnpoint chosen for the respawn.
     */
    public void spawn(Cell cell) {
        this.damage.clear();
        this.position = cell;
    }

    @Override
    public String toString() {
        return getName();
    }
}
