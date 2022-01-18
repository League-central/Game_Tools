package game_tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GameCharacter {

    //position
    public int x;
    public int y;

    //movement
    public int verticalMovementSpeed = 2;
    public int horizontalMovementSpeed = 2;
    public int previousX = x;
    public int previousY = y;

    //size
    public int width = 50;
    public int height = 50;

    //shape color
    public Color color = Color.RED;

    //image
    public BufferedImage image;

    //animation
    public Animation idleAnimation;
    public Animation moveRightAnimation;

    //characters
    public PremadeCharacter premadeCharacter;

    public GameCharacter() {
    }

    public GameCharacter(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GameCharacter(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color=color;
    }

    public GameCharacter(int x, int y, int redValue, int greenValue, int blueValue) {
        this(x, y, new Color(redValue, greenValue, blueValue));
    }

    public GameCharacter(int x, int y, Color color, int width, int height) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.width = width;
        this.height = height;
    }

    public GameCharacter(int x, int y, int redValue, int greenValue, int blueValue, int width, int height) {
        this(x, y, new Color(redValue, greenValue, blueValue), width, height);
    }

    public GameCharacter(int x, int y, int width, int height, String... imageFileLocations) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.idleAnimation = new Animation(imageFileLocations);
    }

    public GameCharacter(String imageLocation){
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imageLocation));
        } catch (Exception e) {
            System.err.println("Failed to load Image: "  + imageLocation);
            e.printStackTrace();
        }
    }

    public GameCharacter(int x, int y, int width, int height, String imageLocation){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imageLocation));
        } catch (Exception e) {
            System.err.println("Failed to load Image: "  + imageLocation);
            e.printStackTrace();
        }
    }

    public GameCharacter(PremadeCharacter premadeCharacter) {
        this.premadeCharacter = premadeCharacter;
        if(premadeCharacter == PremadeCharacter.ALIEN) {
            //initialize idle animation
            String[] idleAnimationImageLocations = {
                    "/game_tools/game_character_images/alien/idle/red__0000_idle_1.png",
                    "/game_tools/game_character_images/alien/idle/red__0001_idle_2.png",
                    "/game_tools/game_character_images/alien/idle/red__0002_idle_3.png"
            };
            this.idleAnimation = new Animation(idleAnimationImageLocations);
            //initialize move right animation
            String[] moveRightAnimationImageLocations = {
                    "/game_tools/game_character_images/alien/walk/red__0006_walk_1.png",
                    "/game_tools/game_character_images/alien/walk/red__0007_walk_2.png",
                    "/game_tools/game_character_images/alien/walk/red__0008_walk_3.png",
                    "/game_tools/game_character_images/alien/walk/red__0009_walk_4.png",
                    "/game_tools/game_character_images/alien/walk/red__0010_walk_5.png",
                    "/game_tools/game_character_images/alien/walk/red__0011_walk_6.png",
            };
            this.moveRightAnimation = new Animation(moveRightAnimationImageLocations);

            //adjust framerate
            idleAnimation.frameRate = 10;
            moveRightAnimation.frameRate = 10;
        }
    }

    public GameCharacter(int x, int y, PremadeCharacter premadeCharacter) {
        this(premadeCharacter);
        this.x = x;
        this.y = y;
    }

    public void moveRight(){
        this.x += horizontalMovementSpeed;
    }

    public void moveLeft(){
        this.x -= horizontalMovementSpeed;
    }

    public void moveUp(){
        this.y -= verticalMovementSpeed;
    }

    public void moveDown(){
        this.y += verticalMovementSpeed;
    }

    public void draw(Graphics g) {
        if(idleAnimation != null){
            idleAnimation.draw(g, x, y, width, height);
            if(moveRightAnimation != null && previousX < x){
                moveRightAnimation.draw(g, x, y, width, height);
                previousX = x;
            }
        }
        else if(image != null){
            g.drawImage(image, x, y, width, height, null);
        }
        else{
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
//        g.setColor(Color.YELLOW);
//        moveMouth(g);
//        ghost.draw(g);
//        x++;
//        count++;
    }
    //if shape == null
        //draw rect


    public enum PremadeCharacter {
        ALIEN,
        DOG,
    }

}
