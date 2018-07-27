package com.elvarg.game.content.skill.construction;

import java.util.ArrayList;
import java.util.Optional;

import com.elvarg.game.GameConstants;
import com.elvarg.game.content.skill.construction.ConstructionData.Furniture;
import com.elvarg.game.content.skill.construction.ConstructionData.HotSpots;
import com.elvarg.game.content.skill.construction.ConstructionData.Portals;
import com.elvarg.game.content.skill.construction.ConstructionData.RoomData;
import com.elvarg.game.content.skill.construction.Palette.PaletteTile;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.object.ObjectManager;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Position;
import com.elvarg.game.model.region.InstancedRegion;
import com.elvarg.game.task.Task;
import com.elvarg.game.task.TaskManager;

public class Construction {

	public static final int[] CONS_LEAVE_COORDS = new int[]{3207, 3433};
	private static final Position CONS_LEAVE_POS = new Position(3207, 3433);
	private static final Position TEMP_CONS_POS = new Position(0, 0, 0);

	public static void newHouse(Player p) {
		if (p.houseRooms[0][0][0] != null)
			return;
		for (int x = 0; x < 13; x++)
			for (int y = 0; y < 13; y++)
				p.houseRooms[0][x][y] = new Room(0, ConstructionData.EMPTY, 0);
		p.houseRooms[0][7][7] = new Room(0, ConstructionData.GARDEN, 0);
		PlayerFurniture pf = new PlayerFurniture(7, 7, 0, HotSpots.CENTREPIECE.getHotSpotId(), Furniture.EXIT_PORTAL.getFurnitureId(), HotSpots.CENTREPIECE.getXOffset(),
				HotSpots.CENTREPIECE.getYOffset());
		p.playerFurniture.clear();
		p.playerFurniture.add(pf);
	}
	
