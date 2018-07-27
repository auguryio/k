package com.elvarg.net.packet.impl;

import java.util.Optional;

import com.elvarg.Server;
import com.elvarg.game.content.Obelisks;
import com.elvarg.game.content.skill.skillable.impl.Smithing;
import com.elvarg.game.content.skill.skillable.impl.Smithing.Bar;
import com.elvarg.game.content.skill.skillable.impl.Smithing.EquipmentMaking;
import com.elvarg.game.content.skill.skillable.impl.Thieving.StallThieving;
import com.elvarg.game.definition.ObjectDefinition;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.object.MapObjects;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Action;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.ForceMovement;
import com.elvarg.game.model.Graphic;
import com.elvarg.game.model.Locations.Location;
import com.elvarg.game.model.MagicSpellbook;
import com.elvarg.game.model.Position;
import com.elvarg.game.model.dialogue.DialogueManager;
import com.elvarg.game.model.dialogue.DialogueOptions;
import com.elvarg.game.model.movement.WalkToAction;
import com.elvarg.game.task.Task;
import com.elvarg.game.task.TaskManager;
import com.elvarg.game.task.impl.ForceMovementTask;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.util.ObjectIdentifiers;

/**
 * This packet listener is called when a player clicked
 * on a game object.
 * 
 * @author relex lawl
 */

public class ObjectActionPacketListener extends ObjectIdentifiers implements PacketListener {

	/**
	 * Handles the first click option on an object.
	 * @param player		The player that clicked on the object.
	 * @param packet		The packet containing the object's information.
	 */
	private static void firstClick(final Player player, Packet packet) {
		final int x = packet.readLEShortA();
		final int id = packet.readUnsignedShort();
		final int y = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final Optional<GameObject> object = MapObjects.get(id, position);
		//Make sure the object actually exists in the region...
		if(!object.isPresent()) {
			Server.getLogger().info("Object with id "+id+" does not exist!");
			return;
		}

		//Get object definition
		final ObjectDefinition def = ObjectDefinition.forId(id);
		if(def == null) {
			Server.getLogger().info("ObjectDefinition for object "+id+" is null.");
			return;
		}

		//Calculate object size...
		final int size = (def.getSizeX() + def.getSizeY()) - 1;

		//Face object..
		player.setPositionToFace(position);

		player.setWalkToTask(new WalkToAction(player, position, size, new Action() {
			@Override
			public void execute() {
				//Skills..
				if(player.getSkillManager().startSkillable(object.get())) {
					return;
				}

				//Wilderness obelisks
				if(player.getLocation() == Location.WILDERNESS) {
					if(Obelisks.activate(id)) {
						return;
					}
				}

				switch(id) {
				case ANVIL:
					EquipmentMaking.openInterface(player);
					break;
					
				case BANK_CHEST:
					player.getBank(player.getCurrentBankTab()).open();
					break;

							
				case LEVER_38:
				player.performAnimation(new Animation(2140));
				player.performGraphic(new Graphic(308));
				TaskManager.submit(new Task(2, player, false) {
				@Override
				protected void execute() {
					player.moveTo(new Position(2539, 4712));
				player.getPacketSender().sendMessage("You are teleported to the Wilderness.");
				stop();
			 }
	 });
				break;
				case LEVER_39:
				player.performAnimation(new Animation(2140));
				player.performGraphic(new Graphic(308));
				TaskManager.submit(new Task(2, player, false) {
				@Override
				protected void execute() {
					player.moveTo(new Position(3090, 3956));			
				stop();
				}
			});
				break;

				case DITCH_PORTAL:
					player.getPacketSender().sendMessage("You are teleported to the Wilderness ditch.");
					player.moveTo(new Position(3087, 3520));
					break;

				case WILDERNESS_DITCH:
					player.getMovementQueue().reset();
					if(player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
						final Position crossDitch = new Position(0, player.getPosition().getY() < 3522 ? 3 : -3);
						TaskManager.submit(new ForceMovementTask(player, 3, new ForceMovement(player.getPosition().copy(), crossDitch, 0, 70, crossDitch.getY() == 3 ? 0 : 2, 6132)));
						player.getClickDelay().reset();
					}
					break;

				case MAGICAL_ALTAR:
					DialogueManager.start(player, 8);
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player, int option) {
							switch(option) {
							case 1: //Normal spellbook option
								player.getPacketSender().sendInterfaceRemoval();
								MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL);
								break;
							case 2: //Ancient spellbook option
								player.getPacketSender().sendInterfaceRemoval();
								MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT);
								break;
							case 3: //Lunar spellbook option
								player.getPacketSender().sendInterfaceRemoval();
								MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR);
								break;
							case 4: //Cancel option
								player.getPacketSender().sendInterfaceRemoval();
								break;
							}
						}
					});				
					break;

