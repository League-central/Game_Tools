package examples.sound;

import game_tools.Sound;

public class SoundTests {
    public static void main(String[] args) {
        speakExample();
        mp3Example();
        wavExample();
    }
    
    static void speakExample() {
        Sound.speak("The value of PI is: 3.14");
    }
    
    static void mp3Example() {
        Sound mp3 = new Sound("examples/sound/soundFiles/awesomeTrack.mp3");
        
        System.out.println("Play 3 seconds of the song");
        mp3.play(3);
        
        delay(6);
        
        System.out.println("Play song once until the end");
        mp3.play();
        
        while( mp3.isPlaying() ) {}
        
        System.out.println("Loop song for 15 seconds");
        mp3.loop();
        delay(15);
        mp3.stop();
    }
    
    static void wavExample() {
        Sound wav = new Sound("examples/sound/soundFiles/jeopardy.wav");
        
        System.out.println("Play 3 seconds of the song");
        wav.play(3);

        delay(6);
        
        System.out.println("Play song once until the end");
        wav.play();
        
        while( wav.isPlaying() ) {}
        
        System.out.println("Loop song for 45 seconds");
        wav.loop();
        delay(45);
        wav.stop();
    }
    
    static void delay(int seconds) {
        int intervalSec = (seconds > 10) ? 5 : 1;
        int i = seconds;
        
        while( i > 0) {
            try {
                System.out.println(i + " seconds remaining");
                Thread.sleep(intervalSec * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if (i < intervalSec) {
                intervalSec = 1;
            }
            
            i -= intervalSec;
        }
    }
}