	public static void createPalette(Player p) {
		Palette palette = new Palette();
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					if (p.houseRooms[z][x][y] == null)
						continue;
					PaletteTile tile = new PaletteTile(p.houseRooms[z][x][y].getX(), p.houseRooms[z][x][y].getY(), p.houseRooms[z][x][y].getZ(),
							p.houseRooms[z][x][y].getRotation());
					palette.setTile(x, y, z, tile);
				}
			}
		}
		InstancedRegion r = new InstancedRegion(Optional.of(p), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(palette));
		r.addEntity(p);
		enterHouse(p, p, p.inBuildingMode, false);
	}
	
	public static void enterHouse(final Player me, final Player houseOwner, final boolean buildingMode, final boolean createPallet) {
		me.inBuildingMode = buildingMode;
		if (createPallet) {
			createPalette(me);
			return;
		}
		if(!houseOwner.getInstancedRegion().isPresent()) {
			return;
		}
		final InstancedRegion reg = houseOwner.getInstancedRegion().get();
		me.getMovementQueue().reset();
		me.getPacketSender().sendInterfaceRemoval();
		me.getPacketSender().sendMapState(2);
		me.moveTo(new Position(ConstructionData.MIDDLE_X, ConstructionData.MIDDLE_Y, me.getPosition().getZ()));
		TaskManager.submit(new Task(1, me, true) {
			int ticks = 0;
			@Override
			public void execute() {
				switch (ticks++) {
				case 1:
					/*if (me.inConstructionDungeon())
						me.getPacketSender().constructMapRegion(((House) houseOwner.getRegionInstance()).getSecondaryPalette());
					else
						me.getPacketSender().constructMapRegion(((House) houseOwner.getRegionInstance()).getPalette());
					me.setRegionInstance(houseOwner.getRegionInstance());*/
					me.getPacketSender().sendConstructMapRegion(houseOwner.getInstancedRegion().get().getPalette().get());
					reg.addEntity(me);
					break;
				case 2:
					if (false) { //me.inConstructionDungeon()
						placeAllFurniture(me, 4);
					} else {
						placeAllFurniture(me, 0);
						placeAllFurniture(me, 1);
					}
					break;
				case 4:
					//if (me.inConstructionDungeon())
						//me.getPacketSender().constructMapRegion(((House) houseOwner.getRegionInstance()).getSecondaryPalette());
					//else
						//me.getPacketSender().constructMapRegion(((House) houseOwner.getRegionInstance()).getPalette());
					break;
				case 5:
					me.getPacketSender().sendInterfaceRemoval();
					me.getPacketSender().sendMapState(0);
					//((House) me.getRegionInstance()).greet(me);

					if (me.toConsCoords != null) {
						me.moveTo(new Position(me.toConsCoords[0], me.toConsCoords[1], me.toConsCoords[2]));
						me.toConsCoords = null;
					} else {
						PlayerFurniture portal = findNearestPortal(me);
						if(portal != null)
						{
						int toX = ConstructionData.BASE_X + ((portal.getRoomX() + 1) * 8 + 2);
						int toY = ConstructionData.BASE_Y + ((portal.getRoomY() + 1) * 8 + 2);
						me.moveTo(new Position(toX, toY, me.getPosition().getZ()));
						}
					}
					this.stop();
					break;
				}
			}
		});
	}

	public static void enterHouse(final Player me, final Player houseOwner,
			final boolean buildingMode) {
		me.loadingHouse = true;
		me.getPacketSender().sendMapState(2);
		if (buildingMode) {
			me.getPacketSender().sendConfig(8000, 0);
			me.getPacketSender().sendConfig(8001, 0);
		} else {
			me.getPacketSender().sendConfig(8000, 1);
			me.getPacketSender().sendConfig(8001, 1);
		}
		
		me.moveTo(new Position(ConstructionData.MIDDLE_X, ConstructionData.MIDDLE_Y));
		TaskManager.submit(new Task(1, me, false) {
			int tick = 0;
			@Override
			protected void execute() {
				if(!me.getInstancedRegion().isPresent() || !houseOwner.getInstancedRegion().isPresent()) {
					me.moveTo(GameConstants.DEFAULT_POSITION);
					stop();
					return;
				}
				if(tick == 2) {
					me.getPacketSender().sendConstructMapRegion(houseOwner.getInstancedRegion().get().getPalette().get());
				} else if(tick == 5) {
					if (me.toConsCoords != null) {
						me.moveTo(new Position(me.toConsCoords[0], me.toConsCoords[1]));
						me.toConsCoords = null;
					} else {
						PlayerFurniture portal = findNearestPortal(houseOwner);
						if(portal != null) {
							int toX = ConstructionData.BASE_X + ((portal.getRoomX() + 1) * 8);
							int toY = ConstructionData.BASE_Y + ((portal.getRoomY() + 1) * 8);
							me.moveTo(new Position(toX + 2, toY + 2));
						}
						placeAllFurniture(me, 0);
						placeAllFurniture(me, 1);
					}
				} else if(tick >= 7) {
					me.getPacketSender().sendInterfaceRemoval();
					me.getPacketSender().sendMapState(0);
					me.loadingHouse = false;
					stop();
				}
				tick++;
			}
		});
	}

	public static void placeAllFurniture(Player p, int x, int y, int z) {
		if(!p.getInstancedRegion().isPresent()
				|| !p.getInstancedRegion().get().getOwner().isPresent()
				|| !p.getInstancedRegion().get().getOwner().get().isPlayer()) {
			return;
		}
		InstancedRegion iR = p.getInstancedRegion().get();
		Player owner = iR.getOwner().get().getAsPlayer();
		for (PlayerFurniture pf : owner.playerFurniture) {
			if (pf.getRoomZ() != z)
				continue;
			if (pf.getRoomX() != x || pf.getRoomY() != y)
				continue;
			Room room = owner.houseRooms[pf.getRoomZ()][pf.getRoomX()][pf.getRoomY()];
			HotSpots hs = HotSpots.forHotSpotIdAndCoords(pf.getHotSpotId(),
					pf.getStandardXOff(), pf.getStandardYOff(), room);
			if (hs == null) {
				continue;
			}

			int actualX = ConstructionData.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionData.getXOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			int actualY = ConstructionData.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionData.getYOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			Furniture f = Furniture.forFurnitureId(pf.getFurnitureId());
			ArrayList<HotSpots> hsses = HotSpots
					.forObjectId_3(f.getHotSpotId());
			doFurniturePlace(hs, f, hsses, getMyChunkFor(actualX, actualY),
					actualX, actualY, room.getRotation(), p, false, z);
		}
	}

	public static void placeAllFurniture(Player p, int heightLevel) {
		if(!p.getInstancedRegion().isPresent()
				|| !p.getInstancedRegion().get().getOwner().isPresent()
				|| !p.getInstancedRegion().get().getOwner().get().isPlayer()) {
			return;
		}
		InstancedRegion iR = p.getInstancedRegion().get();
		Player owner = iR.getOwner().get().getAsPlayer();
		for (PlayerFurniture pf : owner.playerFurniture) {
			if (pf == null)
				continue;
			if (pf.getFurnitureId() < 1)
				continue;
			if (pf.getRoomZ() != heightLevel)
				continue;
			Room room = owner.houseRooms[pf.getRoomZ()][pf.getRoomX()][pf
			                                                           .getRoomY()];
			if (room == null) {
				continue;
			}
			HotSpots hs = HotSpots.forHotSpotIdAndCoords(pf.getHotSpotId(),
					pf.getStandardXOff(), pf.getStandardYOff(), room);
			if (hs == null) {
				continue;
			}

			int actualX = ConstructionData.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionData.getXOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			int actualY = ConstructionData.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionData.getYOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			Furniture f = Furniture.forFurnitureId(pf.getFurnitureId());
			ArrayList<HotSpots> hsses = HotSpots
					.forObjectId_3(f.getHotSpotId());
			doFurniturePlace(hs, f, hsses, getMyChunkFor(actualX, actualY),
					actualX, actualY, room.getRotation(), p, false, heightLevel);
		}
	}

	public static void doFurniturePlace(HotSpots s, Furniture f,
			ArrayList<HotSpots> hsses, int[] myTiles, int actualX, int actualY,
			int roomRot, Player p, boolean placeBack, int height) {
		int portalId = -1;
		if (s == null)
			return;
		if(!p.getInstancedRegion().isPresent()
				|| !p.getInstancedRegion().get().getOwner().isPresent()
				|| !p.getInstancedRegion().get().getOwner().get().isPlayer()) {
			return;
		}
		InstancedRegion iR = p.getInstancedRegion().get();
		Player owner = iR.getOwner().get().getAsPlayer();
		if (s.getHotSpotId() == 72) {
			if (s.getXOffset() == 0) {
				for (Portal portal : owner.portals) {
					if (portal.getRoomX() == myTiles[0] - 1 &&
							portal.getRoomY() == myTiles[1] - 1 &&
							portal.getRoomZ() == height && portal.getId() == 0) {
						if (Portals.forType(portal.getType()).getObjects() != null)
							portalId = Portals.forType(portal.getType()).getObjects()[f.getFurnitureId() - 13636];

					}
				}
			}
			if (s.getXOffset() == 3) {
				for (Portal portal : owner.portals) {
					if (portal.getRoomX() == myTiles[0] - 1 &&
							portal.getRoomY() == myTiles[1] - 1 &&
							portal.getRoomZ() == height && portal.getId() == 1) {
						if (Portals.forType(portal.getType()).getObjects() != null)
							portalId = Portals.forType(portal.getType()).getObjects()[f.getFurnitureId() - 13636];

					}
				}

			}
			if (s.getXOffset() == 7) {
				for (Portal portal : owner.portals) {
					if (portal.getRoomX() == myTiles[0] - 1 &&
							portal.getRoomY() == myTiles[1] - 1 &&
							portal.getRoomZ() == height && portal.getId() == 2) {
						if (Portals.forType(portal.getType()).getObjects() != null)
							portalId = Portals.forType(portal.getType()).getObjects()[f.getFurnitureId() - 13636];

					}
				}
			}
		}
		if (height == 4)
			height = 0;

		if (s.getHotSpotId() == 92) {
			int offsetX = ConstructionData.BASE_X + (myTiles[0] * 8);
			int offsetY = ConstructionData.BASE_Y + (myTiles[1] * 8);
			if (s.getObjectId() == 15329 || s.getObjectId() == 15328) {
				sendObject(p, 
						actualX,
						actualY,
						s.getObjectId() == 15328 ? (placeBack ? 15328 : f
								.getFurnitureId()) : (placeBack ? 15329 : f
										.getFurnitureId() + 1), s.getRotation(roomRot),
								0, height);
				offsetX += ConstructionData.getXOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15329 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				offsetY += ConstructionData.getYOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15329 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				sendObject(p, 
						offsetX,
						offsetY,
						s.getObjectId() == 15329 ? (placeBack ? 15328 : f
								.getFurnitureId()) : (placeBack ? 15329 : f
										.getFurnitureId() + 1), s.getRotation(roomRot),
								0, height);

			}
			if (s.getObjectId() == 15326 || s.getObjectId() == 15327) {
				sendObject(p, 
						actualX,
						actualY,
						s.getObjectId() == 15327 ? (placeBack ? 15327 : f
								.getFurnitureId() + 1) : (placeBack ? 15326 : f
										.getFurnitureId()), s.getRotation(roomRot), 0,
								height);
				offsetX += ConstructionData.getXOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15326 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				offsetY += ConstructionData.getYOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15326 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				sendObject(p, 
						offsetX,
						offsetY,
						s.getObjectId() == 15326 ? (placeBack ? 15327 : f
								.getFurnitureId() + 1) : (placeBack ? 15326 : f
										.getFurnitureId()), s.getRotation(roomRot), 0,
								height);

			}
		} else if (s.getHotSpotId() == 85) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8) + 2;
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8) + 2;
			int type = 22, leftObject = 0, rightObject = 0, upperObject = 0, downObject = 0, middleObject = 0, veryMiddleObject = 0, cornerObject = 0;
			if (f.getFurnitureId() == 13331) {
				leftObject = rightObject = upperObject = downObject = 13332;
				middleObject = 13331;
				cornerObject = 13333;
			}
			if (f.getFurnitureId() == 13334) {
				leftObject = rightObject = upperObject = downObject = 13335;
				middleObject = 13334;
				cornerObject = 13336;
			}
			if (f.getFurnitureId() == 13337) {
				leftObject = rightObject = upperObject = downObject = middleObject = cornerObject = 13337;
				type = 10;
			}
			if (f.getFurnitureId() == 13373) {
				veryMiddleObject = 13373;
				leftObject = rightObject = upperObject = downObject = middleObject = 6951;
			}
			if (placeBack || f.getFurnitureId() == 13337) {
				for (int x = 0; x < 4; x++) {
					for (int y = 0; y < 4; y++) {
						sendObject(p, actualX + x, actualY + y,
								6951, 0, 10, height);
						sendObject(p, actualX + x, actualY + y,
								6951, 0, 22, height);
					}
				}

			}
			sendObject(p, actualX, actualY,
					placeBack ? 15348 : cornerObject, 1, type, height);
			sendObject(p, actualX, actualY + 1,
					placeBack ? 15348 : leftObject, 1, type, height);
			sendObject(p, actualX, actualY + 2,
					placeBack ? 15348 : leftObject, 1, type, height);
			sendObject(p, actualX, actualY + 3,
					placeBack ? 15348 : cornerObject, 2, type, height);
			sendObject(p, actualX + 1, actualY + 3,
					placeBack ? 15348 : upperObject, 2, type, height);
			sendObject(p, actualX + 2, actualY + 3,
					placeBack ? 15348 : upperObject, 2, type, height);
			sendObject(p, actualX + 3, actualY + 3,
					placeBack ? 15348 : cornerObject, 3, type, height);
			sendObject(p, actualX + 3, actualY + 2,
					placeBack ? 15348 : rightObject, 3, type, height);
			sendObject(p, actualX + 3, actualY + 1,
					placeBack ? 15348 : rightObject, 3, type, height);
			sendObject(p, actualX + 3, actualY,
					placeBack ? 15348 : cornerObject, 0, type, height);
			sendObject(p, actualX + 2, actualY,
					placeBack ? 15348 : downObject, 0, type, height);
			sendObject(p, actualX + 1, actualY,
					placeBack ? 15348 : downObject, 0, type, height);
			sendObject(p, actualX + 1, actualY + 1,
					placeBack ? 15348 : middleObject, 0, type, height);
			sendObject(p, actualX + 2, actualY + 1,
					placeBack ? 15348 : middleObject, 0, type, height);
			if (veryMiddleObject != 0)
				sendObject(p, actualX + 1, actualY + 2,
						veryMiddleObject, 0, 10, height);
			sendObject(p, actualX + 1, actualY + 2,
					placeBack ? 15348 : middleObject, 0, type, height);
			sendObject(p, actualX + 2, actualY + 2,
					placeBack ? 15348 : middleObject, 0, type, height);

		} else if (s.getHotSpotId() == 86) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8) + 2;
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8) + 2;

			sendObject(p, actualX + 1, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 3, 0, height);
			sendObject(p, actualX + 2, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 3, 0, height);
			sendObject(p, actualX + 3, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 2, 2, height);
			sendObject(p, actualX + 3, actualY + 1,
					placeBack ? 15352 : f.getFurnitureId(), 2, 0, height);
			sendObject(p, actualX + 3, actualY + 2,
					placeBack ? 15352 : f.getFurnitureId() + 1, 2, 0, height);
			sendObject(p, actualX + 3, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 1, 2, height);
			sendObject(p, actualX + 2, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 1, 0, height);
			sendObject(p, actualX + 1, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 1, 0, height);
			sendObject(p, actualX, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 0, 2, height);
			sendObject(p, actualX, actualY + 2,
					placeBack ? 15352 : f.getFurnitureId(), 0, 0, height);
			sendObject(p, actualX, actualY + 1,
					placeBack ? 15352 : f.getFurnitureId(), 0, 0, height);
			sendObject(p, actualX, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 3, 2, height);

		} else if (s.getHotSpotId() == 78) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
			// south walls
			sendObject(p, actualX, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 2, height);
			sendObject(p, actualX + 1, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			sendObject(p, actualX + 2, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			sendObject(p, actualX + 5, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			sendObject(p, actualX + 6, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			sendObject(p, actualX + 7, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 2, 2, height);
			// north walls
			sendObject(p, actualX, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 0, 2, height);
			sendObject(p, actualX + 1, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			sendObject(p, actualX + 2, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			sendObject(p, actualX + 5, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			sendObject(p, actualX + 6, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			sendObject(p, actualX + 7, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 2, height);
			// left walls
			sendObject(p, actualX, actualY + 1,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			sendObject(p, actualX, actualY + 2,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			sendObject(p, actualX, actualY + 5,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			sendObject(p, actualX, actualY + 6,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			// right walls
			sendObject(p, actualX + 7, actualY + 1,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
			sendObject(p, actualX + 7, actualY + 2,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
			sendObject(p, actualX + 7, actualY + 5,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
			sendObject(p, actualX + 7, actualY + 6,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
		} else if (s.getHotSpotId() == 77) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
			// left down corner
			sendObject(p, actualX, actualY,
					placeBack ? 15372 : f.getFurnitureId() + 1, 3, 10, height);
			sendObject(p, actualX + 1, actualY,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			sendObject(p, actualX + 2, actualY,
					placeBack ? 15370 : f.getFurnitureId(), 0, 10, height);
			sendObject(p, actualX, actualY + 1,
					placeBack ? 15371 : f.getFurnitureId() + 2, 1, 10, height);
			sendObject(p, actualX, actualY + 2,
					placeBack ? 15370 : f.getFurnitureId(), 3, 10, height);
			// right down corner
			sendObject(p, actualX + 7, actualY,
					placeBack ? 15372 : f.getFurnitureId() + 1, 2, 10, height);
			sendObject(p, actualX + 6, actualY,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			sendObject(p, actualX + 5, actualY,
					placeBack ? 15370 : f.getFurnitureId(), 2, 10, height);
			sendObject(p, actualX + 7, actualY + 1,
					placeBack ? 15371 : f.getFurnitureId() + 2, 3, 10, height);
			sendObject(p, actualX + 7, actualY + 2,
					placeBack ? 15370 : f.getFurnitureId(), 3, 10, height);
			// upper left corner
			sendObject(p, actualX, actualY + 7,
					placeBack ? 15372 : f.getFurnitureId() + 1, 0, 10, height);
			sendObject(p, actualX + 1, actualY + 7,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			sendObject(p, actualX + 2, actualY + 7,
					placeBack ? 15370 : f.getFurnitureId(), 0, 10, height);
			sendObject(p, actualX, actualY + 6,
					placeBack ? 15371 : f.getFurnitureId() + 2, 1, 10, height);
			sendObject(p, actualX, actualY + 5,
					placeBack ? 15370 : f.getFurnitureId(), 1, 10, height);
			// upper right corner
			sendObject(p, actualX + 7, actualY + 7,
					placeBack ? 15372 : f.getFurnitureId() + 1, 1, 10, height);
			sendObject(p, actualX + 6, actualY + 7,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			sendObject(p, actualX + 5, actualY + 7,
					placeBack ? 15370 : f.getFurnitureId(), 2, 10, height);
			sendObject(p, actualX + 7, actualY + 6,
					placeBack ? 15371 : f.getFurnitureId() + 2, 3, 10, height);
			sendObject(p, actualX + 7, actualY + 5,
					placeBack ? 15370 : f.getFurnitureId(), 1, 10, height);
		} else if (s.getHotSpotId() == 44) {
			int combatringStrings = 6951;
			int combatringFloorsCorner = 6951;
			int combatringFloorsOuter = 6951;
			int combatringFloorsInner = 6951;
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8) + 1;
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8) + 1;
			if (!placeBack) {
				if (f.getFurnitureId() == 13126) {
					combatringStrings = 13132;
					combatringFloorsCorner = 13126;
					combatringFloorsOuter = 13128;
					combatringFloorsInner = 13127;
				}
				if (f.getFurnitureId() == 13133) {
					combatringStrings = 13133;
					combatringFloorsCorner = 13135;
					combatringFloorsOuter = 13134;
					combatringFloorsInner = 13136;
				}
				if (f.getFurnitureId() == 13137) {
					combatringStrings = 13137;
					combatringFloorsCorner = 13138;
					combatringFloorsOuter = 13139;
					combatringFloorsInner = 13140;
				}
			}

			sendObject(p, actualX + 2, actualY + 3,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			sendObject(p, actualX + 3, actualY + 3,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			sendObject(p, actualX + 3, actualY + 2,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			sendObject(p, actualX + 2, actualY + 2,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			sendObject(p, actualX + 2, actualY + 1,
					placeBack ? 15291 : combatringFloorsOuter, 3, 22, height);
			sendObject(p, actualX + 3, actualY + 1,
					placeBack ? 15291 : combatringFloorsOuter, 3, 22, height);
			sendObject(p, actualX + 2, actualY + 4,
					placeBack ? 15291 : combatringFloorsOuter, 1, 22, height);
			sendObject(p, actualX + 3, actualY + 4,
					placeBack ? 15291 : combatringFloorsOuter, 1, 22, height);
			sendObject(p, actualX + 4, actualY + 3,
					placeBack ? 15291 : combatringFloorsOuter, 2, 22, height);
			sendObject(p, actualX + 4, actualY + 2,
					placeBack ? 15291 : combatringFloorsOuter, 2, 22, height);
			sendObject(p, actualX + 1, actualY + 3,
					placeBack ? 15291 : combatringFloorsOuter, 0, 22, height);
			sendObject(p, actualX + 1, actualY + 2,
					placeBack ? 15291 : combatringFloorsOuter, 0, 22, height);
			sendObject(p, actualX + 4, actualY + 1,
					placeBack ? 15289 : combatringFloorsCorner, 3, 22, height);
			sendObject(p, actualX + 4, actualY + 4,
					placeBack ? 15289 : combatringFloorsCorner, 2, 22, height);
			sendObject(p, actualX + 1, actualY + 4,
					placeBack ? 15289 : combatringFloorsCorner, 1, 22, height);
			sendObject(p, actualX + 1, actualY + 1,
					placeBack ? 15289 : combatringFloorsCorner, 0, 22, height);
			sendObject(p, actualX, actualY + 4,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			sendObject(p, actualX, actualY + 1,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			sendObject(p, actualX + 5, actualY + 4,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			sendObject(p, actualX + 5, actualY + 1,
					placeBack ? 15277 : combatringStrings, 0, 3, height);
			sendObject(p, actualX + 1, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			sendObject(p, actualX + 2, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			sendObject(p, actualX + 3, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			sendObject(p, actualX + 4, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			sendObject(p, actualX + 5, actualY,
					placeBack ? 15277 : combatringStrings, 0, 3, height);
			sendObject(p, actualX + 1, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			sendObject(p, actualX + 2, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			sendObject(p, actualX + 3, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			sendObject(p, actualX + 4, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			sendObject(p, actualX + 5, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 3, height);
			sendObject(p, actualX, actualY + 5,
					placeBack ? 15277 : combatringStrings, 2, 3, height);
			sendObject(p, actualX, actualY,
					placeBack ? 15277 : combatringStrings, 1, 3, height);
			sendObject(p, actualX, actualY + 4,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			sendObject(p, actualX, actualY + 3,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			sendObject(p, actualX, actualY + 2,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			sendObject(p, actualX, actualY + 1,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			sendObject(p, actualX + 5, actualY + 4,
					placeBack ? 15277 : combatringStrings, 0, 0, height);
			sendObject(p, actualX + 5, actualY + 3,
					placeBack ? 15277 : combatringStrings, 0, 0, height);
			sendObject(p, actualX + 5, actualY + 2,
					placeBack ? 15277 : combatringStrings, 0, 0, height);
			sendObject(p, actualX + 5, actualY + 1,
					placeBack ? 15277 : combatringStrings, 0, 0, height);

			if (f.getFurnitureId() == 13145) {
				sendObject(p, actualX + 1, actualY + 1,
						placeBack ? 6951 : 13145, 0, 0, height);
				sendObject(p, actualX + 2, actualY + 1,
						placeBack ? 6951 : 13145, 0, 0, height);
				sendObject(p, actualX + 1, actualY,
						placeBack ? 6951 : 13145, 1, 0, height);
				sendObject(p, actualX + 1, actualY + 2,
						placeBack ? 6951 : 13145, 3, 0, height);
				if (!placeBack)
					sendObject(p, actualX + 1, actualY + 1, 13147,
							0, 22, height);

				sendObject(p, actualX + 3, actualY + 3,
						placeBack ? 6951 : 13145, 0, 0, height);
				sendObject(p, actualX + 4, actualY + 3,
						placeBack ? 6951 : 13145, 0, 0, height);
				sendObject(p, actualX + 3, actualY + 2,
						placeBack ? 6951 : 13145, 1, 0, height);
				sendObject(p, actualX + 3, actualY + 4,
						placeBack ? 6951 : 13145, 3, 0, height);
				if (!placeBack)
					sendObject(p, actualX + 3, actualY + 3, 13147,
							0, 22, height);
			}
			if (f.getFurnitureId() == 13142 && !placeBack) {
				sendObject(p, actualX + 2, actualY + 2, 13142, 0,
						22, height);
				sendObject(p, actualX + 2, actualY + 1, 13143, 0,
						22, height);
				sendObject(p, actualX + 2, actualY + 3, 13144, 1,
						22, height);

			}
		} else if (s.getCarpetDim() != null) {
			for (int x = 0; x < s.getCarpetDim().getWidth() + 1; x++) {
				for (int y = 0; y < s.getCarpetDim().getHeight() + 1; y++) {
					boolean isEdge = (x == 0 && y == 0 || x == 0
							&& y == s.getCarpetDim().getHeight() || y == 0
							&& x == s.getCarpetDim().getWidth() || x == s
							.getCarpetDim().getWidth()
							&& y == s.getCarpetDim().getHeight());
					boolean isWall = ((x == 0 || x == s.getCarpetDim()
							.getWidth())
							&& (y != 0 && y != s.getCarpetDim().getHeight()) || (y == 0 || y == s
							.getCarpetDim().getHeight())
							&& (x != 0 && x != s.getCarpetDim().getWidth()));
					int rot = 0;
					if (x == 0 && y == s.getCarpetDim().getHeight() && isEdge)
						rot = 0;
					if (x == s.getCarpetDim().getWidth()
							&& y == s.getCarpetDim().getHeight() && isEdge)
						rot = 1;
					if (x == s.getCarpetDim().getWidth() && y == 0 && isEdge)
						rot = 2;
					if (x == 0 && y == 0 && isEdge)
						rot = 3;
					if (y == 0 && isWall)
						rot = 2;
					if (y == s.getCarpetDim().getHeight() && isWall)
						rot = 0;
					if (x == 0 && isWall)
						rot = 3;
					if (x == s.getCarpetDim().getWidth() && isWall)
						rot = 1;
					int offsetX = ConstructionData.BASE_X + (myTiles[0] * 8);
					int offsetY = ConstructionData.BASE_Y + (myTiles[1] * 8);
					offsetX += ConstructionData.getXOffsetForObjectId(
							f.getFurnitureId(), s.getXOffset() + x - 1,
							s.getYOffset() + y - 1, roomRot,
							s.getRotation(roomRot));
					offsetY += ConstructionData.getYOffsetForObjectId(
							f.getFurnitureId(), s.getXOffset() + x - 1,
							s.getYOffset() + y - 1, roomRot,
							s.getRotation(roomRot));
					if (isEdge)
						sendObject(p, 
								offsetX,
								offsetY,
								placeBack ? s.getObjectId() + (s == HotSpots.CHAPEL_RUG_1 ? 1 : 2) : f
										.getFurnitureId(),
										HotSpots.getRotation_2(rot, roomRot), 22,
										height);
					else if (isWall)
						sendObject(p, 
								offsetX,
								offsetY,
								placeBack ? s.getObjectId() + (s == HotSpots.CHAPEL_RUG_1 ? 0 : 1) : f
										.getFurnitureId() + 1,
										HotSpots.getRotation_2(rot, roomRot),
										s.getObjectType(), height);
					else
						sendObject(p, 
								offsetX,
								offsetY,
								placeBack ? s.getObjectId() : f
										.getFurnitureId() + 2,
										HotSpots.getRotation_2(rot, roomRot),
										s.getObjectType(), height);
				}
			}
		} else if (s.isMutiple()) {

			Room room = owner.houseRooms[p.inDungeon() ? 4 : p.getPosition().getZ()][myTiles[0] - 1][myTiles[1] - 1];
			for (HotSpots find : hsses) {
				if (find.getObjectId() != s.getObjectId())
					continue;
				if (room != null)
					if (room.getType() != find.getRoomType())
						continue;
				int actualX1 = ConstructionData.BASE_X + (myTiles[0] * 8);
				actualX1 += ConstructionData.getXOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				int actualY1 = ConstructionData.BASE_Y + (myTiles[1] * 8);
				actualY1 += ConstructionData.getYOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				sendObject(p, 
						actualX1,
						actualY1,
						placeBack ? s.getObjectId() : f
								.getFurnitureId(),
								find.getRotation(roomRot),
								find.getObjectType(), height);
			}
		} else {
			sendObject(p, actualX, actualY,
					(portalId != -1 ? portalId : placeBack ? s.getObjectId() : f.getFurnitureId()),
					s.getRotation(roomRot), s.getObjectType(), height);
		}
	}

	public static PlayerFurniture findNearestPortal(Player p) {
		if(!p.getInstancedRegion().isPresent()
				|| !p.getInstancedRegion().get().getOwner().isPresent()
				|| !p.getInstancedRegion().get().getOwner().get().isPlayer()) {
			return null;
		}
		InstancedRegion iR = p.getInstancedRegion().get();
		Player owner = iR.getOwner().get().getAsPlayer();
		for (PlayerFurniture pf : owner.playerFurniture) {
			if (pf.getFurnitureId() != 13405)
				continue;
			if (pf.getRoomZ() != 0)
				continue;
			return pf;
		}
		//Add new portal cause none was found.
		owner.houseRooms[0][7][7] = new Room(0, ConstructionData.GARDEN, 0);
		PlayerFurniture portal = new PlayerFurniture(7, 7, 0, HotSpots.CENTREPIECE.getHotSpotId(),
				Furniture.EXIT_PORTAL.getFurnitureId(), HotSpots.CENTREPIECE.getXOffset(),
				HotSpots.CENTREPIECE.getYOffset());
		owner.playerFurniture.add(portal);
		return portal;
	}

	public static int[] getMyChunk(Player p) {
		for (int x = 0; x < 13; x++) {
			for (int y = 0; y < 13; y++) {
				int minX = ((ConstructionData.BASE_X) + (x * 8));
				int maxX = ((ConstructionData.BASE_X + 7) + (x * 8));
				int minY = ((ConstructionData.BASE_Y) + (y * 8));
				int maxY = ((ConstructionData.BASE_Y + 7) + (y * 8));
				if (p.getPosition().getX() >= minX && p.getPosition().getX() <= maxX && p.getPosition().getY() >= minY
						&& p.getPosition().getY() <= maxY) {
					return new int[]{x, y};
				}
			}
		}
		return null;
	}

	public static int[] getMyChunkFor(int xx, int yy) {
		for (int x = 0; x < 13; x++) {
			for (int y = 0; y < 13; y++) {
				int minX = ((ConstructionData.BASE_X) + (x * 8));
				int maxX = ((ConstructionData.BASE_X + 7) + (x * 8));
				int minY = ((ConstructionData.BASE_Y) + (y * 8));
				int maxY = ((ConstructionData.BASE_Y + 7) + (y * 8));
				if (xx >= minX && xx <= maxX && yy >= minY && yy <= maxY) {
					return new int[]{x, y};
				}
			}
		}
		return null;
	}

	public static int getXTilesOnTile(int[] tile, Player p) {
		int baseX = ConstructionData.BASE_X + (tile[0] * 8);
		return p.getPosition().getX() - baseX;
	}

	public static int getYTilesOnTile(int[] tile, Player p) {
		int baseY = ConstructionData.BASE_Y + (tile[1] * 8);
		return p.getPosition().getY() - baseY;
	}

	public static int getXTilesOnTile(int[] tile, int myX) {
		int baseX = ConstructionData.BASE_X + (tile[0] * 8);
		return myX - baseX;
	}

	public static int getYTilesOnTile(int[] tile, int myY) {
		int baseY = ConstructionData.BASE_Y + (tile[1] * 8);
		return myY - baseY;
	}

	public static void buildRoofs(Palette palette, Player p) {
		if (p.inDungeon())
			return;
		if(!p.getInstancedRegion().isPresent()
				|| !p.getInstancedRegion().get().getOwner().isPresent()
				|| !p.getInstancedRegion().get().getOwner().get().isPlayer()) {
			return;
		}
		InstancedRegion iR = p.getInstancedRegion().get();
		p = iR.getOwner().get().getAsPlayer();
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					Room r = p.houseRooms[z][x][y];
					if (r == null)
						continue;
					if (r.getRotation() == -1)
						r.setRotation(0);
					if (ConstructionData.isGardenRoom(r.getType()))
						continue;
					if (r.getType() == ConstructionData.EMPTY)
						continue;
					if (z == 0) {
						if (p.houseRooms[1][x][y] != null)
							continue;
					}
					int doors = 0;
					if (r.getDoors() == null)
						continue;
					for (boolean b : r.getDoors())
						if (b)
							doors++;
					RoomData roof = null;
					int rotation = 0;
					if (doors == 1 || doors == 4)
						roof = RoomData.ROOF_4_DOORS;
					if (doors == 2)
						roof = RoomData.ROOF_2_DOORS;
					if (doors == 3)
						roof = RoomData.ROOF_3_DOORS;
					if (doors == 3 || doors == 2) {
						boolean[] roomDoors = RoomData.forID(r.getType()).getRotatedDoors(r.getRotation());
						for (int i = 0; i < 4; i++) {
							boolean[] doors_ = roof.getRotatedDoors(i);
							if (doors_[0] == roomDoors[0] && doors_[1] == roomDoors[1]
									&& doors_[2] == roomDoors[2] && doors_[3] == roomDoors[3])
								rotation = i;
						}
					}
					PaletteTile tile = new PaletteTile(roof.getX(), roof.getY(), 0, rotation);
					palette.setTile(x, y, z + 1, tile);
				}
			}
		}
	}

	/**
	 * Sends a construction object
	 * @param objectX
	 * @param objectY
	 * @param objectId
	 * @param face
	 * @param objectType
	 * @param height
	 */
	public static void sendObject(Player p, int objectX, int objectY, int objectId, int face, int objectType, int height) {
		if(!p.getInstancedRegion().isPresent()
				|| !p.getInstancedRegion().get().getOwner().isPresent()
				|| !p.getInstancedRegion().get().getOwner().get().isPlayer()) {
			return;
		}
		InstancedRegion iR = p.getInstancedRegion().get();

		//Create object..
		GameObject object = new GameObject(objectId, new Position(objectX, objectY, height), objectType, face);

		//Add to instanced region..
		iR.addEntity(object);

		//Register to world..
		ObjectManager.register(object, true);
	}
}
