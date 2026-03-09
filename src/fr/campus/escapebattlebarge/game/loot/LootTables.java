package fr.campus.escapebattlebarge.game.loot;

import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.domain.character.player.PlayerClass;
import fr.campus.escapebattlebarge.domain.combat.Power;
import fr.campus.escapebattlebarge.domain.consumable.Consumable;
import fr.campus.escapebattlebarge.domain.item.Item;
import fr.campus.escapebattlebarge.domain.item.equipment.Weapon;
import fr.campus.escapebattlebarge.game.board.Zone;

/*
 * Cette classe choisit le loot donné au joueur après une découverte de trésor.
 * Elle applique des chances simples selon la classe du joueur.
 * Entrées: joueur + zone. Sortie: item (consommable, arme ou pouvoir).
 */
/** Tables de génération de butin selon contexte de jeu. */
public class LootTables {

    // Tire un trésor pseudo-aléatoire selon le profil du joueur.
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