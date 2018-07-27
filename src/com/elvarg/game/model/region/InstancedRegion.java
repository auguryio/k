package com.elvarg.game.model.region;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.elvarg.game.content.skill.construction.Palette;
import com.elvarg.game.entity.Entity;
import com.elvarg.game.entity.impl.Character;
import com.elvarg.game.model.Action;
import com.elvarg.game.model.Area;

/**
 * Represents an instanced region with entities.
 * @author Professor Oak
 */
public class InstancedRegion {

	/**
	 * Creates this {@link InstancedRegion}.
	 * @param onAdd
	 * @param onRemove
	 * @param bounds
	 * @param palette
	 */
	public InstancedRegion(Optional<Character> owner, Optional<Action> onAdd, Optional<Action> onRemove, Optional<List<Area>> bounds, Optional<Palette> palette) {
		this.owner = owner;
		this.onAdd = onAdd;
		this.onRemove = onRemove;
		this.bounds = bounds;
		this.palette = palette;
	}

	/**
	 * The {@link Character} which is the owner of this
	 * instanced region.
	 */
	private Optional<Character> owner = Optional.empty();

	/**
	 * The {@link Entity}s currently in this instanced region.
	 */
	private List<Entity> entities = new ArrayList<Entity>();

	/**
	 * Represents the {@link Action} which will be executed
	 * when a {@link Character} is added to this {@link InstancedRegion}.
	 */
	private Optional<Action> onAdd = Optional.empty();

	/**
	 * Represents the {@link Action} which will be executed
	 * when a {@link Character} is removed from this {@link InstancedRegion}.
	 */
	private Optional<Action> onRemove = Optional.empty();

	/**
	 * The {@link Area} this region is located in. If 
	 * an entity from our list of {@code entities} was to
	 * leave any of the areas, they would be removed from
	 * this {@link InstancedRegion}.
	 */
	private Optional<List<Area>> bounds = Optional.empty();

	/**
	 * The {@link Palette} represents the different tiles 
	 * this instanced region has. This can be used for
	 * constructing private map regions.
	 */
	private Optional<Palette> palette = Optional.empty();

	/**
	 * Adds the given entity to this instanced region.
	 * @param entity		The character to add.
	 * @return				Returns this instance.
	 */
	public InstancedRegion addEntity(Entity entity) {
		if(!entities.contains(entity)) {
			entities.add(entity);
		}
		entity.setInstancedRegion(Optional.of(this));
		onAdd.ifPresent(e -> e.execute());
		return this;
	}

	/**
	 * Removes the given character to this instanced region
	 * if it was found in the {@code characters} list.
	 * 
	 * @param entity		The character to remove.
	 * @return				Returns this instance.
	 */
	public InstancedRegion removeEntity(Entity entity) {
		Iterator<Entity> iterator = entities.iterator();
		while(iterator.hasNext()) {
			Entity e = iterator.next();
			if(e.equals(entity)) {
				iterator.remove();
			}
		}
		entity.setInstancedRegion(Optional.empty());
		onRemove.ifPresent(e -> e.execute());
		return this;
	}

	/**
	 * Processes this {@link InstancedRegion}
	 * for the given character.
	 */
	public void sequence(Character character) {
		if(bounds.isPresent()) {
			for(Area area : bounds.get()) {
				//entities.stream().filter(e -> !area.inBounds(e.getPosition())).forEach(e -> removeEntity(e));
				if(!area.inBounds(character.getPosition())) {
					removeEntity(character);
				}
			}
		}
	}

	/**
	 * Gets this instanced region's owner.
	 * @return		The owner.
	 */
	public Optional<Character> getOwner() {
		return owner;
	}

	/**
	 * Gets this instanced region's palette
	 * @return 		The palette.
	 */
	public Optional<Palette> getPalette() {
		return palette;
	}
}
