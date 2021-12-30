package game_tools;

public class Location {
	int x;
	int y;
	Location(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	double getDistanceTo(Location other) {
		return getDistanceTo(other.x, other.y);
	}
	double getDistanceTo(int x2, int y2) {
		double dist = Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2));
		return dist;
	}
}
