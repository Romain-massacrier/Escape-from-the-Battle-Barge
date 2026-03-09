package fr.campus.escapebattlebarge.domain.character;

/** Modèle runtime d'un ennemi en combat. */
public class Enemy {
    private final String name;
    private int hp;
    private final int minDmg;
    private final int maxDmg;
    private final boolean boss;

    public Enemy(String name, int hp, int minDmg, int maxDmg, boolean boss) {
        this.name = name;
        this.hp = hp;
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
        this.boss = boss;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMinDmg() { return minDmg; }
    public int getMaxDmg() { return maxDmg; }
    public boolean isBoss() { return boss; }

    public boolean isAlive() { return hp > 0; }

    public void damage(int dmg) {
        hp = Math.max(0, hp - Math.max(0, dmg));
    }
}
