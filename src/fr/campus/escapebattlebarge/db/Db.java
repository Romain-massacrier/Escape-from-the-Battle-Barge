package fr.campus.escapebattlebarge.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Cette classe centralise la connexion MariaDB du projet.
 * Elle est utilisée par les DAO pour exécuter les requêtes SQL.
 * Entrée: aucune (config statique). Sortie: Connection JDBC.
 */
public class Db {

    private static final String URL = "jdbc:mariadb://localhost:3306/warhammer_game";
    private static final String USER = "dev";
    private static final String PASSWORD = "dev";

    // Ouvre une connexion SQL, ou lève une erreur runtime en cas d'échec.
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Impossible de se connecter à la base warhammer_game.", e);
        }
    }
}
