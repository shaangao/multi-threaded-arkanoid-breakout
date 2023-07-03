package controller;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Sound {


    public static synchronized void playSound(final String strPath) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Clip clp = AudioSystem.getClip();

                    InputStream audioSrc = Sound.class.getResourceAsStream("/sounds/" + strPath);
                    InputStream bufferedIn = new BufferedInputStream(audioSrc);
                    AudioInputStream aisStream = AudioSystem.getAudioInputStream(bufferedIn);

                    clp.open(aisStream);
                    clp.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    public static Clip clipForLoopFactory(String strPath) {
        Clip clp = null;
        try {
            InputStream audioSrc = Sound.class.getResourceAsStream("/sounds/" + strPath);
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream aisStream = AudioSystem.getAudioInputStream(bufferedIn);
            clp = AudioSystem.getClip();
            clp.open( aisStream );
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        return clp;
    }

}
