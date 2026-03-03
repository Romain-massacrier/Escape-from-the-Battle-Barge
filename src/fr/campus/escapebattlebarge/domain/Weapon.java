package fr.campus.escapebattlebarge.domain;

public class Weapon extends Item {

    private final int minDmg;
    private final int maxDmg;

    public Weapon(String name, int minDmg, int maxDmg) {
        super(name, ItemType.WEAPON);
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
    }

    public int getMinDmg() {
        return minDmg;
    }

    public int getMaxDmg() {
        return maxDmg;
    }

    @Override
    public String toString() {
        return getName() + " (dmg " + minDmg + "-" + maxDmg + ")";
    }
}