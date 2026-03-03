package fr.campus.escapebattlebarge.ui;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class AudioManager {

    private static Clip clip;
    private static String currentAudio;

    public static void startLoopAudio(String resourcePath) {

        if (resourcePath.equals(currentAudio) && clip != null && clip.isRunning()) {
            return;
        }

        stopAudio();

        try {
            InputStream is = AudioManager.class.getResourceAsStream(resourcePath);
            if (is == null) {
                System.out.println("Audio not found: " + resourcePath);
                return;
            }

            try (BufferedInputStream bis = new BufferedInputStream(is);
                 AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {

                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();

                currentAudio = resourcePath;
            }

        } catch (Exception e) {
            System.out.println("Audio error: " + e.getMessage());
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
}