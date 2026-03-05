package fr.campus.escapebattlebarge.ui.console;

import fr.campus.escapebattlebarge.db.BoardDao;
import fr.campus.escapebattlebarge.domain.character.Character;
import fr.campus.escapebattlebarge.domain.character.Player;

import java.util.List;
import java.util.Scanner;

/*
 * Cette classe gère toutes les saisies et affichages texte en version console.
 * Elle est utilisée par la classe Game pour dialoguer avec le joueur.
 * Entrées: clavier (Scanner). Sorties: choix validés et textes affichés.
 */
public class Menu {

    private final Scanner scanner;

    // Initialise le lecteur clavier partagé pour tout le menu.
    public Menu() {
        this.scanner = new Scanner(System.in);
    }

    // Affiche le menu principal et renvoie un choix entre 1 et 6.
    public int askMainChoice() {

        System.out.println();
        System.out.println("Escape from the Battle Barge (Dark Angels)");
        System.out.println("1. Select hero");
        System.out.println("2. New character");
        System.out.println("3. Edit hero");
        System.out.println("4. Start run");
        System.out.println("5. Load board");
        System.out.println("6. Quit");
        System.out.print("Choice: ");

        return readIntInRange(1, 6);
    }

    // Demande la classe du personnage (1 guerrier, 2 magicien).
    public int askCharacterType() {

        System.out.println();
        System.out.println("Choose your type:");
        System.out.println("1. Guerrier");
        System.out.println("2. Magicien");
        System.out.print("Choice: ");

        return readIntInRange(1, 2);
    }

    // Demande un nom non vide pour le personnage.
    public String askCharacterName() {

        System.out.println();
        System.out.print("Enter character name: ");

        String name = scanner.nextLine().trim();

        while (name.isEmpty()) {
            System.out.print("Name cannot be empty. Enter character name: ");
            name = scanner.nextLine().trim();
        }

        return name;
    }

    // Affiche un petit menu lié au personnage et renvoie le choix.
    public int askCharacterMenuChoice() {

        System.out.println();
        System.out.println("Character menu:");
        System.out.println("1. Show character details");
        System.out.println("2. Edit character name");
        System.out.println("3. Back to main menu");
        System.out.println("4. Quit");
        System.out.print("Choice: ");

        return readIntInRange(1, 4);
    }

    // Affiche les infos principales d'un joueur.
    public void showCharacter(Player player) {

        System.out.println();
        System.out.println("Character details:");
        System.out.println("-------------------");

        System.out.println("Name: " + player.getName());
        System.out.println("Class: " + player.getPlayerClass());
        System.out.println("HP: " + player.getHp() + "/" + player.getMaxHp());
        System.out.println("Position: " + player.getPosition());

        if (player.getInventory().getEquippedWeapon() != null) {
            System.out.println("Weapon: " +
                    player.getInventory().getEquippedWeapon().getName());
        } else {
            System.out.println("Weapon: None");
        }
    }

    // Affiche la liste des héros récupérés en base.
    public void showHeroes(List<Character> heroes) {
        System.out.println();
        System.out.println("Heroes:");
        for (Character hero : heroes) {
            System.out.printf("%d | %s | %s | HP=%d | STR=%d%n",
                    hero.getId(),
                    hero.getName(),
                    hero.getType(),
                    hero.getLifePoints(),
                    hero.getStrength());
        }
    }

    // Demande un identifiant de héros.
    public int askHeroId() {
        System.out.print("Enter hero id: ");
        return readInt();
    }

    // Affiche les champs modifiables d'un héros.
    public int askEditFieldChoice() {
        System.out.println();
        System.out.println("Edit hero field:");
        System.out.println("1. name");
        System.out.println("2. type");
        System.out.println("3. offensive_equipment");
        System.out.println("4. defensive_equipment");
        System.out.println("5. strength");
        System.out.println("6. life_points");
        System.out.println("7. Save and back");
        System.out.print("Choice: ");
        return readIntInRange(1, 7);
    }

    // Lit un texte libre court.
    public String askFreeText(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    // Demande un entier positif (>= 0).
    public int askPositiveInt(String label) {
        while (true) {
            System.out.print(label);
            int value = readInt();
            if (value >= 0) {
                return value;
            }
            System.out.println("Please enter a number >= 0.");
        }
    }

    // Affiche les boards disponibles.
    public void showBoards(List<BoardDao.BoardSummary> boards) {
        System.out.println();
        System.out.println("Boards:");
        for (BoardDao.BoardSummary board : boards) {
            System.out.printf("%d | %s | %s%n", board.id, board.name, board.createdAt);
        }
    }

    // Demande un identifiant de board.
    public int askBoardId() {
        System.out.print("Enter board id: ");
        return readInt();
    }

    // Affiche un message simple en console.
    public void showMessage(String message) {
        System.out.println(message);
    }

    // Met le jeu en pause jusqu'à Entrée.
    public void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    // Demande un nouveau nom non vide.
    public String askNewName() {

        System.out.println();
        System.out.print("Enter new name: ");

        String name = scanner.nextLine().trim();

        while (name.isEmpty()) {
            System.out.print("Name cannot be empty. Enter new name: ");
            name = scanner.nextLine().trim();
        }

        return name;
    }

    // Lit un entier dans une plage donnée, et reboucle tant que c'est invalide.
    private int readIntInRange(int min, int max) {

        while (true) {

            String line = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(line);

                if (value >= min && value <= max) {
                    return value;
                }

            } catch (NumberFormatException ignored) {
            }

            // Ici on bloque tant qu'on n'a pas un nombre valide.
            System.out.print("Invalid choice. Enter a number between "
                    + min + " and " + max + ": ");
        }
    }

    // Lit un entier simple, avec relance en cas d'erreur.
    private int readInt() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException ignored) {
                System.out.print("Invalid number. Try again: ");
            }
        }
    }
}