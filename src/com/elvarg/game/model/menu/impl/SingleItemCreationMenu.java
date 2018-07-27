package com.elvarg.game.model.menu.impl;

import com.elvarg.game.definition.ItemDefinition;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.menu.CreationMenu;
import com.elvarg.game.model.syntax.impl.CreationMenuX;

/**
 * Represents a sub class of {@link CreationMenu}.
 * 
 * This class is used to handle creation menus which
 * only display one item on the interface.
 * An example of this would be Cooking.
 * 
 * @author Professor Oak
 */
public class SingleItemCreationMenu extends CreationMenu {
		
	/**
	 * The item to display on the creation menu
	 * interface.
	 */
	private final int item;
	
	/**
	 * Creates a new {@link SingleItemCreationMenu} with the given data.
	 * 
	 * @param player
	 * @param title
	 * @param action
	 * @param item
	 */
	public SingleItemCreationMenu(Player player, int item, String title, CreationMenuAction action) {
		super(player, title, action);
		this.item = item;
	}

	/**
	 * Opens the interface for a singular creation menu.
	 */
	@Override
	public CreationMenu open() {
		getPlayer().getPacketSender().sendInterfaceModel(1746, item, 140);
		getPlayer().getPacketSender().sendString(2799, ItemDefinition.forId(item).getName());
		getPlayer().getPacketSender().sendString(2800, getTitle());
		getPlayer().getPacketSender().sendChatboxInterface(4429);
		return this;
	}

	/**
	 * Handles buttons related to the singular creation menu.
	 */
	@Override
	public boolean handleButton(int id) {
		switch(id) {
		case 2799: //Make 1
			getAction().execute(item, 1);
			return true;
		case 2798: //Make 5
			getAction().execute(item, 5);
			return true;
		case 1748: //Make X
			getPlayer().setEnterSyntax(new CreationMenuX(item));
			getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
			break;
		case 1747: //Make all
			getAction().execute(item, 28);
			return true;
		}
		return false;
	}

}
