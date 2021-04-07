package subsumption;
import lejos.robotics.subsumption.Behavior;
import robotics.MazeDriver;

/**
 * An implementation of LeJOS' {@code Behavior} interface used to make the EV3 move forward.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class MoveForward implements Behavior {
	// Private Fields
	
	/**
	 * The {@code MazeDriver} used for controlling the EV3.
	 */
	private MazeDriver driver;
	
	/**
	 * Whether the behaviour should be suppressed.
	 */
	private boolean shouldSuppress = false;
	
	// Public Constructors
	
	/**
	 * Creates a {@code MoveForward} object.<br/><br/>
	 * 
	 * This behaviour will move the EV3 forward and record the movement with a {@code MazeDriver} object.<br/>
	 * This behaviour will always take control.<br/>
	 * This behaviour is suppressible.
	 * 
	 * @param driver The {@code MazeDriver} used for controlling the EV3.
	 */
	public MoveForward(MazeDriver driver) {
		this.driver = driver;
	}
	
	// Behaviour

	public void action() {
		shouldSuppress = false;
		driver.forward();
		while (!shouldSuppress) {}
		driver.stop();
	}

	public void suppress() {
		shouldSuppress = true;
	}

	public boolean takeControl() {
		return true;
	}
	
}
