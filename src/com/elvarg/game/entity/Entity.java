package com.elvarg.game.entity;

import java.util.Optional;

import com.elvarg.game.GameConstants;
import com.elvarg.game.entity.impl.grounditem.ItemOnGround;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Graphic;
import com.elvarg.game.model.NodeType;
import com.elvarg.game.model.Position;
import com.elvarg.game.model.region.InstancedRegion;

public abstract class Entity {

	/**
	 * The Entity constructor.
	 * @param position	The position the entity is currently in.
	 */
	public Entity(NodeType type, Position position) {
		this.position = position;
		this.type = type;
		this.lastKnownRegion = position;
	}
	
	/**
	 * The entity's type.
	 */
	private NodeType type;
	
	/**
	 * The entity's unique index.
	 */
	private int index;

	/**
	 * The entity's tile size.
	 */
	private int size = 1;

	/**
	 * The default position the entity is in.
	 */
	private Position position = GameConstants.DEFAULT_POSITION.copy();

	/**	
	 * The entity's first position in current map region.
	 */
	private Position lastKnownRegion;

	/**
	 * Gets this type.
	 * @return
	 */
	public NodeType getNodeType() {
		return type;
	}
	
	/**
	 * Gets the entity's unique index.
	 * @return	The entity's index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the entity's index.
	 * @param index		The value the entity's index will contain.
	 * @return			The Entity instance.
	 */
	public Entity setIndex(int index) {
		this.index = index;
		return this;
	}

	/**
	 * Gets this entity's first position upon entering their
	 * current map region.
	 * @return	The lastKnownRegion instance.
	 */
	public Position getLastKnownRegion() {
		return lastKnownRegion;
	}

	/**
	 * Sets the entity's current region's position.
	 * @param lastKnownRegion	The position in which the player first entered the current region.
	 * @return					The Entity instance.
	 */
	public Entity setLastKnownRegion(Position lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
		return this;
	}

	/**
	 * Sets the entity position
	 * @param position the world position
	 */
	public Entity setPosition(Position position) {
		this.position = position;
		return this;
	}

	/**
	 * Gets the entity position.
	 * @return the entity's world position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Performs an animation.
	 * @param animation	The animation to perform.
	 */
	public void performAnimation(Animation animation) {

	}

	/**
	 * Performs a graphic.
	 * @param graphic	The graphic to perform.
	 */
	public void performGraphic(Graphic graphic) {

	}

	/**
	 * gets the entity's tile size.
	 * @return	The size the entity occupies in the world.
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Sets the entity's tile size
	 * @return	The Entity instance.
	 */
	public Entity setSize(int size) {
		this.size = size;
		return this;
	}
	
	/**
	 * The character's current {@link InstancedRegion}.
	 */
	private Optional<InstancedRegion> instancedRegion = Optional.empty();
	
	public Optional<InstancedRegion> getInstancedRegion() {
		return instancedRegion;
	}
	
	public void setInstancedRegion(Optional<InstancedRegion> instancedRegion) {
		this.instancedRegion = instancedRegion;
	}

	
	public boolean isNpc() {
		return this instanceof NPC;
	}

	public boolean isPlayer() {
		return this instanceof Player;
	}

	public boolean isItemOnGround() {
		return this instanceof ItemOnGround;
	}
	
	public boolean isGameObject() {
		return this instanceof GameObject;
	}
	
	public Player getAsPlayer() {
		return ((Player)this);
	}

	public NPC getAsNpc() {
		return ((NPC)this);
	}
	
	public ItemOnGround getAsItem() {
		return ((ItemOnGround)this);
	}
}
