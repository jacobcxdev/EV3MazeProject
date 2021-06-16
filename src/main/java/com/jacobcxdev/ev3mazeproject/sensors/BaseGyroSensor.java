package com.jacobcxdev.ev3mazeproject.sensors;

import lejos.robotics.SampleProvider;

public interface BaseGyroSensor {
    // Enumerations

    /**
     * An enumeration to represent all possible orientations of an {@code EV3GyroSensor} object.
     */
     enum GyroOrientation {
        GLYPH_TOP(1),
        GLYPH_BOTTOM(-1);

        // Public Fields

        /**
         * The multiplier associated with the current orientation.
         */
        public final double multiplier;

        // Constructors

        /**
         * Creates a {@code GyroOrientation} instance.
         *
         * @param multiplier The multiplier associated with the orientation.
         */
        GyroOrientation(double multiplier) {
            this.multiplier = multiplier;
        }
    }

    // Methods

    /**
     * Gets a {@code SampleProvider} which measures the orientation of the {@code BaseGyroSensor} with respect to its start orientation.<br/><br/>
     *
     * The sample contains one element representing the orientation (in degrees) of the sensor with respect to its start orientation.
     *
     * @return A {@code SampleProvider} which measures the orientation of the {@code BaseGyroSensor} with respect to its start orientation.
     */
    SampleProvider getAngleMode();

    /**
     * Gets the orientation of the {@code BaseGyroSensor}.
     *
     * @return The orientation of the {@code BaseGyroSensor}.
     */
    GyroOrientation getGyroOrientation();

    /**
     * Sets the orientation of the {@code BaseGyroSensor} to a given value.
     *
     * @param orientation The given orientation of the {@code BaseGyroSensor}.
     */
    void setGyroOrientation(GyroOrientation orientation);

    /**
     * Hardware calibrates the {@code BaseGyroSensor}, resetting the orientation to 0.<br/><br/>
     *
     * The sensor should be motionless during calibration.
     */
    void reset();
}
