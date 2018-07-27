package com.elvarg.net.packet.impl;

import java.util.Optional;

import com.elvarg.Server;
import com.elvarg.game.World;
import com.elvarg.game.content.clan.ClanChatManager;
import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.WeaponInterfaces;
import com.elvarg.game.content.combat.bountyhunter.BountyHunter;
import com.elvarg.game.content.skill.SkillManager;
import com.elvarg.game.definition.ItemDefinition;
import com.elvarg.game.definition.ShopDefinition;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.entity.impl.player.PlayerSaving;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Flag;
import com.elvarg.game.model.Graphic;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.Locations.Location;
import com.elvarg.game.model.PlayerRights;
import com.elvarg.game.model.Position;
import com.elvarg.game.model.Skill;
import com.elvarg.game.model.SkullType;
import com.elvarg.game.model.container.impl.Bank;
import com.elvarg.game.model.dialogue.DialogueManager;
import com.elvarg.game.model.teleportation.TeleportHandler;
import com.elvarg.game.model.teleportation.TeleportType;
import com.elvarg.game.task.Task;
import com.elvarg.game.task.TaskManager;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.util.Misc;
import com.elvarg.util.PlayerPunishment;


/**
 * This packet listener manages commands a player uses by using the
 * command console prompted by using the "`" char.
 * 
 * @author Gabriel Hannason
 */

