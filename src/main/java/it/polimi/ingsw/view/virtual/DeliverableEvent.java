package it.polimi.ingsw.view.virtual;

public enum DeliverableEvent {
    SPAWN_REQUEST("Which power-up would you like to discard? You will spawn where indicated by its colour."),
    SPAWN_SUCCESS("You spawned."),
    DISCARD_POWERUP_REQUEST("Looks like your hand is full of power-ups, please discard one:"),
    DISCARD_WEAPON_REQUEST("Looks like your hand is full of weapons, please discard one:"),
    CHOOSE_EXECUTION_REQUEST("Choose a moveset:"),
    MOVE_REQUEST("Where would you like to move?"),
    GRAB_AMMO_SUCCESS("You grabbed ammo."),
    GRAB_AMMO_FAILURE("You didn't grab anything because the cell you are on is empty."),
    GRAB_WEAPON_REQUEST_IF("Would you like to buy a weapon?"),
    GRAB_WEAPON_REQUEST_WHICH("Which weapon would you like to purchase?"),
    GRAB_WEAPON_SUCCESS("Congrats on your purchase."),
    GRAB_WEAPON_NEEDS_POWERUP("Choose a powerup to cover the purchase costs:"),
    SHOOT_WEAPON_REQUEST("Which weapon would you like to attack with?"),
    SHOOT_MODULE_REQUEST("Choose how to attack:"),
    SHOOT_PLAYER_FAILURE("Looks like there aren't any valid players to select."),
    SHOOT_CELL_FAILURE("Looks like there aren't any valid cells to select."),
    SHOOT_ROOM_FAILURE("Looks like there aren't any valid rooms to select."),
    RELOAD_REQUEST_IF("Would you like to reload a weapon?"),
    RELOAD_REQUEST_WHICH("Which weapon would you like to reload?"),
    RELOAD_SUCCESS("You have reloaded."),
    RELOAD_NEEDS_POWERUP("Choose a powerup to cover the reload costs:"),
    POWERUP_REQUEST_IF("Would you like to use a power-up?"),
    POWERUP_REQUEST_WHICH("Choose one:"),
    SCOPE_REQUEST_IF("Would you like to use a scope and deal additional damage to one of your targets?"),
    SCOPE_REQUEST_WHICH("Choose a Scope power-up to discard:"),
    SCOPE_REQUEST_AMMO("Which ammo cube would you like to spend to use this power up?"),
    SCOPE_REQUEST_TARGET("Who do you want to deal additional damage to?"),
    GRENADE_REQUEST_IF("Would you like to respond to the fire with a tagback grenade?"),
    GRENADE_REQUEST_WHICH("Choose a Grenade power-up to discard:"),
    NEWTON_REQUEST_PLAYER("Who would you like to move?"),
    NEWTON_REQUEST_CELL("Where would you like to move them?"),
    NEWTON_FAILURE("Looks like you can't move this player to a cell you can see."),
    TELEPORT_REQUEST_CELL("Where would you like to teleport?"),
    TARGET_REQUEST(""),
    UPDATE_DAMAGE(""),
    UPDATE_MARKING(""),
    UPDATE_MOVE(""),
    UPDATE_KILL(""),
    UPDATE_SCORE(""),
    UPDATE_FRENZY(""),
    UPDATE_WINNER(""),
    UPDATE_DISCONNECT(""),
    RESPONSE(""),
    STATUS_INIT(""),
    STATUS_UPDATE("");

    public final String message;
    DeliverableEvent(String message) {
        this.message = message;
    }
}
