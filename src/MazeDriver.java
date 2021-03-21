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
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.*;

public class MazeDriver {

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
	 * The {@code EV3UltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its left.
	 */
	private EV3UltrasonicSensor leftUltrasonicSensor = new EV3UltrasonicSensor(SensorPort.S4);
	
	/**
	 * The {@code EV3UltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its right.
	 */
	private NXTUltrasonicSensor rightUltrasonicSensor = new NXTUltrasonicSensor(SensorPort.S1);
	
	/**
	 * The {@code Wheel} controlling the left track.
	 */
	private Wheel leftWheel = WheeledChassis.modelWheel(new EV3LargeRegulatedMotor(MotorPort.D), WHEEL_DIAMETER).offset(-WHEEL_OFFSET);
	
	/**
	 * The {@code Wheel} controlling the right track.
	 */
	private Wheel rightWheel = WheeledChassis.modelWheel(new EV3LargeRegulatedMotor(MotorPort.B), WHEEL_DIAMETER).offset(WHEEL_OFFSET);
	
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
	 * Creates a {@code MazeDriver} object.
	 * 
	 * @param mazeStore The {@code MazeStore} used for storing the movements of the EV3 and constructing the maze digitally. 
	 */
	public MazeDriver(MazeStore mazeStore) {
		store = mazeStore;
		
		// Set up chassis and pilot.
		GyroWheeledChassis chassis = new GyroWheeledChassis(new Wheel[] {leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL, gyroSensor, gyroOrientation);
		pilot = new MovePilot(chassis);
		pilot.setAngularSpeed(10); // Set the angular speed to a low speed so that the angular momentum of the EV3 doesn't affect its ability to stop after rotating a desired angle too much.
		
		// Set up behaviours and arbitrator.
		arbitrator = new Arbitrator(new Behavior[] { // Create `Arbitrator` to manage behaviours.
			new MoveForward(pilot, store), // Move forward.
			new Centre(pilot, leftUltrasonicSensor, rightUltrasonicSensor), // Centre if needed.
			new TurnRight(pilot, store, touchSensor), // Turn right if needed.
			new TurnLeft(pilot, store, leftUltrasonicSensor) // Turn left if possible.
		});
		
		// Set up buttons.
		Button.ESCAPE.addKeyListener(new KeyListener() {
			public void keyPressed(Key k) {}
			
			public void keyReleased(Key k) {
				System.exit(0); // Exit.
			}
		});
	}
	
	/**
	 * Starts the {@code MazeDriver} by invoking the {@code Arbitrator}.<br/><br/>
	 * 
	 * This method does not return.
	 */
	public void go() {
		arbitrator.go();
	}

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

// TODO: MazeReader class which reads the maze into a series of movements to be performed.
// TODO: Web server.
// TODO: Finish implementing MazeStore (see MazeStore.java for more).
