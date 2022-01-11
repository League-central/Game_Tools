package game_tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class Sound {
    public static final int PLAY_ENTIRE_SOUND = 0;
    public static final String DEFAULT_PATH = "src/";

    public String fileName;
    public mp3Sound mp3Clip = null;
    public Clip wavClip = null;

    public Sound(String fileName) {
        this.fileName = fileName;

        if (fileName.indexOf(".wav") != -1) {
            wavClip = loadWavSound(DEFAULT_PATH, fileName);
        } else {
            mp3Clip = new mp3Sound(DEFAULT_PATH, fileName);
        }
    }

    public Sound(String path, String fileName) {
        this.fileName = fileName;

        if (fileName.indexOf(".wav") != -1) {
            wavClip = loadWavSound(path, fileName);
        } else {
            mp3Clip = new mp3Sound(path, fileName);
        }
    }

    public void play() {
        play(PLAY_ENTIRE_SOUND);
    }

    public void play(int durationSeconds) {
        if (wavClip != null) {
            playWavSound(durationSeconds);
        } else if (mp3Clip != null) {
            mp3Clip.setDuration(durationSeconds);
            mp3Clip.play();
        } else {
            System.out.println("ERROR: no valid sound file loaded");
        }
    }

    public void stop() {
        if (wavClip != null) {
            wavClip.stop();
        } else if (mp3Clip != null) {
            mp3Clip.stopPlaying();
        } else {
            System.out.println("ERROR: no valid sound file loaded");
        }
    }

    public void loop() {
        if (wavClip != null) {
            wavClip.loop(Clip.LOOP_CONTINUOUSLY);
        } else if (mp3Clip != null) {
            this.mp3Clip.loop();
        } else {
            System.out.println("ERROR: no valid sound file loaded");
        }
    }

    public boolean isPlaying() {
        boolean isPlaying = false;

        if (wavClip != null) {
            isPlaying = this.wavClip.isActive();
        } else if (mp3Clip != null) {
            isPlaying = this.mp3Clip.playThread.isAlive();
        } else {
            System.out.println("ERROR: no valid sound file loaded");
        }

        return isPlaying;
    }

    /*
     * Private Methods
     */
    private Clip loadWavSound(String path, String fileName) {
        Clip clip = null;

        try {
            clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(path + fileName));
            clip.open(inputStream);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage() + " for " + path + fileName);
        }

        return clip;
    }

    public Clip playWavSound(int durationSeconds) {

        class Listener implements LineListener {
            // Don't cache/optimize this variable for loop check
            volatile LineEvent.Type soundEvent = null;

            @Override
            public void update(LineEvent event) {
                soundEvent = event.getType();
            }
        }

        Listener l = new Listener();
        this.wavClip.addLineListener(l);
        this.wavClip.setFramePosition(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                wavClip.start();
            }
        }).start();

        if (durationSeconds == PLAY_ENTIRE_SOUND) {
            // Block thread until thread/sound is done
            while (l.soundEvent != LineEvent.Type.STOP) {
                // Wait/poll
            }
        }

        return wavClip;
    }

    public class mp3Sound {
        private int duration;
        private String path = null;
        private String fileName = null;
        private AdvancedPlayer mp3Player = null;
        private InputStream songStream = null;
        private Thread playThread = null;
        private PlaybackListener loopPlaybackListener = null;

        public mp3Sound(String path, String songAddress) {
            this.path = path;
            this.fileName = songAddress;

            /*
             * Define what to do when the sound starts/finishes when looping
             */
            loopPlaybackListener = new PlaybackListener() {
                @Override
                public void playbackStarted(PlaybackEvent arg0) {
                }

                @Override
                public void playbackFinished(PlaybackEvent event) {
                    stopPlaying();
                    loop();
                }
            };
        }

        public void play() {
            playSound(false);
        }

        public void loop() {
            playSound(true);
        }

        public void setDuration(int seconds) {
            this.duration = seconds;
        }

        public void stopPlaying() {
            if (mp3Player != null) {
                mp3Player.close();
            }
            playThread = null;
            songStream = null;
        }

        /*
         * Private methods
         */

        private void playSound(boolean loopSound) {
            if (songStream == null) {
                loadFile();
            }

            if (songStream != null) {
                loadPlayer();

                if (loopSound) {
                    this.mp3Player.setPlayBackListener(loopPlaybackListener);
                }

                startPlaying();
            } else {
                System.err.println("ERROR: Unable to load file: " + path + fileName);
            }
        }

        private void startPlaying() {
            playThread = new Thread() {
                @Override
                public void run() {
                    try {
                        if (duration > 0) {
                            mp3Player.play(duration);
                        } else {
                            mp3Player.play();
                        }
                    } catch (Exception e) {
                    }
                }
            };

            playThread.start();
        }

        private void loadPlayer() {
            try {
                this.mp3Player = new AdvancedPlayer(songStream);
            } catch (Exception e) {
            }
        }

        private void loadFile() {
            if (fileName.contains("http"))
                this.songStream = loadStreamFromInternet();
            else
                this.songStream = loadStreamFromComputer();
        }

        private InputStream loadStreamFromInternet() {
            try {
                return new URL(fileName).openStream();
            } catch (Exception e) {
                return null;
            }
        }

        private InputStream loadStreamFromComputer() {
            try {
                return new FileInputStream(path + fileName);
            } catch (FileNotFoundException e) {
                return this.getClass().getResourceAsStream(fileName);
            }
        }
    }
}
