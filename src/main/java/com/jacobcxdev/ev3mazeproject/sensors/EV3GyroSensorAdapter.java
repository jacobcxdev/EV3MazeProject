package com.jacobcxdev.ev3mazeproject.sensors;

import ev3dev.sensors.ev3.EV3GyroSensor;
import lejos.robotics.SampleProvider;

/**
 * A class used to adapt an {@code EV3GyroSensorAdapter} to a {@code BaseGyroSensor}.
 *
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class EV3GyroSensorAdapter implements BaseGyroSensor {
    // Private Fields

    /**
     * The wrapped {@code EV3GyroSensorAdapter}.
     */
    private final EV3GyroSensor sensor;

    /**
     * The orientation of the {@code EV3GyroSensor}.
     */
    private GyroOrientation gyroOrientation = GyroOrientation.GLYPH_TOP;

    // Public Constructors

    /**
     * Creates a {@code EV3GyroSensorAdapter} object.
     *
     * @param sensor The {@code EV3GyroSensor} to wrap.
     */
    public EV3GyroSensorAdapter(EV3GyroSensor sensor) {
        this.sensor = sensor;
    }

    // BaseGyroSensor Override Methods

    @Override
    public SampleProvider getAngleMode() {
        return sensor.getAngleMode();
    }

    @Override
    public GyroOrientation getGyroOrientation() {
        return gyroOrientation;
    }

    @Override
    public void setGyroOrientation(GyroOrientation orientation) {
        this.gyroOrientation = orientation;
    }

    @Override
    public void reset() {
        sensor.reset();
    }
}
