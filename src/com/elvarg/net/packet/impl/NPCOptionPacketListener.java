package com.elvarg.net.packet.impl;

import com.elvarg.game.World;
import com.elvarg.game.content.PetHandler;
import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.content.combat.bountyhunter.BountyHunter;
import com.elvarg.game.content.combat.magic.CombatSpell;
import com.elvarg.game.content.combat.magic.CombatSpells;
import com.elvarg.game.content.skill.skillable.impl.Fishing;
import com.elvarg.game.content.skill.skillable.impl.Fishing.FishingTool;
import com.elvarg.game.content.skill.skillable.impl.Thieving.Pickpocketing;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Action;
import com.elvarg.game.model.BrokenItem;
import com.elvarg.game.model.PlayerRights;
import com.elvarg.game.model.SkullType;
import com.elvarg.game.model.container.impl.shop.ShopManager;
import com.elvarg.game.model.dialogue.DialogueManager;
import com.elvarg.game.model.dialogue.DialogueOptions;
import com.elvarg.game.model.movement.WalkToAction;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.util.NpcIdentifiers;
import com.elvarg.util.ShopIdentifiers;

public class NPCOptionPacketListener extends NpcIdentifiers implements PacketListener {

	private static void firstClick(Player player, Packet packet) {
		int index = packet.readLEShort();
		if(index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc);
		if(player.getRights() == PlayerRights.ADMINISTRATOR)
			player.getPacketSender().sendMessage("First click npc id: "+npc.getId());
		player.setWalkToTask(new WalkToAction(player, npc.getPosition(), npc.getSize(), new Action() {
			@Override
			public void execute() {
				//Check if we're interacting with our pet..
				if(PetHandler.interact(player, npc)) {
					return;
				}
				
				switch(npc.getId()) {
			/*	case 1497: //Net and bait
				case 1498: // Net and bait
					player.getSkillManager().startSkillable(new Fishing(npc, FishingTool.NET));
					break;*/
				case SHOP_KEEPER_5:
				case SHOP_KEEPER_4:
				case SHOP_ASSISTANT_4:
				case SHOP_ASSISTANT_5:
					ShopManager.open(player, ShopIdentifiers.GENERAL_STORE);
					break;

				case MAKE_OVER_MAGE:
					player.getPacketSender().sendInterfaceRemoval().sendInterface(3559);
					player.getAppearance().setCanChangeAppearance(true);
					break;

				case EMBLEM_TRADER:
					//And then start dialogue
					DialogueManager.start(player, 0);
					//Set dialogue options
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player, int option) {
							switch(option) {
							case 1:
								//Open pvp shop
								ShopManager.open(player, ShopIdentifiers.BOUNTY_HUNTER_STORE);
								break;
							case 2:
								//Sell emblems option
								player.setDialogueOptions(new DialogueOptions() {
									@Override
									public void handleOption(Player player, int option) {
										if(option == 1) {
											int cost = BountyHunter.getValueForEmblems(player, true);
											player.getPacketSender().sendMessage("@red@You have received "+cost+" blood money for your emblem(s).");
											DialogueManager.start(player, 4);
										} else {
											player.getPacketSender().sendInterfaceRemoval();
										}
									}
								});
								int value = BountyHunter.getValueForEmblems(player, false);
								if(value > 0) {
									player.setDialogue(DialogueManager.getDialogues().get(10)); //Yes / no option
									DialogueManager.sendStatement(player, "I will give you "+value+" blood money for those emblems. Agree?");
								} else {
									DialogueManager.start(player, 5);
								}
								break;
							case 3:
								//Skull me option
								if(player.isSkulled()) {
									DialogueManager.start(player, 3);
								} else {
									DialogueManager.start(player, 6);
									player.setDialogueOptions(new DialogueOptions() {
										@Override
										public void handleOption(Player player, int option) {
											if(option == 1) {
												CombatFactory.skull(player, SkullType.WHITE_SKULL, 300);
											} else if(option == 2) {
												CombatFactory.skull(player, SkullType.RED_SKULL, 300);
											}
											player.getPacketSender().sendInterfaceRemoval();
										}
									});
								}
								break;
							case 4:
								//Cancel option
								player.getPacketSender().sendInterfaceRemoval();
								break;
							}
						}
					});
					break;

				case PERDU:
					//Set dialogue options
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player, int option) {
							if(option == 1) {

								int cost = BrokenItem.getRepairCost(player);

								player.setDialogueOptions(new DialogueOptions() {
									@Override
									public void handleOption(Player player, int option) {
										if(option == 1) {
											BrokenItem.repair(player);
										} else {
											player.getPacketSender().sendInterfaceRemoval();
										}
									}
								});								

								if(cost > 0) {
									player.setDialogue(DialogueManager.getDialogues().get(10)); //Yes / no option
									DialogueManager.sendStatement(player, "It will cost you "+cost+" blood money to fix your broken items. Agree?");
								} else {
									DialogueManager.start(player, 20);
								}

							} else {
								player.getPacketSender().sendInterfaceRemoval();
							}
						}
					});

					//Start main dialogue
					DialogueManager.start(player, 19);
					break;


