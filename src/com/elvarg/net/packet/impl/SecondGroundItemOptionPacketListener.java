package com.elvarg.net.packet.impl;

import java.util.Optional;

import com.elvarg.game.content.skill.skillable.impl.Firemaking;
import com.elvarg.game.content.skill.skillable.impl.Firemaking.LightableLog;
import com.elvarg.game.entity.impl.grounditem.ItemOnGround;
import com.elvarg.game.entity.impl.grounditem.ItemOnGroundManager;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Action;
import com.elvarg.game.model.Position;
import com.elvarg.game.model.movement.WalkToAction;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;

/**
 * This packet is received when a player
 * clicks on the second option on a ground item.
 * An example being the "light" option on logs that 
 * are on the ground.
 * 
 * @author Professor Oak
 */

public class SecondGroundItemOptionPacketListener implements PacketListener {

	@Override
	public void handleMessage(final Player player, Packet packet) {
		final int y = packet.readLEShort();
		final int itemId = packet.readShort();
		final int x = packet.readLEShort();
		final Position position = new Position(x, y, player.getPosition().getZ());

		if(player == null || player.getHitpoints() <= 0) {
			return;
		}		
		
		//Stop skilling..
		player.getSkillManager().stopSkillable();
		
		if(!player.getLastItemPickup().elapsed(300))
			return;
		if(player.busy())
			return;
		
		player.setWalkToTask(new WalkToAction(player, position, 1, new Action() {
			@Override
			public void execute() {
				//Make sure distance isn't way off..
				if (Math.abs(player.getPosition().getX() - x) > 25 || Math.abs(player.getPosition().getY() - y) > 25) {
					player.getMovementQueue().reset();
					return;
				}
				
				//Get ground item..
				Optional<ItemOnGround> item = ItemOnGroundManager.getGroundItem(Optional.of(player.getUsername()), itemId, position);
				if(item.isPresent()) {
					//Handle it..
					
					/** FIREMAKING **/
					Optional<LightableLog> log = LightableLog.getForItem(item.get().getItem().getId());
					if(log.isPresent()) {
						player.getSkillManager().startSkillable(new Firemaking(log.get(), item.get()));
						return;
					}
				}
			}
		}));
	}
}
