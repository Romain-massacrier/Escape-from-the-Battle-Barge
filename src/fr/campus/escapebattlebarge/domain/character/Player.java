package fr.campus.escapebattlebarge.domain.character;

import fr.campus.escapebattlebarge.domain.inventory.Inventory;
import fr.campus.escapebattlebarge.domain.character.player.PlayerClass;

/*
 * Cette classe abstraite représente le joueur pendant une partie (runtime).
 * Elle stocke ses PV, sa position et son inventaire utilisés par le gameplay.
 * Entrées/sorties: dégâts, soins, déplacement et accès état joueur.
 */
/** Modèle runtime du joueur contrôlé pendant la partie. */
public abstract class Player {

    private final String name;
    private final PlayerClass playerClass;
    private int hp;
    private final int maxHp;
    private int position; // case actuelle sur le plateau
    private final Inventory inventory;

    // Initialise un joueur avec ses stats de base et un inventaire vide.
    protected Player(String name, PlayerClass playerClass, int maxHp) {
        this.name = name;
        this.playerClass = playerClass;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.position = 1;
        this.inventory = new Inventory();
    }

    // =========================
    // GETTERS
    // =========================

    // Retourne le nom du joueur.
    public String getName() {
        return name;
    }
    // Retourne la classe du joueur.
    public PlayerClass getPlayerClass() {
        return playerClass;
    }
    // Retourne les PV actuels.
    public int getHp() {
        return hp;
    }
    // Retourne les PV max.
    public int getMaxHp() {
        return maxHp;
    }
    // Retourne la position sur le plateau.
    public int getPosition() {
        return position;
    }
    // Retourne l'inventaire du joueur.
    public Inventory getInventory() {
        return inventory;
    }

    // =========================
    // GAME LOGIC
    // =========================

    // Met à jour la position du joueur.
    public void setPosition(int position) {
        this.position = position;
    }

    // Indique si le joueur est encore vivant.
    public boolean isAlive() {
        return hp > 0;
    }

    // Applique des dégâts en restant dans [0, hp actuel].
    public void damage(int dmg) {
        if (dmg < 0) return;
        hp = Math.max(0, hp - dmg);
    }

    // Soigne le joueur sans dépasser ses PV max.
    public void heal(int amount) {
        if (amount < 0) return;
        hp = Math.min(maxHp, hp + amount);
    }
}