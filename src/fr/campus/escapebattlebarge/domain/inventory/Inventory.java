package fr.campus.escapebattlebarge.domain.inventory;

import fr.campus.escapebattlebarge.domain.consumable.Consumable;
import fr.campus.escapebattlebarge.domain.item.equipment.Weapon;
import fr.campus.escapebattlebarge.domain.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Cette classe gère l'inventaire du joueur (arme équipée, consommables, stock).
 * Elle est utilisée par combat et menu inventaire pour ajouter/retirer des objets.
 * Entrées/sorties: objets ajoutés/retirés, accès en lecture aux listes.
 */
/** Inventaire du joueur (arme équipée, consommables, stock). */
public class Inventory {
    private Weapon equippedWeapon;                 // 1 arme équipée
    private final List<Consumable> consumables;    // 3 max
    private final List<Item> stash;                // le reste (armes/pouvoirs en stock)

    // Initialise un inventaire vide.
    public Inventory() {
        this.consumables = new ArrayList<>();
        this.stash = new ArrayList<>();
    }

    // Retourne l'arme actuellement équipée.
    public Weapon getEquippedWeapon() { return equippedWeapon; }

    // Équipe une arme (écrase l'ancienne si présente).
    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    // Retourne la liste des consommables (lecture seule).
    public List<Consumable> getConsumables() {
        return Collections.unmodifiableList(consumables);
    }

    // Retourne la réserve d'objets (lecture seule).
    public List<Item> getStash() {
        return Collections.unmodifiableList(stash);
    }

    // Tente d'ajouter un consommable (max 3).
    public boolean addConsumable(Consumable c) {
        if (consumables.size() >= 3) return false;
        consumables.add(c);
        return true;
    }

    // Ajoute un objet dans le stock.
    public void addToStash(Item item) {
        stash.add(item);
    }

    // Retire un consommable par index, ou null si index invalide.
    public Consumable removeConsumableAt(int index) {
        if (index < 0 || index >= consumables.size()) return null;
        return consumables.remove(index);
    }
}