package maze;
import java.util.ArrayList;
import java.util.List;

import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Move.MoveType;

/**
 * A class which implements LeJOS' {@code MoveListener} interface and is used to store and construct a maze navigated by a {@code MazeDriver}.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class MazeStore implements MoveListener {
	// Private Enumerations
	
	/**
	 * An enumeration to represent different 2D planes.
	 */
	private enum SquarePlane {
		HORIZONTAL,
		VERTICAL
	}
	
	// Private Fields
	
	/**
	 * The list of moves made by the EV3.
	 */
	private List<Move> moves = new ArrayList<>();
	
	// Public Constructors
	
	/**
	 * Creates a {@code MazeStore} object.
	 */
	public MazeStore() {}
	
	// Public Methods
	
	/**
	 * Constructs a {@code LineMap} from the moves made by the EV3.
	 * 
	 * @return A {@code LineMap} constructed from the moves made by the EV3.
	 */
	public LineMap constructLineMap() {
		int r = 200;
		int x = 0;
		int y = 0;
		int minX = 0;
		int minY = 0;
		int maxX = 0;
		int maxY = 0;
		int heading = 0;
		Move[] moves = this.moves.toArray(new Move[this.moves.size()]);
		List<Line> lines = new ArrayList<>();
		
		for (int i = 0; i < moves.length; i++) {
			Move move = moves[i];
			Move previous = i > 0 ? moves[i - 1] : null;
			Move next = i < moves.length - 1 ? moves[i + 1] : null;
			int previousRotation = 0;
			int nextRotation = 0;
			
			if (previous != null && previous.getMoveType() == MoveType.ROTATE) {
				previousRotation = (int)previous.getAngleTurned();
			}
			
			if (next != null && next.getMoveType() == MoveType.ROTATE) {
				nextRotation = (int)next.getAngleTurned();
			}
			
			switch (move.getMoveType()) {
			case ROTATE:
				int angle = (int)move.getAngleTurned();
				if (angle % 90 != 0) {
					throw new RuntimeException(String.format("Angle %d not a multiple of 90 degrees.", angle));
				}
				
				heading += angle;
				heading += heading >= 360 ? -360 : heading < 0 ? 360 : 0;
				break;
			case TRAVEL:
				int x2 = x;
				int y2 = y;
				int yDelta = (int)move.getDistanceTraveled();
				int startStretch = (int)Math.signum(previousRotation) * r;
				int endStretch = (int)Math.signum(nextRotation) * r;
				SquarePlane plane;
				
				switch (heading) {
				case 0:
					plane = SquarePlane.VERTICAL;
					y2 += yDelta;
					break;
				case 90:
					plane = SquarePlane.HORIZONTAL;
					x2 += yDelta;
					break;
				case 180:
					plane = SquarePlane.VERTICAL;
					y2 -= yDelta;
					break;
				case 270:
					plane = SquarePlane.HORIZONTAL;
					x2 -= yDelta;
					break;
				default:
					throw new RuntimeException(String.format("Heading %d not a multiple of 90 degrees.", heading));
				}

				Line line1 = null;
				Line line2 = null;
				
				switch (plane) {
				case HORIZONTAL:
					line1 = new Line(x + startStretch, y - r, x2 - endStretch, y2 - r);
					line2 = new Line(x - startStretch, y + r, x2 + endStretch, y2 + r);
					break;
				case VERTICAL:
					line1 = new Line(x - r, y - startStretch, x2 - r, y2 + endStretch);
					line2 = new Line(x + r, y + startStretch, x2 + r, y2 - endStretch);
				}
				
				x = x2;
				y = y2;
				minX = (int)Math.min(minX, line1.x1);
				minX = (int)Math.min(minX, line2.x1);
				minY = (int)Math.min(minY, line1.y1);
				minY = (int)Math.min(minY, line2.y1);
				maxX = (int)Math.max(maxX, line1.x1);
				maxX = (int)Math.max(maxX, line2.x1);
				maxY = (int)Math.max(maxY, line1.y1);
				maxY = (int)Math.max(maxY, line2.y1);
				
				lines.add(line1);
				lines.add(line2);
				break;
			default:
			}
		}
		return new LineMap(lines.toArray(new Line[0]), new Rectangle(minX, minY, maxX - minX, maxY - minY));
	}
	
	// Override Methods

	@Override
	public void moveStarted(Move event, MoveProvider mp) {
		switch (event.getMoveType()) {
		case ROTATE:
			moves.add(new Move(Move.MoveType.ROTATE, 0, event.getAngleTurned(), false));
			break;
		default:
		}
	}

	@Override
	public void moveStopped(Move event, MoveProvider mp) {
		switch (event.getMoveType()) {
		case TRAVEL:
			moves.add(event);
			break;
		default:
		}
	}

}
