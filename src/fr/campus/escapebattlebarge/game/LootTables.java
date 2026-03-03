package fr.campus.escapebattlebarge.game;

import fr.campus.escapebattlebarge.domain.*;

public class LootTables {

    public Item rollTreasure(Player player, Zone zone) {
        // V1 simple: un trésor = soit potion, soit upgrade classe
        // On peut raffiner par zone ensuite.
        int r = (int)(Math.random() * 100);

        if (r < 45) {
            return new Consumable("Stim-pack", 12);
        }

        if (player.getPlayerClass() == PlayerClass.ASSAULT_MARINE) {
            // Bolter plus tard: on le donne en trésor avec une chance
            if (r < 80) return new Weapon("Bolter", 5, 9);
            return new Consumable("Kit de survie", 18);
        } else {
            // Pouvoir psy plus tard
            if (r < 80) return new Power("Châtiment psychique", 6, 12);
            return new Consumable("Encens sanctifié", 16);
        }
    }
}