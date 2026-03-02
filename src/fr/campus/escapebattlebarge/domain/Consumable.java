package fr.campus.escapebattlebarge.domain;

public class Consumable extends Item {
    private final int healAmount;

    public Consumable(String name, int healAmount) {
        super(name, ItemType.CONSUMABLE);
        this.healAmount = healAmount;
    }

    public int getHealAmount() { return healAmount; }

    @Override
    public String toString() {
        return getName() + " (soin +" + healAmount + ")";
    }
}
