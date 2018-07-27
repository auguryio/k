package com.elvarg.game.model.syntax.impl;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.container.impl.Bank;
import com.elvarg.game.model.syntax.EnterSyntax;

public class SearchBank implements EnterSyntax {
	
	@Override
	public void handleSyntax(Player player, String input) {
		Bank.search(player, input);
	}

	@Override
	public void handleSyntax(Player player, int input) {
		
	}

}
