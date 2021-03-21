import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class TurnLeft implements Behavior {
	
	/**
	 * The {@code Pilot} used for controlling the movement of the EV3.
	 */
	private MovePilot pilot;
	
	/**
	 * The {@code MazeStore} used for storing the movements of the EV3 and constructing the maze digitally.
	 */
	private MazeStore store;
	
	/**
	 * The {@code SampleProvider} used for measuring the distance with the {@code EV3UltrasonicSensor}.
	 */
	private SampleProvider ultrasonic;
	
	/**
	 * Whether this behaviour is currently locked.
	 */
	private boolean locked = false;
	
	/**
	 * Creates a {@code TurnLeft} object.<br/><br/>
	 * 
	 * This behaviour will turn the EV3 left and record the movement to a {@code MazeStore} object.<br/>
	 * This behaviour will take control if there is greater than 30 cm of distance between the EV3 and the nearest object on its left.<br/>
	 * This behaviour is not suppressible.
	 * 
	 * @param pilot The {@code Pilot} used for controlling the movement of the EV3.
	 * @param mazeStore The {@code MazeStore} used for storing the movements of the EV3 and constructing the maze digitally.
	 * @param leftUltrasonicSensor The {@code EV3UltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its left.
	 */
	public TurnLeft(MovePilot pilot, MazeStore mazeStore, EV3UltrasonicSensor leftUltrasonicSensor) {
		this.pilot = pilot;
		store = mazeStore;
		ultrasonic = leftUltrasonicSensor.getDistanceMode();
	}

	public void action() {
		pilot.rotate(-90);
		store.recordLatestMove(pilot);
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
