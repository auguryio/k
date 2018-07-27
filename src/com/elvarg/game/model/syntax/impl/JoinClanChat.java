package com.elvarg.game.model.syntax.impl;

import com.elvarg.game.content.clan.ClanChatManager;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.syntax.EnterSyntax;

public class JoinClanChat implements EnterSyntax {
	
	@Override
	public void handleSyntax(Player player, String input) {
		ClanChatManager.join(player, input);
	}

	@Override
	public void handleSyntax(Player player, int input) {
		
	}
}
