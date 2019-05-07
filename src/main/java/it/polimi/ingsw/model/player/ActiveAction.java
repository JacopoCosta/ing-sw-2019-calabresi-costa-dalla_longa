package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.weaponry.Action;

import java.util.ArrayList;
import java.util.List;

public class ActiveAction {
    private Action action;
    private boolean consumed;

    private ActiveAction(Action action) {
        this.action = action;
        this.consumed = false;
    }

    public Action getAction() {
        return action;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        this.consumed = true;
    }

    public static List<ActiveAction> createList(List<Action> actions) {
        List<ActiveAction> activeActions = new ArrayList<>();

        for(Action action : actions)
            activeActions.add(new ActiveAction(action));

        return activeActions;
    }
}
