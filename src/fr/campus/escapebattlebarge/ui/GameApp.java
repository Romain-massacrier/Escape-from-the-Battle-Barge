package fr.campus.escapebattlebarge.ui;

import fr.campus.escapebattlebarge.domain.*;
import fr.campus.escapebattlebarge.game.*;

import javax.swing.*;

public class GameApp {
    public static void start(Player player) {
        Board board = new Board();
        GameState state = new GameState(player, board);

        SwingUtilities.invokeLater(() -> {
            GameFrame f = new GameFrame(state);
            f.setVisible(true);
        });
    }
}