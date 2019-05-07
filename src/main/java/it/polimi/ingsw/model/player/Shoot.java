package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.exceptions.InvalidMoveException;
import it.polimi.ingsw.model.weaponry.Action;
import it.polimi.ingsw.model.weaponry.Attack;

public class Shoot extends Activity {
    private Action action;

    protected Shoot() {
        this.type = ActivityType.SHOOT;
    }

    public void setAction(Action action) { // this is not part of the constructor because Executions (and their Shoot Activities) are generated before the choice of Action
        this.action = action;
    }

    @Override
    public void perform(Player author) throws ConstraintNotSatisfiedException, InvalidMoveException {
        for(Attack attack : this.action.getAttacks()) {
            Player target = null; //TODO acquire this variable legitimately -- suggest valid targets only

            attack.setAuthor(author);
            attack.setTarget(target);
            attack.deal();
        }
    }
}
