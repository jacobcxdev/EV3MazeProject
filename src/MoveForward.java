import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class MoveForward implements Behavior {
	
	/**
	 * The {@code Pilot} used for controlling the movement of the EV3.
	 */
	private MovePilot pilot;
	
	/**
	 * The {@code MazeStore} used for storing the movements of the EV3 and constructing the maze digitally.
	 */
	private MazeStore store;
	
	/**
	 * Whether the behaviour should be suppressed.
	 */
	private boolean shouldSuppress = false;
	
	/**
	 * Creates a {@code MoveForward} object.<br/><br/>
	 * 
	 * This behaviour will move the EV3 forward and record the movement to a {@code MazeStore} object.<br/>
	 * This behaviour will always take control.<br/>
	 * This behaviour is suppressible.
	 * 
	 * @param pilot The {@code Pilot} used for controlling the movement of the EV3.
	 * @param mazeStore The {@code MazeStore} used for storing the movements of the EV3 and constructing the maze digitally.
	 */
	public MoveForward(MovePilot pilot, MazeStore mazeStore) {
		this.pilot = pilot;
	}

	public void action() {
		shouldSuppress = false;
		pilot.forward();
		while (!shouldSuppress) {}
		pilot.stop();
		store.recordLatestMove(pilot);
	}

	public void suppress() {
		shouldSuppress = true;
	}

	public boolean takeControl() {
		return true;
	}
	
}
