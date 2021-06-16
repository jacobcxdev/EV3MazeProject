package com.jacobcxdev.ev3mazeproject.sensors;

import ev3dev.sensors.nxt.NXTTouchSensor;
import lejos.robotics.SampleProvider;

/**
 * A class used to adapt a {@code NXTTouchSensor} to a {@code BaseTouchSensor}.
 *
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class NXTTouchSensorAdapter implements BaseTouchSensor {
    // Private Fields

    /**
     * The wrapped {@code NXTTouchSensor}.
     */
    private final NXTTouchSensor sensor;

    // Public Constructors

    /**
     * Creates a {@code NXTTouchSensorAdapter} object.
     *
     * @param sensor The {@code NXTTouchSensor} to wrap.
     */
    public NXTTouchSensorAdapter(NXTTouchSensor sensor) {
        this.sensor = sensor;
    }

    // BaseTouchSensor Override Methods

    @Override
    public SampleProvider getTouchMode() {
        return sensor.getTouchMode();
    }

    @Override
    public boolean isPressed() {
        return false;
    }
}
