package fr.campus.escapebattlebarge.app;

import fr.campus.escapebattlebarge.domain.AssaultMarine;
import fr.campus.escapebattlebarge.domain.Librarian;
import fr.campus.escapebattlebarge.domain.Player;
import fr.campus.escapebattlebarge.ui.CharacterSelectScreen;
import fr.campus.escapebattlebarge.ui.GameApp;
import fr.campus.escapebattlebarge.ui.GameIntroScreen;
import fr.campus.escapebattlebarge.ui.MenuScreen;
import fr.campus.escapebattlebarge.ui.TitleScreen;

public class Main {

    private static void showMainMenu() {
        MenuScreen menu = new MenuScreen();

        menu.show(() -> {
            CharacterSelectScreen select = new CharacterSelectScreen();

            select.show(
                    name -> {
                        System.out.println("Space Marine choisi, nom : " + name);
                        Player player = new AssaultMarine(name);
                        GameApp.start(player, Main::showMainMenu);
                    },
                    name -> {
                        System.out.println("Librarian choisi, nom : " + name);
                        Player player = new Librarian(name);
                        GameApp.start(player, Main::showMainMenu);
                    }
            );

        });
    }

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
}

