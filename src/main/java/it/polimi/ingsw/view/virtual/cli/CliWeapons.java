package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.util.printer.Color;
import it.polimi.ingsw.util.printer.ColoredString;

import java.util.List;

public abstract class CliWeapons {
    private static final int top = 0;
    private static final int left = 111;

    public static final int width = 64;
    private static final int height = 10;

    public static void build(Player player) {
        List<Weapon> weapons = player.getWeapons();

        for(int i = 0; i < weapons.size(); i ++)
            writeWeapon(weapons.get(i), i);

        if(player.getWeapons().size() > 0)
            CliCommon.write(top + 1, left - 9, new ColoredString("Weapons:", Color.RESET));
    }

    private static void writeWeapon(Weapon weapon, int index) {
        String weaponColor = "";
        try {
            weaponColor = weapon.isLoaded() ? Color.toAnsi((weapon.getReloadCost().take(weapon.getPurchaseCost())).toStringAsColor()) : Color.BLACK;
        } catch (CannotAffordException ignored) { }
        CliCommon.frame(top + index * height, left, width, height, weaponColor);

        CliCommon.write(top + index * height + 1, left + 2, weapon.toColoredStrings());

        List<List<ColoredString>> headers = weapon.getPattern().getHeaders();
        List<List<String>> descriptions = weapon.getPattern().getDescriptions();

        int row = 2;
        for(int i = 0; i < headers.size(); i ++) {
            CliCommon.write(top + height * index + row, left + 2, headers.get(i));
            row ++;
            for(int j = 0; j < descriptions.get(i).size(); j ++) {
                CliCommon.write(top + height * index + row, left + 2, new ColoredString(descriptions.get(i).get(j), Color.RESET));
                row ++;
            }
        }

        if(!weapon.isLoaded())
            CliCommon.write(top + index * height + 1, left + width - 12, new ColoredString("[UNLOADED]", Color.WHITE));
    }
}
