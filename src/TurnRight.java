import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class TurnRight implements Behavior {
	
	/**
	 * The {@code Pilot} used for controlling the movement of the EV3.
	 */
	private MovePilot pilot;
	
	/**
	 * The {@code MazeStore} used for storing the movements of the EV3 and constructing the maze digitally.
	 */
	private MazeStore store;
	
	/**
	 * The {@code SampleProvider} used for detecting touches from the {@code EV3TouchSensor}.
	 */
	private SampleProvider touch;
	
	/**
	 * Whether the action needs calling.
	 */
	private boolean actionNeeded = false;
	
	/**
	 * Creates a {@code TurnRight} object.<br/><br/>
	 * 
	 * This behaviour will turn the EV3 right and record the movement to a {@code MazeStore} object.<br/>
	 * This behaviour will take control if the {@code EV3TouchSensor} is activated.<br/>
	 * This behaviour is not suppressible.
	 * 
	 * @param pilot The {@code Pilot} used for controlling the movement of the EV3.
	 * @param mazeStore The {@code MazeStore} used for storing the movements of the EV3 and constructing the maze digitally.
	 * @param touchSensor The {@code EV3TouchSensor} used to detect when an object is in front of the EV3.
	 */
	public TurnRight(MovePilot pilot, MazeStore mazeStore, EV3TouchSensor touchSensor) {
		this.pilot = pilot;
		store = mazeStore;
		touch = touchSensor.getTouchMode();
	}

	public void action() {
		actionNeeded = false;
		pilot.travel(-20);
		pilot.rotate(90);
		store.recordLatestMove(pilot);
	}

	public void suppress() {}

	public boolean takeControl() {
		float[] distance = new float[1];
		touch.fetchSample(distance, 0);
		actionNeeded = actionNeeded || distance[0] == 1;
		return actionNeeded;
	}

}
