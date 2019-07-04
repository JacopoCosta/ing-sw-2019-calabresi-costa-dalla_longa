package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.util.printer.Color;
import it.polimi.ingsw.util.printer.ColoredString;

public class CliPowerUps {
    private static final int top = 31;
    private static final int left = 120;

    private static final int width = 16;
    private static final int height = 5;

    public static void build(Player player) {
        for(int i = 0; i < player.getPowerUps().size(); i ++)
            writePowerUp(player.getPowerUps().get(i), i);

        if(player.getPowerUps().size() > 0)
            CliCommon.write(top + 2, left - 10, new ColoredString("Powerups:", Color.RESET));
    }

    private static void writePowerUp(PowerUp powerUp, int index) {
        String name = powerUp.getType().toString();

        CliCommon.frame(top, left + index * width, width, height, Color.toAnsi(powerUp.getAmmoCubes().toStringAsColor()));

        CliCommon.write(top + 2, left + index * width + width / 2 - name.length() / 2, new ColoredString(name, null));
    }
}
