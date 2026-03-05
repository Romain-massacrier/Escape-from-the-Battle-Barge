package fr.campus.escapebattlebarge;

import fr.campus.escapebattlebarge.db.BoardDao;
import fr.campus.escapebattlebarge.db.CharacterDao;
import fr.campus.escapebattlebarge.db.Db;
import fr.campus.escapebattlebarge.domain.character.Character;
import fr.campus.escapebattlebarge.domain.character.player.AssaultMarine;
import fr.campus.escapebattlebarge.domain.character.player.Librarian;
import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.ui.screen.CharacterSelectScreen;
import fr.campus.escapebattlebarge.ui.app.GameApp;
import fr.campus.escapebattlebarge.ui.screen.GameIntroScreen;
import fr.campus.escapebattlebarge.ui.screen.MenuScreen;
import fr.campus.escapebattlebarge.ui.screen.TitleScreen;

/*
 * Cette classe est le point d'entrée principal du jeu en version graphique.
 * Elle enchaîne écran titre -> intro -> menu -> création/sélection de héros -> lancement de partie.
 * Entrées: actions clavier/écran. Sorties: ouverture d'écrans, création de héros et démarrage du GameApp.
 */
public class Main {

    private static final Db DB = new Db();
    private static final CharacterDao CHARACTER_DAO = new CharacterDao(DB);
    private static final BoardDao BOARD_DAO = new BoardDao(DB);

    // Affiche le menu principal et lance le flow de création/sélection du personnage.
    private static void showMainMenu() {
        MenuScreen menu = new MenuScreen();

        menu.show(() -> {
            CharacterSelectScreen select = new CharacterSelectScreen();

            select.show(
                    name -> {
                        // Ici on prépare un héros persistant (BDD) + un Player jouable (runtime).
                        System.out.println("Space Marine choisi, nom : " + name);
                        Character hero = buildHero("Guerrier", name, 35, 6, "Chainsword", "Shield");
                        int id = CHARACTER_DAO.createHero(hero);
                        hero.setId(id);
                        System.out.println("Héros enregistré avec id=" + id);

                        Player player = new AssaultMarine(name);
                        GameApp.start(player, CHARACTER_DAO, BOARD_DAO, hero, Main::showMainMenu);
                    },
                    name -> {
                        System.out.println("Librarian choisi, nom : " + name);
                        Character hero = buildHero("Magicien", name, 28, 5, "Baton de force", "Psychic ward");
                        int id = CHARACTER_DAO.createHero(hero);
                        hero.setId(id);
                        System.out.println("Héros enregistré avec id=" + id);

                        Player player = new Librarian(name);
                        GameApp.start(player, CHARACTER_DAO, BOARD_DAO, hero, Main::showMainMenu);
                    }
            );

        });
    }

    // Démarre la séquence d'intro complète puis bascule vers le menu principal.
    public static void main(String[] args) {

        TitleScreen title = new TitleScreen();

        title.show(() -> {

            String[] pages = {
                    "Dans les ténèbres glacées de l’espace,\n" +
                            "une silhouette sacrée dérive lentement.\n\n" +
                            "La Battle Barge des Dark Angels agonise.\n" +
                            "Sa coque est éventrée.\n" +
                            "Ses ponts brûlent.\n" +
                            "Ses coursives sont souillées par l’invasion.\n\n" +
                            "Des étincelles jaillissent des panneaux fracturés.\n" +
                            "Les statues impériales s’effondrent dans les flammes.\n" +
                            "Le sanctuaire du vaisseau résonne d’échos funèbres.\n\n" +
                            "Les alarmes vox hurlent sans relâche.\n" +
                            "Les prières se mêlent aux ordres brisés.\n" +
                            "Et le métal sacré se tord sous la violence.\n\n" +
                            "Une horde d’Orkz s’est abattue sur le vaisseau.\n\n" +
                            "Appuyez sur Entrée pour continuer.",

                    "Ils martèlent les portes blindées.\n" +
                            "Ils rient au milieu des flammes.\n\n" +
                            "Leurs armes tonnent dans les couloirs.\n" +
                            "Les leurs ombres se projettent sur les parois calcinées.\n" +
                            "Leur sauvagerie ne connaît aucune limite.\n\n" +
                            "Les réacteurs à plasma deviennent instables.\n" +
                            "Les champs de confinement cèdent un à un.\n" +
                            "Chaque secousse annonce une rupture imminente.\n\n" +
                            "Chaque seconde rapproche le cœur du vaisseau\n" +
                            "d’une implosion cataclysmique.\n\n" +
                            "Pourtant…\n" +
                            "au milieu du brasier,\n" +
                            "une présence avance.\n\n" +
                            "Appuyez sur Entrée pour continuer.",

                    "Un fils des Dark Angels.\n\n" +
                            "Il ne connaît ni la peur, ni la fatigue, ni le doute\n\n" +
                            "Son serment est gravé dans l’acier et le sang.\n" +
                            "Sa volonté est une lame.\n" +
                            "Son devoir ne plie jamais.\n\n" +
                            "Sur la surface du monde en contrebas\n" +
                            "attend le Primarque des Dark Angels :\n\n" +
                            "Lion El'Jonson.\n\n" +
                            "Traversez le vaisseau infesté.\n" +
                            "Purger la horde.\n" +
                            "Atteindre la surface avant l’implosion.\n\n" +
                            "Car tant qu’un seul fils du Lion respire,\n" +
                            "l’ennemi ne triomphe jamais.\n\n" +
                            "Appuyez sur Entrée pour commencer."
            };

            GameIntroScreen intro = new GameIntroScreen();

            intro.show(pages, () -> {
                showMainMenu();


            });

        });
    }

    // Construit un objet Character prêt à être enregistré en base.
    private static Character buildHero(String type, String name, int hp, int str, String off, String def) {
        Character hero = new Character();
        hero.setType(type);
        hero.setName(name);
        hero.setLifePoints(hp);
        hero.setStrength(str);
        hero.setOffensiveEquipment(off);
        hero.setDefensiveEquipment(def);
        return hero;
    }
}

