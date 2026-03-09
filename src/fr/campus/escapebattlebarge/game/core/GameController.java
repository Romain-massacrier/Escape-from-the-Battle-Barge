package fr.campus.escapebattlebarge.game.core;

import fr.campus.escapebattlebarge.db.CharacterDao;
import fr.campus.escapebattlebarge.domain.character.Character;
import fr.campus.escapebattlebarge.domain.consumable.Consumable;
import fr.campus.escapebattlebarge.domain.character.Enemy;
import fr.campus.escapebattlebarge.domain.inventory.Inventory;
import fr.campus.escapebattlebarge.domain.item.Item;
import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.domain.item.equipment.Weapon;
import fr.campus.escapebattlebarge.game.combat.CombatEngine;
import fr.campus.escapebattlebarge.game.combat.CombatEngine.InputProvider;
import fr.campus.escapebattlebarge.game.board.Tile;
import fr.campus.escapebattlebarge.game.board.TileType;
import fr.campus.escapebattlebarge.game.board.Zone;
import fr.campus.escapebattlebarge.domain.item.equipment.Potion;
import fr.campus.escapebattlebarge.game.random.Dice;

/** Contrôleur d'un tour de jeu (déplacement, combat, trésor, inventaire). */
public class GameController {

    private static final String MAIN_PROMPT = "1 lancer dé | 2 inventaire";

    private final Dice dice = new Dice();
    private final CombatEngine combat = new CombatEngine(dice);
    private final Potion potion = new Potion();

    private final GameState state;
    private final CharacterDao characterDao;
    private Character currentHero;
    private boolean extractionAlertPlayed = false;

    public GameController(GameState state) {
        this(state, null, null);
    }

    public GameController(GameState state, CharacterDao characterDao, Character currentHero) {
        this.state = state;
        this.characterDao = characterDao;
        this.currentHero = currentHero;
        showMainPrompt();
        refreshStatusLine();
    }

    public void setCurrentHero(Character currentHero) {
        this.currentHero = currentHero;
    }

    /** Action principale: lancer le dé, appliquer la case, puis rafraîchir l'UI. */
    public void onRoll(InputProvider input) {
        Player p = state.getPlayer();
        int hpBefore = p.getHp();
        long session = state.clearConsoleAndStartNewSession();

        try {
            showMainPrompt(session);
            refreshStatusLine(session);

            int oldPos = p.getPosition();
            Zone oldZone = Zone.fromCell(Math.max(1, Math.min(64, oldPos)));

            int roll = dice.rollD6();

            int newPos = p.getPosition() + roll;
            p.setPosition(newPos);

            Zone newZone = Zone.fromCell(Math.max(1, Math.min(64, newPos)));
            if (!extractionAlertPlayed && oldZone != Zone.EXTRACTION && newZone == Zone.EXTRACTION) {
                extractionAlertPlayed = true;
                state.requestExtractionAlertPlayback();
            }

            if (newPos >= 64) {
                state.log(session, "Tu atteins la zone d’extraction. Un boss t’attend.");
                boolean win = combat.fight(p, TileType.ENEMY_WARBOSS.createEnemy(), msg -> state.log(session, msg), input);
                if (win) {
                    state.log(session, "Victoire. Extraction réussie.");
                }
                if (!p.isAlive()) {
                    handleDefeat(input, session);
                    return;
                }
                state.clearConsole();
                showMainPrompt(session);
                refreshStatusLine(session, roll);
                return;
            }

            Tile tile = state.getBoard().getTile(newPos);

            if (tile == null) {
                showMainPrompt(session);
                refreshStatusLine(session, roll);
                return;
            }

            switch (tile.getType()) {
                case ENEMY_ORK, ENEMY_SORCERER, ENEMY_SQUIG, ENEMY_WARBOSS -> handleEnemy(tile.getType(), input, session);
                case TREASURE_POTION, TREASURE_BIG_POTION -> handleTreasure(tile, session);
                default -> state.log(session, "Rien à signaler. Avance.");
            }

            if (!p.isAlive()) {
                handleDefeat(input, session);
                return;
            }

            showMainPrompt(session);
            refreshStatusLine(session, roll);
        } finally {
            syncLifePointsIfChanged(hpBefore);
        }
    }

    /** Synchronise les PV du héros en base s'ils ont changé. */
    private void syncLifePointsIfChanged(int hpBefore) {
        if (characterDao == null || currentHero == null) {
            return;
        }

        int hpAfter = state.getPlayer().getHp();
        if (hpAfter == hpBefore) {
            return;
        }

        try {
            currentHero.setLifePoints(hpAfter);
            characterDao.changeLifePoints(currentHero);
        } catch (RuntimeException e) {
            state.log("Erreur DB: impossible de sauvegarder les PV du héros.");
        }
    }

    /** Ouvre l'inventaire et permet d'équiper une arme depuis le stock. */
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

    /** Lance et résout un combat pour une case ennemie. */
    private void handleEnemy(TileType tileType, InputProvider input, long session) {
        Player p = state.getPlayer();
        state.log(session, "Alerte: contact hostile.");

        Enemy e = tileType.createEnemy();
        state.setCurrentEnemy(e);

        boolean win = combat.fight(p, e, msg -> state.log(session, msg), input);
        state.setCurrentEnemy(null);

        if (!win && !p.isAlive()) return;

        state.clearConsole();
    }

    // Donne le loot d'une case trésor et met la case à vide après récupération.
    private void handleTreasure(Tile tile, long session) {
        Player p = state.getPlayer();
        state.log(session, "Découverte: trésor.");

        Zone zone = Zone.fromCell(Math.max(1, Math.min(64, p.getPosition())));
        for (Consumable cons : potion.rollTreasurePack(p, zone)) {
            boolean ok = p.getInventory().addConsumable(cons);
            if (ok) {
                state.log(session, "Tu obtiens: " + cons.getName());
            } else {
                state.log(session, "Consommables pleins. Stock: " + cons.getName());
                p.getInventory().addToStash(cons);
            }
        }

        tile.setType(TileType.EMPTY);
    }

    /** Gère la défaite et demande le retour au menu principal. */
    private void handleDefeat(InputProvider input, long session) {
        state.log(session, "Vous avez perdu.");
        state.log(session, "appuyer sur entrer pour revenir au menu");
        input.readChoice();
        state.requestReturnToMainMenu();
    }

    private void refreshStatusLine() {
        state.logStatus(buildStatusLine(null));
    }

    private void refreshStatusLine(long session) {
        state.logStatus(session, buildStatusLine(null));
    }

    private void refreshStatusLine(long session, int roll) {
        state.logStatus(session, buildStatusLine(roll));
    }

    /** Construit la ligne de statut affichée dans la console. */
    private String buildStatusLine(Integer roll) {
        Player p = state.getPlayer();
        Zone zone = Zone.fromCell(Math.max(1, Math.min(64, p.getPosition())));
        String rollPart = (roll == null) ? "" : " (+" + roll + ")";
        return "Statut: PV " + p.getHp() + "/" + p.getMaxHp()
                + " | Case " + p.getPosition() + rollPart
                + " | " + zone.getLabel();
    }
}