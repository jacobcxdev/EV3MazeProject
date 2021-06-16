package com.jacobcxdev.ev3mazeproject.sensors;

import lejos.robotics.SampleProvider;
import lejos.robotics.Touch;

public interface BaseTouchSensor extends Touch {
    // Methods

    /**
     * Gets a {@code SampleProvider} which indicates whether the {@code BaseTouchSensor} is depressed.<br/><br/>
     *
     * The sample contains one element representing whether the sensor is depressed:<br/>
     * <ul>
     *     <li>1 if the sensor is depressed,</li>
     *     <li>otherwise 0.</li>
     * </ul>
     *
     * @return A {@code SampleProvider} which indicates whether the {@code BaseTouchSensor} is depressed..
     */
    SampleProvider getTouchMode();
}
