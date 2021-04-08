package sensors;

import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

/**
 * A class used to adapt an {@code EV3UltrasonicSensor} to a {@code BaseUltrasonicSensor}.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class EV3UltrasonicSensorAdapter implements BaseUltrasonicSensor {
	// Private Fields
	
	/**
	 * The wrapped {@code EV3UltrasonicSensor}.
	 */
	private EV3UltrasonicSensor sensor;
	
	// Public Constructors
	
	/**
	 * Creates a {@code EV3UltrasonicSensorAdapter} object.
	 * 
	 * @param sensor The {@code EV3UltrasonicSensor} to wrap.
	 */
	public EV3UltrasonicSensorAdapter(EV3UltrasonicSensor sensor) {
		this.sensor = sensor;
	}
	
	// Override Methods
	
	@Override
	public SampleProvider getDistanceMode() {
		return sensor.getDistanceMode();
	}
	
}
