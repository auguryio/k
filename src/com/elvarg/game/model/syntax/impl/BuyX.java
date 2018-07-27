package com.elvarg.game.model.syntax.impl;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.PlayerStatus;
import com.elvarg.game.model.container.impl.shop.ShopManager;
import com.elvarg.game.model.syntax.EnterSyntax;

public class BuyX implements EnterSyntax {
	
	private final int slot, itemId;
	
	public BuyX(int itemId, int slot) {
		this.itemId = itemId;
		this.slot = slot;
	}
	
	@Override
	public void handleSyntax(Player player, String input) {

	}

	@Override
	public void handleSyntax(Player player, int input) {
		if (player.getStatus() == PlayerStatus.SHOPPING) {
			ShopManager.buyItem(player, slot, itemId, input);
		}
	}

}
