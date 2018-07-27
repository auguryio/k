package com.elvarg.game.model;

/**
 * Represents an area.
 * @author Professor Oak
 */
public class Area {

	private final int x;
	private final int x2;
	private final int y;
	private final int y2;
	
	public Area(int x, int x2, int y, int y2) {
		this.x = x;
		this.x2 = x2;
		this.y = y;
		this.y2 = y2;
	}

	public int getX() {
		return x;
	}

	public int getX2() {
		return x2;
	}

	public int getY() {
		return y;
	}

	public int getY2() {
		return y2;
	}
	
	public boolean inBounds(Position p) {
		return p.getX() >= x && p.getX() <= x2 && p.getY() >= y && p.getY() <= y2;
	}
}
