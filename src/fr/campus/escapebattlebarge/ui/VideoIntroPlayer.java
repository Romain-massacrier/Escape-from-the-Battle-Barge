package fr.campus.escapebattlebarge.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public final class VideoIntroPlayer {

    private static final AtomicBoolean FX_STARTED = new AtomicBoolean(false);

    private VideoIntroPlayer() {}

    private static void ensureJavaFxStarted() {
        if (FX_STARTED.compareAndSet(false, true)) {
            // Démarre le runtime JavaFX dans une appli Swing/AWT
            new JFXPanel();
        }
    }

    public static void play(String resourcePath, Runnable onFinished) {
        ensureJavaFxStarted();

        Platform.runLater(() -> {
            URL url = VideoIntroPlayer.class.getResource(resourcePath);
            if (url == null) {
                throw new IllegalStateException("Ressource introuvable: " + resourcePath);
            }

            Media media = new Media(url.toExternalForm());
            MediaPlayer player = new MediaPlayer(media);
            MediaView view = new MediaView(player);

            view.setPreserveRatio(true);

            StackPane root = new StackPane(view);
            Scene scene = new Scene(root, 1280, 720);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.setFullScreen(true);

            Runnable finish = () -> {
                try { player.stop(); } catch (Exception ignored) {}
                try { player.dispose(); } catch (Exception ignored) {}
                stage.close();
                if (onFinished != null) {
                    SwingUtilities.invokeLater(onFinished);
                }
            };

            player.setOnEndOfMedia(finish);

            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.SPACE) {
                    finish.run();
                }
            });

            view.fitWidthProperty().bind(scene.widthProperty());
            view.fitHeightProperty().bind(scene.heightProperty());

            stage.show();
            player.play();
        });
    }
}