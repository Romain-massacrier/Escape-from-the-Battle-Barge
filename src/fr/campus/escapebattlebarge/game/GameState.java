package fr.campus.escapebattlebarge.game;

import fr.campus.escapebattlebarge.domain.Enemy;
import fr.campus.escapebattlebarge.domain.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public GameState(Board board, Player player) {
        this.board = board;
        this.player = player;
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer() {
        return player;
    }

    public synchronized Enemy getCurrentEnemy() {
        return currentEnemy;
    }

    public synchronized void setCurrentEnemy(Enemy currentEnemy) {
        this.currentEnemy = currentEnemy;
    }

    public synchronized void log(String line) {
        appendLine(line);
    }

    public synchronized void logStatus(String line) {
        upsertStatusLine(line);
    }

    public synchronized void logMainPrompt(String line) {
        upsertMainPromptLine(line);
    }

    public synchronized void log(long session, String line) {
        if (session != consoleSession) {
            return;
        }
        appendLine(formatSessionLine(session, line));
    }

    public synchronized void logStatus(long session, String line) {
        if (session != consoleSession) {
            return;
        }
        upsertStatusLine(formatSessionLine(session, line));
    }

    public synchronized void logMainPrompt(long session, String line) {
        if (session != consoleSession) {
            return;
        }
        upsertMainPromptLine(formatSessionLine(session, line));
    }

    private String formatSessionLine(long session, String line) {
        if (!DEBUG_SESSION_TAGS) {
            return line;
        }
        return "[S" + session + "] " + line;
    }

    private void appendLine(String line) {
        if (isCombatPromptLine(line) || isCombatStatusLine(line) || isCombatActionLine(line)) {
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

        while (consoleLines.size() > MAX_CONSOLE_LINES) {
            consoleLines.remove(0);
        }
    }

    private void removeMainAndStatusLines() {
        for (int i = consoleLines.size() - 1; i >= 0; i--) {
            String existingLine = consoleLines.get(i);
            if (isMainPromptLine(existingLine) || isStatusLine(existingLine)) {
                consoleLines.remove(i);
            }
        }
    }

    private void upsertStatusLine(String line) {
        for (int i = consoleLines.size() - 1; i >= 0; i--) {
            if (isStatusLine(consoleLines.get(i))) {
                consoleLines.remove(i);
                break;
            }
        }
        appendLine(line);
    }

    private void upsertMainPromptLine(String line) {
        for (int i = consoleLines.size() - 1; i >= 0; i--) {
            if (isMainPromptLine(consoleLines.get(i))) {
                consoleLines.remove(i);
                break;
            }
        }
        appendLine(line);
    }

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

    public synchronized void clearConsole() {
        consoleLines.clear();
    }

    public synchronized long clearConsoleAndStartNewSession() {
        consoleSession++;
        consoleLines.clear();
        return consoleSession;
    }

    public synchronized List<String> getConsoleLines() {
        return Collections.unmodifiableList(new ArrayList<>(consoleLines));
    }

    public synchronized void requestReturnToMainMenu() {
        returnToMainMenuRequested = true;
    }

    public synchronized boolean consumeReturnToMainMenuRequest() {
        boolean requested = returnToMainMenuRequested;
        returnToMainMenuRequested = false;
        return requested;
    }

    public synchronized void requestExtractionAlertPlayback() {
        extractionAlertPending = true;
    }

    public synchronized boolean consumeExtractionAlertPlaybackRequest() {
        boolean requested = extractionAlertPending;
        extractionAlertPending = false;
        return requested;
    }
}