package com.elvarg.net.packet.impl;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Flag;
import com.elvarg.game.model.ChatMessage.Message;
import com.elvarg.game.model.dialogue.DialogueManager;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.util.Misc;
import com.elvarg.util.PlayerPunishment;

/**
 * This packet listener manages the spoken text by a player.
 * 
 * @author Gabriel Hannason
 */

public class ChatPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int color = packet.readByte();
		int effect = packet.readByte();
		String text = packet.readString();
		if(text.length() <= 0) {
			return;
		}
		if(player == null) {
			return;
		}
		if(PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAddress())) {
			player.getPacketSender().sendMessage("You are muted and cannot chat.");
			return;
		}
		if(Misc.blockedWord(text)) {
			DialogueManager.sendStatement(player, "A word was blocked in your sentence. Please do not repeat it!");
			return;
		}
		player.getChatMessages().set(new Message(color, effect, text));
		player.getUpdateFlag().flag(Flag.CHAT);
	}

}
