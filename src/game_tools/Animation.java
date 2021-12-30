package game_tools;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class Animation {
	List<Sprite> spritesList = new ArrayList<>();
	ArrayList<ArrayList<Sprite>> spriteSheet = new ArrayList<ArrayList<Sprite>>();
	int currentFrame = 0;
	int frameRate = 2;
	private int count = 0;

	public Animation() {
		this.spritesList = new ArrayList<>();
	}

	Animation(Sprite... sprites) {
		this.spritesList = new ArrayList<>(Arrays.asList(sprites));
	}

	public void add(Sprite sprite) {
		this.spritesList.add(sprite);
	}

	void remove(int index) {
		this.spritesList.remove(index);
	}

	void next() {
		currentFrame++;
		currentFrame %= spritesList.size();
	}

	void loadSpriteSheet(String fileName, int frameCountWidth, int frameCountHeight) {
		BufferedImage img = loadImage(fileName);
		int xSize = (img.getWidth() / 4);
		int ySize = (img.getHeight() / 6);
		ArrayList<ArrayList<Sprite>> sheet = new ArrayList<ArrayList<Sprite>>();
		for (int i = 0; i < frameCountHeight; i++) {
			ArrayList<Sprite> row = new ArrayList<Sprite>();
			for (int k = 0; k < frameCountWidth; k++) {
				row.add(new Sprite(img.getSubimage(i * xSize, k * ySize, xSize, ySize)));
			}
			sheet.add(row);
		}
		spriteSheet = sheet;
	}
	
	void setAnimationFromSheet(int animationNumber) {
		spritesList = spriteSheet.get(animationNumber);
		currentFrame = 0;
	}

	BufferedImage loadImage(String imageFile) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imageFile));
		} catch (Exception e) {
			System.err.println("Failed to load Image: " + imageFile);
			e.printStackTrace();
		}
		return image;
	}

	public void draw(Graphics g, int x, int y, int width, int height) {
		if (spritesList != null && !spritesList.isEmpty()) {
			spritesList.get(currentFrame).draw(g, x, y, width, height);
			count++;
			if (count % frameRate == 0) {
				next();
			}
		}
	}
}
