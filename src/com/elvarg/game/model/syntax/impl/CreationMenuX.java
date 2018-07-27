package com.elvarg.game.model.syntax.impl;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.syntax.EnterSyntax;

public class CreationMenuX implements EnterSyntax {
	
	private final int item;
	public CreationMenuX(int item) {
		this.item = item;
	}

	@Override
	public void handleSyntax(Player player, String input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSyntax(Player player, int input) {
		if(input <= 0 || input > Integer.MAX_VALUE) {
			return;
		}
		player.getPacketSender().sendInterfaceRemoval();
		if(player.getCreationMenu().isPresent()) {
			player.getCreationMenu().get().getAction().execute(item, input);
		}
	}

}
