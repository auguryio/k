package com.elvarg.game.definition;

import com.elvarg.game.model.Position;

/**
 * Represents a definition for a basic entity-spawn, such as
 * for an npc or object.
 * 
 * @author Professor Oak
 */
public class DefaultSpawnDefinition {
	
	private int id;
	private Position position;
	
	public int getId() {
		return id;
	}
	
	public Position getPosition() {
		return position;
	}
}
