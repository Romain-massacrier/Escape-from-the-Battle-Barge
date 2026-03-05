package fr.campus.escapebattlebarge.game.core;

import fr.campus.escapebattlebarge.db.BoardDao;
import fr.campus.escapebattlebarge.db.CharacterDao;
import fr.campus.escapebattlebarge.db.Db;
import fr.campus.escapebattlebarge.domain.character.Character;
import fr.campus.escapebattlebarge.domain.character.player.AssaultMarine;
import fr.campus.escapebattlebarge.domain.character.player.Librarian;
import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.game.board.Board;
import fr.campus.escapebattlebarge.game.board.Tile;
import fr.campus.escapebattlebarge.game.board.TileType;
import fr.campus.escapebattlebarge.ui.console.Menu;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 * Cette classe pilote la version console du jeu (menu principal + actions principales).
 * Elle sert à créer/sélectionner un héros, lancer une run, et charger un board sauvegardé.
 * Entrées: saisies clavier via Menu. Sorties: affichage console + écritures/lectures en base.
 */
public class Game {

    private final Menu menu;
    private final CharacterDao characterDao;
    private final BoardDao boardDao;

    private Player player;
    private Character currentHero;
    private boolean running;

    // Initialise les accès UI console et BDD au démarrage de l'application.
    public Game() {
        this.menu = new Menu();
        Db db = new Db();
        this.characterDao = new CharacterDao(db);
        this.boardDao = new BoardDao(db);
        this.player = null;
        this.currentHero = null;
        this.running = true;
    }

    // Boucle principale: lit le choix du joueur et déclenche le bon flux de jeu.
    public void start() {
        while (running) {
            int choice = menu.askMainChoice();

            if (choice == 1) {
                selectHeroFlow();
            } else if (choice == 2) {
                createCharacterFlow();
            } else if (choice == 3) {
                editHeroFlow();
            } else if (choice == 4) {
                startRunFlow();
            } else if (choice == 5) {
                loadBoardFlow();
            } else {
                running = false;
            }
        }
        System.out.println("Exiting game. For the Lion.");
    }

    // Crée un héros à partir des choix utilisateur puis l'enregistre en base.
    private void createCharacterFlow() {
        int typeChoice = menu.askCharacterType();
        String name = menu.askCharacterName();

        Character hero = new Character();
        hero.setName(name);

        if (typeChoice == 1) {
            hero.setType("Guerrier");
            hero.setLifePoints(35);
            hero.setStrength(6);
            hero.setOffensiveEquipment("Chainsword");
            hero.setDefensiveEquipment("Shield");
        } else {
            hero.setType("Magicien");
            hero.setLifePoints(28);
            hero.setStrength(5);
            hero.setOffensiveEquipment("Baton de force");
            hero.setDefensiveEquipment("Psychic ward");
        }

        int id = characterDao.createHero(hero);
        hero.setId(id);

        menu.showMessage("Héros enregistré avec id=" + id);
    }

    // Permet de choisir un héros existant puis le convertit en Player jouable.
    private void selectHeroFlow() {
        List<Character> heroes = characterDao.getHeroes();
        if (heroes.isEmpty()) {
            menu.showMessage("Aucun héros en base.");
            return;
        }

        while (true) {
            menu.showHeroes(heroes);
            int heroId = menu.askHeroId();
            Character hero = characterDao.getCharacterById(heroId);

            if (hero == null) {
                menu.showMessage("ID incorrect. Réessaie.");
                continue;
            }

            currentHero = hero;
            // Ici on reconstruit le Player runtime à partir des données persistées.
            player = toPlayer(hero);
            menu.showMessage("Héros sélectionné: " + hero.getName() + " (id=" + hero.getId() + ")");
            return;
        }
    }

