package subsumption;

import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import robotics.MazeDriver;

/**
 * An implementation of LeJOS' {@code Behavior} interface used to make the EV3 turn right.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class TurnRight implements Behavior {
	// Private Fields
	
	/**
	 * The {@code MazeDriver} used for controlling the EV3.
	 */
	private MazeDriver driver;
	
	/**
	 * The {@code SampleProvider} used for detecting touches from the {@code EV3TouchSensor}.
	 */
	private SampleProvider touch;
	
	/**
	 * Whether the action needs calling.
	 */
	private boolean actionNeeded = false;
	
	// Public Constructors
	
	/**
	 * Creates a {@code TurnRight} object.<br/><br/>
	 * 
	 * This behaviour will turn the EV3 right and record the movement with a {@code MazeDriver} object.<br/>
	 * This behaviour will take control if the {@code EV3TouchSensor} is activated.<br/>
	 * This behaviour is not suppressible.
	 * 
	 * @param driver The {@code MazeDriver} used for controlling the EV3.
	 * @param touchSensor The {@code EV3TouchSensor} used to detect when an object is in front of the EV3.
	 */
	public TurnRight(MazeDriver driver, EV3TouchSensor touchSensor) {
		this.driver = driver;
		touch = touchSensor.getTouchMode();
	}
	
	// Behaviour Methods

	public void action() {
		actionNeeded = false;
		driver.moveRotate(90, true);
	}

	public void suppress() {}

	public boolean takeControl() {
		if (driver.getState() != MazeDriver.State.MAPPING) {
			return false;
		}
		float[] distance = new float[1];
		touch.fetchSample(distance, 0);
		actionNeeded = actionNeeded || distance[0] == 1;
		return actionNeeded;
	}

}
