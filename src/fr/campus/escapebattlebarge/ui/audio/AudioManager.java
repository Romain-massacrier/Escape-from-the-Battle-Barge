package fr.campus.escapebattlebarge.ui.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/*
 * Cette classe centralise la gestion audio (musique de fond et sons ponctuels).
 * Elle est appelée par les écrans pour lancer/arrêter la bonne ambiance sonore.
 * Entrée: chemin de ressource audio. Sortie: lecture/arrêt de Clip.
 */
public class AudioManager {

    private static Clip clip;
    private static String currentAudio;

    // Lance une musique en boucle; ne redémarre pas si la même piste tourne déjà.
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

    // Arrête la musique de fond courante et libère le clip.
    public static void stopAudio() {
        currentAudio = null;
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }

    // Joue un son court sans couper la musique de fond.
    public static void playOneShotOverlay(String resourcePath) {
        try {
            InputStream is = AudioManager.class.getResourceAsStream(resourcePath);
            if (is == null) {
                System.out.println("Audio not found: " + resourcePath);
                return;
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
            Clip oneShot = AudioSystem.getClip();
            oneShot.open(ais);
            oneShot.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    oneShot.close();
                    try {
                        ais.close();
                        bis.close();
                    } catch (Exception ignored) {
                    }
                }
            });
            oneShot.start();

        } catch (Exception e) {
            System.out.println("Audio error: " + e.getMessage());
        }
    }
}