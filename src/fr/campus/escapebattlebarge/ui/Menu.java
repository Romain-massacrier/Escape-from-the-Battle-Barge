package fr.campus.escapebattlebarge.ui;

// Import de la classe Character depuis la couche domain
// Permet d'afficher les informations du personnage
import fr.campus.escapebattlebarge.domain.Character;

// Scanner utilisé pour lire les entrées utilisateur dans la console
import java.util.Scanner;

public class Menu {

    // Scanner unique utilisé pour toute la durée de vie du menu
    // final = référence non modifiable après initialisation
    private final Scanner scanner;

    /**
     * Constructeur du menu.
     * Initialise le Scanner sur l'entrée standard (clavier).
     */
    public Menu() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Affiche le menu principal.
     * Propose la création d'un personnage ou la sortie du programme.
     *
     * @return un entier compris entre 1 et 2
     */
    public int askMainChoice() {

        System.out.println();
        System.out.println("Escape from the Battle Barge (Dark Angels)");
        System.out.println("1. New character");
        System.out.println("2. Quit");
        System.out.print("Choice: ");

        // Lecture sécurisée d'un entier entre 1 et 2
        return readIntInRange(1, 2);
    }

    /**
     * Demande à l'utilisateur de choisir une classe de personnage.
     *
     * @return un entier compris entre 1 et 2
     */
    public int askCharacterType() {

        System.out.println();
        System.out.println("Choose your class:");
        System.out.println("1. Assault Marine");
        System.out.println("2. Librarian");
        System.out.print("Choice: ");

        return readIntInRange(1, 2);
    }

    /**
     * Demande le nom du personnage.
     * Empêche les noms vides.
     *
     * @return nom valide (non vide)
     */
    public String askCharacterName() {

        System.out.println();
        System.out.print("Enter character name: ");

        String name = scanner.nextLine().trim();

        // Boucle tant que le nom est vide
        while (name.isEmpty()) {
            System.out.print("Name cannot be empty. Enter character name: ");
            name = scanner.nextLine().trim();
        }

        return name;
    }

    /**
     * Menu secondaire lié au personnage.
     *
     * @return un entier compris entre 1 et 4
     */
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

    /**
     * Affiche les informations du personnage.
     * Utilise la méthode toString() de la classe Character.
     */
    public void showCharacter(Character character) {

        System.out.println();
        System.out.println("Character details:");

        // Affichage direct de l'objet (appel implicite à toString())
        System.out.println(character);
    }

    /**
     * Demande un nouveau nom pour le personnage.
     * Refuse les noms vides.
     *
     * @return nouveau nom valide
     */
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

    /**
     * Méthode utilitaire privée.
     * Lit un entier et vérifie qu'il est dans l'intervalle [min, max].
     * Gère les erreurs de saisie (lettres, symboles, etc.).
     *
     * @param min valeur minimale autorisée
     * @param max valeur maximale autorisée
     * @return entier valide dans l'intervalle
     */
    private int readIntInRange(int min, int max) {

        while (true) {

            String line = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(line);

                // Vérifie si la valeur est dans la plage autorisée
                if (value >= min && value <= max) {
                    return value;
                }

            } catch (NumberFormatException ignored) {
                // Si la conversion échoue (ex: utilisateur tape "abc")
                // on ignore l'erreur et on redemande
            }

            System.out.print("Invalid choice. Enter a number between "
                    + min + " and " + max + ": ");
        }
    }
}