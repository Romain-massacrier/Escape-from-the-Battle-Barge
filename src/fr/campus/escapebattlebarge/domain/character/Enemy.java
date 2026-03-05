package fr.campus.escapebattlebarge.domain.character;

/*
 * Cette classe représente un ennemi en combat (runtime).
 * Elle est utilisée par le moteur de combat pour calculer les échanges de dégâts.
 * Entrées/sorties: HP, plage de dégâts, statut boss.
 */
public class Enemy {
    private final String name;
    private int hp;
    private final int minDmg;
    private final int maxDmg;
    private final boolean boss;

    // Initialise un ennemi avec ses stats de combat.
    public Enemy(String name, int hp, int minDmg, int maxDmg, boolean boss) {
        this.name = name;
        this.hp = hp;
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
        this.boss = boss;
    }

    // Retourne le nom affiché de l'ennemi.
    public String getName() { return name; }
    // Retourne les PV actuels.
    public int getHp() { return hp; }
    // Retourne les dégâts minimum.
    public int getMinDmg() { return minDmg; }
    // Retourne les dégâts maximum.
    public int getMaxDmg() { return maxDmg; }
    // Indique si c'est un boss.
    public boolean isBoss() { return boss; }

    // Indique si l'ennemi est encore vivant.
    public boolean isAlive() { return hp > 0; }

    // Applique des dégâts en évitant les valeurs négatives.
    public void damage(int dmg) {
        hp = Math.max(0, hp - Math.max(0, dmg));
    }
}
