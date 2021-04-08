import java.io.IOException;

import mapping.MazeStore;
import networking.ServerThread;
import robotics.MazeDriver;

/**
 * A class used to store the {@code main} method invoked by the EV3's JVM, along with the shared state of the EV3.<br/><br/>
 * 
 * The EV3 Maze Project is our first year CS1822 project @ <a href="https://www.royalholloway.ac.uk">Royal Holloway College, University of London</a> (group R5).
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 * @author Roshan Gajjar (<a href="https://github.com/roshan-RG">@roshan-RG</a>)
 * @author Aman Vaidya (<a href="https://github.com/Amanv16">@Amanv16</a>)
 * @author Adam Zemiri (<a href="https://github.com/Xpl0itR">@Xpl0itR</a>)
 */
public class EV3MazeProject {
	// Public Enumerations
	
	/**
	 * An enumeration to represent the different possible states of the {@code EV3MazeProject}.
	 */
	public enum State {
		STARTUP,
		STANDBY,
		MOBILE
	}
	
	// Private Static Fields
	
	/**
	 * The current {@code State} of the {@code EV3MazeProject}.
	 */
	private static State state = State.STARTUP;
	
	// Public Static Getters/Setters
	
	/**
	 * Gets the current {@code State} of the {@code EV3MazeProject}.
	 * 
	 * @return The current {@code State} of the {@code EV3MazeProject}.
	 */
	public static State getState() {
		return state;
	}
	
	/**
	 * Sets the current {@code State} of the {@code EV3MazeProject} to a given new {@code State}.
	 * 
	 * @param state The new {@code State} of the {@code EV3MazeProject}.
	 */
	public static void setState(State state) {
		EV3MazeProject.state = state;
	}
	
	// Public Static Methods

	/**
	 * The main method which is invoked by the EV3's JVM.
	 * 
	 * @param args The {@code String} arguments passed to the program.
	 */
	public static void main(String[] args) {
		ServerThread server = new ServerThread();
//		server.start();
		server.run();
		
		MazeStore mazeStore = new MazeStore();
		MazeDriver driver = new MazeDriver(mazeStore);
		try {
			driver.startMapping().createSVGFile("maze.svg");
		} catch (IOException e) {
			e.printStackTrace();
		};
	}
	
}