				case FINANCIAL_ADVISOR:
					DialogueManager.start(player, 15);
					//Removed
					break;


				}
				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	public void handleSecondClick(Player player, Packet packet) {
		int index = packet.readLEShortA();
		if(index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if(npc == null)
			return;
		player.setEntityInteraction(npc);
		final int npcId = npc.getId();
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Second click npc id: "+npcId);
		player.setWalkToTask(new WalkToAction(player, npc.getPosition(), npc.getSize(), new Action() {
			@Override
			public void execute() {
				//Check if we're picking up our pet..
				if(PetHandler.pickup(player, npc)) {
					return;
				}
				
				//Check if we're thieving..
				if(Pickpocketing.init(player, npc)) {
					return;
				}

				switch(npc.getId()) {
				case 1497: //Net and bait
				case 1498: // Net and bait
					player.getSkillManager().startSkillable(new Fishing(npc, FishingTool.FISHING_ROD));
					break;
					
				case EMBLEM_TRADER:
					ShopManager.open(player, ShopIdentifiers.BOUNTY_HUNTER_STORE);
					break;

				}
				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	public void handleThirdClick(Player player, Packet packet) {
		int index = packet.readShort();
		if(index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc);
		npc.setPositionToFace(player.getPosition());
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Third click npc id: "+npc.getId());
		player.setWalkToTask(new WalkToAction(player, npc.getPosition(), npc.getSize(), new Action() {
			@Override
			public void execute() {
				//Check if we're morphing up our pet..
				if(PetHandler.morph(player, npc)) {
					return;
				}

				switch(npc.getId()) {
				case EMBLEM_TRADER:
					//Sell emblems option
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player, int option) {
							if(option == 1) {
								int cost = BountyHunter.getValueForEmblems(player, true);
								player.getPacketSender().sendMessage("@red@You have received "+cost+" blood money for your emblem(s).");
								DialogueManager.start(player, 4);
							} else {
								player.getPacketSender().sendInterfaceRemoval();
							}
						}
					});
					int value = BountyHunter.getValueForEmblems(player, false);
					if(value > 0) {
						player.setDialogue(DialogueManager.getDialogues().get(10)); //Yes / no option
						DialogueManager.sendStatement(player, "I will give you "+value+" blood money for those emblems. Agree?");
					} else {
						DialogueManager.start(player, 5);
					}
					break;
				}

				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	public void handleFourthClick(Player player, Packet packet) {
		int index = packet.readLEShort();
		if(index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc);
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Fourth click npc id: "+npc.getId());
		player.setWalkToTask(new WalkToAction(player, npc.getPosition(), npc.getSize(), new Action() {
			@Override
			public void execute() {
				switch(npc.getId()) {
				case EMBLEM_TRADER:
					if(player.isSkulled()) {
						DialogueManager.start(player, 3);
					} else {
						DialogueManager.start(player, 6);
						player.setDialogueOptions(new DialogueOptions() {
							@Override
							public void handleOption(Player player, int option) {
								if(option == 1) {
									CombatFactory.skull(player, SkullType.WHITE_SKULL, 300);
								} else if(option == 2) {
									CombatFactory.skull(player, SkullType.RED_SKULL, 300);
								}
								player.getPacketSender().sendInterfaceRemoval();
							}
						});
					}
					break;
				}
				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	private static void attackNPC(Player player, Packet packet) {
		int index = packet.readShortA();
		if(index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC interact = World.getNpcs().get(index);

		if (interact == null || interact.getDefinition() == null) {
			return;
		}

		if (!interact.getDefinition().isAttackable()) {
			return;
		}

		if(interact == null || interact.getHitpoints() <= 0) {
			player.getMovementQueue().reset();
			return;
		}

		player.getCombat().attack(interact);
	}

	private static void mageNpc(Player player, Packet packet) {
		int npcIndex = packet.readLEShortA();
		int spellId = packet.readShortA();

		if (npcIndex < 0 || spellId < 0 || npcIndex > World.getNpcs().capacity()) {
			return;
		}

		final NPC interact = World.getNpcs().get(npcIndex);

		if (interact == null || interact.getDefinition() == null) {
			return;
		}

		if (!interact.getDefinition().isAttackable()) {
			return;
		}

		if(interact == null || interact.getHitpoints() <= 0) {
			player.getMovementQueue().reset();
			return;
		}

		CombatSpell spell = CombatSpells.getCombatSpell(spellId);

		if(spell == null) {
			player.getMovementQueue().reset();
			return;
		}

		player.setPositionToFace(interact.getPosition());
		player.getCombat().setCastSpell(spell);

		player.getCombat().attack(interact);
	}

	@Override
	public void handleMessage(Player player, Packet packet) {

		if(player == null || player.getHitpoints() <= 0) {
			return;
		}

		if(player.busy()) {
			return;
		}
		switch (packet.getOpcode()) {
		case PacketConstants.ATTACK_NPC_OPCODE:
			attackNPC(player, packet);
			break;
		case PacketConstants.FIRST_CLICK_OPCODE:
			firstClick(player, packet);
			break;
		case PacketConstants.SECOND_CLICK_OPCODE:
			handleSecondClick(player, packet);
			break;
		case PacketConstants.THIRD_CLICK_OPCODE:
			handleThirdClick(player, packet);
			break;
		case PacketConstants.FOURTH_CLICK_OPCODE:
			handleFourthClick(player, packet);
			break;
		case PacketConstants.MAGE_NPC_OPCODE:
			mageNpc(player, packet);
			break;
		}
	}
}
