package com.jacobcxdev.ev3mazeproject.hardware;

import lejos.hardware.port.Port;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * A class used to represent a sensor connected to one of the EV3's ports.
 *
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class SensorDefinition extends PartDefinition {
    // Public Static Classes

    /**
     * A subclass of {@code Exception} which is used to indicate when a {@code Type} is used with an unsupported {@code Family} value.
     */
    public static class InvalidFamilyForTypeException extends Exception {
        // Public Constructors

        /**
         * Creates an {@code InvalidFamilyForTypeException} object.
         *
         * @param family The {@code Family} involved in the exception.
         * @param type The {@code Type} involved in the exception.
         */
        public InvalidFamilyForTypeException(Family family, Type type) {
            super(String.format("Invalid family %s for type %s. Valid families are: %s.", family, type, type.validFamilies));
        }
    }

    // Public Enumerations

    /**
     * An enumeration to represent the different possible types of a sensor.
     */
    public enum Type {
        GYRO("Gyro", EnumSet.of(Family.EV3)),
        TOUCH("Touch", EnumSet.of(Family.EV3, Family.NXT)),
        ULTRASONIC("Ultrasonic", EnumSet.of(Family.EV3, Family.NXT));

        // Public Fields

        /**
         * The type as a string.
         */
        public final String rawValue;

        /**
         * A set of valid {@code Family} values for this {@code Type}.
         */
        public final EnumSet<Family> validFamilies;

        // Constructors

        /**
         * Creates a {@code Type} instance.
         *
         * @param rawValue The type as a string.
         * @param validFamilies A set of valid {@code Family} values for this {@code Type}.
         */
        Type(String rawValue, EnumSet<Family> validFamilies) {
            this.rawValue = rawValue;
            this.validFamilies = validFamilies;
        }

        // Public Methods

        /**
         * Returns whether a given family is valid for this {@code Type}.
         *
         * @param family The family to check with.
         * @return Whether a given family is valid for this {@code Type}.
         */
        public boolean isValidFamily(Family family) {
            return validFamilies.contains(family);
        }
    }

    // Private Static Fields

    /**
     * A {@code Map} mapping the name of each {@code Class} used by the part to its respective {@code Class}.
     */
    private static final Map<String, Class<?>> classStore = new HashMap<>();

    // Public Fields

    /**
     * The {@code Type} of the sensor.
     */
    public final Type type;

    // Public Constructors

    /**
     * Creates a {@code SensorDefinition} object.
     *
     * @param port The {@code Port} which the sensor is connected to.
     * @param family The {@code Family} of the sensor.
     * @param type The {@code Type} of the sensor.
     */
    public SensorDefinition(Port port, Family family, Type type) throws InvalidFamilyForTypeException {
        super(port, family);
        this.type = type;

        if (!type.isValidFamily(family)) {
            throw new InvalidFamilyForTypeException(family, type);
        }
    }

    // PartDefinition Override Methods

    @Override
    protected String[] getPartPackageNames() {
        return new String[] {"com.jacobcxdev.ev3mazeproject.sensors", "ev3dev.hardware", "ev3dev.sensors"};
    }

    @Override
    public Object newPartInstance() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        var sensor = classForUnqualifiedName(family.name() + type.rawValue + "Sensor", classStore).getConstructor(Port.class).newInstance(port);
        return classForUnqualifiedName(family.name() + type.rawValue + "SensorAdapter", classStore).getConstructor(sensor.getClass()).newInstance(sensor);
    }
}
