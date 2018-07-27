package com.elvarg.net.packet.impl;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;

public class PlayerInactivePacketListener implements PacketListener {

	//CALLED EVERY 3 MINUTES OF INACTIVITY
	
	@Override
	public void handleMessage(Player player, Packet packet) {
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
	}
}
