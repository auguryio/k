package com.elvarg.game.model.menu.impl;

import com.elvarg.game.definition.ItemDefinition;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.menu.CreationMenu;
import com.elvarg.game.model.syntax.impl.CreationMenuX;

/**
 * Represents a sub class of {@link CreationMenu}.
 * 
 * This class is used to handle creation menus which
 * display four items on the interface.
 * 
 * @author Professor Oak
 */
public class QuardrupleItemCreationMenu extends CreationMenu {

	/**
	 * The items to display on the interface.
	 */
	private final int firstItem, secondItem, thirdItem, fourthItem;
	
	public QuardrupleItemCreationMenu(Player player, int firstItem, int secondItem, int thirdItem, int fourthItem, String title, CreationMenuAction action) {
		super(player, title, action);
		this.firstItem = firstItem;
		this.secondItem = secondItem;
		this.thirdItem = thirdItem;
		this.fourthItem = fourthItem;
	}

	@Override
	public CreationMenu open() {
		getPlayer().getPacketSender().sendString(8922, getTitle());
		
		getPlayer().getPacketSender().sendInterfaceModel(8902, firstItem, 170);
		getPlayer().getPacketSender().sendString(8909, ""+ItemDefinition.forId(firstItem).getName()+"");
		
		getPlayer().getPacketSender().sendInterfaceModel(8903, secondItem, 170);
		getPlayer().getPacketSender().sendString(8913, ""+ItemDefinition.forId(secondItem).getName()+"");
		
		getPlayer().getPacketSender().sendInterfaceModel(8904, thirdItem, 170);
		getPlayer().getPacketSender().sendString(8917, ""+ItemDefinition.forId(thirdItem).getName()+"");
		
		getPlayer().getPacketSender().sendInterfaceModel(8905, fourthItem, 170);
		getPlayer().getPacketSender().sendString(8921, ""+ItemDefinition.forId(fourthItem).getName()+"");
		
		getPlayer().getPacketSender().sendChatboxInterface(8899);
		return this;
	}

	@Override
	public boolean handleButton(int id) {
		switch(id) {
		case 8909:
			getAction().execute(firstItem, 1);
			return true;
		case 8908:
			getAction().execute(firstItem, 5);
			return true;
		case 8907:
			getAction().execute(firstItem, 10);
			return true;
		case 8906:
			getPlayer().setEnterSyntax(new CreationMenuX(firstItem));
			getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
			return true;
		case 8913:
			getAction().execute(secondItem, 1);
			return true;
		case 8912:
			getAction().execute(secondItem, 5);
			return true;
		case 8911:
			getAction().execute(secondItem, 10);
			return true;
		case 8910:
			getPlayer().setEnterSyntax(new CreationMenuX(secondItem));
			getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
			return true;
		case 8917:
			getAction().execute(thirdItem, 1);
			return true;
		case 8916:
			getAction().execute(thirdItem, 5);
			return true;
		case 8915:
			getAction().execute(thirdItem, 10);
			return true;
		case 8914:
			getPlayer().setEnterSyntax(new CreationMenuX(thirdItem));
			getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
			return true;
		case 8921:
			getAction().execute(fourthItem, 1);
			return true;
		case 8920:
			getAction().execute(fourthItem, 5);
			return true;
		case 8919:
			getAction().execute(fourthItem, 10);
			return true;
		case 8918:
			getPlayer().setEnterSyntax(new CreationMenuX(fourthItem));
			getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
			return true;
		}
		return false;
	}

}
