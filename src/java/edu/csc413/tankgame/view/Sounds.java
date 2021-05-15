package edu.csc413.tankgame.view;

import javax.sound.sampled.*;
import java.io.File;

public class Sounds {

    public static void playSound(String filePath) {
        File getAudioFile;
        try {
            getAudioFile = new File(filePath);
            AudioInputStream audioFile = AudioSystem.getAudioInputStream(getAudioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioFile);
            clip.start();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
