package com.elvarg.game.model.dialogue;

import com.elvarg.game.entity.impl.player.Player;

/**
 * An abstract class for handling dialogue options.
 * 
 * @author Professor Oak
 */
public abstract class DialogueOptions {

	public abstract void handleOption(Player player, int option);
}
