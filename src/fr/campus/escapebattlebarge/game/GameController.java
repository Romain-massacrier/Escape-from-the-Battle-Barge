package fr.campus.escapebattlebarge.game;

import fr.campus.escapebattlebarge.domain.*;
import fr.campus.escapebattlebarge.game.CombatEngine.InputProvider;

public class GameController {

    private final Dice dice = new Dice();
    private final EnemyFactory enemyFactory = new EnemyFactory();
    private final LootTables lootTables = new LootTables();
    private final CombatEngine combat = new CombatEngine(dice);

    private final GameState state;

    public GameController(GameState state) {
        this.state = state;

        state.clearConsole();
        state.log("1 lancer dé | 2 inventaire");
        refreshStatusLine();
    }

    public void onRoll(InputProvider input) {
        Player p = state.getPlayer();

        // ✅ Remplace l’affichage précédent
        state.clearConsole();
        state.log("1 lancer dé | 2 inventaire");

        state.log("Dé en cours...");
        sleep(350);

        int roll = dice.rollD6();
        state.log("Résultat: " + roll);

        int newPos = p.getPosition() + roll;
        p.setPosition(newPos);

        if (newPos >= 64) {
            state.log("Tu atteins la zone d’extraction. Un boss t’attend.");
            boolean win = combat.fight(p, enemyFactory.createBoss(), state::log, input);
            if (win) {
                state.log("Victoire. Extraction réussie.");
            }
            refreshStatusLine();
            return;
        }

        Tile tile = state.getBoard().getTile(newPos);
        Zone zone = (tile != null) ? tile.getZone() : Zone.EXTRACTION;

        state.log("Position: case " + newPos + " | Zone: " + zone.getLabel());

        if (tile == null) {
            refreshStatusLine();
            return;
        }

        switch (tile.getType()) {
            case MONSTER -> handleMonster(zone, input);
            case TREASURE -> handleTreasure(zone);
            default -> state.log("Rien à signaler. Avance.");
        }

        refreshStatusLine();
    }

    public void onInventory(InputProvider input) {
        Player p = state.getPlayer();
        Inventory inv = p.getInventory();

        // ✅ Remplace l’affichage précédent
        state.clearConsole();
        state.log("Inventaire:");

        Weapon eq = inv.getEquippedWeapon();
        state.log("Arme équipée: " + (eq == null ? "Aucune" : eq.getName()));

        state.log("Consommables (" + inv.getConsumables().size() + "/3):");
        if (inv.getConsumables().isEmpty()) {
            state.log("  vide");
        } else {
            for (int i = 0; i < inv.getConsumables().size(); i++) {
                state.log("  " + (i + 1) + ". " + inv.getConsumables().get(i));
            }
        }

        state.log("Stock:");
        if (inv.getStash().isEmpty()) {
            state.log("  vide");
        } else {
            for (int i = 0; i < inv.getStash().size(); i++) {
                Item it = inv.getStash().get(i);
                state.log("  " + (i + 1) + ". " + it.getName());
            }
        }

        state.log("Choix: 1 Equiper arme (stock) | autre retour");
        String c = input.readChoice();
        if (!"1".equals(c)) {
            state.clearConsole();
            state.log("1 lancer dé | 2 inventaire");
            refreshStatusLine();
            return;
        }

        state.log("Numéro arme dans Stock:");
        String idxStr = input.readChoice();

        int idx;
        try {
            idx = Integer.parseInt(idxStr) - 1;
        } catch (Exception e) {
            state.log("Invalide.");
            return;
        }

        if (idx < 0 || idx >= inv.getStash().size()) {
            state.log("Invalide.");
            return;
        }

        Item it = inv.getStash().get(idx);
        if (!(it instanceof Weapon)) {
            state.log("Ce n’est pas une arme.");
            return;
        }

        inv.equipWeapon((Weapon) it);
        state.log("Arme équipée: " + it.getName());

        state.clearConsole();
        state.log("1 lancer dé | 2 inventaire");
        refreshStatusLine();
    }

    private void handleMonster(Zone zone, InputProvider input) {
        Player p = state.getPlayer();
        state.log("Alerte: contact hostile.");

        Enemy e = enemyFactory.createForZone(zone);
        boolean win = combat.fight(p, e, state::log, input);
        if (!win && !p.isAlive()) return;
    }

    private void handleTreasure(Zone zone) {
        Player p = state.getPlayer();
        state.log("Découverte: trésor.");

        Item item = lootTables.rollTreasure(p, zone);

        if (item instanceof Consumable cons) {
            boolean ok = p.getInventory().addConsumable(cons);
            if (ok) {
                state.log("Tu obtiens: " + cons.getName());
            } else {
                state.log("Consommables pleins. Stock: " + cons.getName());
                p.getInventory().addToStash(cons);
            }
            return;
        }

        // Weapon ou Power
        p.getInventory().addToStash(item);
        state.log("Tu obtiens: " + item.getName() + " (stock)");
    }

    private void refreshStatusLine() {
        Player p = state.getPlayer();
        Weapon w = p.getInventory().getEquippedWeapon();

        state.log("Statut: PV " + p.getHp() + "/" + p.getMaxHp()
                + " | Case " + p.getPosition()
                + " | Arme " + (w == null ? "Aucune" : w.getName()));
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}