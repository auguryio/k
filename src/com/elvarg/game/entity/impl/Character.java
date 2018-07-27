package com.elvarg.game.entity.impl;


import com.elvarg.game.content.combat.Combat;
import com.elvarg.game.content.combat.CombatType;
import com.elvarg.game.content.combat.hit.HitDamage;
import com.elvarg.game.entity.Entity;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Direction;
import com.elvarg.game.model.Flag;
import com.elvarg.game.model.Graphic;
import com.elvarg.game.model.Locations.Location;
import com.elvarg.game.model.NodeType;
import com.elvarg.game.model.Position;
import com.elvarg.game.model.UpdateFlag;
import com.elvarg.game.model.movement.MovementQueue;
import com.elvarg.util.Stopwatch;

/**
 * A player or NPC
 * @author Swiffy
 */

public abstract class Character extends Entity {

	/**
	 * Constructs this character/entity
	 * @param position
	 */
	public Character(NodeType type, Position position) {
		super(type, position);
	}
	
	/**
	 * An abstract method used for handling actions
	 * once this entity has been added to the world.
	 */
	public abstract void onAdd();
	
	/**
	 * An abstract method used for handling actions
	 * once this entity has been removed from the world.
	 */
	public abstract void onRemove();

	/**
	 * Teleports the character to a target location
	 * @param teleportTarget
	 * @return
	 */
	public Character moveTo(Position teleportTarget) {
		getMovementQueue().reset();
		setPosition(teleportTarget.copy());
		setNeedsPlacement(true);
		setResetMovementQueue(true);
		setEntityInteraction(null);
		if(isPlayer()) { getMovementQueue().handleRegionChange(getAsPlayer()); }
		return this;
	}
	
	/**
	 * Resets all flags related to updating.
	 */
	public void resetUpdating() {
		getUpdateFlag().reset();
		walkingDirection = Direction.NONE;
		runningDirection = Direction.NONE;
		needsPlacement = false;
		resetMovementQueue = false;
		forcedChat = null;
		interactingEntity = null;
		positionToFace = null;
		animation = null;
		graphic = null;
	}

	public Character forceChat(String message) {
		setForcedChat(message);
		getUpdateFlag().flag(Flag.FORCED_CHAT);
		return this;
	}

	public Character setEntityInteraction(Entity entity) {
		this.interactingEntity = entity;
		getUpdateFlag().flag(Flag.ENTITY_INTERACTION);
		return this;
	}

	@Override
	public void performAnimation(Animation animation) {
		if(this.animation != null && animation != null) {
			if(this.animation.getPriority().ordinal() > animation.getPriority().ordinal()) {
				return;
			}
		}

		this.animation = animation;
		getUpdateFlag().flag(Flag.ANIMATION);
	}

	@Override
	public void performGraphic(Graphic graphic) {
		if(this.graphic != null && graphic != null) {
			if(this.graphic.getPriority().ordinal() > graphic.getPriority().ordinal()) {
				return;
			}
		}

		this.graphic = graphic;
		getUpdateFlag().flag(Flag.GRAPHIC);
	}

	/*
	 * Fields
	 */

	private final Combat combat = new Combat(this);
	private final MovementQueue movementQueue = new MovementQueue(this);
	private String forcedChat;
	private Direction walkingDirection = Direction.NONE, runningDirection = Direction.NONE;
	private Stopwatch lastCombat = new Stopwatch();
	private UpdateFlag updateFlag = new UpdateFlag();
	private Location location = Location.DEFAULT;
	private Position positionToFace;
	private Animation animation;
	private Graphic graphic;
	private Entity interactingEntity;
	public Position singlePlayerPositionFacing;
	private int npcTransformationId = -1;
	private int poisonDamage;
	private boolean[] prayerActive = new boolean[30];
	private boolean resetMovementQueue;
	private boolean needsPlacement;
	private boolean untargetable;
	private boolean hasVengeance;
	private int specialPercentage = 100;
	private boolean specialActivated;
	private boolean recoveringSpecialAttack;

	private HitDamage primaryHit;
	private HitDamage secondaryHit;
	public abstract Character setHitpoints(int hitpoints);
	public abstract void appendDeath();
	public abstract void heal(int damage);
	public abstract int getHitpoints();
	public abstract int getBaseAttack(CombatType type);
	public abstract int getBaseDefence(CombatType type);
	public abstract int getBaseAttackSpeed();
	public abstract int getAttackAnim();
	public abstract int getBlockAnim();
	
	/**
	 * Is this entity registered.
	 */
	private boolean registered;

	/*
	 * Getters and setters
	 * Also contains methods.
	 */

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Graphic getGraphic() {
		return graphic;
	}

	public Animation getAnimation() {
		return animation;
	}

	/**
	 * @return the lastCombat
	 */
	public Stopwatch getLastCombat() {
		return lastCombat;
	}

	public int getAndDecrementPoisonDamage() {
		return poisonDamage--;
	}

	public int getPoisonDamage() {
		return poisonDamage;
	}

	public void setPoisonDamage(int poisonDamage) {
		this.poisonDamage = poisonDamage;
	}

	public boolean isPoisoned() {
		return poisonDamage > 0;
	}

	public Position getPositionToFace() {
		return positionToFace;
	}

