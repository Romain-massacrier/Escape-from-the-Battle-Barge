package fr.campus.escapebattlebarge.domain;

public abstract class Player {

    private final String name;
    private final PlayerClass playerClass;

    private int hp;
    private final int maxHp;

    private int position; // case actuelle sur le plateau
    private final Inventory inventory;

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

    public String getName() {
        return name;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getPosition() {
        return position;
    }

    public Inventory getInventory() {
        return inventory;
    }

    // =========================
    // GAME LOGIC
    // =========================

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void damage(int dmg) {
        if (dmg < 0) return;
        hp = Math.max(0, hp - dmg);
    }

    public void heal(int amount) {
        if (amount < 0) return;
        hp = Math.min(maxHp, hp + amount);
    }
}