package com.jacobcxdev.ev3mazeproject.mapping;

import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Move.MoveType;

import java.util.ArrayList;
import java.util.List;

/**
 * A class which implements LeJOS' {@code MoveListener} interface and is used to store and construct a maze navigated by a {@code MazeDriver}.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class MazeStore implements MoveListener {
	// Private Static Classes
	
	/**
	 * A class used to hold a {@code Move} made by the EV3 and the associated {@code Line}s.
	 */
	private static class MoveLines {
		// Public Fields
		
		/**
		 * A {@code Move} made by the EV3.
		 */
		public Move move;

		/**
		 * A {@code Line} associated with the {@code Move}.
		 */
		public Line line1;
		
		/**
		 * A {@code Line} associated with the {@code Move}.
		 */
		public Line line2;
		
		// Public Constructors
		
		/**
		 * Creates a {@code MoveLines} object.
		 * 
		 * @param move A {@code Move} made by the EV3.
		 * @param line1 A {@code Line} associated with the {@code Move}.
		 * @param line2 A {@code Line} associated with the {@code Move}.
		 */
		public MoveLines(Move move, Line line1, Line line2) {
			this.move = move;
			this.line1 = line1;
			this.line2 = line2;
		}
	}
	
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
	private final List<Move> moves = new ArrayList<>();
	
	/**
	 * The narrowest road width recorded (in millimetres).
	 */
	private double minRoadWidth = Double.MAX_VALUE;
	
	// Public Constructors
	
	/**
	 * Creates a {@code MazeStore} object.
	 */
	public MazeStore() {}
	
	// Public Methods

	/**
	 * Records a given road width.
	 * 
	 * @param roadWidth The road width to record (in millimetres).
	 */
	public void recordRoadWidth(float roadWidth) {
		minRoadWidth = Math.min(minRoadWidth, roadWidth);
	}
	
	/**
	 * Resets the {@code MazeStore}.
	 */
	public void reset() {
		minRoadWidth = Double.MAX_VALUE;
		moves.clear();
	}
	
	/**
	 * Constructs a {@code LineMap} from the moves made by the EV3.
	 * 
	 * @return A {@code LineMap} constructed from the moves made by the EV3.
	 */
	public LineMap constructLineMap() {
		System.out.println("Constructing LineMap...");
		var r = minRoadWidth != Double.MAX_VALUE ? (int)minRoadWidth : 200;
		var x = 0;
		var y = 0;
		var minX = 0;
		var minY = 0;
		var maxX = 0;
		var maxY = 0;
		var heading = 0;
		var moves = this.moves.toArray(new Move[0]);
		List<MoveLines> moveLines = new ArrayList<>();
		
		for (int i = 0; i < moves.length; i++) {
			var move = moves[i];
			var previous = i > 0 ? moves[i - 1] : null;
			var next = i < moves.length - 1 ? moves[i + 1] : null;
			var previousRotation = 0;
			var nextRotation = 0;
			
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
				var x2 = x;
				var y2 = y;
				var delta = (int)move.getDistanceTraveled();
				var startStretch = (int)Math.signum(previousRotation) * r;
				var endStretch = (int)Math.signum(nextRotation) * r;
				SquarePlane plane;

				switch (heading) {
					case 0 -> {
						plane = SquarePlane.VERTICAL;
						y2 += delta;
					}
					case 90 -> {
						plane = SquarePlane.HORIZONTAL;
						x2 += delta;
					}
					case 180 -> {
						plane = SquarePlane.VERTICAL;
						y2 -= delta;
					}
					case 270 -> {
						plane = SquarePlane.HORIZONTAL;
						x2 -= delta;
					}
					default -> throw new RuntimeException(String.format("Heading %d not a multiple of 90 degrees.", heading));
				}
	
				Line line1 = null;
				Line line2 = null;

				switch (plane) {
					case HORIZONTAL -> {
						line1 = new Line(x + startStretch, y - r, x2 - endStretch, y2 - r);
						line2 = new Line(x - startStretch, y + r, x2 + endStretch, y2 + r);
					}
					case VERTICAL -> {
						line1 = new Line(x - r, y - startStretch, x2 - r, y2 + endStretch);
						line2 = new Line(x + r, y + startStretch, x2 + r, y2 - endStretch);
					}
				}
				
				moveLines.add(new MoveLines(move, line1, line2));
				
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
				break;
			default:
			}
		}
		
		List<Line> lines = new ArrayList<>();
		for (var e : moveLines) {
			lines.add(e.line1);
			lines.add(e.line2);
		}
		
		var nMoveLines = moveLines.size();
		if (nMoveLines > 1) {
			var first = moveLines.get(0);
			var last = moveLines.get(nMoveLines - 1);
			Line line1 = new Line(first.line1.x1, first.line1.y1, last.line1.x2, last.line1.y2);
			Line line2 = new Line(first.line2.x1, first.line2.y1, last.line2.x2, last.line2.y2);
			lines.add(line1);
			lines.add(line2);
		}

		System.out.println("Constructed LineMap.");
		return new LineMap(lines.toArray(new Line[0]), new Rectangle(minX, minY, maxX - minX, maxY - minY));
	}
	
	// MoveListener Override Methods

	@Override
	public void moveStarted(Move event, MoveProvider mp) {
		if (event.getMoveType() == MoveType.ROTATE) {
			moves.add(new Move(MoveType.ROTATE, 0, event.getAngleTurned(), false));
		}
	}

	@Override
	public void moveStopped(Move event, MoveProvider mp) {
		if (event.getMoveType() == MoveType.TRAVEL) {
			moves.add(event);
		}
	}
}
