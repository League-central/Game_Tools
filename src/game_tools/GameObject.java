package game_tools;

import java.awt.Color;
import java.awt.Graphics;

public class GameObject {
	Location location;
	Animation animation;
	int width;
	int height;
	boolean hasCircleCollider;
	
	GameObject(int x, int y, int width, int height){
		location = new Location(x,y);
		this.width = width;
		this.height = height;
	}
	
	GameObject(int x, int y, int width, int height, boolean hasCircleCollider){
		this(x,y,width,height);
		this.hasCircleCollider = hasCircleCollider;
	}
	
	void draw(Graphics g) {
		if(animation != null) animation.draw(g, location.x, location.y, width, height);
		else {
			g.setColor(Color.RED);
			g.fillRect(location.x, location.y, width, height);
		}
	}
	
	boolean collidesWith(GameObject other) {
		if(hasCircleCollider) {
			Location thisCenter = new Location(location.x + (width / 2), location.y + (height / 2));
			Location otherCenter = new Location(other.location.x + (other.width / 2), other.location.y + (other.height / 2));
			double dist = thisCenter.getDistanceTo(otherCenter);
			double thisSize = (width+height) / 4;
			double otherSize = (other.width+other.height) / 4;
			if(dist < thisSize + otherSize) return true;
			else return false;
		}
		else {
			boolean selfCloseOnLeft = location.x < other.location.x && location.x + width > other.location.x;
			boolean selfCloseOnRight = location.x > other.location.x && location.x - other.width < other.location.x;
			boolean selfCloseOnTop = location.y < other.location.y && location.y + height > other.location.y;
			boolean selfCloseOnBottom = location.y > other.location.y && location.y - other.height < other.location.y;
			if((selfCloseOnLeft || selfCloseOnRight) && (selfCloseOnTop || selfCloseOnBottom)) return true; 
			else return false;
		}
	}
	
	boolean overlapsPoint(int x, int y) {
		Location center = new Location(location.x + (width / 2), location.y + (height / 2));
		int dist = (int) center.getDistanceTo(x, y);
		
		if (dist < width / 2)
			return true;
		else
			return false;
	}
}
