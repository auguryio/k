package com.elvarg.game.definition;

/**
 * Represents the definition of an object spawn.
 * @author Professor Oak
 *
 */
public class ObjectSpawnDefinition extends DefaultSpawnDefinition {
	
	private int face = 0;
	private int type = 10;
	
	public int getFace() {
		return face;
	}
	
	public int getType() {
		return type;
	}
}
