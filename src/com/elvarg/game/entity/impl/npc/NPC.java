package com.elvarg.game.entity.impl.npc;

import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.content.combat.CombatType;
import com.elvarg.game.content.combat.method.CombatMethod;
import com.elvarg.game.definition.NpcDefinition;
import com.elvarg.game.entity.impl.Character;
import com.elvarg.game.entity.impl.npc.NPCMovementCoordinator.CoordinateState;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.FacingDirection;
import com.elvarg.game.model.Flag;
import com.elvarg.game.model.Locations;
import com.elvarg.game.model.NodeType;
import com.elvarg.game.model.Position;
import com.elvarg.game.model.SecondsTimer;
import com.elvarg.game.task.TaskManager;
import com.elvarg.game.task.impl.NPCDeathTask;

/**
 * Represents a non-playable character, which players can interact with.
 * @author Professor Oak
 */

public class NPC extends Character {

	@Override
	public void onAdd() {
	}
	
	@Override
	public void onRemove() {
	}	
	
	/**
	 * The npc's movement coordinator.
	 * Handles random walking.
	 */
	private NPCMovementCoordinator movementCoordinator = new NPCMovementCoordinator(this);

	/**
	 * The npc's id.
	 */
	private final int id;

	/**
	 * The npc's current hitpoints.
	 */
	private int hitpoints;

	/**
	 * The npc's spawn position (default).
	 */
	private Position spawnPosition;

	/**
	 * The npc's head icon.
	 */
	private int headIcon = -1;

	/**
	 * The npc's current state.
	 * Is it dying?
	 */
	private boolean isDying;
	
	/**
	 * The npc's owner.
	 * 
	 * The only player who can see right-click
	 * actions on the npc.
	 */
	private Player owner;

	/**
	 * The npc's current state.
	 * Is it visible?
	 */
	private boolean visible = true;
	
	/**
	 * The npc's facing.
	 */
	private FacingDirection face = FacingDirection.NORTH;
	
	/**
	 * The npc's combat method, used
	 * for attacking.
	 */
	private CombatMethod combatMethod;
	
	/**
	 * The {@link SecondsTimer} where this npc is
	 * immune to attacks.
	 */
	private final SecondsTimer immunity = new SecondsTimer();

	/**
	 * Constructs a new npc.
	 * @param id		The npc id.
	 * @param position	The npc spawn (default) position.
	 */
	public NPC(int id, Position position) {
		super(NodeType.NPC, position);
		this.id = id;
		this.spawnPosition = position;
		
		if(getDefinition() == null) {
			setHitpoints(10);
		} else {
			setHitpoints(getDefinition().getHitpoints());
		}
		
		CombatFactory.assignCombatMethod(this);
	}

	/**
	 * Processes this npc.
	 */
	public void sequence() {

		//Only process the npc if they have properly been added 
		//to the game with a definition.
		if(getDefinition() != null) {
			
			//Handles random walk and retreating from fights
			getMovementQueue().sequence();
			movementCoordinator.sequence();
			
			//Handle combat
			getCombat().sequence();

			//Process locations
			Locations.sequence(this);

			//Process instanced region..
			if(getInstancedRegion().isPresent()) {
				getInstancedRegion().get().sequence(this);
			}
			
			//Regenerating health if needed, but only after 20 seconds of last attack.
			if(getCombat().getLastAttack().elapsed(20000) || movementCoordinator.getCoordinateState() == CoordinateState.RETREATING) {
				
				//We've been damaged.
				//Regenerate health.
				if(getDefinition().getHitpoints() > hitpoints) {
					setHitpoints(hitpoints + (int)(getDefinition().getHitpoints() * 0.1));
					if(hitpoints > getDefinition().getHitpoints()) {
						setHitpoints(getDefinition().getHitpoints());
					}
				}				
			}
		}
	}

	@Override
	public void appendDeath() {
		if(!isDying) {
			TaskManager.submit(new NPCDeathTask(this));
			isDying = true;
		}
	}

	@Override
	public int getHitpoints() {
		return hitpoints;
	}

	@Override
	public NPC setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
		if(this.hitpoints <= 0)
			appendDeath();
		return this;
	}

	@Override
	public void heal(int heal) {
		if ((this.hitpoints + heal) > getDefinition().getHitpoints()) {
			setHitpoints(getDefinition().getHitpoints());
			return;
		}
		setHitpoints(this.hitpoints + heal);
	}

	@Override
	public boolean isNpc() {
		return true;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof NPC && ((NPC)other).getIndex() == getIndex() && ((NPC)other).getId() == getId();
	}

	@Override
	public int getSize() {
		return getDefinition() == null ? 1 :
			getDefinition().getSize();
	}

	@Override
	public int getBaseAttack(CombatType type) {

		/*if(type == CombatType.RANGED) {
			return getDefinition().getRangedLevel();
		} else if(type == CombatType.MAGIC) {
			return getDefinition().getMagicLevel();
		}

		return getDefinition().getAttackLevel();*/
		return 1;
	}

	@Override
	public int getBaseDefence(CombatType type) {
		int base = 0;
		/*switch(type) {
		case MAGIC:
			base = getDefinition().getDefenceMage();
			break;
		case MELEE:
			base = getDefinition().getDefenceMelee();
			break;
		case RANGED:
			base = getDefinition().getDefenceRange();
			break;
		}*/
		return base;
	}

	@Override
	public int getBaseAttackSpeed() {
		return getDefinition().getAttackSpeed();
	}

	@Override
	public int getAttackAnim() {
		return getDefinition().getAttackAnim();
	}

	@Override
	public int getBlockAnim() {
		return getDefinition().getDefenceAnim();
	}


	/*
	 * Getters and setters
	 */

	public int getId() {
		if(getNpcTransformationId() != -1) {
			return getNpcTransformationId();
		}
		return id;
	}

	public void setHeadIcon(int headIcon) {
		this.headIcon = headIcon;
	//	getUpdateFlag().flag(Flag.NPC_APPEARANCE);
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setDying(boolean isDying) {
		this.isDying = isDying;
	}

	public boolean isDying() {
		return isDying;
	}
	
	public void setOwner(Player owner) {
		this.owner = owner;
	}
	
	public Player getOwner() {
		return owner;
	}

	public NPCMovementCoordinator getMovementCoordinator() {
		return movementCoordinator;
	}

	public NpcDefinition getDefinition() {
		return NpcDefinition.forId(id);
	}

	public Position getSpawnPosition() {
		return spawnPosition;
	}

	public int getHeadIcon() {
		return headIcon;
	}

	public CombatMethod getCombatMethod() {
		return combatMethod;
	}

	public void setCombatMethod(CombatMethod combatMethod) {
		this.combatMethod = combatMethod;
	}
	
	@Override
	public NPC clone() {
		return new NPC(getId(), getSpawnPosition());
	}

	public FacingDirection getFace() {
		return face;
	}

	public void setFace(FacingDirection face) {
		this.face = face;
	}

	public SecondsTimer getImmunity() {
		return immunity;
	}
}
