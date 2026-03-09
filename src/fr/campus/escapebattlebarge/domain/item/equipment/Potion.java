package fr.campus.escapebattlebarge.domain.item.equipment;

import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.domain.consumable.Consumable;
import fr.campus.escapebattlebarge.domain.item.Item;
import fr.campus.escapebattlebarge.game.board.Zone;

/*
 * Cette classe fournit le contenu des trésors en potions.
 * Entrées: joueur + zone. Sortie: potion(s).
 */
/** Génération des potions de trésor. */
public class Potion {

    // Donne toujours le pack de trésor: 2 petites potions + 1 grosse.
    public Consumable[] rollTreasurePack(Player player, Zone zone) {
        return new Consumable[] {
                new Consumable("Stim-pack", 12),
                new Consumable("Medi-dose", 14),
                new Consumable("Kit de survie", 20)
        };
    }

    // Compatibilité: renvoie la première potion du pack.
    public Item rollTreasure(Player player, Zone zone) {
        return rollTreasurePack(player, zone)[0];
    }
}
