package com.jacobcxdev.ev3mazeproject.robotics;

import com.jacobcxdev.ev3mazeproject.hardware.BuildDescription;
import com.jacobcxdev.ev3mazeproject.hardware.PortConfiguration;
import com.jacobcxdev.ev3mazeproject.mapping.MazeStore;
import com.jacobcxdev.ev3mazeproject.subsumption.MoveForward;
import com.jacobcxdev.ev3mazeproject.subsumption.TurnLeft;
import com.jacobcxdev.ev3mazeproject.subsumption.TurnRight;
import ev3dev.sensors.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

/**
 * A class used to control the movement of the EV3 through a maze.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class MazeDriver {
	// Public Enumerations
	
	/**
	 * An enumeration to represent the different possible states of the {@code MazeDriver}.
	 */
	public enum State {
		MAPPING,
		NAVIGATING,
		STANDBY
	}
	
	// Private Fields
	
	/**
	 * The {@code GyroWheeledChassis} used for controlling the wheels.
	 */
	private final GyroWheeledChassis chassis;
	
	/**
	 * The {@code Pilot} used for controlling the movement of the EV3.
	 */
	private final MovePilot pilot;

	/**
	 * The {@code MazeStore} used for storing the movements of the EV3 and constructing the maze digitally.
	 */
	private final MazeStore store;
	
	/**
	 * The {@code Arbitrator} used for managing behaviours.
	 */
	private final Arbitrator arbitrator;
	
	/**
	 * The current {@code State} of the {@code MazeDriver}.
	 */
	private State state = State.STANDBY;
	
	// Public Constructors
	
	/**
	 * Creates a {@code MazeDriver} object.
	 * 
	 * @param mazeStore The {@code MazeStore} used for storing the movements of the EV3 and constructing the maze digitally. 
	 */
	public MazeDriver(BuildDescription buildDescription, PortConfiguration portConfiguration, MazeStore mazeStore) {
		store = mazeStore;
		
		// Set up buttons.
		Button.ESCAPE.addKeyListener(new KeyListener() {
			public void keyPressed(Key k) {}
			
			public void keyReleased(Key k) {
				if (state == State.MAPPING) {
					stopMapping();
				}
//				System.exit(0); // Exit.
			}
		});
		
		// Set up the chassis.
		var leftWheel = WheeledChassis.modelWheel(portConfiguration.leftRegulatedMotor, buildDescription.wheelDiameter).offset(-buildDescription.wheelOffset);
		var rightWheel = WheeledChassis.modelWheel(portConfiguration.rightRegulatedMotor, buildDescription.wheelDiameter).offset(buildDescription.wheelOffset);
		portConfiguration.gyroSensor.setGyroOrientation(buildDescription.gyroOrientation);
		chassis = new GyroWheeledChassis(new Wheel[] {leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL, portConfiguration.gyroSensor);
		chassis.calibrateGyroSensor();
		
		// Set up the pilot.
		pilot = new MovePilot(chassis);
		pilot.setAngularSpeed(30); // Set the angular speed to a low speed so that the angular momentum of the EV3 doesn't affect its ability to stop after rotating a desired angle too much.
		pilot.setLinearSpeed(100); // Set the linear speed to a low speed so that the linear momentum of the EV3 doesn't affect its ability to stop, causing it to overshoot turns.

		// Set up behaviours and arbitrator.
		arbitrator = new Arbitrator(new Behavior[] { // Create `Arbitrator` to manage behaviours.
			new MoveForward(this, portConfiguration.leftUltrasonicSensor, portConfiguration.rightUltrasonicSensor, buildDescription.ultrasonicSensorGap), // Move forward.
			new TurnRight(this, portConfiguration.touchSensor), // Turn right if needed.
			new TurnLeft(this, portConfiguration.leftUltrasonicSensor) // Turn left if possible.
		}, true);
	}
	
	// Public Getters/Setters
	
	/**
	 * Gets the current {@code State} of the {@code MazeDriver}.
	 * 
	 * @return The current {@code State} of the {@code MazeDriver}.
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * Sets the current {@code State} of the {@code MazeDriver} to a given new {@code State}.
	 * 
	 * @param state The new {@code State} of the {@code MazeDriver}.
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	// Public Methods

	/**
	 * Records a given road width to the {@code MazeStore}.
	 * 
	 * @param roadWidth The road width to record (in millimetres).
	 */
	public void recordRoadWidth(float roadWidth) {
		store.recordRoadWidth(roadWidth);
	}
	
	/**
	 * Starts mapping the maze by invoking the {@code Arbitrator}.<br/><br/>
	 * 
	 * @return The {@code LineMap} constructed from the moves made while mapping the maze.
	 */
	public LineMap startMapping() {
		if (this.state == State.MAPPING) {
			stopMapping();
		}
		this.state = State.MAPPING;
		store.reset();
		pilot.addMoveListener(store);
		System.out.println("Started mapping...");
		arbitrator.go();
		System.out.println("Finished mapping.");
		return store.constructLineMap();
	}
	
	/**
	 * Stops mapping the maze by stopping the {@code Arbitrator}.
	 */
	public void stopMapping() {
		System.out.println("Stopping mapping...");
		arbitrator.stop();
		synchronized (this) {
			notify();
		}
		moveStop();
		while (pilot.isMoving()) {
			Thread.yield();
		}
		this.state = State.STANDBY;
	}
	
	// MovePilot Wrapper Methods
	
	/**
	 * Moves the EV3 forward.
	 */
	public void moveForward() {
		pilot.forward();
	}
	
	/**
	 * Rotates the EV3 a given angle.
	 * 
	 * @param angle The angle to rotate in degrees.
	 * @param shouldStore Whether the movement should be stored (performed by the {@code MovePilot} vs. the chassis).
	 */
	public void moveRotate(double angle, boolean shouldStore) {
		if (shouldStore) {
			pilot.rotate(angle);
		} else {
			chassis.rotate(angle);
		}
	}
	
	/**
	 * Stops the EV3.
	 */
	public void moveStop() {
		pilot.stop();
	}
	
	/**
	 * Makes the EV3 travel a given distance.
	 * 
	 * @param distance The distance to travel.
	 * @param shouldStore Whether the movement should be stored (performed by the {@code MovePilot} vs. the chassis).
	 */
	public void moveTravel(double distance, boolean shouldStore) {
		if (shouldStore) {
			pilot.travel(distance);
		} else {
			chassis.travel(distance);
		}
	}
}
