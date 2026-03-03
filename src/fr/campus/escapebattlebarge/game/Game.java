package fr.campus.escapebattlebarge.game;

import fr.campus.escapebattlebarge.domain.AssaultMarine;
import fr.campus.escapebattlebarge.domain.Librarian;
import fr.campus.escapebattlebarge.domain.Player;
import fr.campus.escapebattlebarge.domain.PlayerClass;
import fr.campus.escapebattlebarge.ui.Menu;

public class Game {

    private final Menu menu;
    private Player player;
    private boolean running;

    public Game() {
        this.menu = new Menu();
        this.player = null;
        this.running = true;
    }

    public void start() {
        while (running) {
            int choice = menu.askMainChoice();
            if (choice == 1) {
                createCharacterFlow();
                characterMenuFlow();
            } else {
                running = false;
            }
        }
        System.out.println("Exiting game. For the Lion.");
    }

    private void createCharacterFlow() {
        int typeChoice = menu.askCharacterType();
        String name = menu.askCharacterName();

        if (typeChoice == 1) {
            player = new AssaultMarine(name);
        } else {
            player = new Librarian(name);
        }
    }

    private void characterMenuFlow() {
        boolean inCharacterMenu = true;

        while (inCharacterMenu && running) {
            int choice = menu.askCharacterMenuChoice();

            if (choice == 1) {
                menu.showCharacter(player);

            } else if (choice == 2) {
                // Player.name est final, donc on "renomme" en recréant le perso
                String newName = menu.askNewName();
                PlayerClass cls = player.getPlayerClass();

                if (cls == PlayerClass.ASSAULT_MARINE) {
                    player = new AssaultMarine(newName);
                } else {
                    player = new Librarian(newName);
                }

                System.out.println("Name updated.");

            } else if (choice == 3) {
                inCharacterMenu = false;

            } else {
                running = false;
            }
        }
    }
}