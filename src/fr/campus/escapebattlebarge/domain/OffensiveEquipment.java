package fr.campus.escapebattlebarge.domain;

public class OffensiveEquipment {
    private String type; // "Weapon" or "Spell"
    private String name;
    private int attackBonus;

    public OffensiveEquipment(String type, String name, int attackBonus) {
        this.type = type;
        this.name = name;
        this.attackBonus = attackBonus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    @Override
    public String toString() {
        return  "OffensiveEquipment {type='" + type + "', name='" + name + "', attackBonus=" + attackBonus + "}";
    }
}