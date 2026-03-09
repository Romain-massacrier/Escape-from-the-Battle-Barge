package fr.campus.escapebattlebarge.ui.screen;

/** Centralise les textes affichés dans l'UI. */
public final class UiText {

    private UiText() {
    }

    public static final String APP_TITLE = "Escape from the Battle Barge";

    public static final class CharacterSelect {
        public static final String FRAME_TITLE = "Select Your Astartes";
        public static final String TERMINAL_SELECT_TITLE = "Console d'Auspex : sélection d'Astartes";
        public static final String TERMINAL_SELECT_OPTIONS = "1  Space Marine       2  Librarian";
        public static final String TERMINAL_SELECT_PROMPT = "Entrez 1 ou 2, puis scellez votre choix par Entrée :";
        public static final String TERMINAL_NAME_TITLE = "Console d'Auspex : identité du guerrier";
        public static final String TERMINAL_CLASS_MARINE = "Classe : Space Marine";
        public static final String TERMINAL_CLASS_LIBRARIAN = "Classe : Librarian";
        public static final String TERMINAL_NAME_PROMPT = "Entrez le nom du personnage, puis appuyez sur Entrée :";

        public static final String MARINE_INFO =
                "SPACE MARINE\n\n" +
                "Il avance dans le sang et ne s’arrête jamais.\n\n" +
                "Armements: Bolter, Chainsword.\n" +
                "Attaque : +5 - Vie : +10\n";

        public static final String LIBRARIAN_INFO =
                "LIBRARIAN\n\n" +
                "Sa pensée tranche plus profond qu’aucune lame.\n\n" +
                "Armements : Bâton de force.\n" +
                "Attaque : +8 - Vie : +6\n";

        private CharacterSelect() {
        }
    }

    public static final class Menu {
        public static final String OPTIONS = "1  New Game      2  Quit";
        public static final String PROMPT = "Prononcez votre décision : ";

        private Menu() {
        }
    }

    public static final class Title {
        public static final String FALLBACK = "Escape from the Battle Barge - Appuyez sur Entrée";

        private Title() {
        }
    }

    public static final class GamePanel {
        public static final String MAIN_PROMPT = "1 lancer dé | 2 inventaire";
        public static final String COMBAT_PROMPT = "1 Attaquer | 2 Potion | 3 Fuir";
        public static final String UNKNOWN_NAME = "Inconnu";
        public static final String NO_WEAPON = "Aucune";
        public static final String LABEL_NAME = "Nom: ";
        public static final String LABEL_HP = "PV: ";
        public static final String LABEL_WEAPON = "Arme: ";
        public static final String LABEL_ENEMY_HP = "PV Ennemi: ";

        private GamePanel() {
        }
    }

    public static final class GameFrame {
        public static final String INVALID_CHOICE = "Choix invalide. 1 lancer dé | 2 inventaire";

        private GameFrame() {
        }
    }
}
