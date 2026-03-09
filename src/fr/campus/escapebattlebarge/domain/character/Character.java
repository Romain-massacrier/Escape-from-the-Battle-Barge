package fr.campus.escapebattlebarge.domain.character;

import fr.campus.escapebattlebarge.domain.item.equipment.DefensiveEquipment;
import fr.campus.escapebattlebarge.domain.item.equipment.OffensiveEquipment;

/** Modèle persistant d'un personnage stocké en base. */
public class Character {
    private int id;
    private String type;
    private String name;
    private int health;
    private int attack;
    private OffensiveEquipment offensiveEquipment;
    private DefensiveEquipment defensiveEquipment;

    public Character() {}

    public Character(String type, String name, int health, int attack, OffensiveEquipment offensiveEquipment) {
        this.type = type;
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.offensiveEquipment = offensiveEquipment;
        this.defensiveEquipment = null;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getHealth() { return health; }

    public void setHealth(int health) { this.health = health; }

    public int getLifePoints() { return health; }

    public void setLifePoints(int lifePoints) { this.health = lifePoints; }

    public int getAttack() { return attack; }

    public void setAttack(int attack) { this.attack = attack; }

    public int getStrength() { return attack; }

    public void setStrength(int strength) { this.attack = strength; }

    public OffensiveEquipment getOffensiveEquipment() { return offensiveEquipment; }

    public void setOffensiveEquipment(OffensiveEquipment offensiveEquipment) {
        this.offensiveEquipment = offensiveEquipment;
    }

    public void setOffensiveEquipment(String offensiveEquipment) {
        if (offensiveEquipment == null || offensiveEquipment.isBlank()) {
            this.offensiveEquipment = null;
            return;
        }
        this.offensiveEquipment = new OffensiveEquipment("DB", offensiveEquipment, 0);
    }

    public String getOffensiveEquipmentName() {
        return offensiveEquipment == null ? null : offensiveEquipment.getName();
    }

    public DefensiveEquipment getDefensiveEquipment() { return defensiveEquipment; }

    public void setDefensiveEquipment(DefensiveEquipment defensiveEquipment) {
        this.defensiveEquipment = defensiveEquipment;
    }

    public void setDefensiveEquipment(String defensiveEquipment) {
        if (defensiveEquipment == null || defensiveEquipment.isBlank()) {
            this.defensiveEquipment = null;
            return;
        }
        this.defensiveEquipment = new DefensiveEquipment("DB", defensiveEquipment, 0);
    }

    public String getDefensiveEquipmentName() {
        return defensiveEquipment == null ? null : defensiveEquipment.getName();
    }

    public int getTotalAttack() {
        return attack + (offensiveEquipment == null ? 0 : offensiveEquipment.getAttackBonus());
    }

    @Override
    public String toString() {
        return "Character{type='" + type + "', name='" + name + "', health=" + health +
                ", attack=" + attack + ", totalAttack=" + getTotalAttack() +
                ", offensiveEquipment=" + offensiveEquipment +
                ", defensiveEquipment=" + defensiveEquipment + "}";
    }
}