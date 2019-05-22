package it.polimi.ingsw.view.virtual;

public interface Deliverable {
    String FATAL_ERROR = "You should never see this message. If you do, run for your life!";
    String SPAWN_REQUEST = "Which power-up would you like to keep? You will spawn where indicated by the one you discard.";
    String SPAWN_SUCCESS = "You spawned.";
    String DISCARD_POWER_UP_REQUEST = "Looks like your hand is full of power-ups, please discard one:";
    String DISCARD_WEAPON_REQUEST = "Looks like your hand is full of weapons, please discard one:";
    String CHOOSE_EXECUTION_REQUEST = "Choose a moveset:";
    String MOVE_REQUEST = "Where would you like to move?";
    String GRAB_AMMO_SUCCESS = "You grabbed ammo.";
    String GRAB_AMMO_FAILURE = "You didn't grab anything because the cell you are on is empty.";
    String GRAB_WEAPON_REQUEST_IF = "Would you like to buy a weapon?";
    String GRAB_WEAPON_REQUEST_WHICH = "Which weapon would you like to purchase?";
    String GRAB_WEAPON_SUCCESS = "Congrats on your purchase.";
    String GRAB_WEAPON_FAILURE = "Looks like you don't have enough ammo to afford this weapon.";
    String SHOOT_WEAPON_REQUEST = "Which weapon would you like to attack with?";
    String SHOOT_MODULE_REQUEST = "Choose how to attack:";
    String SHOOT_PLAYER_FAILURE = "Looks like there aren't any valid players to select.";
    String SHOOT_CELL_FAILURE = "Looks like there aren't any valid cells to select.";
    String SHOOT_ROOM_FAILURE = "Looks like there aren't any valid rooms to select.";
    String RELOAD_REQUEST_IF = "Would you like to reload a weapon?";
    String RELOAD_REQUEST_WHICH = "Which weapon would you like to reload?";
    String RELOAD_SUCCESS = "You have reloaded.";
    String RELOAD_FAILURE = "Looks like you don't have enough ammo to reload this weapon.";
}