    // Modifie un héros existant champ par champ, puis sauvegarde en base.
    private void editHeroFlow() {
        List<Character> heroes = characterDao.getHeroes();
        if (heroes.isEmpty()) {
            menu.showMessage("Aucun héros en base.");
            return;
        }

        Character heroToEdit;
        while (true) {
            menu.showHeroes(heroes);
            int heroId = menu.askHeroId();
            heroToEdit = characterDao.getCharacterById(heroId);

            if (heroToEdit == null) {
                menu.showMessage("ID incorrect. Réessaie.");
                continue;
            }
            break;
        }

        boolean editing = true;
        while (editing) {
            int field = menu.askEditFieldChoice();

            switch (field) {
                case 1 -> heroToEdit.setName(menu.askFreeText("New name: "));
                case 2 -> {
                    int type = menu.askCharacterType();
                    heroToEdit.setType(type == 1 ? "Guerrier" : "Magicien");
                }
                case 3 -> heroToEdit.setOffensiveEquipment(menu.askFreeText("New offensive_equipment: "));
                case 4 -> heroToEdit.setDefensiveEquipment(menu.askFreeText("New defensive_equipment: "));
                case 5 -> heroToEdit.setStrength(menu.askPositiveInt("New strength: "));
                case 6 -> heroToEdit.setLifePoints(menu.askPositiveInt("New life_points: "));
                case 7 -> {
                    characterDao.editHero(heroToEdit);
                    menu.showMessage("Héros mis à jour.");

                    // Si c'est le héros actif, on synchronise l'état courant du jeu.
                    if (currentHero != null && currentHero.getId() == heroToEdit.getId()) {
                        currentHero = heroToEdit;
                        player = toPlayer(heroToEdit);
                    }
                    editing = false;
                }
                default -> {
                }
            }
        }
    }

    // Lance une nouvelle run: crée un board, génère ses cases et les sauvegarde en base.
    private void startRunFlow() {
        if (currentHero == null || player == null) {
            menu.showMessage("Sélectionne un héros d'abord.");
            return;
        }

        String boardName = "Run " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int boardId = boardDao.createBoard(boardName);

        Board board = new Board();
        List<BoardDao.DbCell> cells = convertBoardToDbCells(board);
        boardDao.saveCells(boardId, cells);

        menu.showMessage("Partie créée avec boardId=" + boardId + " pour " + currentHero.getName());
    }

    // Charge un board existant depuis la base puis l'affiche en console.
    private void loadBoardFlow() {
        List<BoardDao.BoardSummary> boards = boardDao.listBoards();
        if (boards.isEmpty()) {
            menu.showMessage("Aucun board en base.");
            return;
        }

        menu.showBoards(boards);

        while (true) {
            int boardId = menu.askBoardId();
            List<BoardDao.DbCell> cells = boardDao.loadCells(boardId);

            if (cells.isEmpty()) {
                menu.showMessage("ID incorrect ou board vide. Réessaie.");
                continue;
            }

            menu.showMessage("Board " + boardId + " chargé:");
            for (BoardDao.DbCell cell : cells) {
                System.out.printf("pos=%d | zone=%s | enemy=%s | treasure=%s | off=%s | def=%s%n",
                        cell.position,
                        cell.zone,
                        cell.enemyCharacterId,
                        cell.treasure,
                        cell.offensiveLoot,
                        cell.defensiveLoot);
            }
            return;
        }
    }

    // Transforme un Character BDD en Player jouable, en gardant ses PV actuels.
    private Player toPlayer(Character hero) {
        boolean mage = "Magicien".equalsIgnoreCase(hero.getType()) || "Librarian".equalsIgnoreCase(hero.getType());

        Player created = mage ? new Librarian(hero.getName()) : new AssaultMarine(hero.getName());
        int delta = created.getHp() - hero.getLifePoints();
        // Pourquoi c’est comme ça: on adapte les PV du Player aux PV sauvegardés du héros.
        if (delta > 0) {
            created.damage(delta);
        } else if (delta < 0) {
            created.heal(-delta);
        }
        return created;
    }

    // Convertit le plateau en cellules prêtes à être persistées en base.
    private List<BoardDao.DbCell> convertBoardToDbCells(Board board) {
        List<BoardDao.DbCell> cells = new ArrayList<>();

        // On parcourt toutes les cases du plateau 8x8 (1 à 64).
        for (int position = 1; position <= 64; position++) {
            Tile tile = board.getTile(position);
            if (tile == null) {
                continue;
            }

            BoardDao.DbCell cell = new BoardDao.DbCell();
            cell.position = position;
            cell.zone = tile.getZone().name();
            cell.enemyCharacterId = findEnemyIdForTileType(tile.getType());
            cell.treasure = mapTreasure(tile.getType());
            cell.offensiveLoot = null;
            cell.defensiveLoot = null;
            cells.add(cell);
        }

        return cells;
    }

    // Associe un type de case ennemi à un identifiant ennemi en base.
    private Integer findEnemyIdForTileType(TileType type) {
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

    // Associe un type de case trésor à une valeur persistable en base.
    private String mapTreasure(TileType type) {
        return switch (type) {
            case TREASURE_POTION -> "POTION";
            case TREASURE_BIG_POTION -> "BIG_POTION";
            default -> null;
        };
    }
}