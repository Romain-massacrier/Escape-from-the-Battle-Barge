package fr.campus.escapebattlebarge.db;

import fr.campus.escapebattlebarge.domain.character.Character;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
 * Ce DAO gère la lecture/écriture des personnages dans la base.
 * Il sert à créer/éditer des héros et à retrouver des ennemis par type.
 * Entrées: objets Character / ids. Sorties: résultats SQL convertis en objets Java.
 */
public class CharacterDao {

    private final Db db;

    // Reçoit l'accès base partagé.
    public CharacterDao(Db db) {
        this.db = db;
    }

    // Récupère tous les héros triés par id.
    public List<Character> getHeroes() {
        String sql = "SELECT id,type,name,life_points,strength,offensive_equipment,defensive_equipment,role " +
                "FROM characters WHERE role='HERO' ORDER BY id";
        List<Character> heroes = new ArrayList<>();

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Character c = mapCharacter(rs);
                heroes.add(c);
            }

            System.out.println("Héros trouvés :");
            for (Character c : heroes) {
                System.out.printf("%d | %s | %s | HP=%d | STR=%d | OFF=%s | DEF=%s%n",
                        c.getId(),
                        c.getName(),
                        c.getType(),
                        c.getLifePoints(),
                        c.getStrength(),
                        c.getOffensiveEquipmentName(),
                        c.getDefensiveEquipmentName());
            }

            return heroes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des héros.", e);
        }
    }

    // Récupère un personnage par id, ou null s'il n'existe pas.
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

    // Insère un héros en base et renvoie son id généré.
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
            // ATTENTION : insertion OK mais id absent => état incohérent côté application.
            throw new RuntimeException("Insertion du héros réussie mais ID généré introuvable.");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du héros.", e);
        }
    }

    // Met à jour les champs éditables d'un héros existant.
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

    // Met à jour uniquement les PV d'un personnage (sync runtime -> BDD).
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

    // Cherche un ennemi par type et renvoie son id, ou null s'il est introuvable.
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

    // Convertit une ligne SQL en objet Character.
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
