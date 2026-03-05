package fr.campus.escapebattlebarge.game.core;

import fr.campus.escapebattlebarge.domain.character.Enemy;
import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.game.board.Board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Cette classe centralise l'état courant d'une partie (joueur, plateau, ennemi, console UI).
 * Elle est utilisée par le contrôleur pour écrire l'affichage et piloter les transitions d'écran.
 * Entrées: événements de jeu. Sorties: lignes console, flags menu/audio, accès lecture état joueur/board.
 */
public class GameState {

    private static final int MAX_CONSOLE_LINES = 12;
    private static final boolean DEBUG_SESSION_TAGS = false;
    private static final String STATUS_PREFIX = "Statut:";
    private static final String COMBAT_STATUS_PREFIX = "Combat:";
    private static final String COMBAT_ACTION_PREFIX = "Action:";
    private static final String MAIN_PROMPT = "1 lancer dé | 2 inventaire";
    private static final String COMBAT_PROMPT = "1 Attaquer | 2 Potion | 3 Fuir";

    private final Board board;
    private final Player player;
    private Enemy currentEnemy;
    private boolean returnToMainMenuRequested = false;
    private boolean extractionAlertPending = false;

    private final List<String> consoleLines = new ArrayList<>();
    private long consoleSession = 0L;

    // Crée un état de partie avec un plateau et un joueur déjà initialisés.
    public GameState(Board board, Player player) {
        this.board = board;
        this.player = player;
    }

    // Retourne le plateau de jeu actif.
    public Board getBoard() {
        return board;
    }

    // Retourne le joueur actif.
    public Player getPlayer() {
        return player;
    }

    // Lit l'ennemi en cours de combat (thread-safe).
    public synchronized Enemy getCurrentEnemy() {
        return currentEnemy;
    }

    // Met à jour l'ennemi en cours de combat (thread-safe).
    public synchronized void setCurrentEnemy(Enemy currentEnemy) {
        this.currentEnemy = currentEnemy;
    }

    // Ajoute une ligne brute à la console.
    public synchronized void log(String line) {
        appendLine(line);
    }

    // Met à jour la ligne de statut (PV/position) dans la console.
    public synchronized void logStatus(String line) {
        upsertStatusLine(line);
    }

    // Met à jour la ligne de prompt principal dans la console.
    public synchronized void logMainPrompt(String line) {
        upsertMainPromptLine(line);
    }

    // Ajoute une ligne liée à une session, ignorée si la session n'est plus active.
    public synchronized void log(long session, String line) {
        if (session != consoleSession) {
            return;
        }
        appendLine(formatSessionLine(session, line));
    }

    // Met à jour le statut pour une session active précise.
    public synchronized void logStatus(long session, String line) {
        if (session != consoleSession) {
            return;
        }
        upsertStatusLine(formatSessionLine(session, line));
    }

    // Met à jour le prompt principal pour une session active précise.
    public synchronized void logMainPrompt(long session, String line) {
        if (session != consoleSession) {
            return;
        }
        upsertMainPromptLine(formatSessionLine(session, line));
    }

    // Formate les logs de session (tag activable pour debug).
    private String formatSessionLine(long session, String line) {
        if (!DEBUG_SESSION_TAGS) {
            return line;
        }
        return "[S" + session + "] " + line;
    }

    // Ajoute une ligne et garde la console propre (prompt/statut/combat + taille max).
    private void appendLine(String line) {
        if (isCombatPromptLine(line) || isCombatStatusLine(line) || isCombatActionLine(line)) {
            // Pourquoi c’est comme ça: en combat on masque les anciennes lignes de menu pour éviter le bruit.
            removeMainAndStatusLines();
        }

        if (isCombatStatusLine(line)) {
            for (int i = consoleLines.size() - 1; i >= 0; i--) {
                if (isCombatStatusLine(consoleLines.get(i))) {
                    consoleLines.remove(i);
                    break;
                }
            }
        }

        if (isCombatPromptLine(line)) {
            for (int i = consoleLines.size() - 1; i >= 0; i--) {
                if (isCombatPromptLine(consoleLines.get(i))) {
                    consoleLines.remove(i);
                    break;
                }
            }
        }

        if (isCombatActionLine(line)) {
            for (int i = consoleLines.size() - 1; i >= 0; i--) {
                if (isCombatActionLine(consoleLines.get(i))) {
                    consoleLines.remove(i);
                    break;
                }
            }
        }

        consoleLines.add(line);

        // ATTENTION : on tronque l'historique au-delà de 12 lignes pour ne pas saturer l'UI.
        while (consoleLines.size() > MAX_CONSOLE_LINES) {
            consoleLines.remove(0);
        }
    }

