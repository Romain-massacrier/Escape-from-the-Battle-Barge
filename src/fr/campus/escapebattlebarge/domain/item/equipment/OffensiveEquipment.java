package fr.campus.escapebattlebarge.domain.item.equipment;

/*
 * Cette classe représente un équipement offensif persisté en base.
 * Elle sert surtout au modèle Character (hors runtime combat direct).
 */
public class OffensiveEquipment {
    private String type; // "Weapon" or "Spell"
    private String name;
    private int attackBonus;

    // Crée un équipement offensif avec son bonus d'attaque.
    public OffensiveEquipment(String type, String name, int attackBonus) {
        this.type = type;
        this.name = name;
        this.attackBonus = attackBonus;
    }

    // Retourne le type d'équipement offensif.
    public String getType() {
        return type;
    }

    // Met à jour le type d'équipement.
    public void setType(String type) {
        this.type = type;
    }

    // Retourne le nom de l'équipement.
    public String getName() {
        return name;
    }

    // Met à jour le nom de l'équipement.
    public void setName(String name) {
        this.name = name;
    }

    // Retourne le bonus d'attaque.
    public int getAttackBonus() {
        return attackBonus;
    }

    // Met à jour le bonus d'attaque.
    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    // Retourne une description texte utile en debug.
    @Override
    public String toString() {
        return  "OffensiveEquipment {type='" + type + "', name='" + name + "', attackBonus=" + attackBonus + "}";
    }
}