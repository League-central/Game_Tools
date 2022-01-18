package examples.game_character;

import game_tools.Game;
import game_tools.GameCharacter;
import game_tools.GameControlScene;

import java.awt.*;
import java.awt.event.KeyEvent;

public class GameCharacterExample implements GameControlScene {

    Game game = new Game();

    GameCharacter character1 = new GameCharacter();

    GameCharacter character2 = new GameCharacter(50, 50);

    GameCharacter character3 = new GameCharacter(100, 100, Color.BLUE);

    GameCharacter character4 = new GameCharacter(150, 150, 96, 214, 163);

    GameCharacter character5 = new GameCharacter(200, 200, 75,  75, "/game_tools/game_character_images/cat_face/cat-face.png");

    GameCharacter character6 = new GameCharacter(20, 400, GameCharacter.PremadeCharacter.ALIEN);

    GameCharacter character7;

    public GameCharacterExample() {
        //initialize character 7
        String[] animationImageLocations = {
                "/examples/flappy/bird0.png",
                "/examples/flappy/bird1.png",
                "/examples/flappy/bird2.png",
                "/examples/flappy/bird3.png",
                "/examples/flappy/bird4.png",
                "/examples/flappy/bird5.png",
        };
        character7 = new GameCharacter(275, 275, 100, 100, animationImageLocations);

        //start game
        game.setScene(this);
        game.start();
    }

    public static void main(String[] args) throws InterruptedException {
        GameCharacterExample example = new GameCharacterExample();
        //move character6
        for(int i=0; i<100; i++){
            Thread.sleep(20);
            example.character6.moveRight();
        }
    }

    @Override
    public void draw(Graphics g) {
        //background
        g.setColor(new Color(100, 150, 230));
        g.fillRect(0, 0, game.screenWidth, game.screenHeight);

        // draw character1
        character1.draw(g);

        //draw character2
        character2.draw(g);

        //draw character3
        character3.draw(g);

        //draw character4
        character4.draw(g);

        //draw character 5
        character5.draw(g);

        //draw character6
        character6.draw(g);

        //draw character6
        character7.draw(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

}
