package fr.campus.escapebattlebarge.domain.consumable;

import fr.campus.escapebattlebarge.domain.item.Item;
import fr.campus.escapebattlebarge.domain.item.ItemType;

/*
 * Cette classe représente un consommable de soin.
 * Elle est utilisée en combat pour restaurer des PV.
 */
public class Consumable extends Item {
    private final int healAmount;

    // Crée un consommable avec son montant de soin.
    public Consumable(String name, int healAmount) {
        super(name, ItemType.CONSUMABLE);
        this.healAmount = healAmount;
    }

    // Retourne la valeur de soin.
    public int getHealAmount() { return healAmount; }

    // Retourne un format texte lisible pour menu/inventaire.
    @Override
    public String toString() {
        return getName() + " (soin +" + healAmount + ")";

    }
}

