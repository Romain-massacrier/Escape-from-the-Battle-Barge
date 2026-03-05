package fr.campus.escapebattlebarge.domain.character;

import fr.campus.escapebattlebarge.domain.item.equipment.DefensiveEquipment;
import fr.campus.escapebattlebarge.domain.item.equipment.OffensiveEquipment;

/*
 * Cette classe représente un personnage persistant (sauvegarde BDD).
 * Elle est utilisée pour créer/éditer des héros et lire les ennemis de la base.
 * Entrées/sorties: getters/setters des stats et équipements.
 */
public class Character {
    private int id;
    private String type; // "AssaultMarine" or "Librarian"
    private String name;
    private int health;
    private int attack;
    private OffensiveEquipment offensiveEquipment;
    private DefensiveEquipment defensiveEquipment; // optional for now

    // Constructeur vide utilisé surtout par le mapping BDD.
    public Character() {
    }

    // Construit un personnage avec ses stats principales et son équipement offensif.
    public Character(String type, String name, int health, int attack, OffensiveEquipment offensiveEquipment) {
        this.type = type;
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.offensiveEquipment = offensiveEquipment;
        this.defensiveEquipment = null;
    }

    // Retourne l'identifiant BDD.
    public int getId() {
        return id;
    }

    // Définit l'identifiant BDD.
    public void setId(int id) {
        this.id = id;
    }

    // Retourne le type de personnage.
    public String getType() {
        return type;
    }

    // Définit le type de personnage.
    public void setType(String type) {
        this.type = type;
    }

    // Retourne le nom du personnage.
    public String getName() {
        return name;
    }

    // Définit le nom du personnage.
    public void setName(String name) {
        this.name = name;
    }

    // Alias historique des points de vie.
    public int getHealth() {
        return health;
    }

    // Met à jour les points de vie.
    public void setHealth(int health) {
        this.health = health;
    }

    // Retourne les PV (nom utilisé côté gameplay).
    public int getLifePoints() {
        return health;
    }

    // Définit les PV (nom utilisé côté gameplay).
    public void setLifePoints(int lifePoints) {
        this.health = lifePoints;
    }

    // Retourne la stat d'attaque brute.
    public int getAttack() {
        return attack;
    }

    // Définit la stat d'attaque brute.
    public void setAttack(int attack) {
        this.attack = attack;
    }

    // Alias "strength" pour la même stat d'attaque.
    public int getStrength() {
        return attack;
    }

    // Alias "strength" pour la même stat d'attaque.
    public void setStrength(int strength) {
        this.attack = strength;
    }

    // Retourne l'équipement offensif objet.
    public OffensiveEquipment getOffensiveEquipment() {
        return offensiveEquipment;
    }

    // Définit l'équipement offensif objet.
    public void setOffensiveEquipment(OffensiveEquipment offensiveEquipment) {
        this.offensiveEquipment = offensiveEquipment;
    }

    // Définit l'équipement offensif depuis son nom texte (format BDD).
    public void setOffensiveEquipment(String offensiveEquipment) {
        if (offensiveEquipment == null || offensiveEquipment.isBlank()) {
            this.offensiveEquipment = null;
            return;
        }
        this.offensiveEquipment = new OffensiveEquipment("DB", offensiveEquipment, 0);
    }

    // Retourne le nom d'équipement offensif, ou null.
    public String getOffensiveEquipmentName() {
        return offensiveEquipment == null ? null : offensiveEquipment.getName();
    }

    // Retourne l'équipement défensif objet.
    public DefensiveEquipment getDefensiveEquipment() {
        return defensiveEquipment;
    }

    // Définit l'équipement défensif objet.
    public void setDefensiveEquipment(DefensiveEquipment defensiveEquipment) {
        this.defensiveEquipment = defensiveEquipment;
    }

    // Définit l'équipement défensif depuis son nom texte (format BDD).
    public void setDefensiveEquipment(String defensiveEquipment) {
        if (defensiveEquipment == null || defensiveEquipment.isBlank()) {
            this.defensiveEquipment = null;
            return;
        }
        this.defensiveEquipment = new DefensiveEquipment("DB", defensiveEquipment, 0);
    }

    // Retourne le nom d'équipement défensif, ou null.
    public String getDefensiveEquipmentName() {
        return defensiveEquipment == null ? null : defensiveEquipment.getName();
    }

    // Calcule l'attaque totale (base + bonus offensif si présent).
    public int getTotalAttack() {
        if (offensiveEquipment == null) {
            return attack;
        }
        return attack + offensiveEquipment.getAttackBonus();
    }

    // Retourne une version texte utile pour le debug.
    @Override
    public String toString() {
        return "Character{type='" + type + "', name='" + name + "', health=" + health +
                ", attack=" + attack + ", totalAttack=" + getTotalAttack() +
                ", offensiveEquipment=" + offensiveEquipment +
                ", defensiveEquipment=" + defensiveEquipment + "}";
    }
}