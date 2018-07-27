package com.elvarg;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.elvarg.net.NetworkConstants;
import com.elvarg.util.flood.Flooder;
import com.google.common.base.Preconditions;

/**
 * The starting point of Elvarg.
 * Starts the game server.
 * 
 * @author Professor Oak
 * @author Lare96
 */
public class Server {
	
	/**
	 * The logger that will print important information.
	 */
	private static Logger logger = Logger.getLogger(Server.class.getSimpleName());
	
	/**
	 * The flag that determines if the server is currently being updated or not.
	 */
	private static boolean isUpdating;
	
	/**
	 * The flooder used to stress-test the server.
	 */
	private static Flooder flooder = new Flooder();

	/**
	 * The default constructor, will throw an
	 * {@link UnsupportedOperationException} if instantiated.
	 *
	 * @throws UnsupportedOperationException
	 *             if this class is instantiated.
	 */
	private Server() {
		throw new UnsupportedOperationException("This class cannot be instantiated!");
	}

	/**
	 * The main method that will put the server online.
	 */
	public static void main(String[] args) {
		try {
			Preconditions.checkState(args.length == 0, "No runtime arguments needed!");
			logger.info("Initializing the Bootstrap...");
			Bootstrap bootstrap = new Bootstrap(NetworkConstants.GAME_PORT);
			bootstrap.bind();
			logger.info("The Bootstrap has been bound, Elvarg is now online!");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An error occurred while binding the Bootstrap!", e);

			// No point in continuing server startup when the
			// bootstrap either failed to bind or was bound
			// incorrectly.
			System.exit(1);
		}
	}
	
	public static Logger getLogger() {
		return logger;
	}

	public static boolean isUpdating() {
		return isUpdating;
	}

	public static void setUpdating(boolean isUpdating) {
		Server.isUpdating = isUpdating;
	}
	
	public static Flooder getFlooder() {
		return flooder;
	}
}