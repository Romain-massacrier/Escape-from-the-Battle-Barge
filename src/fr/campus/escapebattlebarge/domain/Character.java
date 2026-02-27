package fr.campus.escapebattlebarge.domain;

public class Character {
    private String type; // "AssaultMarine" or "Librarian"
    private String name;
    private int health;
    private int attack;
    private OffensiveEquipment offensiveEquipment;
    private DefensiveEquipment defensiveEquipment; // optional for now

    public Character(String type, String name, int health, int attack, OffensiveEquipment offensiveEquipment) {
        this.type = type;
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.offensiveEquipment = offensiveEquipment;
        this.defensiveEquipment = null;
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public OffensiveEquipment getOffensiveEquipment() {
        return offensiveEquipment;
    }

    public void setOffensiveEquipment(OffensiveEquipment offensiveEquipment) {
        this.offensiveEquipment = offensiveEquipment;
    }

    public DefensiveEquipment getDefensiveEquipment() {
        return defensiveEquipment;
    }

    public void setDefensiveEquipment(DefensiveEquipment defensiveEquipment) {
        this.defensiveEquipment = defensiveEquipment;
    }

    public int getTotalAttack() {
        if (offensiveEquipment == null) {
            return attack;
        }
        return attack + offensiveEquipment.getAttackBonus();
    }

    @Override
    public String toString() {
        return "Character{type='" + type + "', name='" + name + "', health=" + health +
                ", attack=" + attack + ", totalAttack=" + getTotalAttack() +
                ", offensiveEquipment=" + offensiveEquipment +
                ", defensiveEquipment=" + defensiveEquipment + "}";
    }
}