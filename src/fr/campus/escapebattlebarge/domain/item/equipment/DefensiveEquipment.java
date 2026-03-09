package fr.campus.escapebattlebarge.domain.item.equipment;

/*
 * Cette classe représente un équipement défensif persisté en base.
 * Elle sert surtout au modèle Character (hors logique combat actuelle).
 */
/** Équipement défensif persistant (format simple BDD). */
public class DefensiveEquipment {
    private String type; // "Shield" or "Potion"
    private String name;
    private int defenseBonus;

    // Crée un équipement défensif avec son bonus de défense.
    public DefensiveEquipment(String type, String name, int defenseBonus) {
        this.type = type;
        this.name = name;
        this.defenseBonus = defenseBonus;
    }

    // Retourne le type d'équipement défensif.
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

    // Retourne le bonus de défense.
    public int getDefenseBonus() {
        return defenseBonus;
    }

    // Met à jour le bonus de défense.
    public void setDefenseBonus(int defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    // Retourne une description texte utile en debug.
    @Override
    public String toString() {
        return "DefensiveEquipment{type='" + type + "', name='" + name + "', defenseBonus=" + defenseBonus + "}";
    }
}