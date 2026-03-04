package fr.campus.escapebattlebarge.ui;

import fr.campus.escapebattlebarge.game.CombatEngine;
import fr.campus.escapebattlebarge.game.GameController;
import fr.campus.escapebattlebarge.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GameFrame extends JFrame implements CombatEngine.InputProvider {

    private final GameState state;
    private final GamePanel panel;
    private final GameController controller;
    private final Runnable onReturnToMenu;

    private final JTextField inputField = new JTextField();
    private final BlockingQueue<String> inputs = new ArrayBlockingQueue<>(10);
    private volatile boolean actionInProgress = false;
    private volatile boolean waitingForChoice = false;

    public GameFrame(GameState state, Runnable onReturnToMenu) {
        super("Escape from the Battle Barge");

        this.state = state;
        this.panel = new GamePanel(state);
        this.controller = new GameController(state);
        this.onReturnToMenu = onReturnToMenu;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        Dimension size = panel.getPreferredSize();

        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(size);
        layered.setLayout(null);

        panel.setBounds(0, 0, size.width, size.height);
        layered.add(panel, Integer.valueOf(0));

        Rectangle r = panel.getInputBounds();
        inputField.setBounds(r);
        inputField.setOpaque(false);
        inputField.setBorder(null);
        inputField.setForeground(new Color(0, 255, 70));
        inputField.setCaretColor(new Color(0, 255, 70));
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 22));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        layered.add(inputField, Integer.valueOf(1));

        inputField.addActionListener(e -> {
            String raw = inputField.getText();
            String v = (raw == null) ? "" : raw.trim();
            inputField.setText("");

            if (waitingForChoice) {
                inputs.offer(v);
                return;
            }

            if (v.isEmpty()) return;

            if (actionInProgress) {
                return;
            }

            if ("1".equals(v)) {
                runAction(() -> controller.onRoll(GameFrame.this));
            } else if ("2".equals(v)) {
                runAction(() -> controller.onInventory(GameFrame.this));
            } else {
                state.log("Choix invalide. 1 lancer dé | 2 inventaire");
                SwingUtilities.invokeLater(panel::repaint);
            }
        });

        setContentPane(layered);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());

        AudioManager.startLoopAudio("/audio/game.wav");
    }

    private void flushPendingInputs() {
        inputs.clear();
        inputField.setText("");
    }

    private void runAction(Runnable action) {
        actionInProgress = true;
        flushPendingInputs();

        new Thread(() -> {
            try {
                action.run();
            } finally {
                if (state.consumeExtractionAlertPlaybackRequest()) {
                    AudioManager.playOneShotOverlay("/audio/alarme.wav");
                }

                if (state.consumeReturnToMainMenuRequest()) {
                    SwingUtilities.invokeLater(() -> {
                        AudioManager.stopAudio();
                        dispose();
                        if (onReturnToMenu != null) {
                            onReturnToMenu.run();
                        }
                    });
                    actionInProgress = false;
                    return;
                }

                actionInProgress = false;
                SwingUtilities.invokeLater(panel::repaint);
                SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
            }
        }).start();
    }

    @Override
    public String readChoice() {
        SwingUtilities.invokeLater(panel::repaint);
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
        waitingForChoice = true;
        try {
            return inputs.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        } finally {
            waitingForChoice = false;
        }
    }
}