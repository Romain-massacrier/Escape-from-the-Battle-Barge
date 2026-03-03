package fr.campus.escapebattlebarge.domain;

public class Power extends Item {
    private final int minDmg;
    private final int maxDmg;

    public Power(String name, int minDmg, int maxDmg) {
        super(name, ItemType.POWER);
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
    }

    public int getMinDmg() { return minDmg; }
    public int getMaxDmg() { return  maxDmg; }

    @Override
    public String toString() {
        return getName() + " (psy " + minDmg + "-" + maxDmg + ")";
    }
}
