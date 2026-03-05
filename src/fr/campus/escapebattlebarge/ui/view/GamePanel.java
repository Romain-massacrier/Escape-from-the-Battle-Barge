package fr.campus.escapebattlebarge.ui.view;

import fr.campus.escapebattlebarge.domain.character.Enemy;
import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.domain.character.player.PlayerClass;
import fr.campus.escapebattlebarge.game.core.GameState;
import fr.campus.escapebattlebarge.game.board.Zone;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Ce panel dessine tout l'écran de jeu: décor, portraits, infos et console.
 * Il lit uniquement l'état courant (GameState) pour afficher une vue cohérente.
 * Entrée: GameState. Sortie: rendu graphique Swing.
 */
public class GamePanel extends JPanel {

    // Petits réglages d'échelle pour que les images rentrent bien.
    private static final double PLAYER_IMAGE_RATIO_SCALE = 0.75;
    private static final double ZONE_IMAGE_RATIO_SCALE = 0.9;

    // Plateau (fond)
    private static final int plateauX = 0;
    private static final int plateauY = 0;
    private static final int plateauW = 1536;
    private static final int plateauH = 1024;

    // Zone 1: portrait joueur
    private static final int playerImgX = 45;
    private static final int playerImgY = 190;
    private static final int playerImgW = 320;
    private static final int playerImgH = 400;

    // Zone 2: image zone centrale
    private static final int zoneImgX = 370;
    private static final int zoneImgY = 180;
    private static final int zoneImgW = 800;
    private static final int zoneImgH = 600;

    // Zone 3: portrait ennemi
    private static final int enemyImgX = 1230;
    private static final int enemyImgY = 300;
    private static final int enemyImgW = 190;
    private static final int enemyImgH = 200;

    // Textes sous portraits
    private static final int playerTextX = 85;
    private static final int playerTextY = 550;
    private static final int enemyTextX = 1220;
    private static final int enemyTextY = 550;

    private static final Rectangle CONSOLE_AREA = new Rectangle(400, 700, 880, 250);
    private static final int CONSOLE_PADDING_X = 20;
    private static final int CONSOLE_PADDING_TOP = 14;
    private static final int CONSOLE_PADDING_BOTTOM = 12;
    private static final int INPUT_HEIGHT = 42;
    private static final int INPUT_TO_TEXT_GAP = 12;
    private static final int INPUT_OFFSET_X = -72;
    private static final int INPUT_OFFSET_Y = 0;
    private static final int TEXT_OFFSET_X = INPUT_OFFSET_X;
    private static final String MAIN_PROMPT = "1 lancer dé | 2 inventaire";
    private static final String COMBAT_PROMPT = "1 Attaquer | 2 Potion | 3 Fuir";

    private final GameState state;

    private Image plateau;

    private Image spaceMarinePortrait;
    private Image librarianPortrait;

    private Image caserne;
    private Image coursives;
    private Image sanctuaire;
    private Image armements;
    private Image noyau;
    private Image extraction;

    private Image orkPortrait;
    private Image sorcierPortrait;
    private Image squigPortrait;
    private Image warbossPortrait;

    // Charge les images et prépare la taille de l'écran.
    public GamePanel(GameState state) {
        this.state = state;

        plateau = load("/images/backgrounds/plateau.png");

        spaceMarinePortrait = load("/images/characters/spacemarine.png");
        librarianPortrait = load("/images/characters/librarian.png");

        caserne = load("/images/zones/caserne.png");
        coursives = load("/images/zones/coursives.png");
        sanctuaire = load("/images/zones/sanctuaire.png");
        armements = load("/images/zones/armements.png");
        noyau = load("/images/zones/noyau.png");
        extraction = load("/images/zones/extraction.png");

        orkPortrait = load("/images/enemies/orks.png");
        sorcierPortrait = load("/images/enemies/sorcier.png");
        squigPortrait = load("/images/enemies/squig.png");
        warbossPortrait = load("/images/enemies/warboss.png");

        int w = (plateau != null) ? plateau.getWidth(this) : plateauW;
        int h = (plateau != null) ? plateau.getHeight(this) : plateauH;

        if (w > 0 && h > 0) {
            setPreferredSize(new Dimension(w, h));
        }

        setBackground(Color.BLACK);
    }

