package com.elvarg.net.packet.impl;

import com.elvarg.game.collision.RegionManager;
import com.elvarg.game.entity.impl.grounditem.ItemOnGroundManager;
import com.elvarg.game.entity.impl.npc.NpcAggression;
import com.elvarg.game.entity.impl.object.ObjectManager;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;


public class RegionChangePacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}

		if(player.isAllowRegionChangePacket()) {
			RegionManager.loadMapFiles(player.getPosition().getX(), player.getPosition().getY());
			//player.getPacketSender().deleteRegionalSpawns();
			ItemOnGroundManager.onRegionChange(player);
			ObjectManager.onRegionChange(player);
			player.getAggressionTolerance().start(NpcAggression.NPC_TOLERANCE_SECONDS); //Every 4 minutes, reset aggression for npcs in the region.
			player.setAllowRegionChangePacket(false);
		}
	}
}