				case ORNATE_REJUVENATION_POOL:
					player.getPacketSender().sendMessage("You feel slightly renewed.");
					player.performGraphic(new Graphic(683));
					player.resetAttributes();
					break;

				}
			}
		}));
	}

	/**
	 * Handles the second click option on an object.
	 * @param player		The player that clicked on the object.
	 * @param packet		The packet containing the object's information.
	 */
	private static void secondClick(final Player player, Packet packet) {
		final int id = packet.readLEShortA();
		final int y = packet.readLEShort();
		final int x = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final Optional<GameObject> object = MapObjects.get(id, position);

		//Make sure the object actually exists in the region...
		if(!object.isPresent()) {
			Server.getLogger().info("Object with id "+id+" does not exist!");
			return;
		}

		//Get object definition
		final ObjectDefinition def = ObjectDefinition.forId(id);
		if(def == null) {
			Server.getLogger().info("ObjectDefinition for object "+id+" is null.");
			return;
		}

		//Calculate object size...
		final int size = (def.getSizeX() + def.getSizeY()) - 1;

		//Face object..
		player.setPositionToFace(position);

		player.setWalkToTask(new WalkToAction(player, position, size, new Action() {
			public void execute() {
				//Check thieving..
				if(StallThieving.init(player, object.get())) {
					return;
				}
				switch(id) {
				case FURNACE_18:
					for(Bar bar : Smithing.Bar.values()) {
						player.getPacketSender().sendInterfaceModel(bar.getFrame(), bar.getBar(), 150);
					}
					player.getPacketSender().sendChatboxInterface(2400);
					break;
				case BANK_CHEST:
				case BANK:

				case BANK_BOOTH:
					player.getBank(player.getCurrentBankTab()).open();
					break;
				case MAGICAL_ALTAR:
					player.getPacketSender().sendInterfaceRemoval();
					MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL);
					break;
				}
			}
		}));
	}

	/**
	 * Handles the third click option on an object.
	 * @param player		The player that clicked on the object.
	 * @param packet		The packet containing the object's information.
	 */
	private static void thirdClick(Player player, Packet packet) {
		final int x = packet.readLEShort();
		final int y = packet.readShort();
		final int id = packet.readLEShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final Optional<GameObject> object = MapObjects.get(id, position);

		//Make sure the object actually exists in the region...
		if(!object.isPresent()) {
			Server.getLogger().info("Object with id "+id+" does not exist!");
			return;
		}

		//Get object definition
		final ObjectDefinition def = ObjectDefinition.forId(id);
		if(def == null) {
			Server.getLogger().info("ObjectDefinition for object "+id+" is null.");
			return;
		}

		//Calculate object size...
		final int size = (def.getSizeX() + def.getSizeY()) - 1;

		//Face object..
		player.setPositionToFace(position);

		player.setWalkToTask(new WalkToAction(player, position, size, new Action() {
			public void execute() {
				switch(id) {
				case MAGICAL_ALTAR:
					player.getPacketSender().sendInterfaceRemoval();
					MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT);
					break;
				}
			}
		}));
	}

	/**
	 * Handles the fourth click option on an object.
	 * @param player		The player that clicked on the object.
	 * @param packet		The packet containing the object's information.
	 */
	private static void fourthClick(Player player, Packet packet) {
		final int x = packet.readLEShortA();
		final int id = packet.readUnsignedShortA();
		final int y = packet.readLEShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final Optional<GameObject> object = MapObjects.get(id, position);

		//Make sure the object actually exists in the region...
		if(!object.isPresent()) {
			Server.getLogger().info("Object with id "+id+" does not exist!");
			return;
		}

		//Get object definition
		final ObjectDefinition def = ObjectDefinition.forId(id);
		if(def == null) {
			Server.getLogger().info("ObjectDefinition for object "+id+" is null.");
			return;
		}

		//Calculate object size...
		final int size = (def.getSizeX() + def.getSizeY()) - 1;

		//Face object..
		player.setPositionToFace(position);

		player.setWalkToTask(new WalkToAction(player, position, size, new Action() {
			public void execute() {
				switch(id) {
				case MAGICAL_ALTAR:
					player.getPacketSender().sendInterfaceRemoval();
					MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR);
					break;
				}
			}
		}));
	}

	/**
	 * Handles the fifth click option on an object.
	 * @param player		The player that clicked on the object.
	 * @param packet		The packet containing the object's information.
	 */
	private static void fifthClick(final Player player, Packet packet) {

	}

	@Override
	public void handleMessage(Player player, Packet packet) {

		if(player == null || player.getHitpoints() <= 0) {
			return;
		}

		//Make sure we aren't doing something else..
		if(player.busy()) {
			return;
		}

		switch (packet.getOpcode()) {
		case PacketConstants.OBJECT_FIRST_CLICK_OPCODE:
			firstClick(player, packet);
			break;
		case PacketConstants.OBJECT_SECOND_CLICK_OPCODE:
			secondClick(player, packet);
			break;
		case PacketConstants.OBJECT_THIRD_CLICK_OPCODE:
			thirdClick(player, packet);
			break;
		case PacketConstants.OBJECT_FOURTH_CLICK_OPCODE:
			fourthClick(player, packet);
			break;
		case PacketConstants.OBJECT_FIFTH_CLICK_OPCODE:
			fifthClick(player, packet);
			break;
		}
	}
}
