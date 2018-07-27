package com.elvarg.game;

import com.elvarg.game.model.Position;

/**
 * A class containing different attributes
 * which affect the game in different ways.
 * @author Professor Oak
 */
public class GameConstants {
	
	/**
	 * The current game/client version.
	 */
	public static final int GAME_VERSION = 3;

	/**
	 * The secure game UID /Unique Identifier/ 
	 */
	public static final int GAME_UID = 4 >> 1;

	/**
	 * The directory of the definition files.
	 */
	public static final String DEFINITIONS_DIRECTORY = "./data/definitions/";
	
	/**
	 * The directory of the clipping files.
	 */
	public static final String CLIPPING_DIRECTORY = "./data/clipping/";

    /**
     * The flag that determines if processing should be parallelized, improving
     * the performance of the server times {@code n} (where
     * {@code n = Runtime.getRuntime().availableProcessors()}) at the cost of
     * substantially more CPU usage.
     */
    public static final boolean CONCURRENCY = (Runtime.getRuntime().availableProcessors() > 1);
    
	/**
	 * The game engine cycle rate in milliseconds.
	 */
	public static final int GAME_ENGINE_PROCESSING_CYCLE_RATE = 600;

	/**
	 * The maximum amount of iterations for a queue/list that should occur each cycle.
	 */
	public static final int QUEUED_LOOP_THRESHOLD = 45;

	/**
	 *  The default position, where players will
	 *  spawn upon logging in for the first time.
	 */
	public static final Position DEFAULT_POSITION = new Position(3093, 3509);

	/**
	 * The default clan chat a player will join upon logging in,
	 * if they aren't in one already.
	 */
	public static final String DEFAULT_CLAN_CHAT = "";
	
	/**
	 * Should the inventory be refreshed immediately
	 * on switching items or should it be delayed
	 * until next game cycle?
	 */
	public static final boolean QUEUE_SWITCHING_REFRESH = false;

    /**
     * The maximum amount of drops that can be rolled from the dynamic drop
     * table.
     */
    public static final int DROP_THRESHOLD = 2;
    
	/**
	 * Multiplies the experience gained.
	 */
	public static final double EXP_MULTIPLIER = 6;
	
	/**
	 * The gameframe's tab interface ids.
	 */
	public static final int TAB_INTERFACES[] = {2423, 3917, 638, 3213, 1644, 5608, -1, 37128, 5065, 5715, 2449, 42500, 147, 32000};
}
