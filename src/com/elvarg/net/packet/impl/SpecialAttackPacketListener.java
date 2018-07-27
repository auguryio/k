package com.elvarg.net.packet.impl;

import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;

/**
 * This packet listener handles the action when pressing
 * a special attack bar.
 * @author Professor Oak
 */

public class SpecialAttackPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		@SuppressWarnings("unused")
		int specialBarButton = packet.readInt();
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
		CombatSpecial.activate(player);	
	}
}
