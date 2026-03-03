package fr.campus.escapebattlebarge.game;

import fr.campus.escapebattlebarge.domain.*;

import java.util.function.Consumer;

public class CombatEngine {
    private final Dice dice;

    public CombatEngine(Dice dice) {
        this.dice = dice;
    }

    public boolean fight(Player player, Enemy enemy, Consumer<String> log, InputProvider input) {
        log.accept("Action: Combat engagé contre " + enemy.getName() + " (PV " + enemy.getHp() + ")");

        while (player.isAlive() && enemy.isAlive()) {
            log.accept("Combat: " + player.getName() + " PV " + player.getHp() + "/" + player.getMaxHp() +
                " | Ennemi PV " + enemy.getHp());
            log.accept("1 Attaquer | 2 Potion | 3 Fuir");

            String choice = input.readChoice();

            if ("1".equals(choice)) {
                int dmg = computePlayerDamage(player);
                enemy.damage(dmg);
                String playerAction = "Tu frappes: -" + dmg + " PV";

                if (!enemy.isAlive()) {
                    log.accept("Action: " + playerAction);
                    break;
                }

                int enemyDmg = dice.between(enemy.getMinDmg(), enemy.getMaxDmg());
                player.damage(enemyDmg);
                String enemyAction = enemy.getName() + " frappe: -" + enemyDmg + " PV";
                log.accept("Action: " + playerAction + " | " + enemyAction);
                continue;

            } else if ("2".equals(choice)) {
                boolean used = tryUsePotion(player, log, input);
                if (!used) continue;

                if (!enemy.isAlive()) {
                    break;
                }

                int enemyDmg = dice.between(enemy.getMinDmg(), enemy.getMaxDmg());
                player.damage(enemyDmg);
                String enemyAction = enemy.getName() + " frappe: -" + enemyDmg + " PV";
                log.accept("Action: Potion utilisée | " + enemyAction);
                continue;

            } else if ("3".equals(choice)) {
                // Fuite avec pénalité simple: tu recules de 1 à 3 cases
                int back = dice.between(1, 3);
                player.setPosition(Math.max(1, player.getPosition() - back));
                log.accept("Action: Fuite! Recul de " + back + " cases. Nouvelle case: " + player.getPosition());
                waitForEnterToContinue(log, input);
                return false;

            } else {
                log.accept("Action: Choix invalide.");
                continue;
            }
        }

        if (!player.isAlive()) {
            log.accept("Tu t’effondres. Fin.");
            waitForEnterToContinue(log, input);
            return false;
        }

        log.accept("Ennemi abattu!");
        waitForEnterToContinue(log, input);
        return true;
    }

    private void waitForEnterToContinue(Consumer<String> log, InputProvider input) {
        log.accept("Appuie sur Entrée pour continuer.");
        input.readChoice();
    }

    private int computePlayerDamage(Player player) {
        Weapon w = player.getInventory().getEquippedWeapon();
        int base = (w == null) ? dice.between(1, 3) : dice.between(w.getMinDmg(), w.getMaxDmg());

        // Si Librarian et possède un pouvoir, on a une petite chance d’ajouter du psy
        if (player.getPlayerClass() == PlayerClass.LIBRARIAN) {
            boolean hasPower = player.getInventory().getStash().stream().anyMatch(i -> i instanceof Power);
            if (hasPower && dice.between(1, 100) <= 30) {
                // Prend le premier pouvoir du stash
                Power p = (Power) player.getInventory().getStash().stream()
                        .filter(i -> i instanceof Power)
                        .findFirst()
                        .orElse(null);
                if (p != null) {
                    base += dice.between(p.getMinDmg(), p.getMaxDmg());
                }
            }
        }
        return base;
    }

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

    public interface InputProvider {
        String readChoice();
    }
}