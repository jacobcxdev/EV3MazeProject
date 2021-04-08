package sensors;

import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.SampleProvider;

/**
 * A class used to adapt a {@code NXTUltrasonicSensor} to a {@code BaseUltrasonicSensor}.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class NXTUltrasonicSensorAdapter implements BaseUltrasonicSensor {
	// Private Fields
	
	/**
	 * The wrapped {@code NXTUltrasonicSensor}.
	 */
	private NXTUltrasonicSensor sensor;
	
	// Public Constructors
	
	/**
	 * Creates a {@code NXTUltrasonicSensorAdapter} object.
	 * 
	 * @param sensor The {@code NXTUltrasonicSensor} to wrap.
	 */
	public NXTUltrasonicSensorAdapter(NXTUltrasonicSensor sensor) {
		this.sensor = sensor;
	}
	
	// Override Methods
	
	@Override
	public SampleProvider getDistanceMode() {
		return sensor.getDistanceMode();
	}
	
}
