package com.elvarg.game.entity.impl.object;

import java.util.Optional;

import com.elvarg.game.World;
import com.elvarg.game.definition.ObjectDefinition;
import com.elvarg.game.entity.Entity;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Graphic;
import com.elvarg.game.model.NodeType;
import com.elvarg.game.model.Position;
import com.elvarg.game.model.region.InstancedRegion;

/**
 * This file manages a game object entity on the globe.
 * 
 * @author Relex lawl / iRageQuit2012
 *
 */

public class GameObject extends Entity {
	/**
	 * The object's id.
	 */
	private int id;
	
	/**
	 * The object's type (default=10).
	 */
	private int type = 10;
	
	/**
	 * The object's current direction to face.
	 */
	private int face;
	
	/**
	 * The {@link Player} which this {@link GameObject}
	 * was spawned for.
	 */
	private Optional<Player> spawnedFor = Optional.empty();

	/**
	 * GameObject constructor to call upon a new game object.
	 * @param id		The new object's id.
	 * @param position	The new object's position on the globe.
	 */
	public GameObject(int id, Position position) {
		super(NodeType.OBJECT, position);
		this.id = id;
	}
	
	/**
	 * GameObject constructor to call upon a new game object.
	 * @param id
	 * @param position
	 * @param state
	 */
	public GameObject(Optional<Player> spawnedFor, int id, Position position) {
		super(NodeType.OBJECT, position);
		this.id = id;
		this.spawnedFor = spawnedFor;
	}


	/**
	 * GameObject constructor to call upon a new game object.
	 * @param id		The new object's id.
	 * @param position	The new object's position on the globe.
	 * @param type		The new object's type.
	 */
	public GameObject(int id, Position position, int type) {
		super(NodeType.OBJECT, position);
		this.id = id;
		this.type = type;
	}

	/**
	 * GameObject constructor to call upon a new game object.
	 * @param id		The new object's id.
	 * @param position	The new object's position on the globe.
	 * @param type		The new object's type.
	 * @param face		The new object's facing position.
	 */
	public GameObject(int id, Position position, int type, int face) {
		super(NodeType.OBJECT, position);
		this.id = id;
		this.type = type;
		this.face = face;
	}

	/**
	 * Gets the object's id.
	 * @return id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the object's type.
	 * @return	type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the object's type.
	 * @param type	New type value to assign.
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Gets the object's current face direction.
	 * @return	face.
	 */
	public int getFace() {
		return face;
	}

	/**
	 * Sets the object's face direction.
	 * @param face	Face value to which object will face.
	 */
	public void setFace(int face) {
		this.face = face;
	}
	
	/**
	 * Gets the object's definition.
	 * @return	definition.
	 */
	public ObjectDefinition getDefinition() {
		return ObjectDefinition.forId(id);
	}
	
	/**
	 * Gets the player this object was spawned for.
	 * @return
	 */
	public Optional<Player> getSpawnedFor() {
		return spawnedFor;
	}
	
	/**
	 * Sets the player this object was spawned for.
	 * @param spawnedFor
	 * @return
	 */
	public GameObject setSpawnedFor(Optional<Player> spawnedFor) {
		this.spawnedFor = spawnedFor;
		return this;
	}

	@Override
	public void performAnimation(Animation animation) {
		for (Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if(player.getPosition().isWithinDistance(getPosition()))
				player.getPacketSender().sendObjectAnimation(this, animation);
		}
	}

	@Override
	public void performGraphic(Graphic graphic) {
		for (Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if (player.getPosition().isWithinDistance(getPosition()))
				player.getPacketSender().sendGraphic(graphic, getPosition());
		}
	}

	@Override
	public int getSize() {
		ObjectDefinition definition = getDefinition();
		if(definition == null)
			return 1;
		return (definition.getSizeX() + definition.getSizeY()) - 1;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof GameObject))
			return false;
		GameObject object = (GameObject)o;
		if(getSpawnedFor().isPresent()) {
			if(!object.getSpawnedFor().isPresent()) {
				return false;
			}
			if(!getSpawnedFor().get().equals(object.getSpawnedFor().get())) {
				return false;
			}
		}
		return object.getPosition().equals(getPosition()) 
				&& object.getId() == getId() 
				&& object.getFace() == getFace() 
				&& object.getType() == getType();
	}
	
	@Override
	public GameObject clone() {
		GameObject object = new GameObject(getId(), getPosition(), getType(), getFace());
		return object;
	}
}
