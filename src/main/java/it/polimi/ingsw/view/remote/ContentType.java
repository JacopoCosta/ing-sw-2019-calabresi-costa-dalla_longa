package it.polimi.ingsw.view.remote;

public enum ContentType {

    NONE,       //Void cell
    CELL,       //Existing cell

    VER_VOID,   //wall between cells of the same room, or two adjacent void cells
    HOR_VOID,

    VER_FULL,   //wall between two cells that are ghostlyAdjacent yet not adjacent
    HOR_FULL,

    VER_DOOR,   //wall between two cells that are adjacent but separated by a door
    HOR_DOOR,

    ANGLE       //angle
}
