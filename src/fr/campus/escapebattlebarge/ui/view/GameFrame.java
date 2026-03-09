package fr.campus.escapebattlebarge.ui.view;

import fr.campus.escapebattlebarge.db.CharacterDao;
import fr.campus.escapebattlebarge.domain.character.Character;
import fr.campus.escapebattlebarge.game.combat.CombatEngine;
import fr.campus.escapebattlebarge.game.core.GameController;
import fr.campus.escapebattlebarge.game.core.GameState;
import fr.campus.escapebattlebarge.ui.audio.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Fenêtre de jeu Swing: saisie utilisateur + orchestration du contrôleur. */
public class GameFrame extends JFrame implements CombatEngine.InputProvider {

    private final GameState state;
    private final GamePanel panel;
    private final GameController controller;
    private final Runnable onReturnToMenu;

    private final JTextField inputField = new JTextField();
    private final BlockingQueue<String> inputs = new ArrayBlockingQueue<>(10);
    private volatile boolean actionInProgress = false;
    private volatile boolean waitingForChoice = false;

    public GameFrame(GameState state, CharacterDao characterDao, Character currentHero, Runnable onReturnToMenu) {
        super("Escape from the Battle Barge");

        this.state = state;
        this.panel = new GamePanel(state);
        this.controller = new GameController(state, characterDao, currentHero);
        this.onReturnToMenu = onReturnToMenu;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        Dimension size = panel.getPreferredSize();

        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(size);
        layered.setLayout(null);

        panel.setBounds(0, 0, size.width, size.height);
        layered.add(panel, Integer.valueOf(0));

        configureInputField(layered);

        setContentPane(layered);
        pack();
        setLocationRelativeTo(null);
        disableMouseInput();
        setVisible(true);

        refreshAndFocusInput();

        AudioManager.startLoopAudio("/audio/game.wav");
    }

    private void disableMouseInput() {
        JPanel blocker = new JPanel();
        blocker.setOpaque(false);

        blocker.addMouseListener(new MouseAdapter() {
        });
        blocker.addMouseMotionListener(new MouseMotionAdapter() {
        });
        blocker.addMouseWheelListener((MouseWheelEvent e) -> {
        });

        setGlassPane(blocker);
        blocker.setVisible(true);
    }

    private void configureInputField(JLayeredPane layered) {
        inputField.setBounds(panel.getInputBounds());
        inputField.setOpaque(false);
        inputField.setBorder(null);
        inputField.setForeground(new Color(0, 255, 70));
        inputField.setCaretColor(new Color(0, 255, 70));
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 22));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        layered.add(inputField, Integer.valueOf(1));

        inputField.addActionListener(e -> onInputSubmitted());
    }

    private void onInputSubmitted() {
        String value = inputField.getText();
        String choice = value == null ? "" : value.trim();
        inputField.setText("");

        if (waitingForChoice) {
            inputs.offer(choice);
            return;
        }

        if (choice.isEmpty() || actionInProgress) {
            return;
        }

        switch (choice) {
            case "1" -> runAction(() -> controller.onRoll(this));
            case "2" -> runAction(() -> controller.onInventory(this));
            default -> {
                state.log("Choix invalide. 1 lancer dé | 2 inventaire");
                SwingUtilities.invokeLater(panel::repaint);
            }
        }
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
                refreshAndFocusInput();
            }
        }).start();
    }

    private void refreshAndFocusInput() {
        SwingUtilities.invokeLater(() -> {
            panel.repaint();
            inputField.requestFocusInWindow();
        });
    }

    @Override
    public String readChoice() {
        refreshAndFocusInput();
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