public class CommandPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		String command = packet.readString();
		String[] parts = command.toLowerCase().split(" ");
		if(command.contains("\r") || command.contains("\n")) {
			return;
		}

		if(player == null || player.getHitpoints() <= 0) {
			return;
		}

		if(command.startsWith("/") && command.length() >= 1) {
			ClanChatManager.sendMessage(player, command.substring(1, command.length()));
			return;
		}
		try {

			command = command.toLowerCase();
			switch(player.getRights()) {
			case PLAYER:
				playerCommands(player, command, parts);
				break;
			case DONATOR:
			case SUPER_DONATOR:
			case LEGENDARY_DONATOR:
			case YOUTUBER:
				playerCommands(player, command, parts);
				donorCommands(player, command, parts);
				break;
			case MODERATOR:
				playerCommands(player, command, parts);
				donorCommands(player, command, parts);
				modCommands(player, command, parts);
				break;
			case ADMINISTRATOR:
				playerCommands(player, command, parts);
				donorCommands(player, command, parts);
				modCommands(player, command, parts);
				adminCommands(player, command, parts);
				break;
			case DEVELOPER:
			case OWNER:
				playerCommands(player, command, parts);
				donorCommands(player, command, parts);
				modCommands(player, command, parts);
				adminCommands(player, command, parts);
				devCommands(player, command, parts);
				ownerCommands(player, command, parts);
				break;
			}

		} catch (Exception exception) {
			exception.printStackTrace();

			if(player.getRights() == PlayerRights.DEVELOPER) {
				player.getPacketSender().sendMessage("Error executing that command.");

			} else {
				player.getPacketSender().sendMessage("Error executing that command.");
			}

		}
	}

	private static void playerCommands(Player player, String command, String[] parts) {
		if(parts[0].startsWith("lockxp")) {
			player.setExperienceLocked(!player.experienceLocked());
			player.getPacketSender().sendMessage("Lock: "+player.experienceLocked());
		} else if(parts[0].startsWith("empty")) {
			player.getSkillManager().stopSkillable();
			player.getInventory().resetItems().refreshItems();
		} else if(parts[0].startsWith("veng")) {
			if(player.busy()) {
				player.getPacketSender().sendMessage("You cannot do that right now.");
				return;
			}
			if(player.getInventory().getFreeSlots() < 3) {
				player.getPacketSender().sendMessage("You don't have enough free inventory space to do that.");
				return;
			}
			player.getInventory().add(9075, 1000).add(557, 1000).add(560, 1000);
		} else if(parts[0].startsWith("barrage")) {
			if(player.busy()) {
				player.getPacketSender().sendMessage("You cannot do that right now.");
				return;
			}
			if(player.getInventory().getFreeSlots() < 3) {
				player.getPacketSender().sendMessage("You don't have enough free inventory space to do that.");
				return;
			}
			player.getInventory().add(565, 1000).add(555, 1000).add(560, 1000);
		} else if(parts[0].startsWith("donate") || parts[0].startsWith("store")) {
			player.getPacketSender().sendURL("http://osrspk.com/store");
		} else if(parts[0].startsWith("claim")) {
			player.getPacketSender().sendMessage("To claim purchased items, please talk to the Financial Advisor at home.");
		} else if(parts[0].startsWith("players")) {
			player.getPacketSender().sendConsoleMessage("There are currently "+World.getPlayers().size()+" players online and "+BountyHunter.PLAYERS_IN_WILD.size()+" players in the Wilderness.");
		} else if(parts[0].startsWith("kdr")) {
			player.forceChat("I currently have "+player.getKillDeathRatio()+" kdr!");
		} else if(parts[0].startsWith("pvp")) {
			if(player.busy() || player.getLocation() == Location.WILDERNESS || player.getDueling().inDuel()) {
				player.getPacketSender().sendMessage("You Cannot Do That Here");
				return;
			}
			TeleportHandler.teleport(player, new Position(3510, 3483), TeleportType.ANCIENT);
			} else if(parts[0].startsWith("mb")) {
			if(player.busy() || player.getLocation() == Location.WILDERNESS || player.getDueling().inDuel()) {
				player.getPacketSender().sendMessage("You Cannot Do That Here");
				return;
			}
			TeleportHandler.teleport(player, new Position(2538, 4718), TeleportType.ANCIENT);
			} else if(parts[0].startsWith("home")) {
				if(player.busy() || player.getLocation() == Location.WILDERNESS || player.getDueling().inDuel()) {
				player.getPacketSender().sendMessage("You Cannot Do That Here");
				return;
			}
			TeleportHandler.teleport(player, new Position(3101, 3510), TeleportType.ANCIENT);
		} else if(parts[0].startsWith("35")) {
			if(player.busy() || player.getLocation() == Location.WILDERNESS || player.getDueling().inDuel()) {
				player.getPacketSender().sendMessage("You Cannot Do That Here");
				return;
			}
			TeleportHandler.teleport(player, new Position(3097, 3795), TeleportType.ANCIENT);
		} else if(parts[0].startsWith("vespula")) {
			if(player.busy() || player.getLocation() == Location.WILDERNESS || player.getDueling().inDuel()) {
				player.getPacketSender().sendMessage("You Cannot Do That Here");
				return;
			}
			TeleportHandler.teleport(player, new Position(3355, 3920), TeleportType.ANCIENT);
		} else if(parts[0].startsWith("torture")) {
			if(player.busy() || player.getLocation() == Location.WILDERNESS || player.getDueling().inDuel()) {
				player.getPacketSender().sendMessage("You Cannot Do That Here");
				return;
			}
			TeleportHandler.teleport(player, new Position(2954, 3749), TeleportType.ANCIENT);
		} else if(parts[0].startsWith("graves")) {
			if(player.busy() || player.getLocation() == Location.WILDERNESS || player.getDueling().inDuel()) {
				player.getPacketSender().sendMessage("You Cannot Do That Here");
				return;
			}
		} else if(parts[0].equals("changepassword")) {
			String pass = command.substring(parts[0].length() + 1);
			if(pass.length() > 0 && pass.length() < 15) {
				player.setPassword(pass);
				player.getPacketSender().sendMessage("Your password is now: "+pass);
			} else {
				player.getPacketSender().sendMessage("Invalid password input.");
			}
		} else if(parts[0].startsWith("skull") || parts[0].startsWith("redskull")) {
			if(CombatFactory.inCombat(player)) {
				player.getPacketSender().sendMessage("You cannot change that during combat!");
				return;
			}
			if(parts[0].contains("red")) {
				CombatFactory.skull(player, SkullType.RED_SKULL, (60 * 30)); //Should be 30 mins
			} else {
				CombatFactory.skull(player, SkullType.WHITE_SKULL, 300); //Should be 5 mins
			}
		}
	}

	private static void donorCommands(Player player, String command, String[] parts) {
		if(parts[0].startsWith("yell")) {
			if(PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAddress())) {
				player.getPacketSender().sendMessage("You are muted and cannot yell.");
				return;
			}
			if(!player.getYellDelay().finished()) {
				player.getPacketSender().sendMessage("You must wait another "+player.getYellDelay().secondsRemaining()+" seconds to do that.");
				return;
			}
			final String yellMessage = command.substring(4, command.length());
			if(Misc.blockedWord(yellMessage)) {
				DialogueManager.sendStatement(player, "A word was blocked in your sentence. Please do not repeat it!");
				return;
			}
			World.sendMessage(""+player.getRights().getYellPrefix()+"[Global Chat]<img="+(player.getRights().ordinal() - 1)+"> "+player.getUsername()+":"+yellMessage);
			player.getYellDelay().start(player.getRights().getYellDelay());
		}
	}

	private static void modCommands(Player player, String command, String[] parts) {
		if(parts[0].equals("teleto")) {
			Optional<Player> plr = World.getPlayerByName(command.substring(parts[0].length() + 1));
			if(plr.isPresent()) {
				player.moveTo(plr.get().getPosition().copy());
			}
		} else if(parts[0].equals("teletome")) {
			Optional<Player> plr = World.getPlayerByName(command.substring(parts[0].length() + 1));
			if(plr.isPresent()) {
				plr.get().moveTo(player.getPosition().copy());
			}
		} else if(parts[0].equals("up")) {
			player.moveTo(player.getPosition().add(0, 0, 1));
		} else if(parts[0].equals("down")) {
			player.moveTo(player.getPosition().add(0, 0, -1));
		} else if(parts[0].equals("mute")) {
			String player2 = command.substring(parts[0].length() + 1);
			Optional<Player> plr = World.getPlayerByName(player2);
			if(!PlayerSaving.playerExists(player2) && plr == null) {
				player.getPacketSender().sendMessage("Player "+player2+" does not exist.");
				return;
			}
			if(PlayerPunishment.muted(player2)) {
				player.getPacketSender().sendMessage("Player "+player2+" already has an active mute.");
				return;
			}
			PlayerPunishment.mute(player2);
			player.getPacketSender().sendMessage("Player "+player2+" was successfully muted.");
			if(plr.isPresent()) {
				plr.get().getPacketSender().sendMessage("You have been muted by "+player.getUsername()+".");
			}
		} else if(parts[0].equals("unmute")) {
			String player2 = command.substring(parts[0].length() + 1);
			Optional<Player> plr = World.getPlayerByName(player2);
			if(!PlayerSaving.playerExists(player2) && !plr.isPresent()) {
				player.getPacketSender().sendMessage("Player "+player2+" does not exist.");
				return;
			}
			if(!PlayerPunishment.muted(player2)) {
				player.getPacketSender().sendMessage("Player "+player2+" does not have an active mute.");
				return;
			}
			PlayerPunishment.unmute(player2);
			player.getPacketSender().sendMessage("Player "+player2+" was successfully unmuted.");
			if(plr.isPresent()) {
				plr.get().getPacketSender().sendMessage("You have been unmuted by "+player.getUsername()+".");
			}
		} else if(parts[0].equals("ipmute")) {
			Optional<Player> player2 = World.getPlayerByName(command.substring(parts[0].length() + 1));
			if(!player2.isPresent()) {
				player.getPacketSender().sendMessage("Player "+player2+" is not online.");
				return;
			}
			if(PlayerPunishment.IPMuted(player2.get().getHostAddress())){
				player.getPacketSender().sendMessage("Player "+player2.get().getUsername()+"'s IP is already IPMuted.");
				return;
			}
			PlayerPunishment.addMutedIP(player2.get().getHostAddress());
			player.getPacketSender().sendMessage("Player "+player2.get().getUsername()+" was successfully IPMuted.");
			player2.get().getPacketSender().sendMessage("You have been IPMuted by "+player.getUsername()+".");
		} else if(parts[0].equals("ban")) {
			String player2 = command.substring(parts[0].length() + 1);
			Optional<Player> plr = World.getPlayerByName(player2);
			if(!PlayerSaving.playerExists(player2) && !plr.isPresent()) {
				player.getPacketSender().sendMessage("Player "+player2+" is not a valid online player.");
				return;
			}
			if(PlayerPunishment.banned(player2)) {
				player.getPacketSender().sendMessage("Player "+player2+" already has an active ban.");
				if(plr.isPresent()) {
					plr.get().requestLogout();
				}
				return;
			}
			PlayerPunishment.ban(player2);
			player.getPacketSender().sendMessage("Player "+player2+" was successfully banned. Command logs written.");
			if(plr.isPresent()) {
				plr.get().requestLogout();
			}
		} else if(parts[0].equals("unban")) {
			String player2 = command.substring(parts[0].length() + 1);
			if(!PlayerSaving.playerExists(player2)) {
				player.getPacketSender().sendMessage("Player "+player2+" is not online.");
				return;
			}
			if(!PlayerPunishment.banned(player2)) {
				player.getPacketSender().sendMessage("Player "+player2+" is not banned!");
				return;
			}
			PlayerPunishment.unban(player2);
			player.getPacketSender().sendMessage("Player "+player2+" was successfully unbanned.");
		} else if(parts[0].equals("ipban")) {
			String player2 = command.substring(parts[0].length() + 1);
			Optional<Player> plr = World.getPlayerByName(player2);
			if(!plr.isPresent()) {
				player.getPacketSender().sendMessage("Player "+player2+" is not online.");
				return;
			}
			if(PlayerPunishment.IPBanned(plr.get().getHostAddress())){
				player.getPacketSender().sendMessage("Player "+player2+"'s IP is already banned.");
				plr.get().requestLogout();
				return;
			}
			PlayerPunishment.addBannedIP(plr.get().getHostAddress());
			player.getPacketSender().sendMessage("Player "+player2+" was successfully ipbanned. Command logs written.");
			plr.get().requestLogout();
		} else if(parts[0].equals("unipmute")) {
			player.getPacketSender().sendMessage("Unipmutes can only be handled manually.");
		} else if(parts[0].equals("kick")) {
			String player2 = command.substring(parts[0].length() + 1);
			Optional<Player> plr = World.getPlayerByName(player2);
			if(!plr.isPresent()) {
				player.getPacketSender().sendMessage("Player "+player2+" is not online.");
				return;
			}
			if(CombatFactory.inCombat(plr.get())) {
				player.getPacketSender().sendMessage("Player "+player2+" is in combat!");
				return;
			}
			player.getPacketSender().sendMessage("Player "+player2+" was successfully kicked. Command logs written.");
			plr.get().requestLogout();
		} else if(parts[0].startsWith("exit")) {
			String player2 = command.substring(parts[0].length() + 1);
			Optional<Player> plr = World.getPlayerByName(player2);
			if(!plr.isPresent()) {
				player.getPacketSender().sendMessage("Player "+player2+" is not online.");
				return;
			}
			if(CombatFactory.inCombat(plr.get())) {
				player.getPacketSender().sendMessage("Player "+player2+" is in combat!");
				return;
			}
			plr.get().getPacketSender().sendExit();
			player.getPacketSender().sendMessage("Closed other player's client.");
		}
	}

	private static void adminCommands(Player player, String command, String[] parts) {

	}

	private static void ownerCommands(Player player, String command, String[] parts) {
		
	}

	private static void devCommands(Player player, String command, String[] parts) {
		if(parts[0].startsWith("flood")) {
			int amt = Integer.parseInt(parts[1]);
			Server.getFlooder().login(amt);
		} 

		if(parts[0].startsWith("rights")) {
			int right = Integer.parseInt(parts[1]);
			if(parts.length >= 3) {
				String other = command.substring(parts[0].length() + parts[1].length() + 2);
				Optional<Player> player_ = World.getPlayerByName(other);
				if(!player_.isPresent()) {
					player.getPacketSender().sendMessage("Could not find player: "+other);
					return;
				}
				player_.get().setRights(PlayerRights.values()[right]);
				player_.get().getPacketSender().sendRights();
				player_.get().getPacketSender().sendMessage("You're now a "+player_.get().getRights().name());
				player.getPacketSender().sendMessage("Gave "+other+" rank: "+player_.get().getRights().name());
			} else {
				player.setRights(PlayerRights.values()[right]);
				player.getPacketSender().sendRights();
			}
		}
		if(parts[0].startsWith("copybank")) {
			String player2 = command.substring(parts[0].length() + 1);
			Optional<Player> plr = World.getPlayerByName(player2);
			if(plr.isPresent()) {
				for(int i = 0; i < Bank.TOTAL_BANK_TABS; i++) {
					if(player.getBank(i) != null) {
						player.getBank(i).resetItems();
					}
				}
				for(int i = 0; i < Bank.TOTAL_BANK_TABS; i++) {
					if(plr.get().getBank(i) != null) {
						for(Item item : plr.get().getBank(i).getValidItems()) {
							player.getBank(i).add(item, false);
						}
					}
				}
			}
		}
		if(parts[0].equals("reloaditems")) {
			new ItemDefinition();
			player.getPacketSender().sendConsoleMessage("Reloaded items.");
		}
		if(parts[0].equals("reloadshops")) {
			//	ShopDefinition.parse().load();
			player.getPacketSender().sendConsoleMessage("Reloaded shops.");
		}
		if(parts[0].equals("reloaddrops")) {
			//NpcDropDefinition.parse().load();
			player.getPacketSender().sendConsoleMessage("Reloaded drops.");
		}
		if(parts[0].startsWith("points")) {
			int points = Integer.parseInt(parts[1]);
			if(parts.length == 3) {
				String other = command.substring(parts[0].length() + parts[1].length() + 2);
				Optional<Player> player_ = World.getPlayerByName(other);
				if(!player_.isPresent()) {
					player.getPacketSender().sendMessage("Could not find player: "+other);
					return;
				}
				player_.get().incrementPoints(points);
				player_.get().getPacketSender().sendString(52032, "@or1@Points: "+Misc.getTotalAmount(player.getPoints()));
				player_.get().getPacketSender().sendMessage("You've got "+points+" points.");
				player.getPacketSender().sendMessage("Gave "+other+" points: "+points);
			} else {
				player.incrementPoints(points);
				player.getPacketSender().sendString(52032, "@or1@Points: "+Misc.getTotalAmount(player.getPoints()));
				player.getPacketSender().sendMessage("You've got "+points+" points.");
			}
		}

		if(parts[0].startsWith("unlock")) {
			int type = Integer.parseInt(parts[1]);
			if(type == 0) {
				player.setPreserveUnlocked(true);
			} else if(type == 1) {
				player.setRigourUnlocked(true);
			} else if(type == 2) {
				player.setAuguryUnlocked(true);
			}
			player.getPacketSender().sendConfig(709, player.isPreserveUnlocked() ? 1 : 0);
			player.getPacketSender().sendConfig(711, player.isRigourUnlocked() ? 1 : 0);
			player.getPacketSender().sendConfig(713, player.isAuguryUnlocked() ? 1 : 0);
		}
		if(parts[0].startsWith("bank")) {
			player.getBank(player.getCurrentBankTab()).open();
		}
		if(parts[0].startsWith("setlevel")) {
			Skill skill = Skill.values()[Integer.parseInt(parts[1])];
			int level = Integer.parseInt(parts[2]);
			player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(level));
			WeaponInterfaces.assign(player);
		}
		if(parts[0].startsWith("master")) {
			for(Skill skill : Skill.values()) {
				int level = SkillManager.getMaxAchievingLevel(skill);
				player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(level));
			}
			WeaponInterfaces.assign(player);
			player.getUpdateFlag().flag(Flag.PLAYER_APPEARANCE);
		}
		if(parts[0].startsWith("reset")) {
			for(Skill skill : Skill.values()) {
				int level = skill == Skill.HITPOINTS ? 10 : 1;
				player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(level));
			}
			WeaponInterfaces.assign(player);
		}
		if(parts[0].startsWith("playnpc")) {
			player.setNpcTransformationId(Integer.parseInt(parts[1]));
		}
		if(parts[0].startsWith("npc")) {
			NPC npc = new NPC(Integer.parseInt(parts[1]), player.getPosition().copy().add(1, 0));
			World.getAddNPCQueue().add(npc);
		}
		if(parts[0].startsWith("save")) {
			player.save();
		}
		if(parts[0].startsWith("pos")) {
			player.getPacketSender().sendMessage(player.getPosition().toString());
		}
		if(parts[0].startsWith("config")) {
			player.getPacketSender().sendConfig(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
		}
		if(parts[0].startsWith("object")) {
			player.getPacketSender().sendObject(new GameObject(Integer.parseInt(parts[1]), player.getPosition().copy()));
		}
		if(parts[0].startsWith("spec")) {
			int amt = Integer.parseInt(parts[1]);
			player.setSpecialPercentage(amt);
			CombatSpecial.updateBar(player);
		}
		if(parts[0].startsWith("runes")) {
			int[] runes = new int[]{554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565};
			for(int rune : runes) {
				player.getInventory().add(rune, 1000);
			}
		}
		if(parts[0].equals("tele")) {
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			int z = 0;
			if(parts.length == 4) {
				z = Integer.parseInt(parts[3]);
			}
			player.moveTo(new Position(x, y, z));
		}
		if(parts[0].startsWith("anim")) {
			int anim = Integer.parseInt(parts[1]);
			player.performAnimation(new Animation(anim));
		}
		if(parts[0].startsWith("gfx")) {
			int gfx = Integer.parseInt(parts[1]);
			player.performGraphic(new Graphic(gfx));
		}
		if(parts[0].startsWith("item")) {
			int amount = 1;
			if(parts.length > 2) {
				amount = Integer.parseInt(parts[2]);
			}
			player.getInventory().add(new Item(Integer.parseInt(parts[1]), amount));
		}
		if (parts[0].equals("update")) {
			int time = Integer.parseInt(parts[1]);
			if(time > 0) {
				Server.setUpdating(true);
				for (Player players : World.getPlayers()) {
					if (players == null)
						continue;
					players.getPacketSender().sendSystemUpdate(time);
				}
				TaskManager.submit(new Task(time) {
					@Override
					protected void execute() {
						for (Player player : World.getPlayers()) {
							if (player != null) {
								player.requestLogout();
							}
						}
						ClanChatManager.save();
						Server.getLogger().info("Update task finished!");
						stop();
					}
				});
			}
		} else if(parts[0].equals("noclip")) {
			player.getPacketSender().sendEnableNoclip();
			player.getPacketSender().sendConsoleMessage("Noclip enabled.");
		} else if(parts[0].equals("int")) {
			player.getPacketSender().sendInterface(Integer.parseInt(parts[1]));
		} else if(parts[0].equals("cint")) {
			player.getPacketSender().sendChatboxInterface(Integer.parseInt(parts[1]));
		} else if(parts[0].equals("reloadpunishments")) {
			PlayerPunishment.init();
			player.getPacketSender().sendConsoleMessage("Reloaded");
		} else if(parts[0].startsWith("find")) {
			String name = command.substring(parts[0].length() + 1, command.length()).toLowerCase();
			for(ItemDefinition def : ItemDefinition.definitions.values()) {
				if(def.getName().toLowerCase().contains(name)) {
					player.getPacketSender().sendConsoleMessage("Found item, id: "+def.getId()+", name: "+def.getName());
				}
			}
		}
	}

	public static final int OP_CODE = 103;
}
