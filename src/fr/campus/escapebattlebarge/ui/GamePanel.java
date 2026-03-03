package fr.campus.escapebattlebarge.ui;

import fr.campus.escapebattlebarge.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {

    private static final Rectangle CONSOLE_AREA = new Rectangle(520, 720, 880, 250);
    private static final int CONSOLE_PADDING_X = 20;
    private static final int CONSOLE_PADDING_TOP = 14;
    private static final int CONSOLE_PADDING_BOTTOM = 12;
    private static final int INPUT_HEIGHT = 42;
    private static final int INPUT_TO_TEXT_GAP = 12;
    private static final String MAIN_PROMPT = "1 lancer dé | 2 inventaire";
    private static final String COMBAT_PROMPT = "1 Attaquer | 2 Potion | 3 Fuir";

    private final GameState state;

    private Image caserne;
    private Image coursives;
    private Image sanctuaire;
    private Image armements;
    private Image noyaux;
    private Image extraction;

    public GamePanel(GameState state) {
        this.state = state;

        caserne = load("/images/caserne.png");
        coursives = load("/images/coursives.png");
        sanctuaire = load("/images/sanctuaire.png");
        armements = load("/images/armements.png");
        noyaux = load("/images/noyaux.png");
        extraction = load("/images/extraction.png");

        int w = caserne.getWidth(this);
        int h = caserne.getHeight(this);

        if (w > 0 && h > 0) {
            setPreferredSize(new Dimension(w, h));
        }

        setBackground(Color.BLACK);
    }

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

    private Image pickZoneImage() {
        int pos = state.getPlayer().getPosition();
        int cell = Math.max(1, Math.min(64, pos));

        if (cell <= 8) return caserne;
        if (cell <= 20) return coursives;
        if (cell <= 32) return sanctuaire;
        if (cell <= 44) return armements;
        if (cell <= 56) return noyaux;
        return extraction;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image bg = pickZoneImage();

        if (bg != null) {
            g.drawImage(bg, 0, 0, this);
        }

        drawConsole((Graphics2D) g);
    }

    private void drawConsole(Graphics2D g2) {
        g2.setColor(new Color(0, 255, 70));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 22));

        List<String> lines = state.getConsoleLines();

        Rectangle input = getInputBounds();
        int textTop = CONSOLE_AREA.y + CONSOLE_PADDING_TOP;
        int textBottom = input.y - INPUT_TO_TEXT_GAP;

        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();
        int maxVisibleLines = Math.max(1, (textBottom - textTop) / lineHeight);

        String pinnedPrompt = findPinnedPrompt(lines);
        List<String> visibleLines;

        if (pinnedPrompt == null) {
            int start = Math.max(0, lines.size() - maxVisibleLines);
            visibleLines = lines.subList(start, lines.size());
        } else {
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
        g2.setClip(CONSOLE_AREA.x, textTop, CONSOLE_AREA.width, Math.max(1, textBottom - textTop));

        int lineY = textTop + fm.getAscent();
        for (String line : visibleLines) {
            g2.drawString(line, CONSOLE_AREA.x + CONSOLE_PADDING_X, lineY);
            lineY += lineHeight;
        }

        g2.setClip(oldClip);
    }

    public Rectangle getInputBounds() {
        int inputY = CONSOLE_AREA.y + CONSOLE_AREA.height - INPUT_HEIGHT - CONSOLE_PADDING_BOTTOM;
        int inputX = CONSOLE_AREA.x + CONSOLE_PADDING_X;
        int inputW = CONSOLE_AREA.width - (CONSOLE_PADDING_X * 2);

        return new Rectangle(inputX, inputY, inputW, INPUT_HEIGHT);
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
        return MAIN_PROMPT.equals(normalized) || COMBAT_PROMPT.equals(normalized);
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