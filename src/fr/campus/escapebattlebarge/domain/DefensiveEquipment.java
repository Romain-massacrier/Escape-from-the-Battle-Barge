package fr.campus.escapebattlebarge.domain;

public class DefensiveEquipment {
    private String type; // "Shield" or "Potion"
    private String name;
    private int defenseBonus;

    public DefensiveEquipment(String type, String name, int defenseBonus) {
        this.type = type;
        this.name = name;
        this.defenseBonus = defenseBonus;
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

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(int defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    @Override
    public String toString() {
        return "DefensiveEquipment{type='" + type + "', name='" + name + "', defenseBonus=" + defenseBonus + "}";
    }
}