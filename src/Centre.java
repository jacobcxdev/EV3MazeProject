import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class Centre implements Behavior {
	
	/**
	 * An enumeration to represent all possible directions the EV3 can turn.
	 */
	private enum TurnDirection {
		LEFT(-90),
		RIGHT(90),
		NONE(0);
		
		/**
		 * The angle for the EV3 to turn.
		 */
		public double angle;
		
		/**
		 * Creates a {@code TurnDirection} instance.
		 * 
		 * @param angle The angle for the EV3 to turn.
		 */
		private TurnDirection(double angle) {
			this.angle = angle;
		}
	}
	
	/**
	 * The {@code Pilot} used for controlling the movement of the EV3.
	 */
	private MovePilot pilot;
	
	/**
	 * The {@code SampleProvider} used for measuring the distance with the left {@code EV3UltrasonicSensor}.
	 */
	private SampleProvider leftUltrasonic;
	
	/**
	 * The {@code SampleProvider} used for measuring the distance with the right {@code NXTUltrasonicSensor}.
	 */
	private SampleProvider rightUltrasonic;
	
	/**
	 * The {@code TurnDirection} to turn.
	 */
	private TurnDirection turnDirection = TurnDirection.NONE;
	
	/**
	 * The distance to travel after having turned.
	 */
	private float delta = 0;
	
	/**
	 * Creates a {@code Centre} object.<br/><br/>
	 * 
	 * This behaviour will centre the EV3 between the nearest objects on its left and right.<br/>
	 * This behaviour will take control if the absolute value of the difference between the distances to the nearest objects on the EV3's left and right is greater than 10 cm.<br/>
	 * This behaviour is not suppressible.
	 * 
	 * @param pilot The {@code Pilot} used for controlling the movement of the EV3.
	 * @param leftUltrasonicSensor The {@code EV3UltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its left.
	 * @param rightUltrasonicSensor The {@code EV3UltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its right.
	 */
	public Centre(MovePilot pilot, EV3UltrasonicSensor leftUltrasonicSensor, NXTUltrasonicSensor rightUltrasonicSensor) {
		this.pilot = pilot;
		leftUltrasonic = leftUltrasonicSensor.getDistanceMode();
		rightUltrasonic = rightUltrasonicSensor.getDistanceMode();
	}

	public void action() {
		pilot.rotate(turnDirection.angle);
		pilot.travel(delta / 2);
		pilot.rotate(-turnDirection.angle);
	}

	public void suppress() {}

	public boolean takeControl() {
		float[] distances = new float[2];
		leftUltrasonic.fetchSample(distances, 0);
		rightUltrasonic.fetchSample(distances, 1);
		delta = distances[0] - distances[1];
		turnDirection = Math.abs(delta) <= .1 ? TurnDirection.NONE : delta < 0 ? TurnDirection.LEFT : TurnDirection.RIGHT;
		return turnDirection != TurnDirection.NONE;
	}

}
