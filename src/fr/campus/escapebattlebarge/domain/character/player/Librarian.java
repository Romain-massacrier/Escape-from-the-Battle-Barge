package fr.campus.escapebattlebarge.domain.character.player;

import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.domain.item.equipment.Weapon;

/*
 * Cette classe représente le joueur de type Librarian.
 * Elle initialise sa vie de base et son bâton de départ.
 */
/** Implémentation de joueur orientée pouvoirs psychiques. */
public class Librarian extends Player {
    // Crée un Librarian avec Bâton de force équipé.
    public Librarian(String name) {
        super(name, PlayerClass.LIBRARIAN, 28);
        Weapon staff = new Weapon("Bâton de force", 3, 7);
        getInventory().equipWeapon(staff);
    }
}