package fr.campus.escapebattlebarge.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/*
 * Ce DAO gère la persistance des boards et de leurs cellules.
 * Il sert à enregistrer une run et à relire un plateau déjà créé.
 * Entrées: boardId, cellules. Sorties: ids, listes de cellules, résumés de boards.
 */
public class BoardDao {

    // Résumé léger d'un board pour lister les sauvegardes.
    public static class BoardSummary {
        public int id;
        public String name;
        public String createdAt;
    }

    // Représentation d'une cellule du plateau au format BDD.
    public static class DbCell {
        public int position;
        public String zone;
        public Integer enemyCharacterId;
        public String treasure;
        public String offensiveLoot;
        public String defensiveLoot;
    }

    private final Db db;

    // Reçoit l'accès base partagé.
    public BoardDao(Db db) {
        this.db = db;
    }

    // Crée un board et renvoie son id.
    public int createBoard(String name) {
        String sql = "INSERT INTO boards(name) VALUES(?)";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            // ATTENTION : insertion OK mais id manquant => impossible de relier les cellules.
            throw new RuntimeException("Création du board réussie mais ID généré introuvable.");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du board.", e);
        }
    }

    // Sauvegarde toutes les cellules du board (insert ou update si déjà présentes).
    public void saveCells(int boardId, List<DbCell> cells) {
        String sql = "INSERT INTO board_cells(board_id, position, zone, enemy_character_id, treasure, offensive_loot, defensive_loot) " +
                "VALUES(?,?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "zone=VALUES(zone), enemy_character_id=VALUES(enemy_character_id), treasure=VALUES(treasure), " +
                "offensive_loot=VALUES(offensive_loot), defensive_loot=VALUES(defensive_loot)";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // On batch les insert/update pour limiter les allers-retours SQL.
            for (DbCell cell : cells) {
                statement.setInt(1, boardId);
                statement.setInt(2, cell.position);
                statement.setString(3, cell.zone);

                if (cell.enemyCharacterId == null) {
                    statement.setNull(4, Types.INTEGER);
                } else {
                    statement.setInt(4, cell.enemyCharacterId);
                }

                statement.setString(5, cell.treasure);
                statement.setString(6, cell.offensiveLoot);
                statement.setString(7, cell.defensiveLoot);
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde des cellules du board id=" + boardId + ".", e);
        }
    }

    // Charge toutes les cellules d'un board triées par position.
    public List<DbCell> loadCells(int boardId) {
        String sql = "SELECT position, zone, enemy_character_id, treasure, offensive_loot, defensive_loot " +
                "FROM board_cells WHERE board_id=? ORDER BY position";
        List<DbCell> cells = new ArrayList<>();

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, boardId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    DbCell cell = new DbCell();
                    cell.position = rs.getInt("position");
                    cell.zone = rs.getString("zone");

                    int enemyId = rs.getInt("enemy_character_id");
                    // Pourquoi c’est comme ça: getInt renvoie 0 si null, donc on vérifie wasNull.
                    cell.enemyCharacterId = rs.wasNull() ? null : enemyId;

                    cell.treasure = rs.getString("treasure");
                    cell.offensiveLoot = rs.getString("offensive_loot");
                    cell.defensiveLoot = rs.getString("defensive_loot");
                    cells.add(cell);
                }
            }

            return cells;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des cellules du board id=" + boardId + ".", e);
        }
    }

    // Liste les boards existants pour l'écran de chargement.
    public List<BoardSummary> listBoards() {
        String sql = "SELECT id, name, created_at FROM boards ORDER BY id DESC";
        List<BoardSummary> boards = new ArrayList<>();

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                BoardSummary board = new BoardSummary();
                board.id = rs.getInt("id");
                board.name = rs.getString("name");
                board.createdAt = rs.getString("created_at");
                boards.add(board);
            }

            return boards;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des boards.", e);
        }
    }
}
