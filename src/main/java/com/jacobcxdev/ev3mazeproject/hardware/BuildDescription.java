package com.jacobcxdev.ev3mazeproject.hardware;

import com.jacobcxdev.ev3mazeproject.sensors.BaseGyroSensor;

/**
 * A class used to describe various parameters of the EV3 build.
 *
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class BuildDescription {
    // Public Fields

    /**
     * The width between the two {@code BaseUltrasonicSensor}s (in millimetres).
     */
    public final float ultrasonicSensorGap;

    /**
     * The diameter of the EV3's wheels (in millimetres).
     */
    public final float wheelDiameter;

    /**
     * The offset of the EV3's wheels from the centre (in millimetres).
     */
    public final float wheelOffset;

    /**
     * The orientation of the {@code BaseGyroSensor}.
     */
    public final BaseGyroSensor.GyroOrientation gyroOrientation;

    // Public Constructors

    /**
     * Creates a {@code BuildDescription} object.
     *
     * @param ultrasonicSensorGap The width between the two {@code BaseUltrasonicSensor}s (in millimetres).
     * @param wheelDiameter The diameter of the EV3's wheels (in millimetres).
     * @param wheelOffset The offset of the EV3's wheels from the centre (in millimetres).
     * @param gyroOrientation The orientation of the {@code BaseGyroSensor}.
     */
    public BuildDescription(float ultrasonicSensorGap, float wheelDiameter, float wheelOffset, BaseGyroSensor.GyroOrientation gyroOrientation) {
        this.ultrasonicSensorGap = ultrasonicSensorGap;
        this.wheelDiameter = wheelDiameter;
        this.wheelOffset = wheelOffset;
        this.gyroOrientation = gyroOrientation;
    }
}
