package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.CannotGrabException;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.exceptions.FullHandException;
import it.polimi.ingsw.model.powerups.PowerUp;

public class Grab extends Activity {

    public Grab() {
        this.type = ActivityType.GRAB;
    }

    @Override
    public void perform(Player subject) throws CannotGrabException {
        Cell cell = subject.getPosition();
        if(cell.isSpawnPoint()) {
            SpawnCell spawnCell = (SpawnCell) cell;
            spawnCell.getWeaponShop(); //TODO allow player to buy weapons
        }
        else {
            AmmoCell ammoCell = (AmmoCell) cell;
            AmmoTile ammoTile = ammoCell.getAmmoTile();
            if(ammoTile == null)
                throw new CannotGrabException("Cannot grab ammo from an empty cell.");

            if(ammoTile.includesPowerUp()) {
                Deck<PowerUp> deck = subject.getPosition().getBoard().getPowerUpDeck();
                PowerUp card = null;
                try {
                    card = deck.draw();
                } catch (EmptyDeckException e) {
                    deck.regenerate();
                    deck.shuffle();
                }
                try {
                    subject.givePowerUp(card);
                } catch (FullHandException e) {
                    //TODO player must discard
                }
            }
        }
    }
}
