import maze.MazeStore;
import robotics.MazeDriver;

/**
 * A class used to store the {@code main} method invoked by the EV3's JVM.<br/><br/>
 * 
 * The EV3 Maze Project is our first year CS1822 project @ <a href="https://www.royalholloway.ac.uk">Royal Holloway College, University of London</a> (group R5).
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 * @author Roshan Gajjar (<a href="https://github.com/roshan-RG">@roshan-RG</a>)
 * @author Aman Vaidya (<a href="https://github.com/Amanv16">@Amanv16</a>)
 * @author Adam Zemiri (<a href="https://github.com/Xpl0itR">@Xpl0itR</a>)
 */
public class EV3MazeProject {
	// Static Methods

	/**
	 * The main method which is invoked by the EV3's JVM.
	 * 
	 * @param args The {@code String} arguments passed to the program.
	 */
	public static void main(String[] args) {
		MazeStore mazeStore = new MazeStore();
		MazeDriver driver = new MazeDriver(mazeStore);
		driver.go();
	}
	
}

// TODO: Fix a bug with rotation speed in GyroWheeledChassis.java.
// TODO: Finish implementing behaviours (in particular, Centre.java).
// TODO: MazeReader class which reads the maze into a series of movements to be performed.
// TODO: Web server.