	public Character setPositionToFace(Position positionToFace) {
		this.positionToFace = positionToFace;
		getUpdateFlag().flag(Flag.FACE_POSITION);
		return this;
	}

	public UpdateFlag getUpdateFlag() {
		return updateFlag;
	}

	public MovementQueue getMovementQueue() {
		return movementQueue;
	}

	public Combat getCombat() {
		return combat;
	}

	public Entity getInteractingEntity() {
		return interactingEntity;
	}

	public void setDirection(Direction direction) {
		int[] directionDeltas = direction.getDirectionDelta();
		setPositionToFace(getPosition().copy().add(directionDeltas[0], directionDeltas[1]));
	}

	public String getForcedChat() {
		return forcedChat;
	}

	public Character setForcedChat(String forcedChat) {
		this.forcedChat = forcedChat;
		return this;
	}

	public boolean[] getPrayerActive() {
		return prayerActive;
	}

	public Character setPrayerActive(boolean[] prayerActive) {
		this.prayerActive = prayerActive;
		return this;
	}

	public Character setPrayerActive(int id, boolean prayerActive) {
		this.prayerActive[id] = prayerActive;
		return this;
	}

	public int getNpcTransformationId() {
		return npcTransformationId;
	}

	public Character setNpcTransformationId(int npcTransformationId) {
		this.npcTransformationId = npcTransformationId;
		getUpdateFlag().flag(isPlayer() ? Flag.PLAYER_APPEARANCE : Flag.NPC_APPEARANCE);
		return this;
	}

	public HitDamage decrementHealth(HitDamage hit) {
		if (getHitpoints() <= 0)
			return hit;
		if(hit.getDamage() > getHitpoints())
			hit.setDamage(getHitpoints());
		if(hit.getDamage() < 0)
			hit.setDamage(0);
		int outcome = getHitpoints() - hit.getDamage();
		if (outcome < 0)
			outcome = 0;
		setHitpoints(outcome);
		return hit;
	}

	/**
	 * Get the primary hit for this entity.
	 * 
	 * @return the primaryHit.
	 */
	public HitDamage getPrimaryHit() {
		return primaryHit;
	}
	
	public void setPrimaryHit(HitDamage hit) {
		this.primaryHit = hit;
	}
	
	public void setSecondaryHit(HitDamage hit) {
		this.secondaryHit = hit;
	}

	/**
	 * Get the secondary hit for this entity.
	 * 
	 * @return the secondaryHit.
	 */
	public HitDamage getSecondaryHit() {
		return secondaryHit;
	}
	
	public void setWalkingDirection(Direction walkDirection) {
		this.walkingDirection = walkDirection;
	}
	
	public void setRunningDirection(Direction runDirection) {
		this.runningDirection = runDirection;
	}
	
	public Direction getWalkingDirection() {
		return walkingDirection;
	}

	public Direction getRunningDirection() {
		return runningDirection;
	}

	/**
	 * Determines if this character needs to reset their movement queue.
	 *
	 * @return {@code true} if this character needs to reset their movement
	 *         queue, {@code false} otherwise.
	 */
	public final boolean isResetMovementQueue() {
		return resetMovementQueue;
	}

	/**
	 * Gets if this entity is registered.
	 * 
	 * @return the unregistered.
	 */
	public boolean isRegistered() {
		return registered;
	}

	/**
	 * Sets if this entity is registered,
	 * 
	 * @param unregistered
	 *            the unregistered to set.
	 */
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	/**
	 * Sets the value for {@link CharacterNode#resetMovementQueue}.
	 *
	 * @param resetMovementQueue
	 *            the new value to set.
	 */
	public final void setResetMovementQueue(boolean resetMovementQueue) {
		this.resetMovementQueue = resetMovementQueue;
	}

	public void setNeedsPlacement(boolean needsPlacement) {
		this.needsPlacement = needsPlacement;
	}

	public boolean isNeedsPlacement() {
		return needsPlacement;
	}

	public boolean hasVengeance() {
		return hasVengeance;
	}

	public void setHasVengeance(boolean hasVengeance) {
		this.hasVengeance = hasVengeance;
	}

	public boolean isSpecialActivated() {
		return specialActivated;
	}

	public void setSpecialActivated(boolean specialActivated) {
		this.specialActivated = specialActivated;
	}

	public int getSpecialPercentage() {
		return specialPercentage;
	}

	public void setSpecialPercentage(int specialPercentage) {
		this.specialPercentage = specialPercentage;
	}

	public void decrementSpecialPercentage(int drainAmount) {
		this.specialPercentage -= drainAmount;

		if (specialPercentage < 0) {
			specialPercentage = 0;
		}
	}

	public void incrementSpecialPercentage(int gainAmount) {
		this.specialPercentage += gainAmount;

		if (specialPercentage > 100) {
			specialPercentage = 100;
		}
	}

	public boolean isRecoveringSpecialAttack() {
		return recoveringSpecialAttack;
	}

	public void setRecoveringSpecialAttack(boolean recoveringSpecialAttack) {
		this.recoveringSpecialAttack = recoveringSpecialAttack;
	}


	public boolean isUntargetable() {
		return untargetable;
	}

	public void setUntargetable(boolean untargetable) {
		this.untargetable = untargetable;
	}
	
	public boolean inDungeon() {
		return false;
	}
}