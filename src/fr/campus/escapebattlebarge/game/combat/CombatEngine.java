package fr.campus.escapebattlebarge.game.combat;

import fr.campus.escapebattlebarge.domain.character.Enemy;
import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.domain.consumable.Consumable;
import fr.campus.escapebattlebarge.domain.inventory.Inventory;
import fr.campus.escapebattlebarge.domain.item.equipment.Weapon;
import fr.campus.escapebattlebarge.game.random.Dice;

import java.util.function.Consumer;

/** Moteur de combat tour par tour joueur vs ennemi. */
public class CombatEngine {
    private final Dice dice;

    public CombatEngine(Dice dice) {
        this.dice = dice;
    }

    /** Exécute un combat; renvoie true si le joueur survit. */
    public boolean fight(Player player, Enemy enemy, Consumer<String> log, InputProvider input) {
        log.accept("Action: Combat engagé contre " + enemy.getName() + " (PV " + enemy.getHp() + ")");

        while (player.isAlive() && enemy.isAlive()) {
            log.accept("1 Attaquer | 2 Potion | 3 Fuir");

            String choice = input.readChoice();

            switch (choice) {
                case "1" -> handleAttackTurn(player, enemy, log);
                case "2" -> {
                    if (tryUsePotion(player, log, input) && enemy.isAlive()) {
                        applyEnemyCounterAttack(player, enemy, log, "Potion utilisée");
                    }
                }
                case "3" -> {
                    int back = dice.between(1, 3);
                    player.setPosition(Math.max(1, player.getPosition() - back));
                    log.accept("Action: Fuite! Recul de " + back + " cases. Nouvelle case: " + player.getPosition());
                    waitForEnterToContinue(log, input);
                    return false;
                }
                default -> log.accept("Action: Choix invalide.");
            }
        }

        if (!player.isAlive()) {
            return false;
        }

        log.accept("Ennemi abattu!");
        waitForEnterToContinue(log, input);
        return true;
    }

    private void handleAttackTurn(Player player, Enemy enemy, Consumer<String> log) {
        int dmg = computePlayerDamage(player);
        enemy.damage(dmg);
        String playerAction = "Tu frappes: -" + dmg + " PV";

        if (!enemy.isAlive()) {
            log.accept("Action: " + playerAction);
            return;
        }
        applyEnemyCounterAttack(player, enemy, log, playerAction);
    }

    private void applyEnemyCounterAttack(Player player, Enemy enemy, Consumer<String> log, String firstAction) {
        int enemyDmg = dice.between(enemy.getMinDmg(), enemy.getMaxDmg());
        player.damage(enemyDmg);
        log.accept("Action: " + firstAction + " | " + enemy.getName() + " frappe: -" + enemyDmg + " PV");
    }

    /** Pause pour laisser le joueur lire le dernier message. */
    private void waitForEnterToContinue(Consumer<String> log, InputProvider input) {
        log.accept("Appuie sur Entrée pour continuer.");
        input.readChoice();
    }

    /** Calcule les dégâts du joueur (arme + éventuel bonus psy). */
    private int computePlayerDamage(Player player) {
        Weapon w = player.getInventory().getEquippedWeapon();
        return (w == null) ? dice.between(1, 3) : dice.between(w.getMinDmg(), w.getMaxDmg());
    }

    /** Essaie de consommer une potion choisie par le joueur. */
    private boolean tryUsePotion(Player player, Consumer<String> log, InputProvider input) {
        Inventory inv = player.getInventory();
        if (inv.getConsumables().isEmpty()) {
            log.accept("Action: Aucune potion disponible.");
            return false;
        }

        log.accept("Choisis une potion:");
        for (int i = 0; i < inv.getConsumables().size(); i++) {
            log.accept((i + 1) + ". " + inv.getConsumables().get(i));
        }
        String c = input.readChoice();
        int idx;
        try {
            idx = Integer.parseInt(c) - 1;
        } catch (Exception e) {
            log.accept("Action: Choix invalide.");
            return false;
        }

        Consumable used = inv.removeConsumableAt(idx);
        if (used == null) {
            log.accept("Action: Choix invalide.");
            return false;
        }

        player.heal(used.getHealAmount());
        log.accept("Action: Tu utilises " + used.getName() + ". PV actuels: " + player.getHp() + "/" + player.getMaxHp());
        return true;
    }

    /** Fournit un choix texte (UI ou console). */
    public interface InputProvider {
        String readChoice();
    }
}