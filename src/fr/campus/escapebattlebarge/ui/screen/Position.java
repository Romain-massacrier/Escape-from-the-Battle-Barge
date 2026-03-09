package fr.campus.escapebattlebarge.ui.screen;

import java.awt.*;

/** Centralise les positions/sizes UI des écrans Swing. */
public final class Position {

    private Position() {
    }

    public static final class CharacterSelect {
        public static final Rectangle MARINE_INFO_BOUNDS = new Rectangle(620, 280, 560, 180);
        public static final Rectangle LIBRARIAN_INFO_BOUNDS = new Rectangle(620, 510, 560, 180);
        public static final Rectangle TERMINAL_BOUNDS = new Rectangle(390, 700, 880, 120);
        public static final Rectangle INPUT_BOUNDS = new Rectangle(620, 50, 120, 28);

        public static final Rectangle TERMINAL_LINE1_BOUNDS = new Rectangle(18, 10, 844, 20);
        public static final Rectangle TERMINAL_LINE2_BOUNDS = new Rectangle(18, 32, 844, 20);
        public static final Rectangle TERMINAL_LINE3_BOUNDS = new Rectangle(18, 55, 844, 18);

        private CharacterSelect() {
        }
    }

    public static final class Menu {
        public static final Rectangle OPTIONS_BOUNDS = new Rectangle(540, 715, 600, 40);
        public static final Rectangle PROMPT_BOUNDS = new Rectangle(540, 775, 400, 30);
        public static final Rectangle INPUT_BOUNDS = new Rectangle(840, 770, 120, 35);

        private Menu() {
        }
    }

    public static final class GameIntro {
        public static final Rectangle STORY_BOUNDS = new Rectangle(420, 240, 820, 620);

        private GameIntro() {
        }
    }

    public static final class GamePanel {
        public static final Rectangle PLATEAU_AREA = new Rectangle(0, 0, 1536, 1024);
        public static final Rectangle PLAYER_IMAGE_AREA = new Rectangle(45, 190, 320, 400);
        public static final Rectangle ZONE_IMAGE_AREA = new Rectangle(370, 180, 800, 600);
        public static final Rectangle ENEMY_IMAGE_AREA = new Rectangle(1230, 300, 190, 200);

        public static final Point PLAYER_TEXT_POINT = new Point(85, 550);
        public static final Point ENEMY_TEXT_POINT = new Point(1220, 550);

        public static final Rectangle CONSOLE_AREA = new Rectangle(400, 700, 880, 250);
        public static final int CONSOLE_PADDING_X = 20;
        public static final int CONSOLE_PADDING_TOP = 14;
        public static final int CONSOLE_PADDING_BOTTOM = 12;
        public static final int INPUT_HEIGHT = 42;
        public static final int INPUT_TO_TEXT_GAP = 12;
        public static final int INPUT_OFFSET_X = -72;
        public static final int INPUT_OFFSET_Y = 0;
        public static final int TEXT_OFFSET_X = INPUT_OFFSET_X;

        private GamePanel() {
        }
    }
}
