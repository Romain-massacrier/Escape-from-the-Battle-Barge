package fr.campus.escapebattlebarge.domain.item.equipment;

import fr.campus.escapebattlebarge.domain.item.Item;
import fr.campus.escapebattlebarge.domain.item.ItemType;

/*
 * Cette classe représente une arme avec une plage de dégâts.
 * Elle est utilisée en combat pour calculer les dégâts du joueur.
 */
public class Weapon extends Item {

    private final int minDmg;
    private final int maxDmg;

    // Crée une arme avec ses dégâts min/max.
    public Weapon(String name, int minDmg, int maxDmg) {
        super(name, ItemType.WEAPON);
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
    }

    // Retourne les dégâts minimum.
    public int getMinDmg() {
        return minDmg;
    }

    // Retourne les dégâts maximum.
    public int getMaxDmg() {
        return maxDmg;
    }

    // Retourne un format texte lisible pour les menus.
    @Override
    public String toString() {
        return getName() + " (dmg " + minDmg + "-" + maxDmg + ")";
    }
}