    // Supprime les lignes de menu/statut quand on entre dans une séquence combat.
    private void removeMainAndStatusLines() {
        for (int i = consoleLines.size() - 1; i >= 0; i--) {
            String existingLine = consoleLines.get(i);
            if (isMainPromptLine(existingLine) || isStatusLine(existingLine)) {
                consoleLines.remove(i);
            }
        }
    }

    // Remplace la dernière ligne de statut existante, sinon l'ajoute.
    private void upsertStatusLine(String line) {
        for (int i = consoleLines.size() - 1; i >= 0; i--) {
            if (isStatusLine(consoleLines.get(i))) {
                consoleLines.remove(i);
                break;
            }
        }
        appendLine(line);
    }

    // Remplace le prompt principal existant, sinon l'ajoute.
    private void upsertMainPromptLine(String line) {
        for (int i = consoleLines.size() - 1; i >= 0; i--) {
            if (isMainPromptLine(consoleLines.get(i))) {
                consoleLines.remove(i);
                break;
            }
        }
        appendLine(line);
    }

    // Détecte si une ligne correspond au statut standard.
    private boolean isStatusLine(String line) {
        if (line == null) {
            return false;
        }
        if (line.startsWith(STATUS_PREFIX)) {
            return true;
        }
        int closeBracket = line.indexOf(']');
        if (closeBracket >= 0 && closeBracket + 2 < line.length()) {
            return line.startsWith(STATUS_PREFIX, closeBracket + 2);
        }
        return false;
    }

    // Détecte si une ligne correspond au prompt principal.
    private boolean isMainPromptLine(String line) {
        if (line == null) {
            return false;
        }
        if (line.equals(MAIN_PROMPT)) {
            return true;
        }
        int closeBracket = line.indexOf(']');
        if (closeBracket >= 0 && closeBracket + 2 < line.length()) {
            return line.startsWith(MAIN_PROMPT, closeBracket + 2);
        }
        return false;
    }

    // Détecte si une ligne correspond au statut de combat.
    private boolean isCombatStatusLine(String line) {
        if (line == null) {
            return false;
        }
        if (line.startsWith(COMBAT_STATUS_PREFIX)) {
            return true;
        }
        int closeBracket = line.indexOf(']');
        if (closeBracket >= 0 && closeBracket + 2 < line.length()) {
            return line.startsWith(COMBAT_STATUS_PREFIX, closeBracket + 2);
        }
        return false;
    }

    // Détecte si une ligne correspond à une action de combat.
    private boolean isCombatActionLine(String line) {
        if (line == null) {
            return false;
        }
        if (line.startsWith(COMBAT_ACTION_PREFIX)) {
            return true;
        }
        int closeBracket = line.indexOf(']');
        if (closeBracket >= 0 && closeBracket + 2 < line.length()) {
            return line.startsWith(COMBAT_ACTION_PREFIX, closeBracket + 2);
        }
        return false;
    }

    // Détecte si une ligne correspond au prompt d'actions de combat.
    private boolean isCombatPromptLine(String line) {
        if (line == null) {
            return false;
        }
        if (line.equals(COMBAT_PROMPT)) {
            return true;
        }
        int closeBracket = line.indexOf(']');
        if (closeBracket >= 0 && closeBracket + 2 < line.length()) {
            return line.startsWith(COMBAT_PROMPT, closeBracket + 2);
        }
        return false;
    }

    // Vide entièrement la console mémoire.
    public synchronized void clearConsole() {
        consoleLines.clear();
    }

    // Vide la console et démarre une nouvelle session de logs.
    public synchronized long clearConsoleAndStartNewSession() {
        consoleSession++;
        consoleLines.clear();
        return consoleSession;
    }

    // Renvoie une copie en lecture seule des lignes console.
    public synchronized List<String> getConsoleLines() {
        return Collections.unmodifiableList(new ArrayList<>(consoleLines));
    }

    // Demande un retour au menu principal (flag consommable).
    public synchronized void requestReturnToMainMenu() {
        returnToMainMenuRequested = true;
    }

    // Lit et réinitialise la demande de retour menu.
    public synchronized boolean consumeReturnToMainMenuRequest() {
        boolean requested = returnToMainMenuRequested;
        returnToMainMenuRequested = false;
        return requested;
    }

    // Demande la lecture du son d'alerte d'extraction.
    public synchronized void requestExtractionAlertPlayback() {
        extractionAlertPending = true;
    }

    // Lit et réinitialise la demande de son d'extraction.
    public synchronized boolean consumeExtractionAlertPlaybackRequest() {
        boolean requested = extractionAlertPending;
        extractionAlertPending = false;
        return requested;
    }
}