package com.elvarg.game.model.movement;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Action;
import com.elvarg.game.model.Position;

/**
 * This class handles the execution of an action
 * once we're close enough to the given position.
 * Used for things such as clicking on entities.
 */

public class WalkToAction {
	
	/**
	 * The WalkToTask constructor.
	 * @param entity			The associated game character.
	 * @param destination		The destination the game character will move to.
	 * @param finalizedTask		The task a player must execute upon reaching said destination.
	 */
	public WalkToAction(Player entity, Position destination, int distance, Action finalizedTask) {
		this.player = entity;
		this.destination = destination;
		this.finalizedAction = finalizedTask;
		this.distance = distance;
	}

	private int distance = -1;

	/**
	 * The associated game character.
	 */
	private final Player player;

	/**
	 * The destination the game character will move to.
	 */
	private Position destination;

	/**
	 * The action a player must execute upon reaching said destination.
	 */
	private final Action finalizedAction;

	/**
	 * Executes the action if distance is correct
	 */
	public void sequence() {
		if(player == null)
			return;
		if(!player.isRegistered()) {
			player.setWalkToTask(null);
			return;
		}
		if(player.busy() || destination == null || player.getMovementQueue().getMovementStatus() == MovementStatus.DISABLED) {
			player.setWalkToTask(null);
			return;
		}
		if(player.getPosition().getDistance(destination) <= distance) {
			finalizedAction.execute();
			player.setEntityInteraction(null);
			player.setWalkToTask(null);
		}
	}
}
