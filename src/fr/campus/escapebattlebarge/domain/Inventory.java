package fr.campus.escapebattlebarge.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private Weapon equippedWeapon;                 // 1 arme équipée
    private final List<Consumable> consumables;    // 3 max
    private final List<Item> stash;                // le reste (armes/pouvoirs en stock)

    public Inventory() {
        this.consumables = new ArrayList<>();
        this.stash = new ArrayList<>();
    }

    public Weapon getEquippedWeapon() { return equippedWeapon; }

    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    public List<Consumable> getConsumables() {
        return Collections.unmodifiableList(consumables);
    }

    public List<Item> getStash() {
        return Collections.unmodifiableList(stash);
    }

    public boolean addConsumable(Consumable c) {
        if (consumables.size() >= 3) return false;
        consumables.add(c);
        return true;
    }

    public void addToStash(Item item) {
        stash.add(item);
    }

    public Consumable removeConsumableAt(int index) {
        if (index < 0 || index >= consumables.size()) return null;
        return consumables.remove(index);
    }
}