package com.elvarg.game.model;

import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.player.Player;

/**
 * The enumerated type whose elements represent the different types of
 * node implementations.
 *
 * @author lare96 <http://github.com/lare96>
 */
public enum NodeType {

    /**
     * The element used to represent the {@link Item} implementation.
     */
    ITEM,

    /**
     * The element used to represent the {@link GameObject} implementation.
     */
    OBJECT,

    /**
     * The element used to represent the {@link Player} implementation.
     */
    PLAYER,

    /**
     * The element used to represent the {@link NPC} implementation.
     */
    NPC
}