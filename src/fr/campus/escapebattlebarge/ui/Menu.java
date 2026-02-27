package fr.campus.escapebattlebarge.ui;

import fr.campus.escapebattlebarge.domain.Character;
import java.util.Scanner;

public class Menu {

    private final Scanner scanner;

    public Menu() {
        this.scanner = new Scanner(System.in);
    }

    public int askMainChoice() {
        System.out.println();
        System.out.println("Escape from the Battle Barge (Dark Angels)");
        System.out.println("1. New character");
        System.out.println("2. Quit");
        System.out.print("Choice: ");
        return readIntInRange(1, 2);
    }

    public int askCharacterType() {
        System.out.println();
        System.out.println("Choose your class:");
        System.out.println("1. Assault Marine");
        System.out.println("2. Librarian");
        System.out.print("Choice: ");
        return readIntInRange(1, 2);
    }

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

    public void showCharacter(Character character) {
        System.out.println();
        System.out.println("Character details:");
        System.out.println(character);
    }

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
            System.out.print("Invalid choice. Enter a number between " + min + " and " + max + ": ");
        }
    }
}