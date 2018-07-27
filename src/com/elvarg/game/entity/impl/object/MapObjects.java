package com.elvarg.game.entity.impl.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.elvarg.game.collision.RegionManager;
import com.elvarg.game.model.Position;

/**
 * Map objects are objects that are in the maps.
 * These are loaded when the maps are so that we can
 * verify that an object exists when a player
 * tries to interact with it.
 * 
 * @author Professor Oak
 */
public class MapObjects {

	/**
	 * A map which holds all of our map objects.
	 */
	public static final Map<Long, ArrayList<GameObject>> mapObjects = new HashMap<Long, ArrayList<GameObject>>();

	/**
	 * Attempts to get an object with the given id and position.
	 * 
	 * @param id
	 * @param position
	 */
	public static Optional<GameObject> get(int id, Position position) {
		//Load region..
		RegionManager.loadMapFiles(position.getX(), position.getY());
		
		//Get hash..
		long hash = getHash(position.getX(), position.getY(), position.getZ());

		//Check if the map contains the hash..
		if (!mapObjects.containsKey(hash)) {
			return Optional.empty();
		}

		//Go through the objects in the list..
		ArrayList<GameObject> list = mapObjects.get(hash);
		if (list != null) {
			Iterator<GameObject> it = list.iterator();
			for(; it.hasNext() ;) {
				GameObject o = it.next();
				if(o.getId() == id 
						&& o.getPosition().equals(position)) {
					return Optional.of(o);
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Checks if an object with the given id and position exists.
	 * @param id
	 * @param position
	 * @return
	 */
	public static boolean exists(int id, Position position) {
		return get(id, position).isPresent();
	}

	/**
	 * Checks if an gameobject exists.
	 * @param object
	 * @return
	 */
	public static boolean exists(GameObject object) {
		return get(object.getId(), object.getPosition()).isPresent();
	}

	/**
	 * Attempts to add a new object to our map of mapobjects.
	 * @param object
	 */
	public static void add(GameObject object) {		
		//Get hash for object..
		long hash = getHash(object.getPosition().getX(), object.getPosition().getY(), object.getPosition().getZ());

		if(mapObjects.containsKey(hash)) {
			//Check if object already exists in this list..
			boolean exists = false;
			List<GameObject> list = mapObjects.get(hash);
			Iterator<GameObject> it = list.iterator();
			for(; it.hasNext() ;) {
				GameObject o = it.next();
				if(o.equals(object)) {
					exists = true;
					break;
				}
			}
			//If it didn't exist, add it.
			if(!exists) {
				mapObjects.get(hash).add(object);
			}
		} else {
			ArrayList<GameObject> list = new ArrayList<GameObject>();
			list.add(object);
			mapObjects.put(hash, list);
		}

		//Add clipping for object.
		RegionManager.addObjectClipping(object);
	}

	/**
	 * Attempts to remove the given object from our map of mapobjects.
	 * @param object
	 */
	public static void remove(GameObject object) {
		//Get hash for object..
		long hash = getHash(object.getPosition().getX(), object.getPosition().getY(), object.getPosition().getZ());

		//Attempt to delete..
		if(mapObjects.containsKey(hash)) {
			Iterator<GameObject> it = mapObjects.get(hash).iterator();
			while(it.hasNext()) {
				GameObject o = it.next();
				if(o.getId() == object.getId() && o.getPosition().equals(object.getPosition())) {
					it.remove();
				}
			}
		}

		//Remove clipping from this area..
		RegionManager.removeObjectClipping(object);
	}

	/**
	 * Removes all objects in this position.
	 * @param position
	 */
	public static void clear(Position position, int clipShift) {
		//Get hash for pos..
		long hash = getHash(position.getX(), position.getY(), position.getZ());

		//Attempt to delete..
		if(mapObjects.containsKey(hash)) {
			Iterator<GameObject> it = mapObjects.get(hash).iterator();
			while(it.hasNext()) {
				GameObject o = it.next();
				if(o.getPosition().equals(position)) {
					it.remove();
				}
			}
		}

		//Remove clipping from this area..
		RegionManager.removeClipping(position.getX(), position.getY(), position.getZ(), clipShift);
	}
	
	/**
	 * Gets the hash for a map object.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static long getHash(int x, int y, int z) {
		return (z + ((long) x << 24) + ((long) y << 48));
	}
}
