package com.jacobcxdev.ev3mazeproject.sensors;

import lejos.robotics.SampleProvider;

public interface BaseUltrasonicSensor {
	// Methods
	
	/**
	 * Gets a {@code SampleProvider} which measures the distance to an object in front of the {@code BaseUltrasonicSensor}.<br/><br/>
	 *
	 * The sample contains one element representing the distance (in metres) to an object in front of the sensor.
	 *
	 * @return A {@code SampleProvider} which measures the distance to an object in front of the {@code BaseUltrasonicSensor}.
	 */
	SampleProvider getDistanceMode();
}
