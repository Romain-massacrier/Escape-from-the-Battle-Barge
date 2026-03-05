package fr.campus.escapebattlebarge.ui.app;

import fr.campus.escapebattlebarge.db.BoardDao;
import fr.campus.escapebattlebarge.db.CharacterDao;
import fr.campus.escapebattlebarge.domain.character.Character;
import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.game.board.Board;
import fr.campus.escapebattlebarge.game.core.GameState;
import fr.campus.escapebattlebarge.game.board.Tile;
import fr.campus.escapebattlebarge.game.board.TileType;
import fr.campus.escapebattlebarge.ui.view.GameFrame;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

/*
 * Cette classe démarre une partie graphique et prépare son état initial.
 * Elle crée le board, peut le sauvegarder en base, puis ouvre la fenêtre de jeu.
 * Entrées: joueur/héros/dao. Sorties: GameState initialisé + GameFrame affichée.
 */
public class GameApp {

	// Point d'entrée côté UI pour lancer une run.
	public static void start(Player player,
                             CharacterDao characterDao,
                             BoardDao boardDao,
                             Character currentHero,
                             Runnable onReturnToMenu) {
		if (player == null) {
			// ATTENTION : sans joueur, la partie ne peut pas être initialisée.
			throw new IllegalArgumentException("player cannot be null");
		}

		SwingUtilities.invokeLater(() -> {
			Board board = new Board();
			if (boardDao != null) {
				String boardName = "Run " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				int boardId = boardDao.createBoard(boardName);
				// On persiste une photo du plateau au début de la run.
				boardDao.saveCells(boardId, convertBoardToDbCells(board, characterDao));
				System.out.println("Board enregistré avec id=" + boardId);
			}

			GameState state = new GameState(board, player);
			new GameFrame(state, characterDao, currentHero, onReturnToMenu);
		});
	}

	// Convertit les cases du board en format BDD.
	private static List<BoardDao.DbCell> convertBoardToDbCells(Board board, CharacterDao characterDao) {
		List<BoardDao.DbCell> cells = new ArrayList<>();

		// Boucle complète sur les 64 cases du plateau.
		for (int position = 1; position <= 64; position++) {
			Tile tile = board.getTile(position);
			if (tile == null) {
				continue;
			}

			BoardDao.DbCell cell = new BoardDao.DbCell();
			cell.position = position;
			cell.zone = tile.getZone().name();
			cell.enemyCharacterId = mapEnemyId(tile.getType(), characterDao);
			cell.treasure = mapTreasure(tile.getType());
			cell.offensiveLoot = null;
			cell.defensiveLoot = null;
			cells.add(cell);
		}

		return cells;
	}

	// Mappe un type de case ennemi vers un id ennemi en base.
	private static Integer mapEnemyId(TileType type, CharacterDao characterDao) {
		if (characterDao == null) {
			return null;
		}

		String enemyType = switch (type) {
			case ENEMY_ORK -> "OrkBoy";
			case ENEMY_SORCERER -> "Sorcerer";
			case ENEMY_SQUIG -> "Squig";
			case ENEMY_WARBOSS -> "Warboss";
			default -> null;
		};

		if (enemyType == null) {
			return null;
		}

		return characterDao.findEnemyIdByType(enemyType);
	}

	// Mappe un type de case trésor vers une valeur persistable.
	private static String mapTreasure(TileType type) {
		return switch (type) {
			case TREASURE_POTION -> "POTION";
			case TREASURE_BIG_POTION -> "BIG_POTION";
			default -> null;
		};
	}
}
