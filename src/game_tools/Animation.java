package game_tools;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Animation {
	List<Sprite> spritesList = new ArrayList<>();
	int currentFrame = 0;
	int frameRate = 2;
	private int count = 0;

	public Animation() {
		this.spritesList = new ArrayList<>();
	}

	Animation(Sprite... sprites) {
		this.spritesList = new ArrayList<>(Arrays.asList(sprites));
	}

	Animation(String... imageFileLocations) {
		this.spritesList = new ArrayList<>(Arrays.asList(
				Arrays.stream(imageFileLocations).map(Sprite::new).toArray(Sprite[]::new)
		));
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
