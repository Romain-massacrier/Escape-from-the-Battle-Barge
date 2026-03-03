package fr.campus.escapebattlebarge.game;

import fr.campus.escapebattlebarge.domain.Player;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final Player player;
    private final Board board;
    private final List<String> consoleLines;

    public GameState(Player player, Board board) {
        this.player = player;
        this.board = board;
        this.consoleLines = new ArrayList<>();
    }
    public void clearConsole() {
        consoleLines.clear();
    }

    public Player getPlayer() { return player; }
    public Board getBoard() { return board; }

    public List<String> getConsoleLines() { return consoleLines; }

    public void log(String s) {
        consoleLines.add(s);
        if (consoleLines.size() > 10) {
            consoleLines.remove(0);
        }
    }
}