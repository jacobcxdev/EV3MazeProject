package com.jacobcxdev.ev3mazeproject.sensors;

import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.robotics.SampleProvider;

/**
 * A class used to adapt an {@code EV3TouchSensor} to a {@code BaseTouchSensor}.
 *
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class EV3TouchSensorAdapter implements BaseTouchSensor {
    // Private Fields

    /**
     * The wrapped {@code EV3TouchSensor}.
     */
    private final EV3TouchSensor sensor;

    // Public Constructors

    /**
     * Creates a {@code EV3TouchSensorAdapter} object.
     *
     * @param sensor The {@code EV3TouchSensor} to wrap.
     */
    public EV3TouchSensorAdapter(EV3TouchSensor sensor) {
        this.sensor = sensor;
    }

    // BaseTouchSensor Override Methods

    @Override
    public SampleProvider getTouchMode() {
        return sensor.getTouchMode();
    }

    @Override
    public boolean isPressed() {
        return sensor.isPressed();
    }
}
