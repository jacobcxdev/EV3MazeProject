package com.jacobcxdev.ev3mazeproject.subsumption;

import com.jacobcxdev.ev3mazeproject.robotics.MazeDriver;
import com.jacobcxdev.ev3mazeproject.sensors.BaseUltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import org.slf4j.LoggerFactory;

/**
 * An implementation of LeJOS' {@code Behavior} interface used to make the EV3 turn left.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class TurnLeft implements Behavior {
	// Private Fields
	
	/**
	 * The {@code MazeDriver} used for controlling the EV3.
	 */
	private final MazeDriver driver;
	
	/**
	 * The {@code SampleProvider} used for measuring the distance with the {@code BaseUltrasonicSensor}.
	 */
	private final SampleProvider ultrasonic;
	
	/**
	 * Whether this behaviour is currently locked.
	 */
	private boolean locked = false;
	
	// Public Constructors
	
	/**
	 * Creates a {@code TurnLeft} object.<br/><br/>
	 * 
	 * This behaviour will turn the EV3 left and record the movement with a {@code MazeDriver} object.<br/>
	 * This behaviour will take control if there is greater than 30 cm of distance between the EV3 and the nearest object on its left.<br/>
	 * This behaviour is not suppressible.
	 * 
	 * @param driver The {@code MazeDriver} used for controlling the EV3.
	 * @param leftUltrasonicSensor The {@code BaseUltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its left.
	 */
	public TurnLeft(MazeDriver driver, BaseUltrasonicSensor leftUltrasonicSensor) {
		this.driver = driver;
		ultrasonic = leftUltrasonicSensor.getDistanceMode();
	}

	// Behavior Override Methods

	@Override
	public void action() {
		driver.moveRotate(-90, true);
		locked = true;
	}

	@Override
	public void suppress() {}

	@Override
	public boolean takeControl() {
		if (driver.getState() != MazeDriver.State.MAPPING) {
			return false;
		}
		var distance = new float[1];
		ultrasonic.fetchSample(distance, 0);
		var canTurn = distance[0] <= .3;
		if (!canTurn) {
			locked = false;
		}
		return canTurn && !locked;
	}
}
