import java.util.ArrayList;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MovePilot;

public class MazeStore {
	// TODO: Construct maze from moves.
	// TODO: Export maze to SVG (or some other portable vector-based format which can be communicated to a web server).
	
	/**
	 * The list of moves made by the EV3.
	 */
	private ArrayList<Move> moves = new ArrayList<Move>();
	
	/**
	 * Creates a {@code MazeStore} object.
	 */
	public MazeStore() {}
	
	/**
	 * Records the latest movement made by the EV3 to a list.
	 * 
	 * @param pilot The {@code MovePilot} to record the movement from.
	 */
	public void recordLatestMove(MovePilot pilot) {
		moves.add(pilot.getMovement());
	}

}
