package com.elvarg.net.packet.impl;

import java.util.Optional;

import com.elvarg.game.definition.ItemDefinition;
import com.elvarg.game.entity.impl.grounditem.ItemOnGround;
import com.elvarg.game.entity.impl.grounditem.ItemOnGroundManager;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Action;
import com.elvarg.game.model.Position;
import com.elvarg.game.model.movement.WalkToAction;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;

/**
 * This packet listener is used to pick up ground items
 * that exist in the world.
 * 
 * @author relex lawl
 */

public class PickupItemPacketListener implements PacketListener {

	@Override
	public void handleMessage(final Player player, Packet packet) {
		final int y = packet.readLEShort();
		final int itemId = packet.readShort();
		final int x = packet.readLEShort();
		final Position position = new Position(x, y, player.getPosition().getZ());

		if(player == null || player.getHitpoints() <= 0) {
			return;
		}		
				
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

				//Check if we can hold it..
				if(!(player.getInventory().getFreeSlots() > 0 || (player.getInventory().getFreeSlots() == 0 && ItemDefinition.forId(itemId).isStackable() && player.getInventory().contains(itemId)))) {
					player.getInventory().full();
					return;
				}

				Optional<ItemOnGround> item = ItemOnGroundManager.getGroundItem(Optional.of(player.getUsername()), itemId, position);
				if(item.isPresent()) {
					if(player.getInventory().getAmount(item.get().getItem().getId()) + item.get().getItem().getAmount() > Integer.MAX_VALUE 
							|| player.getInventory().getAmount(item.get().getItem().getId()) + item.get().getItem().getAmount() <= 0) {
						player.getPacketSender().sendMessage("You cannot hold that amount of this item. Clear your inventory!");
						return;
					}
					ItemOnGroundManager.deregister(item.get());
					player.getInventory().add(item.get().getItem());
					player.getLastItemPickup().reset();
				}
			}
		}));
	}
}
