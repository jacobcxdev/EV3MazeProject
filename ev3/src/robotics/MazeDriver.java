package robotics;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.*;
import mapping.MazeStore;
import sensors.BaseUltrasonicSensor;
import sensors.EV3UltrasonicSensorAdapter;
import sensors.NXTUltrasonicSensorAdapter;
import subsumption.MoveForward;
import subsumption.TurnLeft;
import subsumption.TurnRight;

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
	 * The width between the two {@code BaseUltrasonicSensor}s (in millimetres).
	 */
	private final static float ULTRASONIC_SENSOR_GAP = 150;

	/**
	 * The diameter of the EV3's wheels (in millimetres).
	 */
	private final static float WHEEL_DIAMETER = 45;

	/**
	 * The offset of the EV3's wheels from the centre (in millimetres).
	 */
	private final static float WHEEL_OFFSET = 800;
	
	/**
	 * The orientation of the {@code EV3GyroSensor}.
	 */
	private final static GyroWheeledChassis.GyroOrientation gyroOrientation = GyroWheeledChassis.GyroOrientation.GLYPH_TOP;

	/**
	 * The {@code EV3TouchSensor} used to detect when an object is in front of the EV3.
	 */
	private EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S2);
	
	/**
	 * The {@code EV3GyroSensor} used to measure angles for the EV3.
	 */
	private EV3GyroSensor gyroSensor = new EV3GyroSensor(SensorPort.S3);
	
	/**
	 * The {@code BaseUltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its left.
	 */
	private BaseUltrasonicSensor leftUltrasonicSensor = new EV3UltrasonicSensorAdapter(new EV3UltrasonicSensor(SensorPort.S4));
	
	/**
	 * The {@code BaseUltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its right.
	 */
	private BaseUltrasonicSensor rightUltrasonicSensor = new NXTUltrasonicSensorAdapter(new NXTUltrasonicSensor(SensorPort.S1));
	
	/**
	 * The {@code Wheel} controlling the left track.
	 */
	private Wheel leftWheel = WheeledChassis.modelWheel(new EV3LargeRegulatedMotor(MotorPort.D), WHEEL_DIAMETER).offset(-WHEEL_OFFSET);
	
	/**
	 * The {@code Wheel} controlling the right track.
	 */
	private Wheel rightWheel = WheeledChassis.modelWheel(new EV3LargeRegulatedMotor(MotorPort.B), WHEEL_DIAMETER).offset(WHEEL_OFFSET);
	
	/**
	 * The {@code GyroWheeledChassis} used for controlling the wheels.
	 */
	private GyroWheeledChassis chassis;
	
	/**
	 * The {@code Pilot} used for controlling the movement of the EV3.
	 */
	private MovePilot pilot;

	/**
	 * The {@code MazeStore} used for storing the movements of the EV3 and constructing the maze digitally.
	 */
	private MazeStore store;
	
	/**
	 * The {@code Arbitrator} used for managing behaviours.
	 */
	private Arbitrator arbitrator;
	
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
	public MazeDriver(MazeStore mazeStore) {
		store = mazeStore;
		
		// Set up buttons.
		Button.ESCAPE.addKeyListener(new KeyListener() {
			public void keyPressed(Key k) {}
			
			public void keyReleased(Key k) {
				stopMapping();
				System.exit(0); // Exit.
			}
		});
		
		// Set up the chassis.
		chassis = new GyroWheeledChassis(new Wheel[] {leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL, gyroSensor, gyroOrientation);
		chassis.calibrateGyroSensor();
		
		// Set up the pilot.
		pilot = new MovePilot(chassis);
		pilot.setAngularSpeed(10); // Set the angular speed to a low speed so that the angular momentum of the EV3 doesn't affect its ability to stop after rotating a desired angle too much.
		pilot.setLinearSpeed(100); // Set the linear speed to a low speed so that the linear momentum of the EV3 doesn't affect its ability to stop, causing it to overshoot turns.
		
		// Set up behaviours and arbitrator.
		arbitrator = new Arbitrator(new Behavior[] { // Create `Arbitrator` to manage behaviours.
			new MoveForward(this, leftUltrasonicSensor, rightUltrasonicSensor, ULTRASONIC_SENSOR_GAP), // Move forward.
			new TurnRight(this, touchSensor), // Turn right if needed.
			new TurnLeft(this, leftUltrasonicSensor) // Turn left if possible.
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
		stopMapping();
		this.state = State.MAPPING;
		store.reset();
		pilot.addMoveListener(store);
		arbitrator.go();
		return store.constructLineMap();
	}
	
	/**
	 * Stops mapping the maze by stopping the {@code Arbitrator}.
	 */
	public void stopMapping() {
		arbitrator.stop();
		moveStop();
		while (pilot.isMoving()) {}
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
