package fr.campus.escapebattlebarge.ui.view;

import fr.campus.escapebattlebarge.domain.character.Enemy;
import fr.campus.escapebattlebarge.domain.character.Player;
import fr.campus.escapebattlebarge.game.core.GameState;
import fr.campus.escapebattlebarge.game.board.Zone;
import fr.campus.escapebattlebarge.ui.screen.Position;
import fr.campus.escapebattlebarge.ui.screen.UiText;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Panel principal de rendu du jeu (décor, portraits, console). */
public class GamePanel extends JPanel {

    private static final double PLAYER_IMAGE_RATIO_SCALE = 0.75;
    private static final double ZONE_IMAGE_RATIO_SCALE = 0.9;
    private static final Color UI_GREEN = new Color(0, 255, 70);
    private static final Font UI_FONT = new Font("Monospaced", Font.PLAIN, 24);
    private static final Font CONSOLE_FONT = new Font("Monospaced", Font.PLAIN, 22);

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

        int w = (plateau != null) ? plateau.getWidth(this) : Position.GamePanel.PLATEAU_AREA.width;
        int h = (plateau != null) ? plateau.getHeight(this) : Position.GamePanel.PLATEAU_AREA.height;

        if (w > 0 && h > 0) {
            setPreferredSize(new Dimension(w, h));
        }

