package fr.campus.escapebattlebarge.domain.character.player;

import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.domain.item.equipment.Weapon;

/*
 * Cette classe représente le joueur de type Assault Marine.
 * Elle initialise sa vie de base et son arme de départ.
 */
public class AssaultMarine extends Player {
    // Crée un Assault Marine avec Chainsword équipée.
    public AssaultMarine(String name) {
        super(name, PlayerClass.ASSAULT_MARINE, 35);
        Weapon sword = new Weapon("Chainsword", 4, 8);
        getInventory().equipWeapon(sword);
    }
}