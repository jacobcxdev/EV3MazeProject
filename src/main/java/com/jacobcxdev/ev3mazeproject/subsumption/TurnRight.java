package com.jacobcxdev.ev3mazeproject.subsumption;

import com.jacobcxdev.ev3mazeproject.robotics.MazeDriver;
import com.jacobcxdev.ev3mazeproject.sensors.BaseTouchSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import org.slf4j.LoggerFactory;

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
	private final MazeDriver driver;
	
	/**
	 * The {@code SampleProvider} used for detecting touches from the {@code EV3TouchSensor}.
	 */
	private final SampleProvider touch;
	
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
	 *  @param driver The {@code MazeDriver} used for controlling the EV3.
	 * @param touchSensor The {@code EV3TouchSensor} used to detect when an object is in front of the EV3.
     */
	public TurnRight(MazeDriver driver, BaseTouchSensor touchSensor) {
		this.driver = driver;
		touch = touchSensor.getTouchMode();
	}

	// Behavior Override Methods

	@Override
	public void action() {
		actionNeeded = false;
		driver.moveRotate(90, true);
	}

	@Override
	public void suppress() {}

	@Override
	public boolean takeControl() {
		if (driver.getState() != MazeDriver.State.MAPPING) {
			return false;
		}
		var distance = new float[1];
		touch.fetchSample(distance, 0);
		actionNeeded = actionNeeded || distance[0] == 1;
		return actionNeeded;
	}
}
