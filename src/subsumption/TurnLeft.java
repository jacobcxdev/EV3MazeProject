package subsumption;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import robotics.MazeDriver;

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
	private MazeDriver driver;
	
	/**
	 * The {@code SampleProvider} used for measuring the distance with the {@code EV3UltrasonicSensor}.
	 */
	private SampleProvider ultrasonic;
	
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
	 * @param leftUltrasonicSensor The {@code EV3UltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its left.
	 */
	public TurnLeft(MazeDriver driver, EV3UltrasonicSensor leftUltrasonicSensor) {
		this.driver = driver;
		ultrasonic = leftUltrasonicSensor.getDistanceMode();
	}
	
	// Behaviour

	public void action() {
		driver.rotate(-90, true);
		locked = true;
	}

	public void suppress() {}

	public boolean takeControl() {
		float[] distance = new float[1];
		ultrasonic.fetchSample(distance, 0);
		if (distance[0] <= .3) {
			locked = false;
		} else {
			return !locked;
		}
		return false;
	}

}
