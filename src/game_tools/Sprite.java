package game_tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class Sprite {
	public BufferedImage image;
	public boolean needImage = true;
	public boolean gotImage = false;

	public Sprite(String imageLocation) {
		if(imageLocation.charAt(0) != '/')
			loadImage("/"+imageLocation);
		else 
			loadImage(imageLocation);
	}
	
	public Sprite(BufferedImage img) {
		image = img;
	}

	void loadImage(String imageFile) {
		if (needImage) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream(imageFile));
				gotImage = true;
			} catch (Exception e) {
				System.err.println("Failed to load Image: "  + imageFile);
				e.printStackTrace();
			}
			needImage = false;
		}
	}

	public void draw(Graphics g, int x, int y, int width, int height) {
		if (gotImage) {
			g.drawImage(image, x, y, width, height, null);
		} else {
			g.setColor(Color.BLUE);
			g.fillRect(x, y, width, height);
			g.setColor(Color.WHITE);
			g.drawString("image failed", x, y+height/2);
		}
	}
	private Path getUsersProjectRootDirectory() {
	    String envRootDir = System.getProperty("user.dir");
	    Path rootDir = Paths.get(".").normalize().toAbsolutePath();
	    if ( rootDir.startsWith(envRootDir) ) {
	        return rootDir;
	    } else {
	        throw new RuntimeException("Root dir not found in user directory.");
	    }
	}

}
