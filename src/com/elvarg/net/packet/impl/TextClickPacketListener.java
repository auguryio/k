package com.elvarg.net.packet.impl;

import com.elvarg.game.content.clan.ClanChatManager;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.container.impl.Bank;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;

public class TextClickPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int frame = packet.readInt();
		int action = packet.readByte();
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
		if(Bank.handleButton(player, frame, action)) {
			return;
		}
		if(ClanChatManager.handleButton(player, frame, action)) {
			return;
		}
	}
}
