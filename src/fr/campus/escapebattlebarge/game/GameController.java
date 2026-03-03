package fr.campus.escapebattlebarge.game;

import fr.campus.escapebattlebarge.domain.*;
import fr.campus.escapebattlebarge.game.CombatEngine.InputProvider;

public class GameController {

    private static final String MAIN_PROMPT = "1 lancer dé | 2 inventaire";

    private final Dice dice = new Dice();
    private final EnemyFactory enemyFactory = new EnemyFactory();
    private final LootTables lootTables = new LootTables();
    private final CombatEngine combat = new CombatEngine(dice);

    private final GameState state;

    public GameController(GameState state) {
        this.state = state;
        showMainPrompt();
        refreshStatusLine();
    }

    // Test manuel:
    // - Appuyer sur 1 puis Entrée.
    // - Vérifier que la console est remplacée (clear) au début du tour,
    //   et que les nouvelles lignes s'affichent sans s'empiler sous l'ancien tour.
    public void onRoll(InputProvider input) {
        Player p = state.getPlayer();
        long session = state.clearConsoleAndStartNewSession();

        showMainPrompt(session);
        refreshStatusLine(session);

        int roll = dice.rollD6();

        int newPos = p.getPosition() + roll;
        p.setPosition(newPos);

        if (newPos >= 64) {
            state.log(session, "Tu atteins la zone d’extraction. Un boss t’attend.");
            boolean win = combat.fight(p, enemyFactory.createBoss(), msg -> state.log(session, msg), input);
            if (win) {
                state.log(session, "Victoire. Extraction réussie.");
            }
            if (p.isAlive()) {
                state.clearConsole();
                showMainPrompt(session);
            }
            refreshStatusLine(session, roll);
            return;
        }

        Tile tile = state.getBoard().getTile(newPos);
        Zone zone = (tile != null) ? tile.getZone() : Zone.EXTRACTION;

        if (tile == null) {
            showMainPrompt(session);
            refreshStatusLine(session, roll);
            return;
        }

        switch (tile.getType()) {
            case MONSTER -> handleMonster(zone, input, session);
            case TREASURE -> handleTreasure(zone, session);
            default -> state.log(session, "Rien à signaler. Avance.");
        }

        showMainPrompt(session);
        refreshStatusLine(session, roll);
    }

    public void onInventory(InputProvider input) {
        Player p = state.getPlayer();
        Inventory inv = p.getInventory();

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
            returnToMainPrompt();
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

        returnToMainPrompt();
        refreshStatusLine();
    }

    private void showMainPrompt() {
        state.logMainPrompt(MAIN_PROMPT);
    }

    private void showMainPrompt(long session) {
        state.logMainPrompt(session, MAIN_PROMPT);
    }

    private void returnToMainPrompt() {
        state.clearConsole();
        showMainPrompt();
    }

    private void handleMonster(Zone zone, InputProvider input, long session) {
        Player p = state.getPlayer();
        state.log(session, "Alerte: contact hostile.");

        Enemy e = enemyFactory.createForZone(zone);
        boolean win = combat.fight(p, e, msg -> state.log(session, msg), input);
        if (!win && !p.isAlive()) return;

        state.clearConsole();
    }

    private void handleTreasure(Zone zone, long session) {
        Player p = state.getPlayer();
        state.log(session, "Découverte: trésor.");

        Item item = lootTables.rollTreasure(p, zone);

        if (item instanceof Consumable cons) {
            boolean ok = p.getInventory().addConsumable(cons);
            if (ok) {
                state.log(session, "Tu obtiens: " + cons.getName());
            } else {
                state.log(session, "Consommables pleins. Stock: " + cons.getName());
                p.getInventory().addToStash(cons);
            }
            return;
        }

        // Weapon ou Power
        p.getInventory().addToStash(item);
        state.log(session, "Tu obtiens: " + item.getName() + " (stock)");
    }

    private void refreshStatusLine() {
        Player p = state.getPlayer();
        Zone zone = Zone.fromCell(Math.max(1, Math.min(64, p.getPosition())));

        state.logStatus("Statut: PV " + p.getHp() + "/" + p.getMaxHp()
                + " | Case " + p.getPosition()
            + " | Zone " + zone.getLabel());
    }

    private void refreshStatusLine(long session) {
        Player p = state.getPlayer();
        Zone zone = Zone.fromCell(Math.max(1, Math.min(64, p.getPosition())));

        state.logStatus(session, "Statut: PV " + p.getHp() + "/" + p.getMaxHp()
                + " | Case " + p.getPosition()
            + " | Zone " + zone.getLabel());
        }

        private void refreshStatusLine(long session, int roll) {
        Player p = state.getPlayer();
        Zone zone = Zone.fromCell(Math.max(1, Math.min(64, p.getPosition())));

        state.logStatus(session, "Statut: PV " + p.getHp() + "/" + p.getMaxHp()
            + " | Case " + p.getPosition() + " (+" + roll + ")"
            + " | Zone " + zone.getLabel());
    }
}