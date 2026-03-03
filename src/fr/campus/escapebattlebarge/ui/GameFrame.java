package fr.campus.escapebattlebarge.ui;

import fr.campus.escapebattlebarge.game.CombatEngine;
import fr.campus.escapebattlebarge.game.GameController;
import fr.campus.escapebattlebarge.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GameFrame extends JFrame implements CombatEngine.InputProvider {

    private final GamePanel panel;
    private final GameController controller;

    private final JTextField inputField = new JTextField();
    private final BlockingQueue<String> inputs = new ArrayBlockingQueue<>(10);

    public GameFrame(GameState state) {
        super("Escape from the Battle Barge");

        this.panel = new GamePanel(state);
        this.controller = new GameController(state);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        Dimension size = panel.getPreferredSize();

        // ✅ Conteneur superposé propre
        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(size);
        layered.setLayout(null);

        // ✅ Panel (fond) à taille exacte
        panel.setBounds(0, 0, size.width, size.height);
        layered.add(panel, Integer.valueOf(0));

        // ✅ Champ de saisie au-dessus
        Rectangle r = panel.getInputBounds();
        inputField.setBounds(r);
        inputField.setOpaque(false);
        inputField.setBorder(null);
        inputField.setForeground(new Color(0, 255, 70));
        inputField.setCaretColor(new Color(0, 255, 70));
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 22));
        layered.add(inputField, Integer.valueOf(1));

        inputField.addActionListener(e -> {
            String v = inputField.getText().trim();
            inputField.setText("");
            if (!v.isEmpty()) inputs.offer(v);
        });

        // ✅ IMPORTANT: contentPane = layered
        setContentPane(layered);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        installKeyBindings();

        SwingUtilities.invokeLater(() -> {
            inputField.requestFocusInWindow();
            panel.repaint();
        });

        AudioManager.startLoopAudio("/audio/game.wav");
    }

    private void installKeyBindings() {
        JComponent c = panel;
        InputMap im = c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = c.getActionMap();

        im.put(KeyStroke.getKeyStroke('1'), "roll");
        am.put("roll", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controller.onRoll(GameFrame.this);
                panel.repaint();
                inputField.requestFocusInWindow();
            }
        });

        im.put(KeyStroke.getKeyStroke('2'), "inv");
        am.put("inv", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controller.onInventory(GameFrame.this);
                panel.repaint();
                inputField.requestFocusInWindow();
            }
        });
    }

    @Override
    public String readChoice() {
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
        try {
            return inputs.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        }
    }
}