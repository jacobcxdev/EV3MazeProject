package com.jacobcxdev.ev3mazeproject.hardware;

import lejos.hardware.port.Port;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * A class used to represent a regulated motor connected to one of the EV3's ports.
 *
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class RegulatedMotorDefinition extends PartDefinition {
    // Public Enumerations

    /**
     * An enumeration to represent the different possible sizes of a regulated motor.
     */
    public enum Size {
        LARGE("Large"),
        MEDIUM("Medium"),
        NULL("");

        // Public Fields

        /**
         * The size as a string.
         */
        public final String rawValue;

        // Constructors

        /**
         * Creates a {@code Size} instance.
         *
         * @param rawValue The size as a string.
         */
        Size(String rawValue) {
            this.rawValue = rawValue;
        }
    }

    // Private Static Fields

    /**
     * A {@code Map} mapping the name of each {@code Class} used by the part to its respective {@code Class}.
     */
    private static final Map<String, Class<?>> classStore = new HashMap<>();

    //  Public Fields

    /**
     * The {@code Size} of the motor.
     */
    public final Size size;

    // Public Constructors

    /**
     * Creates a {@code RegulatedMotorDefinition} object.
     *
     * @param port The {@code Port} which the motor is connected to.
     * @param family The {@code Family} of the motor.
     * @param size The {@code Size} of the motor.
     */
    public RegulatedMotorDefinition(Port port, Family family, Size size) {
        super(port, family);
        this.size = size;
    }

    // PartDefinition Override Methods

    @Override
    protected String[] getPartPackageNames() {
        return new String[] {"ev3dev.actuators.lego.motors", "ev3dev.hardware"};
    }

    @Override
    public Object newPartInstance() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return classForUnqualifiedName(family.name() + size.rawValue + "RegulatedMotor", classStore).getConstructor(Port.class).newInstance(port);
    }
}