    // Charge une image; si elle manque, on renvoie null.
    private Image load(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            System.out.println("Image introuvable : " + path);
            return null;
        }
        Image img = new ImageIcon(url).getImage();
        System.out.println("OK image: " + path + " (" + img.getWidth(this) + "x" + img.getHeight(this) + ")");
        return img;
    }

    // Choisit le portrait joueur selon sa classe.
    private Image getPlayerImage() {
        PlayerClass playerClass = state.getPlayer().getPlayerClass();
        return (playerClass == PlayerClass.LIBRARIAN) ? librarianPortrait : spaceMarinePortrait;
    }

    // Choisit l'image de zone selon la case actuelle.
    private Image getZoneImage() {
        int pos = state.getPlayer().getPosition();
        Zone zone = Zone.fromCell(Math.max(1, Math.min(64, pos)));

        return switch (zone) {
            case CASERNE -> caserne;
            case COURSIVES -> coursives;
            case SANCTUAIRE -> sanctuaire;
            case ARMEMENTS -> armements;
            case NOYAUX -> noyau;
            case EXTRACTION -> extraction;
        };
    }

    // Choisit le portrait ennemi à afficher.
    private Image getEnemyImage(Enemy enemy) {
        if (enemy == null) {
            return null;
        }

        String name = enemy.getName();
        if (name == null) {
            return null;
        }

        String normalized = name.toLowerCase();
        if (normalized.contains("warboss")) return warbossPortrait;
        if (normalized.contains("squig")) return squigPortrait;
        if (normalized.contains("sorc")) return sorcierPortrait;
        if (normalized.contains("ork")) return orkPortrait;
        return null;
    }

    // Redessine l'écran complet.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        drawPlateau(g2);
        drawGameVisuals(g2);
        drawConsole(g2);

        g2.dispose();
    }

    // Dessine le fond du plateau.
    private void drawPlateau(Graphics2D g2) {
        if (plateau != null) {
            g2.drawImage(plateau, plateauX, plateauY, plateauW, plateauH, null);
        }
    }

    // Dessine joueur, zone, ennemi et stats.
    private void drawGameVisuals(Graphics2D g2) {
        Player player = state.getPlayer();
        Enemy enemy = state.getCurrentEnemy();

        Image playerImage = getPlayerImage();
        if (playerImage != null) {
            drawImageKeepingRatio(g2, playerImage, playerImgX, playerImgY, playerImgW, playerImgH, PLAYER_IMAGE_RATIO_SCALE);
        }

        Image zoneImage = getZoneImage();
        if (zoneImage != null) {
            drawImageFillBox(g2, zoneImage, zoneImgX, zoneImgY, zoneImgW, zoneImgH, ZONE_IMAGE_RATIO_SCALE);
        }

        Image enemyImage = getEnemyImage(enemy);
        if (enemyImage != null) {
            g2.drawImage(enemyImage, enemyImgX, enemyImgY, enemyImgW, enemyImgH, null);
        }

        g2.setColor(new Color(0, 255, 70));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 24));

        String playerWeapon = (player.getInventory().getEquippedWeapon() == null)
                ? "Aucune"
                : player.getInventory().getEquippedWeapon().getName();

        String playerName = player.getName();
        if (playerName == null || playerName.isBlank()) {
            playerName = "Inconnu";
        }

        g2.drawString("Nom: " + playerName, playerTextX, playerTextY);
        g2.drawString("PV: " + player.getHp() + "/" + player.getMaxHp(), playerTextX, playerTextY + 84);
        g2.drawString("Arme: " + playerWeapon, playerTextX, playerTextY + 118);

        if (enemy != null && enemyImage != null) {
            String enemyName = enemy.getName();
            if (enemyName == null || enemyName.isBlank()) {
                enemyName = "Inconnu";
            }
            g2.drawString("Nom: " + enemyName, enemyTextX, enemyTextY);
            g2.drawString("PV Ennemi: " + enemy.getHp(), enemyTextX, enemyTextY + 84);
        }
    }

    // Dessine une image centrée en gardant son ratio.
    private void drawImageKeepingRatio(Graphics2D g2, Image image, int x, int y, int boxW, int boxH, double ratioScale) {
        int imageW = image.getWidth(null);
        int imageH = image.getHeight(null);
        if (imageW <= 0 || imageH <= 0) {
            return;
        }

        double fitScale = Math.min((double) boxW / imageW, (double) boxH / imageH);
        double finalScale = fitScale * ratioScale;

        int drawW = Math.max(1, (int) Math.round(imageW * finalScale));
        int drawH = Math.max(1, (int) Math.round(imageH * finalScale));

        int drawX = x + (boxW - drawW) / 2;
        int drawY = y + (boxH - drawH) / 2;

        g2.drawImage(image, drawX, drawY, drawW, drawH, null);
    }

    // Dessine une image centrée avec un zoom simple.
    private void drawImageFillBox(Graphics2D g2, Image image, int x, int y, int boxW, int boxH, double ratioScale) {
        int drawW = Math.max(1, (int) Math.round(boxW * ratioScale));
        int drawH = Math.max(1, (int) Math.round(boxH * ratioScale));

        int drawX = x + (boxW - drawW) / 2;
        int drawY = y + (boxH - drawH) / 2;

        g2.drawImage(image, drawX, drawY, drawW, drawH, null);
    }

    // Dessine la console du bas avec les dernières infos.
    private void drawConsole(Graphics2D g2) {
        g2.setColor(new Color(0, 255, 70));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 22));

        List<String> lines = state.getConsoleLines();
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();

        Rectangle input = getInputBounds();
        int textTop = CONSOLE_AREA.y + CONSOLE_PADDING_TOP;
        int defaultTextBottom = CONSOLE_AREA.y + CONSOLE_AREA.height - CONSOLE_PADDING_BOTTOM;
        int textBottom = Math.min(defaultTextBottom, input.y - INPUT_TO_TEXT_GAP);
        textBottom = Math.max(textTop + lineHeight, textBottom);
        int maxVisibleLines = Math.max(1, (textBottom - textTop) / lineHeight);

        String pinnedPrompt = findPinnedPrompt(lines);
        List<String> visibleLines;

        if (pinnedPrompt == null) {
            int start = Math.max(0, lines.size() - maxVisibleLines);
            visibleLines = lines.subList(start, lines.size());
        } else {
            // Pourquoi c’est comme ça: on garde le prompt visible pour que le joueur sache quoi taper.
            visibleLines = new ArrayList<>();
            visibleLines.add(pinnedPrompt);

            int remainingSlots = Math.max(0, maxVisibleLines - 1);
            List<String> nonPromptLines = new ArrayList<>();
            for (String line : lines) {
                if (!isPromptLine(line)) {
                    nonPromptLines.add(line);
                }
            }

            int start = Math.max(0, nonPromptLines.size() - remainingSlots);
            visibleLines.addAll(nonPromptLines.subList(start, nonPromptLines.size()));
        }

        Shape oldClip = g2.getClip();
        int textX = CONSOLE_AREA.x + CONSOLE_PADDING_X + TEXT_OFFSET_X;
        g2.setClip(CONSOLE_AREA.x + TEXT_OFFSET_X, textTop, CONSOLE_AREA.width, Math.max(1, textBottom - textTop));

        int lineY = textTop + fm.getAscent();
        for (String line : visibleLines) {
            String displayLine = line.replace("| Zone ", "| ");
            g2.drawString(displayLine, textX, lineY);
            lineY += lineHeight;
        }

        g2.setClip(oldClip);
    }

    // Donne la zone où on place le champ de saisie.
    public Rectangle getInputBounds() {
        int inputY = CONSOLE_AREA.y + CONSOLE_AREA.height - INPUT_HEIGHT - CONSOLE_PADDING_BOTTOM + INPUT_OFFSET_Y;
        int inputX = CONSOLE_AREA.x + CONSOLE_PADDING_X + INPUT_OFFSET_X;
        int inputW = CONSOLE_AREA.width - (CONSOLE_PADDING_X * 2);

        return new Rectangle(inputX, inputY, inputW, INPUT_HEIGHT);
    }

    // Cherche le dernier prompt à garder affiché.
    private String findPinnedPrompt(List<String> lines) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            String line = lines.get(i);
            if (isPromptLine(line)) {
                return stripSessionTag(line);
            }
        }
        return null;
    }

    // Vérifie si une ligne correspond à un prompt du jeu.
    private boolean isPromptLine(String line) {
        String normalized = stripSessionTag(line);
        return MAIN_PROMPT.equals(normalized) || COMBAT_PROMPT.equals(normalized);
    }

    // Enlève le tag de session pour afficher une ligne propre.
    private String stripSessionTag(String line) {
        if (line == null) {
            return "";
        }
        if (line.startsWith("[S")) {
            int close = line.indexOf(']');
            if (close >= 0 && close + 2 <= line.length()) {
                return line.substring(close + 2);
            }
        }
        return line;
    }
}