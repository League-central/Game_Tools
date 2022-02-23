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
    public Mp3Sound mp3Clip = null;
    public WavSound wavClip = null;

    /**
     * Load a sound file. Only .mp3 and .wav have been tested.
     * <p>
     * @param fileName
     *            file name from 'src/' relative path. Example:
     *            Sound("jeopardy.wav") for file in "src/jeopardy.wav"
     */
    public Sound(String fileName) {
        this.fileName = fileName;

        if (fileName.indexOf(".wav") != -1) {
            wavClip = new WavSound(DEFAULT_PATH, fileName);
        } else {
            mp3Clip = new Mp3Sound(DEFAULT_PATH, fileName);
        }
    }

    /**
     * Load a sound file. Only .mp3 and .wav have been tested.
     * <p>
     * @param path
     *            path to sound file from the top of the project. Example:
     *            "src/module_1/"
     * @param fileName
     *            file name. Example: "jeopardy.wav"
     */
    public Sound(String path, String fileName) {
        this.fileName = fileName;

        if (fileName.indexOf(".wav") != -1) {
            wavClip = new WavSound(path, fileName);
        } else {
            mp3Clip = new Mp3Sound(path, fileName);
        }
    }

    /**
     * Play sound until done or stop() method called.
     * <p>
     * Does not block main thread.
     */
    public void play() {
        play(PLAY_ENTIRE_SOUND);
    }

    /**
     * Play for a specific duration in seconds, then stop.
     * <p>
     * Does not block main thread.
     * <p>
     * @param durationSeconds
     */
    public void play(int durationSeconds) {
        if (wavClip != null) {
            wavClip.play(durationSeconds);
        } else if (mp3Clip != null) {
            mp3Clip.setDuration(durationSeconds);
            mp3Clip.play();
        } else {
            System.out.println("ERROR: no valid sound file loaded " + this.fileName);
        }
    }

    /**
     * Stop sound from playing.
     */
    public void stop() {
        if (wavClip != null) {
            wavClip.stop();
        } else if (mp3Clip != null) {
            mp3Clip.stopPlaying();
        } else {
            System.out.println("ERROR: no valid sound file loaded");
        }
    }

    /**
     * Play sound in a loop until stop() is called.
     * <p>
     * Does not block main thread.
     */
    public void loop() {
        if (wavClip != null) {
            wavClip.loop();
        } else if (mp3Clip != null) {
            this.mp3Clip.loop();
        } else {
            System.out.println("ERROR: no valid sound file loaded");
        }
    }

    /**
     * Check if sound is playing.
     * <p>
     * @return true = sound is playing; false = not playing
     */
    public boolean isPlaying() {
        boolean isPlaying = false;

        if (wavClip != null) {
            isPlaying = this.wavClip.isPlaying();
        } else if (mp3Clip != null) {
            isPlaying = this.mp3Clip.isPlaying();
        } else {
            System.out.println("ERROR: no valid sound file loaded");
        }

        return isPlaying;
    }

    /**
     * Speak words in input string.
     * <p>
     * @param words
     */
    public static void speak(String words) {
        if (System.getProperty("os.name").contains("Windows")) {
            String cmd = "PowerShell -Command \"Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('"
                    + words + "');\"";
            try {
                Runtime.getRuntime().exec(cmd).waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Runtime.getRuntime().exec("say " + words).waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    class WavSound {
        Clip clip = null;
        String pathFileName = null;
        volatile Thread playThread = null;  // volatile b/c this is used by isPlaying(),
                                            // which can be called in a loop in the main thread

        class Listener implements LineListener {
            // Don't cache/optimize this variable for loop checking
            volatile LineEvent.Type soundEvent = null;

            @Override
            public void update(LineEvent event) {
                soundEvent = event.getType();
            }
        }

        public WavSound(String path, String fileName) {
            this.pathFileName = path + fileName;
            
            try {
                clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(pathFileName));
                clip.open(inputStream);
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage() + " for " + pathFileName);
                e.printStackTrace();
            }
        }

        public boolean isPlaying() {
            return this.playThread != null;
        }

        public void stop() {
            this.clip.stop();
            this.playThread = null;
        }

        public void loop() {
            stop();
            this.clip.setFramePosition(0);

            Runnable clipStart = () -> {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                while (true) {
                    // Intentional inf loop to keep thread alive to play clip loop
                    // Call stop() method to stop this thread
                }
            };
            this.playThread = new Thread(clipStart);
            this.playThread.start();
        }

        public void play(int durationSeconds) {
            stop();
            this.clip.setFramePosition(0);
            Runnable clipStart = null;

            if (durationSeconds == PLAY_ENTIRE_SOUND) {
                Listener l = new Listener();
                this.clip.addLineListener(l);
                
                clipStart = () -> {
                    this.clip.start();
                    
                    // Block thread until thread/sound is done
                    while (l.soundEvent != LineEvent.Type.STOP) {
                        // Wait/poll
                    }
                    
                    stop();
                };
            } else {
                clipStart = () -> {
                    this.clip.start();
                    
                    try {
                        Thread.sleep(durationSeconds * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    stop();
                };
            }

            this.playThread = new Thread(clipStart);
            this.playThread.start();
        }
    }

    class Mp3Sound {
        private int FRAMES_PER_SECOND = 33; // ball park estimate
        private int durationFrames;
        private String path = null;
        private String fileName = null;
        private AdvancedPlayer mp3Player = null;
        private InputStream songStream = null;
        private Thread playThread = null;
        private PlaybackListener loopPlaybackListener = null;
        private PlaybackListener playbackListener = null;

        public Mp3Sound(String path, String songAddress) {
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
            
            playbackListener = new PlaybackListener() {
                @Override
                public void playbackStarted(PlaybackEvent arg0) {
                }

                @Override
                public void playbackFinished(PlaybackEvent event) {
                    stopPlaying();
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
            this.durationFrames = seconds * FRAMES_PER_SECOND;
        }

        public void stopPlaying() {
            if (mp3Player != null) {
                mp3Player.close();
            }
            playThread = null;
            songStream = null;
        }
        
        public boolean isPlaying() {
            if( this.playThread != null) {
                return this.playThread.isAlive();
            }
            return false;
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
                } else {
                    this.mp3Player.setPlayBackListener(playbackListener);
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
                        if (durationFrames > 0) {
                            mp3Player.play(durationFrames);
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
