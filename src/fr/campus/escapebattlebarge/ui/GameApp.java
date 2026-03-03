package fr.campus.escapebattlebarge.ui;

import fr.campus.escapebattlebarge.domain.Player;
import fr.campus.escapebattlebarge.game.Board;
import fr.campus.escapebattlebarge.game.GameState;

import javax.swing.SwingUtilities;

public class GameApp {

	public static void start(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null");
		}

		SwingUtilities.invokeLater(() -> {
			Board board = new Board();
			GameState state = new GameState(board, player);
			new GameFrame(state);
		});
	}
}