        setBackground(Color.BLACK);
    }

    /** Charge une image depuis les ressources, ou null si absente. */
    private Image load(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    private Image getPlayerImage() {
        return state.getPlayer().isLibrarian() ? librarianPortrait : spaceMarinePortrait;
    }

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

    private Image getEnemyImage(Enemy enemy) {
        String name = enemy == null ? null : enemy.getName();
        if (name == null || name.isBlank()) {
            return null;
        }

        String normalized = name.toLowerCase();
        if (normalized.contains("warboss")) return warbossPortrait;
        if (normalized.contains("squig")) return squigPortrait;
        if (normalized.contains("sorc")) return sorcierPortrait;
        if (normalized.contains("ork")) return orkPortrait;
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        drawPlateau(g2);
        drawGameVisuals(g2);
        drawConsole(g2);

        g2.dispose();
    }

    private void drawPlateau(Graphics2D g2) {
        if (plateau != null) {
            g2.drawImage(
                    plateau,
                    Position.GamePanel.PLATEAU_AREA.x,
                    Position.GamePanel.PLATEAU_AREA.y,
                    Position.GamePanel.PLATEAU_AREA.width,
                    Position.GamePanel.PLATEAU_AREA.height,
                    null
            );
        }
    }

    private void drawGameVisuals(Graphics2D g2) {
        Player player = state.getPlayer();
        Enemy enemy = state.getCurrentEnemy();

        Image playerImage = getPlayerImage();
        if (playerImage != null) {
            drawImageKeepingRatio(g2, playerImage, Position.GamePanel.PLAYER_IMAGE_AREA, PLAYER_IMAGE_RATIO_SCALE);
        }

        Image zoneImage = getZoneImage();
        if (zoneImage != null) {
            drawImageFillBox(g2, zoneImage, Position.GamePanel.ZONE_IMAGE_AREA, ZONE_IMAGE_RATIO_SCALE);
        }

        Image enemyImage = getEnemyImage(enemy);
        if (enemyImage != null) {
            g2.drawImage(
                    enemyImage,
                    Position.GamePanel.ENEMY_IMAGE_AREA.x,
                    Position.GamePanel.ENEMY_IMAGE_AREA.y,
                    Position.GamePanel.ENEMY_IMAGE_AREA.width,
                    Position.GamePanel.ENEMY_IMAGE_AREA.height,
                    null
            );
        }

        g2.setColor(UI_GREEN);
        g2.setFont(UI_FONT);

        String playerWeapon = (player.getInventory().getEquippedWeapon() == null)
                ? UiText.GamePanel.NO_WEAPON
                : player.getInventory().getEquippedWeapon().getName();

        g2.drawString(UiText.GamePanel.LABEL_NAME + safeName(player.getName()), Position.GamePanel.PLAYER_TEXT_POINT.x, Position.GamePanel.PLAYER_TEXT_POINT.y);
        g2.drawString(UiText.GamePanel.LABEL_HP + player.getHp() + "/" + player.getMaxHp(), Position.GamePanel.PLAYER_TEXT_POINT.x, Position.GamePanel.PLAYER_TEXT_POINT.y + 84);
        g2.drawString(UiText.GamePanel.LABEL_WEAPON + playerWeapon, Position.GamePanel.PLAYER_TEXT_POINT.x, Position.GamePanel.PLAYER_TEXT_POINT.y + 118);

        if (enemy != null && enemyImage != null) {
            g2.drawString(UiText.GamePanel.LABEL_NAME + safeName(enemy.getName()), Position.GamePanel.ENEMY_TEXT_POINT.x, Position.GamePanel.ENEMY_TEXT_POINT.y);
            g2.drawString(UiText.GamePanel.LABEL_ENEMY_HP + enemy.getHp(), Position.GamePanel.ENEMY_TEXT_POINT.x, Position.GamePanel.ENEMY_TEXT_POINT.y + 84);
        }
    }

    private String safeName(String name) {
        return (name == null || name.isBlank()) ? UiText.GamePanel.UNKNOWN_NAME : name;
    }

    private void drawImageKeepingRatio(Graphics2D g2, Image image, Rectangle box, double ratioScale) {
        int imageW = image.getWidth(null);
        int imageH = image.getHeight(null);
        if (imageW <= 0 || imageH <= 0) {
            return;
        }

        double fitScale = Math.min((double) box.width / imageW, (double) box.height / imageH);
        double finalScale = fitScale * ratioScale;

        int drawW = Math.max(1, (int) Math.round(imageW * finalScale));
        int drawH = Math.max(1, (int) Math.round(imageH * finalScale));

        int drawX = box.x + (box.width - drawW) / 2;
        int drawY = box.y + (box.height - drawH) / 2;

        g2.drawImage(image, drawX, drawY, drawW, drawH, null);
    }

    private void drawImageFillBox(Graphics2D g2, Image image, Rectangle box, double ratioScale) {
        int drawW = Math.max(1, (int) Math.round(box.width * ratioScale));
        int drawH = Math.max(1, (int) Math.round(box.height * ratioScale));

        int drawX = box.x + (box.width - drawW) / 2;
        int drawY = box.y + (box.height - drawH) / 2;

        g2.drawImage(image, drawX, drawY, drawW, drawH, null);
    }

    private void drawConsole(Graphics2D g2) {
        g2.setColor(UI_GREEN);
        g2.setFont(CONSOLE_FONT);

        List<String> lines = state.getConsoleLines();
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();

        Rectangle input = getInputBounds();
        int textTop = Position.GamePanel.CONSOLE_AREA.y + Position.GamePanel.CONSOLE_PADDING_TOP;
        int defaultTextBottom = Position.GamePanel.CONSOLE_AREA.y + Position.GamePanel.CONSOLE_AREA.height - Position.GamePanel.CONSOLE_PADDING_BOTTOM;
        int textBottom = Math.min(defaultTextBottom, input.y - Position.GamePanel.INPUT_TO_TEXT_GAP);
        textBottom = Math.max(textTop + lineHeight, textBottom);
        int maxVisibleLines = Math.max(1, (textBottom - textTop) / lineHeight);

        List<String> visibleLines = getVisibleConsoleLines(lines, maxVisibleLines);

        Shape oldClip = g2.getClip();
        int textX = Position.GamePanel.CONSOLE_AREA.x + Position.GamePanel.CONSOLE_PADDING_X + Position.GamePanel.TEXT_OFFSET_X;
        g2.setClip(
            Position.GamePanel.CONSOLE_AREA.x + Position.GamePanel.TEXT_OFFSET_X,
            textTop,
            Position.GamePanel.CONSOLE_AREA.width,
            Math.max(1, textBottom - textTop)
        );

        int lineY = textTop + fm.getAscent();
        for (String line : visibleLines) {
            String displayLine = line.replace("| Zone ", "| ");
            g2.drawString(displayLine, textX, lineY);
            lineY += lineHeight;
        }

        g2.setClip(oldClip);
    }

    private List<String> getVisibleConsoleLines(List<String> lines, int maxVisibleLines) {
        String pinnedPrompt = findPinnedPrompt(lines);
        if (pinnedPrompt == null) {
            int start = Math.max(0, lines.size() - maxVisibleLines);
            return lines.subList(start, lines.size());
        }

        List<String> visibleLines = new ArrayList<>();
        visibleLines.add(pinnedPrompt);

        int remainingSlots = Math.max(0, maxVisibleLines - 1);
        List<String> nonPromptLines = lines.stream().filter(line -> !isPromptLine(line)).toList();
        int start = Math.max(0, nonPromptLines.size() - remainingSlots);
        visibleLines.addAll(nonPromptLines.subList(start, nonPromptLines.size()));
        return visibleLines;
    }

    public Rectangle getInputBounds() {
        int inputY = Position.GamePanel.CONSOLE_AREA.y + Position.GamePanel.CONSOLE_AREA.height - Position.GamePanel.INPUT_HEIGHT - Position.GamePanel.CONSOLE_PADDING_BOTTOM + Position.GamePanel.INPUT_OFFSET_Y;
        int inputX = Position.GamePanel.CONSOLE_AREA.x + Position.GamePanel.CONSOLE_PADDING_X + Position.GamePanel.INPUT_OFFSET_X;
        int inputW = Position.GamePanel.CONSOLE_AREA.width - (Position.GamePanel.CONSOLE_PADDING_X * 2);

        return new Rectangle(inputX, inputY, inputW, Position.GamePanel.INPUT_HEIGHT);
    }

    private String findPinnedPrompt(List<String> lines) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            String line = lines.get(i);
            if (isPromptLine(line)) {
                return stripSessionTag(line);
            }
        }
        return null;
    }

    private boolean isPromptLine(String line) {
        String normalized = stripSessionTag(line);
        return UiText.GamePanel.MAIN_PROMPT.equals(normalized) || UiText.GamePanel.COMBAT_PROMPT.equals(normalized);
    }

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