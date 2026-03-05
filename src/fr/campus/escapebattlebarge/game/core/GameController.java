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
import fr.campus.escapebattlebarge.game.factory.EnemyFactory;
import fr.campus.escapebattlebarge.game.random.Dice;

/*
 * Cette classe orchestre un tour de jeu côté UI: déplacement, combat, trésor et inventaire.
 * Elle est appelée par l'écran principal quand le joueur clique sur "lancer" ou "inventaire".
 * Entrées: choix utilisateur (InputProvider). Sorties: logs UI, état du joueur et sync BDD des PV.
 */
public class GameController {

    private static final String MAIN_PROMPT = "1 lancer dé | 2 inventaire";

    private final Dice dice = new Dice();
    private final EnemyFactory enemyFactory = new EnemyFactory();
    private final CombatEngine combat = new CombatEngine(dice);

    private final GameState state;
    private final CharacterDao characterDao;
    private Character currentHero;
    private boolean extractionAlertPlayed = false;

    // Construit le contrôleur avec seulement l'état de jeu (mode sans BDD).
    public GameController(GameState state) {
        this(state, null, null);
    }

    // Construit le contrôleur complet avec sync possible vers la base.
    public GameController(GameState state, CharacterDao characterDao, Character currentHero) {
        this.state = state;
        this.characterDao = characterDao;
        this.currentHero = currentHero;
        showMainPrompt();
        refreshStatusLine();
    }

    // Met à jour le héros courant utilisé pour synchroniser les PV en base.
    public void setCurrentHero(Character currentHero) {
        this.currentHero = currentHero;
    }

    // Test manuel:
    // - Appuyer sur 1 puis Entrée.
    // - Vérifier que la console est remplacée (clear) au début du tour,
    //   et que les nouvelles lignes s'affichent sans s'empiler sous l'ancien tour.
    // Gère l'action "lancer le dé": déplacement, événements de case, combat et statut UI.
    public void onRoll(InputProvider input) {
        Player p = state.getPlayer();
        int hpBefore = p.getHp();
        long session = state.clearConsoleAndStartNewSession();

        try {
            showMainPrompt(session);
            refreshStatusLine(session);

            int oldPos = p.getPosition();
            Zone oldZone = Zone.fromCell(Math.max(1, Math.min(64, oldPos)));

            // Règle de déplacement principale: avance de 1 à 6 cases.
            int roll = dice.rollD6();

            int newPos = p.getPosition() + roll;
            p.setPosition(newPos);

            Zone newZone = Zone.fromCell(Math.max(1, Math.min(64, newPos)));
            if (!extractionAlertPlayed && oldZone != Zone.EXTRACTION && newZone == Zone.EXTRACTION) {
                // On joue l'alerte une seule fois à l'entrée dans la zone finale.
                extractionAlertPlayed = true;
                state.requestExtractionAlertPlayback();
            }

            if (newPos >= 64) {
                state.log(session, "Tu atteins la zone d’extraction. Un boss t’attend.");
                boolean win = combat.fight(p, enemyFactory.createBoss(), msg -> state.log(session, msg), input);
                if (win) {
                    state.log(session, "Victoire. Extraction réussie.");
                }
                if (!p.isAlive()) {
                    handleDefeat(input, session);
                    return;
                }
                if (p.isAlive()) {
                    state.clearConsole();
                    showMainPrompt(session);
                }
                refreshStatusLine(session, roll);
                return;
            }

            Tile tile = state.getBoard().getTile(newPos);

            if (tile == null) {
                // ATTENTION : case absente => on sort proprement pour éviter un plantage plus loin.
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

    // Sauvegarde les PV du héros en base seulement s'ils ont vraiment changé.
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
            // ATTENTION : si la BDD échoue, le jeu continue mais la sauvegarde des PV est perdue.
            state.log("Erreur DB: impossible de sauvegarder les PV du héros.");
        }
    }

    // Ouvre l'inventaire, laisse équiper une arme depuis le stock, puis revient au prompt principal.
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

    // Affiche le prompt principal dans la console de jeu.
    private void showMainPrompt() {
        state.logMainPrompt(MAIN_PROMPT);
    }

    // Affiche le prompt principal pour une session de console donnée.
    private void showMainPrompt(long session) {
        state.logMainPrompt(session, MAIN_PROMPT);
    }

    // Nettoie la console puis réaffiche le prompt principal.
    private void returnToMainPrompt() {
        state.clearConsole();
        showMainPrompt();
    }

    // Lance et résout un combat pour une case ennemie.
    private void handleEnemy(TileType tileType, InputProvider input, long session) {
        Player p = state.getPlayer();
        state.log(session, "Alerte: contact hostile.");

        Enemy e = enemyFactory.createForTileType(tileType);
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

        Item item = switch (tile.getType()) {
            case TREASURE_POTION -> new Consumable("Potion standard", 12);
            case TREASURE_BIG_POTION -> new Consumable("Grande potion", 24);
            default -> throw new IllegalStateException("Type de trésor inattendu: " + tile.getType());
        };

        if (item instanceof Consumable cons) {
            boolean ok = p.getInventory().addConsumable(cons);
            if (ok) {
                state.log(session, "Tu obtiens: " + cons.getName());
            } else {
                // Pourquoi c’est comme ça: les consommables sont limités, on envoie le surplus dans le stock.
                state.log(session, "Consommables pleins. Stock: " + cons.getName());
                p.getInventory().addToStash(cons);
            }
            tile.setType(TileType.EMPTY);
            return;
        }

        // Weapon ou Power
        p.getInventory().addToStash(item);
        state.log(session, "Tu obtiens: " + item.getName() + " (stock)");
        tile.setType(TileType.EMPTY);
    }

    // Gère l'écran de défaite et déclenche le retour menu.
    private void handleDefeat(InputProvider input, long session) {
        state.log(session, "Vous avez perdu.");
        state.log(session, "appuyer sur entrer pour revenir au menu");
        input.readChoice();
        state.requestReturnToMainMenu();
    }

    // Met à jour la ligne de statut (PV, position, zone) hors session explicite.
    private void refreshStatusLine() {
        Player p = state.getPlayer();
        Zone zone = Zone.fromCell(Math.max(1, Math.min(64, p.getPosition())));

        state.logStatus("Statut: PV " + p.getHp() + "/" + p.getMaxHp()
                + " | Case " + p.getPosition()
            + " | " + zone.getLabel());
    }

    // Met à jour la ligne de statut pour une session donnée.
    private void refreshStatusLine(long session) {
        Player p = state.getPlayer();
        Zone zone = Zone.fromCell(Math.max(1, Math.min(64, p.getPosition())));

        state.logStatus(session, "Statut: PV " + p.getHp() + "/" + p.getMaxHp()
                + " | Case " + p.getPosition()
            + " | " + zone.getLabel());
        }

        // Met à jour la ligne de statut en affichant aussi la valeur du dernier dé.
        private void refreshStatusLine(long session, int roll) {
        Player p = state.getPlayer();
        Zone zone = Zone.fromCell(Math.max(1, Math.min(64, p.getPosition())));

        state.logStatus(session, "Statut: PV " + p.getHp() + "/" + p.getMaxHp()
            + " | Case " + p.getPosition() + " (+" + roll + ")"
            + " | " + zone.getLabel());
    }
}