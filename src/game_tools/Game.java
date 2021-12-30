package game_tools;

import java.awt.Dimension;
import java.util.Random;

public class Game {
	static Random gen = new Random();
	GameFrame frame;
	public int screenWidth;
	public int screenHeight;

	public Game() {
		frame = new GameFrame();
	}
	
	public Game(String title) {
		frame = new GameFrame(title);
	}

	public void start() {
		frame.start();
	}

	public void setSize(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;
		frame.setSize(width, height);
	}

	public void setSize(Dimension d) {
		this.screenWidth = d.width;
		this.screenHeight = d.height;
		frame.setSize(d);
	}

	public void setScene(GameControlScene scene) {
		frame.setGameScene(scene);
	}

	public void setScene(GameScene scene) {
		frame.setGameScene(scene);
	}

	public void addController(Controller controller) {
		frame.addController(controller);
	}
	public GameFrame getFrame() {
		return this.frame;
	}
}
