package fr.campus.escapebattlebarge.ui.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/** Utilitaire audio (boucle de fond + sons ponctuels). */
public class AudioManager {

    private static Clip clip;
    private static String currentAudio;

    public static void startLoopAudio(String resourcePath) {

        if (resourcePath.equals(currentAudio) && clip != null && clip.isRunning()) {
            return;
        }

        stopAudio();

        try {
            Clip loopClip = createClip(resourcePath);
            if (loopClip == null) {
                return;
            }
            clip = loopClip;
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            currentAudio = resourcePath;

        } catch (Exception e) {
            System.err.println("Audio error: " + e.getMessage());
        }
    }

    public static void stopAudio() {
        currentAudio = null;
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }

    /** Joue un son court sans couper la musique de fond. */
    public static void playOneShotOverlay(String resourcePath) {
        try {
            Clip oneShot = createClip(resourcePath);
            if (oneShot == null) {
                return;
            }
            oneShot.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    oneShot.close();
                }
            });
            oneShot.start();

        } catch (Exception e) {
            System.err.println("Audio error: " + e.getMessage());
        }
    }

    private static Clip createClip(String resourcePath) {
        try {
            InputStream inputStream = AudioManager.class.getResourceAsStream(resourcePath);
            if (inputStream == null) {
                System.err.println("Audio not found: " + resourcePath);
                return null;
            }

            try (BufferedInputStream buffered = new BufferedInputStream(inputStream);
                 AudioInputStream audioInput = AudioSystem.getAudioInputStream(buffered)) {
                Clip newClip = AudioSystem.getClip();
                newClip.open(audioInput);
                return newClip;
            }
        } catch (Exception e) {
            System.err.println("Audio error: " + e.getMessage());
            return null;
        }
    }
}