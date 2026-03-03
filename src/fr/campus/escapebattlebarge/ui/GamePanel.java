package fr.campus.escapebattlebarge.ui;

import fr.campus.escapebattlebarge.game.GameState;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private final GameState state;

    private final ImageIcon caserne;
    private final ImageIcon coursives;
    private final ImageIcon sanctuaire;
    private final ImageIcon armements;
    private final ImageIcon noyaux;
    private final ImageIcon extraction;

    public GamePanel(GameState state) {
        this.state = state;

        caserne   = requireIcon("/images/caserne.png");
        coursives = requireIcon("/images/coursives.png");
        sanctuaire= requireIcon("/images/sanctuaire.png");
        armements = requireIcon("/images/armements.png");
        noyaux    = requireIcon("/images/noyaux.png");
        extraction= requireIcon("/images/extraction.png");

        setPreferredSize(new Dimension(caserne.getIconWidth(), caserne.getIconHeight()));
        setBackground(Color.BLACK);
        setOpaque(true);
    }

    private ImageIcon requireIcon(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            // Si ça arrive: c’est 100% un problème de nom/chemin/casse
            throw new IllegalStateException("Ressource introuvable: " + path);
        }
        ImageIcon icon = new ImageIcon(url);
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            throw new IllegalStateException("Image invalide: " + path);
        }
        System.out.println("OK image: " + path + " (" + icon.getIconWidth() + "x" + icon.getIconHeight() + ")");
        return icon;
    }

    private ImageIcon pickZoneIcon() {
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

        ImageIcon bg = pickZoneIcon();
        bg.paintIcon(this, g, 0, 0);

        drawConsole((Graphics2D) g);
    }

    private void drawConsole(Graphics2D g2) {
        int x = 520;
        int y = 720;
        int w = 880;
        int h = 250;

        g2.setColor(new Color(0, 255, 70));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 22));

        int lineY = y + 40;
        for (String line : state.getConsoleLines()) {
            g2.drawString(line, x + 20, lineY);
            lineY += 24;
        }

        int inputH = 42;
        int inputY = y + h - inputH - 12;

        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x + 16, inputY, w - 32, inputH, 12, 12);
    }

    public Rectangle getInputBounds() {
        int x = 520;
        int y = 720;
        int w = 880;
        int h = 250;

        int inputH = 42;
        int inputY = y + h - inputH - 12;

        return new Rectangle(x + 16, inputY, w - 32, inputH);
    }
}