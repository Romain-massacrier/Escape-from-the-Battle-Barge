package fr.campus.escapebattlebarge.db;

import fr.campus.escapebattlebarge.domain.character.Character;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** DAO pour persister les personnages (héros et ennemis) en base. */
public class CharacterDao {

    private final Db db;

    public CharacterDao(Db db) {
        this.db = db;
    }

    /** Retourne tous les héros triés par id. */
    public List<Character> getHeroes() {
        String sql = "SELECT id,type,name,life_points,strength,offensive_equipment,defensive_equipment,role " +
                "FROM characters WHERE role='HERO' ORDER BY id";
        List<Character> heroes = new ArrayList<>();

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                heroes.add(mapCharacter(rs));
            }

            return heroes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des héros.", e);
        }
    }

    /** Retourne un personnage par id, ou null s'il n'existe pas. */
    public Character getCharacterById(int id) {
        String sql = "SELECT id,type,name,life_points,strength,offensive_equipment,defensive_equipment,role " +
                "FROM characters WHERE id=?";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapCharacter(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du personnage id=" + id + ".", e);
        }
    }

    /** Crée un héros et renvoie son id généré. */
    public int createHero(Character c) {
        String sql = "INSERT INTO characters(type,name,life_points,strength,offensive_equipment,defensive_equipment,role) " +
                "VALUES(?,?,?,?,?,?,'HERO')";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, c.getType());
            statement.setString(2, c.getName());
            statement.setInt(3, c.getLifePoints());
            statement.setInt(4, c.getStrength());
            statement.setString(5, c.getOffensiveEquipmentName());
            statement.setString(6, c.getDefensiveEquipmentName());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new RuntimeException("Insertion du héros réussie mais ID généré introuvable.");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du héros.", e);
        }
    }

    /** Met à jour les champs éditables d'un héros existant. */
    public void editHero(Character c) {
        String sql = "UPDATE characters SET type=?,name=?,life_points=?,strength=?,offensive_equipment=?,defensive_equipment=? " +
                "WHERE id=? AND role='HERO'";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, c.getType());
            statement.setString(2, c.getName());
            statement.setInt(3, c.getLifePoints());
            statement.setInt(4, c.getStrength());
            statement.setString(5, c.getOffensiveEquipmentName());
            statement.setString(6, c.getDefensiveEquipmentName());
            statement.setInt(7, c.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification du héros id=" + c.getId() + ".", e);
        }
    }

    /** Met à jour uniquement les points de vie d'un personnage. */
    public void changeLifePoints(Character c) {
        String sql = "UPDATE characters SET life_points=? WHERE id=?";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, c.getLifePoints());
            statement.setInt(2, c.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour des points de vie id=" + c.getId() + ".", e);
        }
    }

    /** Retourne l'id d'un ennemi par type, ou null si introuvable. */
    public Integer findEnemyIdByType(String type) {
        String sql = "SELECT id FROM characters WHERE role='ENEMY' AND type=? ORDER BY id LIMIT 1";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, type);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'ennemi type=" + type + ".", e);
        }
    }

    /** Convertit une ligne SQL en objet Character. */
    private Character mapCharacter(ResultSet rs) throws SQLException {
        Character c = new Character();
        c.setId(rs.getInt("id"));
        c.setType(rs.getString("type"));
        c.setName(rs.getString("name"));
        c.setLifePoints(rs.getInt("life_points"));
        c.setStrength(rs.getInt("strength"));
        c.setOffensiveEquipment(rs.getString("offensive_equipment"));
        c.setDefensiveEquipment(rs.getString("defensive_equipment"));
        return c;
    }
}
