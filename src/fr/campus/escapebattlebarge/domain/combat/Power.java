package fr.campus.escapebattlebarge.domain.combat;

import fr.campus.escapebattlebarge.domain.item.Item;
import fr.campus.escapebattlebarge.domain.item.ItemType;

/*
 * Cette classe représente un pouvoir offensif (surtout pour Librarian).
 * Elle peut ajouter des dégâts bonus pendant un combat.
 * Entrées/sorties: min/max dégâts du pouvoir.
 */
public class Power extends Item {
    private final int minDmg;
    private final int maxDmg;

    // Crée un pouvoir avec sa plage de dégâts.
    public Power(String name, int minDmg, int maxDmg) {
        super(name, ItemType.POWER);
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
    }

    // Retourne les dégâts minimum.
    public int getMinDmg() { return minDmg; }
    // Retourne les dégâts maximum.
    public int getMaxDmg() { return  maxDmg; }

    // Retourne un format texte lisible pour l'inventaire/log.
    @Override
    public String toString() {
        return getName() + " (psy " + minDmg + "-" + maxDmg + ")";
    }
}
